/*
 * GameMode.java
 *
 * This is the primary class file for running the game.  You should study this file for
 * ideas on how to structure your own root class. This class follows a 
 * model-view-controller pattern fairly strictly.
 *
 * Author: Walker M. White
 * Based on original GameX Ship Demo by Rama C. Hoetzlein, 2002
 * LibGDX version, 1/16/2015
 */
package edu.cornell.gdiac.shipdemo;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;

import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.audio.SoundEffect;
import edu.cornell.gdiac.shipdemo.GameCanvas.BlendState;
import edu.cornell.gdiac.util.FilmStrip;

import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;

/**
 * The primary controller class for the game.
 *
 * While GDXRoot is the root class, it delegates all of the work to the player mode
 * classes. This is the player mode class for running the game. In initializes all 
 * of the other classes in the game and hooks them together.  It also provides the
 * basic game loop (update-draw).
 */
public class GameMode implements ModeController {
	/** Number of rows in the ship image filmstrip */
	private static final int SHIP_ROWS = 4;
	/** Number of columns in this ship image filmstrip */
	private static final int SHIP_COLS = 5;
	/** Number of elements in this ship image filmstrip */
	private static final int SHIP_SIZE = 18;
	
	/** The background image for the battle */
	private Texture background;
	/** The image for a single proton */
	private Texture photonTexture;
	/** Texture for the ship (colored for each player) */
	private Texture shipTexture;
	/** Texture for the target reticule */
	private Texture targetTexture;
	/** The weapon fire sound for the blue player */
	private Sound blueSound;
	/** The weapon fire sound for the red player */
	private Sound redSound;
	/** amount to rotate the image by when updated*/
	private static final float ROTATION_INCREMENT = 1f;
	/**counter for rotation angle for background*/
	private float rotationAngle = 0;

    // Instance variables
	/** Read input for blue player from keyboard or game pad (CONTROLLER CLASS) */
	protected InputController blueController;
	/** Read input for red player from keyboard or game pad (CONTROLLER CLASS) */
	protected InputController redController;
    /** Handle collision and physics (CONTROLLER CLASS) */
    protected CollisionController physicsController;

	/** Location and animation information for blue ship (MODEL CLASS) */
	protected Ship shipBlue;
	/** Location and animation information for red ship (MODEL CLASS) */
	protected Ship shipRed;
	/** Shared memory pool for photons. (MODEL CLASS) */
	protected PhotonQueue photons;

	/** Store the bounds to enforce the playing region */	
	private Rectangle bounds;

	/**
	 * Creates a new game with a playing field of the given size.
	 *
	 * This constructor initializes the models and controllers for the game.  The
	 * view has already been initialized by the root class.
	 *
	 * @param width 	The width of the game window
	 * @param height 	The height of the game window
	 * @param assets	The asset directory containing all the loaded assets
	 */
	public GameMode(float width, float height, AssetDirectory assets) {
		// Extract the assets from the asset directory.  All images are textures.
		background = assets.getEntry("background", Texture.class );
		shipTexture = assets.getEntry( "ship", Texture.class );
		targetTexture = assets.getEntry( "target", Texture.class );
		photonTexture = assets.getEntry( "photon", Texture.class );

		// Initialize the photons.
		photons = new PhotonQueue();
		photons.setTexture(photonTexture);
		bounds = new Rectangle(0,0,width,height);

		// Load the sounds.  We need to use the subclass SoundBuffer because of our changes to audio.
		blueSound = assets.getEntry( "laser",  SoundEffect.class);
		redSound  = assets.getEntry( "fusion", SoundEffect.class);

		// Create the two ships and place them across from each other.

        // RED PLAYER
		shipRed  = new Ship(width*(1.0f / 3.0f), height*(1.0f / 2.0f), 0);
		shipRed.setFilmStrip(new FilmStrip(shipTexture,SHIP_ROWS,SHIP_COLS,SHIP_SIZE));
		shipRed.setTargetTexture(targetTexture);
		shipRed.setColor(new Color(1.0f, 0.25f, 0.25f, 1.0f));  // Red, but makes texture easier to see
		
        // BLUE PLAYER
		shipBlue = new Ship(width*(2.0f / 3.0f), height*(1.0f / 2.0f), 180);
		shipBlue.setFilmStrip(new FilmStrip(shipTexture,SHIP_ROWS,SHIP_COLS,SHIP_SIZE));
		shipBlue.setTargetTexture(targetTexture);
		shipBlue.setColor(new Color(0.5f, 0.5f, 1.0f, 1.0f));   // Blue, but makes texture easier to see

		// Create the input controllers.
		redController  = new InputController(1);
		blueController = new InputController(0);
        physicsController = new CollisionController();
	}

	/** 
	 * Read user input, calculate physics, and update the models.
	 *
	 * This method is HALF of the basic game loop.  Every graphics frame 
	 * calls the method update() and the method draw().  The method update()
	 * contains all of the calculations for updating the world, such as
	 * checking for collisions, gathering input, and playing audio.  It
	 * should not contain any calls for drawing to the screen.
	 */
	@Override
	public void update() {
		// Read the keyboard for each controller.
		redController.readInput ();
		blueController.readInput ();
		
		// Move the photons forward, and add new ones if necessary.
		//photons.move (width,height);
		if (redController.didPressFire() && firePhoton(shipRed,photons)) {
            redSound.play(); 
		}
		if (blueController.didPressFire() && firePhoton(shipBlue,photons)) {
			blueSound.stop();
			blueSound.play();
		}

		// Move the ships forward (ignoring collisions)
		shipRed.move(redController.getForward(),   redController.getTurn());
		shipBlue.move(blueController.getForward(), blueController.getTurn());
		photons.move(bounds);

		// Decrement teleport cooldowns and teleport if cooldown and teleport key is pressed
			shipRed.teleportCount --;
			shipBlue.teleportCount --;
		if(redController.didTeleport()){
			if(shipRed.teleportCount <= 0){
				shipRed.teleportShip(background.getHeight());
			}
		}
		if(blueController.didTeleport() && shipBlue.teleportCount <= 0){
			shipBlue.teleportShip(background.getHeight());
		}
		
		// Change the target position.
		shipRed.acquireTarget(shipBlue);
		shipBlue.acquireTarget(shipRed);
		
		// This call handles BOTH ships.
		physicsController.checkForCollision(shipBlue, shipRed);
		physicsController.checkInBounds(shipBlue, bounds);
		physicsController.checkInBounds(shipRed, bounds);

		// This call handles ship-to-photon collisions
		physicsController.checkForCollisionPhotons(shipBlue, shipRed, photons);
	}

	/**
	 * Draw the game on the provided GameCanvas
	 *
	 * There should be no code in this method that alters the game state.  All 
	 * assignments should be to local variables or cache fields only.
	 *
	 * @param canvas The drawing context
	 */
	@Override
	public void draw(GameCanvas canvas) {
		rotationAngle+=ROTATION_INCREMENT;
		Color backgroundTint = new Color(.4f,.3f,.3f,1);
		canvas.draw(background, backgroundTint, background.getWidth()/2, background.getHeight()/2,
				canvas.getWidth()/2, canvas.getHeight()/2, rotationAngle, 4, 4);
		
		// First drawing pass (ships + shadows)
		shipBlue.drawShip(canvas);		// Draw Red and Blue ships
		shipRed.drawShip(canvas);

		// Second drawing pass (photons)
		canvas.setBlendState(BlendState.ADDITIVE);
		shipBlue.drawTarget(canvas);  // Draw target
		shipRed.drawTarget(canvas);   // Draw target
		photons.draw(canvas);         // Draw Photons
		canvas.setBlendState(BlendState.ALPHA_BLEND);
	}

	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		// Garbage collection here is sufficient.  Nothing to do
	}
	
	/**
	 * Resize the window for this player mode to the given dimensions.
	 *
	 * This method is not guaranteed to be called when the player mode
	 * starts.  If the window size is important to the player mode, then
	 * these values should be passed to the constructor at start.
	 *
	 * @param width The width of the game window
	 * @param height The height of the game window
	 */
	public void resize(int width, int height) {
		bounds.set(0,0,width,height);
	}
	
	/**
 	 * Fires a photon from the ship, adding it to the PhotonQueue.
 	 * 
 	 * This is not inside either PhotonQueue or Ship because it is a relationship
 	 * between to objects.  As we will see in class, we do not want to code binary
 	 * relationships that way (because it increases dependencies).
 	 *
 	 * @param ship  	Ship firing the photon
 	 * @param photons 	PhotonQueue for allocation
 	 */
	private boolean firePhoton(Ship ship, PhotonQueue photons) {
		// Only process if enough time has passed since last.
		if (ship.canFireWeapon()) {
			if(ship.equals(shipBlue)){
				photons.addPhoton(ship.getPosition(),ship.getVelocity(),ship.getAngle(), true);
			}
			else{
				photons.addPhoton(ship.getPosition(),ship.getVelocity(),ship.getAngle(), false);
			}
			ship.reloadWeapon();
			return true;
		}
		return false;
	}
}