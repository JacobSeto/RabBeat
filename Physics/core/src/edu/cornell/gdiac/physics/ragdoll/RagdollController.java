/*
 * RagdollController.java
 *
 * You are not expected to modify this file at all.  You are free to look at it, however,
 * and determine how it works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.physics.ragdoll;

import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.util.RandomController;

/**
 * Gameplay specific controller for the ragdoll fishtank. 
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class RagdollController extends WorldController {
	/** The transparency for foreground image */
	private static Color FORE_COLOR = new Color(0.0f, 0.2f, 0.3f, 0.2f);

	/** Texture asset for mouse crosshairs */
	private TextureRegion crosshairTexture;
	/** Texture asset for background image */
	private TextureRegion backgroundTexture;
	/** Texture asset for watery foreground */
	private TextureRegion foregroundTexture;
	/** Texture asset for the bubble generator */
	private TextureRegion bubbleTexture;
	/** Texture assets for the body parts */
	private TextureRegion[] bodyTextures;

	/** The bubble sounds */
	private Sound[] bubbleSounds;

	/** Physics constants for initialization */
	private JsonValue constants;

	/** Reference to the character's ragdoll */
	private RagdollModel ragdoll;

	/** Mouse selector to move the ragdoll */
	private ObstacleSelector selector;
	
	/**
	 * Creates and initialize a new instance of the ragdoll fishtank
	 *
	 * The world has lower gravity to simulate being underwater.
	 */
	public RagdollController() {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
	}

	/**
	 * Gather the assets for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory	Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {

		constants = directory.getEntry( "ragdoll:constants", JsonValue.class );
		crosshairTexture  = new TextureRegion(directory.getEntry( "ragdoll:crosshair", Texture.class ));
		backgroundTexture = new TextureRegion(directory.getEntry( "ragdoll:background", Texture.class ));
		foregroundTexture = new TextureRegion(directory.getEntry( "ragdoll:foreground", Texture.class ));

		bubbleTexture = new TextureRegion(directory.getEntry( "ragdoll:bubble", Texture.class ));
		bodyTextures = new TextureRegion[RagdollModel.BODY_PARTS.length];
		for(int ii = 0; ii < RagdollModel.BODY_PARTS.length; ii++) {
			bodyTextures[ii] =  new TextureRegion(directory.getEntry( "ragdoll:"+RagdollModel.BODY_PARTS[ii], Texture.class ));
		}

		bubbleSounds = new Sound[constants.get("bubbles").getInt("sounds",0)];
		for(int ii = 0; ii < bubbleSounds.length; ii++) {
			bubbleSounds[ii] = directory.getEntry( "ragdoll:glub"+(ii+1), Sound.class );
		}

		constants = directory.getEntry( "ragdoll:constants", JsonValue.class );
		super.gatherAssets(directory);
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity() );
		
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		
		world = new World(gravity,false);
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Make the ragdoll
		ragdoll = new RagdollModel(constants);
		ragdoll.setDrawScale(scale.x,scale.y);
		ragdoll.setPartTextures(bodyTextures);
		ragdoll.getBubbleGenerator().setTexture(bubbleTexture);
		addObject(ragdoll);

		// Create ground pieces
		PolygonObstacle obj;
		JsonValue walljv = constants.get("walls");
		JsonValue defaults = constants.get("defaults");
		obj = new PolygonObstacle(walljv.get(0).asFloatArray(), 0, 0);
		obj.setBodyType(BodyDef.BodyType.StaticBody);
		obj.setDensity(defaults.getFloat("density",0));
		obj.setFriction(defaults.getFloat("friction",0));
		obj.setRestitution(defaults.getFloat("restitution",0));
		obj.setDrawScale(scale);
		obj.setTexture(earthTile);
		obj.setName("wall1");
		addObject(obj);

		obj = new PolygonObstacle(walljv.get(1).asFloatArray(), 0, 0);
		obj.setBodyType(BodyDef.BodyType.StaticBody);
		obj.setDensity(defaults.getFloat("density",0));
		obj.setFriction(defaults.getFloat("friction",0));
		obj.setRestitution(defaults.getFloat("restitution",0));
		obj.setDrawScale(scale);
		obj.setTexture(earthTile);
		obj.setName("wall2");
		addObject(obj);

		selector = new ObstacleSelector(world);
		selector.setTexture(crosshairTexture);
		selector.setDrawScale(scale);
		world.setGravity( new Vector2(0, defaults.getFloat( "gravity", 0 )) );
	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt 	Number of seconds since last animation frame
	 */
	public void update(float dt) {
	    // Move an object if touched
		InputController input = InputController.getInstance();
	    if (input.didTertiary() && !selector.isSelected()) {
	        selector.select(input.getCrossHair().x,input.getCrossHair().y);
	    } else if (!input.didTertiary() && selector.isSelected()) {
	        selector.deselect();
	    } else {
	        selector.moveTo(input.getCrossHair().x,input.getCrossHair().y);
	    }

	    // Play a sound for each bubble
	    if (ragdoll.getBubbleGenerator().didBubble()) {
	        // Pick a sound
	        int indx =  RandomController.rollInt(0,bubbleSounds.length-1);
	        bubbleSounds[indx].play(); // It is okay to play simultaneous copies
	    }
	}
	
	/**
	 * Draw the physics objects together with foreground and background
	 *
	 * This is completely overridden to support custom background and foreground art.
	 *
	 * @param dt Timing values from parent loop
	 */
	public void draw(float dt) {
		canvas.clear();
		
		// Draw background unscaled.
		canvas.begin();
		canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
		canvas.end();
		
		canvas.begin();
		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}
		canvas.end();
		
		if (isDebug()) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}
		
		// Draw foreground last.
		canvas.begin();
		canvas.draw(foregroundTexture, FORE_COLOR,  0, 0, canvas.getWidth(), canvas.getHeight());
		selector.draw(canvas);
		canvas.end();
	}

}