/*
 * RoundedRectGameObject.java
 *
 *  This class implements a capsule physics object. A capsule is a box with semicircular
 *  ends along the major axis.  They are a popular physics objects, particularly for
 *  character avatars.  The rounded ends means they are less likely to snag, and they
 *  naturally fall off platforms when they go too far.
 *
 * Based on Walker M. White's CapsuleObstacle, which was based on the original PhysicsDemo
 * Lab by Don Holden, 2007
 */
package edu.cornell.gdiac.rabbeat.objects;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.rabbeat.*;  // For GameCanvas

/**
 * Box-shaped model to support collisions.
 *
 * Unless otherwise specified, the center of mass is as the center.
 */
public class RoundedRectGameObject extends SimpleGameObject {
	/** Epsilon factor to prevent issues with the fixture seams */
	private static final float DEFAULT_EPSILON = 0.01f;

	/** Shape information for this box */
	protected PolygonShape shape;
	/** Shape information for the end cap */
	protected CircleShape end1;
	/** Shape information for the end cap */
	protected PolygonShape end2;
	/** Rectangle representation of capsule core for fast computation */
	protected Rectangle center;

	/** The width and height of the box */
	private Vector2 dimension;
	/** A cache value for when the user wants to access the dimensions */
	private Vector2 sizeCache;
	/** A cache value for the center fixture (for resizing) */
	private Fixture core;
	/** A cache value for the first end cap fixture (for resizing) */
	private Fixture cap1;
	/** A cache value for the second end cap fixture (for resizing) */
	private Fixture cap2;
	/** Cache of the polygon vertices (for resizing) */
	private float[] vertices;
    /** Cache of the semi-oval vertices */
    private final Vector2[] end2Vertices;


    /** A cache value for computing fixtures */
	private Vector2 posCache = new Vector2();
	/** The seam offset of the core rectangle */
	private float seamEpsilon;

	/**
	 * Returns the dimensions of this box
	 *
	 * This method does NOT return a reference to the dimension vector. Changes to this
	 * vector will not affect the shape.  However, it returns the same vector each time
	 * its is called, and so cannot be used as an allocator.
	 *
	 * @return the dimensions of this box
	 */
	public Vector2 getDimension() {
		return sizeCache.set(dimension);
	}

	/**
	 * Sets the dimensions of this box
	 *
	 * This method does not keep a reference to the parameter.
	 *
	 * @param value  the dimensions of this box
	 */
	public void setDimension(Vector2 value) {
		setDimension(value.x, value.y);
	}

	/**
	 * Sets the dimensions of this box
	 *
	 * @param width   The width of this box
	 * @param height  The height of this box
	 */
	public void setDimension(float width, float height) {
		dimension.set(width, height);
		markDirty(true);
		resize(width, height);
	}

	/**
	 * Returns the box width
	 *
	 * @return the box width
	 */
	public float getWidth() {
		return dimension.x;
	}

	/**
	 * Sets the box width
	 *
	 * @param value  the box width
	 */
	public void setWidth(float value) {
		sizeCache.set(value,dimension.y);
		setDimension(sizeCache);
	}

	/**
	 * Returns the box height
	 *
	 * @return the box height
	 */
	public float getHeight() {
		return dimension.y;
	}

	/**
	 * Sets the box height
	 *
	 * @param value  the box height
	 */
	public void setHeight(float value) {
		sizeCache.set(dimension.x,value);
		setDimension(sizeCache);
	}

	public void setPlayer(){
		end1.setRadius(0);
		vertices[3] = center.y+center.height;
		vertices[5] = center.y+center.height;
		shape.set(vertices);
	}

	/**
	 * Sets the seam offset of the core rectangle
	 *
	 * If the center rectangle is exactly the same size as the circle radius,
	 * you may get catching at the seems.  To prevent this, you should make
	 * the center rectangle epsilon narrower so that everything rolls off the
	 * round shape. This parameter is that epsilon value
	 *
	 * @parm  value the seam offset of the core rectangle
	 */
	public void setSeamOffset(float value) {
		seamEpsilon = value;
		markDirty(true);
	}

	/**
	 * Returns the seam offset of the core rectangle
	 *
	 * If the center rectangle is exactly the same size as the circle radius,
	 * you may get catching at the seems.  To prevent this, you should make
	 * the center rectangle epsilon narrower so that everything rolls off the
	 * round shape. This parameter is that epsilon value
	 *
	 * @return the seam offset of the core rectangle
	 */
	public float getSeamOffset() {
		return seamEpsilon;
	}

	/**
	 * Creates a new capsule object width the given orientation.
	 *
	 * The size is expressed in physics units NOT pixels.  In order for
	 * drawing to work properly, you MUST set the drawScale. The drawScale
	 * converts the physics units to pixels.
	 *
	 * @param x  		Initial x position of the box center
	 * @param y  		Initial y position of the box center
	 * @param width		The object width in physics units
	 * @param height	The object width in physics units
	 */
	public RoundedRectGameObject(float x, float y, float width, float height) {
		super(x,y);
		dimension = new Vector2();
		sizeCache = new Vector2();
		shape = new PolygonShape();
		end1 = new CircleShape();
		end2 = new PolygonShape();

		center = new Rectangle();
		vertices = new float[8];
        end2Vertices = new Vector2[7];

		core = null;
		cap1 = null;
		cap2 = null;
		seamEpsilon = DEFAULT_EPSILON;

		// Initialize
		resize(width, height);
	}

	/**
	 * Reset the polygon vertices in the shape to match the dimension.
	 */
	private void resize(float width, float height) {
		dimension.set(width,height);

		// Get an AABB for the core
		center.x =  -width/2.0f;
		center.y =  -height/2.0f;
		center.width  = width;
		center.height = height;

		// Now adjust the core
		float r = 0;

		r = width/2.0f;
		center.y += r;
		center.height -= 2*r;
		center.x += seamEpsilon;
		center.width -= 2*seamEpsilon;

		// Make the box with the center in the center
		vertices[0] = center.x;
		vertices[1] = center.y;
		vertices[2] = center.x;
		vertices[3] = center.y+center.height;
		vertices[4] = center.x+center.width;
		vertices[5] = center.y+center.height;
		vertices[6] = center.x+center.width;
		vertices[7] = center.y;
		shape.set(vertices);

		end1.setRadius(r);

        // Make the bottom semi-oval
        end2Vertices[0] = new Vector2(r, 0f);
        end2Vertices[1] = new Vector2(r, -5f * r / 7f);
        end2Vertices[2] = new Vector2(6f * r / 7f, -27f * r / 28f);
        end2Vertices[3] = new Vector2(0f, -r);
        end2Vertices[4] = new Vector2(-6f * r / 7f, -27f * r / 28f);
        end2Vertices[5] = new Vector2(-r, -5f * r / 7f);
        end2Vertices[6] = new Vector2(-r, 0f);
        end2.set(end2Vertices);
	}



	/**
	 * Sets the density of this body
	 *
	 * The density is typically measured in usually in kg/m^2. The density can be zero or
	 * positive. You should generally use similar densities for all your fixtures. This
	 * will improve stacking stability.
	 *
	 * @param value  the density of this body
	 */
	public void setDensity(float value) {
		fixture.density = value;
		if (body != null) {
			core.setDensity(value);
			cap1.setDensity(value/2.0f);
			cap2.setDensity(value/2.0f);
			if (!masseffect) {
				body.resetMassData();
			}
		}
	}

	/**
	 * Create new fixtures for this body, defining the shape
	 *
	 * This is the primary method to override for custom physics objects
	 */
	protected void createFixtures() {
		if (body == null) {
			return;
		}

		releaseFixtures();

		// Create the fixture
		fixture.shape = shape;
		core = body.createFixture(fixture);

		fixture.density = fixture.density/2.0f;
		posCache.set(0,0);

		posCache.y = center.y+center.height;
		end1.setPosition(posCache);
		fixture.shape = end1;
		cap1 = body.createFixture(fixture);
		posCache.y = center.y;

        // Set the position of the semi-oval
        for (int i = 0; i < end2Vertices.length; i++) {
            end2Vertices[i] = new Vector2(posCache.x + end2Vertices[i].x, posCache.y + end2Vertices[i].y);
        }
        end2.set(end2Vertices);

		fixture.shape = end2;
		cap2 = body.createFixture(fixture);

		markDirty(false);
	}

	/**
	 * Release the fixtures for this body, reseting the shape
	 *
	 * This is the primary method to override for custom physics objects
	 */
	protected void releaseFixtures() {
		if (core != null) {
			body.destroyFixture(core);
			core = null;
		}
		if (cap1 != null) {
			body.destroyFixture(cap1);
			cap1 = null;
		}
		if (cap2 != null) {
			body.destroyFixture(cap2);
			cap2 = null;
		}
	}

	/**
	 * Draws the outline of the physics body.
	 *
	 * This method can be helpful for understanding issues with collisions.
	 *
	 * @param canvas Drawing context
	 */
	public void drawDebug(GameCanvas canvas) {
		canvas.drawPhysics(shape,Color.YELLOW,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
		if (cap1 != null) {
			// Need to manually rotate caps off axis
			float dx; float dy;

			float r = center.y+center.height;
			dx = (float)(r*Math.cos(Math.PI/2.0f+getAngle()));
			dy = (float)(r*Math.sin(Math.PI/2.0f+getAngle()));

			canvas.drawPhysics(end1,Color.YELLOW,getX()+dx,getY()+dy,drawScale.x,drawScale.y);
		}
		if (cap2 != null) {
			// Need to manually rotate caps off axis
			float dx; float dy;

			float r = -center.y;
			dx = (float)(r*Math.cos(-Math.PI/2.0f+getAngle()));
			dy = (float)(r*Math.sin(-Math.PI/2.0f+getAngle()));

//			canvas.drawPhysics(end2,Color.YELLOW,getX()+dx,getY()+dy,drawScale.x,drawScale.y);
		}
        canvas.drawPhysics(end2,Color.YELLOW,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
    }

}