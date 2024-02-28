/*
 * RagdollModel.java
 *
 * This is one of the files that you are expected to modify. Please limit changes to 
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.physics.ragdoll;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * A ragdoll whose body parts are boxes connected by joints
 *
 * This class has several bodies connected by joints.  For information on how
 * the joints fit together, see the ragdoll diagram at the start of the class.
 * 
 * 
 */
public class RagdollModel extends ComplexObstacle {
	/** Files for the body textures */
	public static final String[] BODY_PARTS = { "torso", "head", "arm", "forearm", "thigh", "shin" };

	// Layout of ragdoll
	//
	// o = joint
	//                   ___
	//                  |   |
	//                  |_ _|
	//   ______ ______ ___o___ ______ ______
	//  |______o______o       o______o______|
	//                |       |
	//                |       |
	//                |_______|
	//                | o | o |
	//                |   |   |
	//                |___|___|
	//                | o | o |
	//                |   |   |
	//                |   |   |
	//                |___|___|
	//

	/** Indices for the body parts in the bodies array */
	private static final int PART_NONE = -1;
	private static final int PART_BODY = 0;
	private static final int PART_HEAD = 1;
	private static final int PART_LEFT_ARM  = 2;
	private static final int PART_RIGHT_ARM = 3;
	private static final int PART_LEFT_FOREARM  = 4;
	private static final int PART_RIGHT_FOREARM = 5;
	private static final int PART_LEFT_THIGH  = 6;
	private static final int PART_RIGHT_THIGH = 7;
	private static final int PART_LEFT_SHIN  = 8;
	private static final int PART_RIGHT_SHIN = 9;
	
	/**
	 * Returns the texture index for the given body part 
	 *
	 * As some body parts are symmetrical, we reuse textures.
	 *
	 * @return the texture index for the given body part
	 */
	private static int partToAsset(int part) {
		switch (part) {
		case PART_BODY:
			return 0;
		case PART_HEAD:
			return 1;
		case PART_LEFT_ARM:
		case PART_RIGHT_ARM:
			return 2;
		case PART_LEFT_FOREARM:
		case PART_RIGHT_FOREARM:
			return 3;
		case PART_LEFT_THIGH:
		case PART_RIGHT_THIGH:
			return 4;
		case PART_LEFT_SHIN:
		case PART_RIGHT_SHIN:
			return 5;
		default:
			return -1;
		}
	}

	/**
	 * Returns true if the body part is on the right side
	 *
	 * @return true if the body part is on the right side
	 */
	private static boolean partOnLeft(int part) {
		switch (part) {
			case PART_LEFT_ARM:
			case PART_LEFT_FOREARM:
			case PART_LEFT_THIGH:
			case PART_LEFT_SHIN:
				return true;
			default:
				return false;
		}
	}

	/** The initializing data (to avoid magic numbers) */
	private final JsonValue data;

    /** Bubble generator to glue to snorkler. */
    private final BubbleGenerator bubbler;
    
	/** Texture assets for the body parts */
	private TextureRegion[] partTextures;	

	/** Cache vector for organizing body parts */
	private final Vector2 partCache = new Vector2();

	/**
	 * Creates a new ragdoll with defined by the given data.
	 *
	 * The position of the ragdoll defines the position of the
	 * head. All other body parts are offset from that.
	 *
	 * @param data  	The physics constants for this model
	 */
	public RagdollModel(JsonValue data) {
		super(	data.get("doll").get("torso").getFloat(0),
				data.get("doll").get("torso").getFloat(1));
		this.data = data.get("doll");

		float ox = getX()+this.data.get("head").getFloat(0)+data.get("bubbles").get("offset").getFloat(0);
		float oy = getY()+this.data.get("head").getFloat(1)+data.get("bubbles").get("offset").getFloat(1);
	    bubbler = new BubbleGenerator(data.get("bubbles"),ox,oy);
	}

	/**
	 * Initializes the various body parts.
	 */
	protected void init() {
		// We do not do anything yet.
		BoxObstacle part;
		
		// TORSO
	    part = makePart(PART_BODY, PART_NONE);
	    part.setFixedRotation(true);
		
		// HEAD
		makePart(PART_HEAD, PART_BODY);

		// ARMS
		makePart(PART_LEFT_ARM, PART_BODY);
		part = makePart(PART_RIGHT_ARM, PART_BODY);
		part.setAngle((float)Math.PI);
		
		// FOREARMS
		makePart(PART_LEFT_FOREARM, PART_LEFT_ARM);
		part = makePart(PART_RIGHT_FOREARM, PART_RIGHT_ARM);
		part.setAngle((float)Math.PI);
		
		// THIGHS
		makePart(PART_LEFT_THIGH,  PART_BODY);
		makePart(PART_RIGHT_THIGH, PART_BODY);

		// SHINS
		makePart(PART_LEFT_SHIN,  PART_LEFT_THIGH);
		makePart(PART_RIGHT_SHIN, PART_RIGHT_THIGH);

		bubbler.setDrawScale(drawScale);
		bodies.add(bubbler);
	}
	
    /**
     * Sets the drawing scale for this physics object
     *
     * The drawing scale is the number of pixels to draw before Box2D unit. Because
     * mass is a function of area in Box2D, we typically want the physics objects
     * to be small.  So we decouple that scale from the physics object.  However,
     * we must track the scale difference to communicate with the scene graph.
     *
     * We allow for the scaling factor to be non-uniform.
     *
     * @param x  the x-axis scale for this physics object
     * @param y  the y-axis scale for this physics object
     */
    public void setDrawScale(float x, float y) {
    	super.setDrawScale(x,y);
    	
    	if (partTextures != null && bodies.size == 0) {
    		init();
    	}
    }
    
    /**
     * Sets the array of textures for the individual body parts.
     *
     * The array should be BODY_TEXTURE_COUNT in size.
     *
     * @param textures the array of textures for the individual body parts.
     */
    public void setPartTextures(TextureRegion[] textures) {
    	assert textures != null && textures.length > BODY_PARTS.length : "Texture array is not large enough";
    	
    	partTextures = new TextureRegion[BODY_PARTS.length];
    	System.arraycopy(textures, 0, partTextures, 0, BODY_PARTS.length);
    	if (bodies.size == 0) {
    		init();
    	} else {
    		for(int ii = 0; ii <= PART_RIGHT_SHIN; ii++) {
    			((SimpleObstacle)bodies.get(ii)).setTexture(partTextures[partToAsset(ii)]);
    		}
    	}
    }
    
	/**
     * Returns the array of textures for the individual body parts.
     *
     * Modifying this array will have no affect on the physics objects.
     *
     * @return the array of textures for the individual body parts.
     */
    public TextureRegion[] getPartTextures() {
    	return partTextures;
    }
    
    /**
     * Returns the bubble generator welded to the mask
     *
     * @return the bubble generator welded to the mask
     */
    public BubbleGenerator getBubbleGenerator() {
    	return bubbler;
    }

	/**
	 * Helper method to make a single body part
	 *
	 * While it looks like this method "connects" the pieces, it does not really.  It
	 * puts them in position to be connected by joints, but they will fall apart unless
	 * you make the joints.
	 *
	 * @param part		The part to make
	 * @param connect	The part to connect to
	 *
	 * @return the newly created part
	 */
	private BoxObstacle makePart(int part, int connect) {
		TextureRegion texture = partTextures[partToAsset(part)];
		float x = getX();
		float y = getY();
		if (connect != PART_NONE) {
			x = data.get( BODY_PARTS[partToAsset( part )] ).getFloat( 0 );
			y = data.get( BODY_PARTS[partToAsset( part )] ).getFloat( 1 );
			if (partOnLeft( part )) {
				x = -x;
			}
		}

		partCache.set(x,y);
		if (connect != PART_NONE) {
			partCache.add(bodies.get(connect).getPosition());
		}

		float dwidth  = texture.getRegionWidth()/drawScale.x;
		float dheight = texture.getRegionHeight()/drawScale.y;

		BoxObstacle body = new BoxObstacle(partCache.x, partCache.y, dwidth, dheight);
		body.setDrawScale(drawScale);
		body.setTexture(texture);
		body.setDensity(data.getFloat( "density", 0.0f ));
		bodies.add(body);
		return body;
	}

	/**
	 * Creates the joints for this object.
	 * 
	 * We implement our custom logic here.
	 *
	 * @param world Box2D world to store joints
	 *
	 * @return true if object allocation succeeded
	 */
	protected boolean createJoints(World world) {
		assert bodies.size > 0;

		//#region INSERT CODE HERE
		// Implement all of the Ragdoll Joints here
		// You may add additional methods if you find them useful

		//#endregion
		
		
		// Weld the bubbler to this mask
		WeldJointDef weldDef = new WeldJointDef();
		weldDef.bodyA = bodies.get(PART_HEAD).getBody();
		weldDef.bodyB = bubbler.getBody();
		weldDef.localAnchorA.set(bubbler.getOffset());
		weldDef.localAnchorB.set(0,0);
		Joint wjoint = world.createJoint(weldDef);
		joints.add(wjoint);

		return true;
	}
}