/*
 * WorldController.java
 *
 * This is the most important new class in this lab.  This class serves as a combination 
 * of the CollisionController and GameplayController from the previous lab.  There is not 
 * much to do for collisions; Box2d takes care of all of that for us.  This controller 
 * invokes Box2d and then performs any after the fact modifications to the data 
 * (e.g. gameplay).
 *
 * If you study this class, and the contents of the edu.cornell.cs3152.physics.obstacles
 * package, you should be able to understand how the Physics engine works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.BatEnemy;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.BeeEnemy;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.BeeHive;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.Enemy;
import edu.cornell.gdiac.rabbeat.obstacles.platforms.MovingPlatform;
import edu.cornell.gdiac.rabbeat.obstacles.platforms.WeightedPlatform;
import edu.cornell.gdiac.rabbeat.sync.BeatTest;
import edu.cornell.gdiac.rabbeat.sync.Bullet;
import edu.cornell.gdiac.rabbeat.sync.ISynced;
import edu.cornell.gdiac.rabbeat.sync.SyncController;
import java.util.Iterator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.rabbeat.obstacles.*;

/**
 * Base class for a world-specific controller.
 *
 *
 * A world has its own objects, assets, and input controller. Thus this is
 * really a mini-GameEngine in its own right. The only thing that it does
 * not do is create a GameCanvas; that is shared with the main application.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop,
 * which
 * is much more scalable. However, we still want the assets themselves to be
 * static.
 * This is the purpose of our AssetState variable; it ensures that multiple
 * instances
 * place nicely with the static assets.
 */
public class GameController implements Screen, ContactListener {

	/** The genre state of the game */
	public Genre genre = Genre.SYNTH;
	/** The Sync object that will sync the world to the beat */
	public SyncController syncController;

	/** The SoundController object to handle audio */
	public SoundController soundController;
	public ObjectController objectController;

	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	/** How many frames after winning/losing do we continue? */
	public static final int EXIT_COUNT = 2;

	/** The amount of time for a physics engine step. */
	public static final float WORLD_STEP = 1 / 60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;
	/** Width of the screen in Box2d units */
	protected static final float DEFAULT_WIDTH = 38.4f;
	/** Height of the screen in Box2d units */
	protected static final float DEFAULT_HEIGHT = 21.64f;
	/** The default value of gravity (going down) */
	protected static final float DEFAULT_GRAVITY = -4.9f;


	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The Box2D world */
	protected World world;
	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;
	/** Width of the game world in Box2d units */
	protected float worldWidth;
	/** Height of the game world in Box2d units */
	protected float worldHeight;

	/** Whether or not this is an active controller */
	private boolean active;
	/** Whether we have completed this level */
	private boolean complete;
	/** Whether we have failed at this world (and need a reset) */
	private boolean failed;
	/** Whether or not debug mode is active */
	private boolean debug;
	/** Countdown active for winning or losing */
	private int countdown;

	// TODO: Add sounds and sound id fields here
	/** synth soundtrack of game */
	private Music synthSoundtrack;
	/** jazz soundtrack of game */
	private Music jazzSoundtrack;

	// Physics objects for the game

	/** last platform collided with*/
	private WeightedPlatform lastCollideWith;

	private MovingPlatform lastMCollideWith;
	/** the spawnpoint location of the player */

	private Vector2 respawnPoint;

	/** Mark set to handle more sophisticated collision callbacks */
	protected ObjectSet<Fixture> sensorFixtures;

	private static GameController theController = null;

	public static GameController getInstance() {
		if (theController == null) {
			theController = new GameController();
		}
		return theController;
	}

	/**
	 * Returns true if debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @return true if debug mode is active.
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Sets whether debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @param value whether debug mode is active.
	 */
	public void setDebug(boolean value) {
		debug = value;
	}

	/**
	 * Returns true if the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @return true if the level is completed.
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets whether the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @param value whether the level is completed.
	 */
	public void setComplete(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		complete = value;
	}

	/**
	 * Returns true if the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @return true if the level is failed.
	 */
	public boolean isFailure() {
		return failed;
	}

	/**
	 * Sets whether the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @param value whether the level is failed.
	 */
	public void setFailure(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		failed = value;
	}

	/**
	 * Returns true if this is the active screen
	 *
	 * @return true if this is the active screen
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Returns the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers
	 *
	 * @return the canvas associated with this controller
	 */
	public GameCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Sets the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers. Setting this value will compute
	 * the drawing scale from the canvas size.
	 *
	 * @param canvas the canvas associated with this controller
	 */
	public void setCanvas(GameCanvas canvas) {
		this.canvas = canvas;
		this.scale.x = canvas.getWidth() / bounds.getWidth() * 2;
		this.scale.y = canvas.getHeight() / bounds.getHeight() * 2;
	}

	/**
	 * Creates a new game world with the default values.
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates. The bounds are in terms of the Box2d
	 * world, not the screen.
	 */
	protected GameController() {
		this(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT),
				new Vector2(0, DEFAULT_GRAVITY));
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
		syncController = new SyncController();
		soundController = new SoundController();
		objectController = new ObjectController();
		theController = this;
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates. The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param bounds  The game bounds in Box2d coordinates
	 * @param gravity The gravitational force on this Box2d world
	 */
	protected GameController(Rectangle bounds, Vector2 gravity) {
		world = new World(gravity, false);
		this.bounds = new Rectangle(bounds);
		this.scale = new Vector2(1, 1);
		complete = false;
		failed = false;
		debug = false;
		active = false;
		countdown = -1;
	}

	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		for (GameObject obj : objectController.objects) {
			obj.deactivatePhysics(world);
		}
		objectController.objects.clear();
		objectController.addQueue.clear();
		world.dispose();
		objectController.objects = null;
		objectController.addQueue = null;
		objectController.genreObjects = null;
		objectController = null;
		bounds = null;
		scale = null;
		world = null;
		canvas = null;
		syncController = null;

	}

	// TODO: Adjust to the correct assets after assets have been added
	/**
	 * Gather the assets for this controller.
	 *
	 * This method extracts the asset variables from the given asset directory. It
	 * should only be called after the asset directory is completed.
	 *
	 * @param directory Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {
		objectController.gatherAssets(directory);
		// set the soundtrack
		setSoundtrack(directory);
	}

	/**
	 * Sets the synth and jazz soundtrack to the correct tracks. This function will
	 * be significant
	 * if there are multiple different soundtracks for different levels
	 *
	 * @param directory Reference to global asset manager.
	 */
	public void setSoundtrack(AssetDirectory directory) {
		synthSoundtrack = directory.getEntry("music:synth1", Music.class);
		jazzSoundtrack = directory.getEntry("music:jazz1", Music.class);
		soundController.setSynthTrack(synthSoundtrack);
		soundController.setJazzTrack(jazzSoundtrack);
	}

	public Vector2 getScale() {
		return scale;
	}

	public Genre getGenre() {
		return genre;
	}

	/**
	 *
	 * Adds a physics object in to the insertion queue.
	 *
	 * Objects on the queue are added just before collision processing. We do this
	 * to
	 * control object creation.
	 *
	 * param obj The object to add
	 */
	public void instantiateQueue(GameObject obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objectController.addQueue.add(obj);
	}

	/**
	 * If the object is implements {@link ISynced}, add
	 * to the sync. If it is a {@link IGenreObject}, add to genreObstacles.
	 * 
	 * @param object: The object you are instantiating
	 *
	 */
	protected void instantiate(GameObject object) {
		assert inBounds(object) : "Object is not in bounds";
		objectController.objects.add(object);
		if (object instanceof ISynced) {
			syncController.addSync((ISynced) object);
		}
		if (object instanceof IGenreObject) {
			objectController.genreObjects.add((IGenreObject) object);
		}
		object.activatePhysics(world);
	}

	/**
	 * Returns true if the object is in bounds.
	 *
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
	 *
	 * @return true if the object is in bounds.
	 */
	public boolean inBounds(GameObject obj) {
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x + bounds.width);
		boolean vert = (bounds.y <= obj.getY() && obj.getY() <= bounds.y + bounds.height);
		return horiz && vert;
	}

	/**
	 * Initialize the game for the first time
	 */
	public void initialize() {
		genre = Genre.SYNTH;
		Vector2 gravity = new Vector2(world.getGravity());

		world = new World(gravity, false);
		worldWidth = DEFAULT_WIDTH * objectController.backgroundTexture.getRegionWidth() / getCanvas().getWidth();
		worldHeight = DEFAULT_HEIGHT * objectController.backgroundTexture.getRegionHeight() / getCanvas().getHeight();
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
		objectController.setFirstCheckpointAsSpawn(scale);
		objectController.player.setPosition(respawnPoint);
		soundController.playMusic(Genre.SYNTH);
	}

	// TODO: Reset to SYNTH defaults
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		// Default genre is synth
		genre = Genre.SYNTH;
		Vector2 gravity = new Vector2(world.getGravity());

		for (GameObject obj : objectController.objects) {
			obj.deactivatePhysics(world);
		}
		objectController.objects.clear();
		objectController.addQueue.clear();
		world.dispose();

		world = new World(gravity, false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		syncController = new SyncController();
		populateLevel();
		objectController.player.setPosition(respawnPoint);
		syncController.setSync(synthSoundtrack, jazzSoundtrack);
		soundController.resetMusic();
	}

	// TODO: Will use level data json to populate
	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {

		// world starts with Synth gravity
		world.setGravity(new Vector2(0, objectController.defaultConstants.get("genre_gravity").getFloat("synth", 0)));
		// TODO This volume constant is never used
		float volume = objectController.defaultConstants.getFloat("volume", 1.0f);

		syncController.addSync(new BeatTest());
		syncController.setSync(synthSoundtrack, jazzSoundtrack);
		objectController.populateObjects(scale);
		;

	}

	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode. If not, the update proceeds
	 * normally.
	 *
	 * @param dt Number of seconds since last animation frame
	 * 
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		InputController input = InputController.getInstance();
		input.readInput(bounds, scale);
		if (listener != null) {
			// Toggle debug
			if (input.didDebug()) {
				debug = !debug;
			}

			// Handle resets
			if (input.didReset()) {
				reset();
			}

			// Now it is time to maybe switch screens.
			if (input.didExit()) {
				pause();
				listener.exitScreen(this, EXIT_QUIT);
				return false;
			} else if (countdown > 0) {
				countdown--;
			} else if (countdown == 0) {
				if (failed) {
					reset();
				} else if (complete) {
					pause();
					// TODO: Make Win Condition
					System.out.println("You win the game");
					return false;
				}
			}
		}
		if (!isFailure() && objectController.player.getY() < -1) {
			setFailure(true);
			return false;
		}
		return true;
	}

	// TODO: Update physics based on genre
	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class
	 * WorldController.
	 * This method is called after input is read, but before collisions are
	 * resolved.
	 * The very last thing that it should do is apply forces to the appropriate
	 * objects.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void update(float dt) {
		// TODO: bullet stuff needs to go and make the update in the object itself, not
		// here

		if (InputController.getInstance().getSwitchGenre()) {
			switchGenre();
			InputController.getInstance().setSwitchGenre(false);
			updateGenreSwitch();
		}
		if(InputController.getInstance().getDelay() != 0f){
			syncController.addDelay(InputController.getInstance().getDelay());
		}
		syncController.updateBeat();
		soundController.update();

		if (lastCollideWith != null){
			Vector2 displace = lastCollideWith.currentVelocity();
			objectController.player.setDisplace(displace);
		}
		if (lastMCollideWith != null){
			Vector2 displace = lastMCollideWith.currentVelocity();
			objectController.player.setDisplace(displace);
		}
	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects. We
	 * use
	 * this method to test if it is the "right" kind of collision. In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	@Override
	public void beginContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		try {
			GameObject bd1 = (GameObject) body1.getUserData();
			GameObject bd2 = (GameObject) body2.getUserData();

			// Checks whether player is grounded (prevents double jumping)
			if ((objectController.player.getSensorName().equals(fd2) && objectController.player != bd1) ||
					(objectController.player.getSensorName().equals(fd1) && objectController.player != bd2)) {
				// Prevents checkpoints from being detected as ground
				if (objectController.player == bd1 ? !bd2.isSensor() : !bd1.isSensor()) {
					objectController.player.setGrounded(true);
					sensorFixtures.add(objectController.player == bd1 ? fix2 : fix1); // Could have more than one ground
				}
			}

			// Check for win condition
			if ((bd1 == objectController.player && bd2 == objectController.goalDoor) ||
					(bd1 == objectController.goalDoor && bd2 == objectController.player)) {
				setComplete(true);
			}

			if ((bd1.equals(objectController.player) && bd2 instanceof Enemy)) {
				setFailure(true);
			}

			if (bd1 instanceof Bullet && !(bd2 instanceof Enemy) ) {
				bd1.markRemoved(true);
			}

			if (bd2 instanceof Bullet && !(bd1 instanceof Enemy) ) {
				bd2.markRemoved(true);
			}

			if ((bd1.equals(objectController.player) && bd2 instanceof Bullet)) {
				setFailure(true);
			}

			if (bd1 instanceof BeeEnemy && !(bd2 instanceof BeeHive) ) {
				bd1.markRemoved(true);
			}

			if (bd2 instanceof BeeEnemy && !(bd1 instanceof BeeHive) ) {
				bd2.markRemoved(true);
			}

			if ((bd1.equals(objectController.player) && bd2 instanceof Enemy)) {
				setFailure(true);
			}

			if ((bd1.equals(objectController.player) && bd2 instanceof BatEnemy)) {
				setFailure(true);
			}

			//TODO: implement lethal obstacle code which checks for the first obstacle being the player, then checking if the
			if ((bd2 instanceof Player && bd1 instanceof SimpleGameObject)){
				if (((SimpleGameObject) bd1).getType() == SimpleGameObject.ObjectType.LETHAL){
					setFailure(true);
				}
			}
			if ((bd1 instanceof WeightedPlatform) && (bd2 instanceof Player)){
				lastCollideWith = (WeightedPlatform) bd1;
			}
			if ((bd1 instanceof MovingPlatform) && (bd2 instanceof Player)){
				lastMCollideWith = (MovingPlatform) bd1;
			}
			// Check for collision with checkpoints and set new current checkpoint
			for (Checkpoint checkpoint : objectController.checkpoints) {
				if (!checkpoint.isActive && ((bd1 == objectController.player && bd2 == checkpoint) ||
						(bd1 == checkpoint && bd2 == objectController.player))) {
					checkpoint.setActive();
					respawnPoint = checkpoint.getPosition();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch. The main use of this
	 * method
	 * is to determine when the character is NOT on the ground. This is how we
	 * prevent
	 * double jumping.
	 */
	public void endContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();

		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if ((objectController.player.getSensorName().equals(fd2) && objectController.player != bd1) ||
				(objectController.player.getSensorName().equals(fd1) && objectController.player != bd2)) {
			sensorFixtures.remove(objectController.player == bd1 ? fix2 : fix1);
			if (sensorFixtures.size == 0) {
				objectController.player.setGrounded(false);
			}
		}
		if ((bd1 instanceof WeightedPlatform) && (bd2 instanceof Player)){
			if (bd1 == lastCollideWith){
				lastCollideWith = null;
			}
			objectController.player.setDisplace(new Vector2(0,0));
		}
		if ((bd1 instanceof MovingPlatform) && (bd2 instanceof Player)){
			if (bd1 == lastMCollideWith){
				lastMCollideWith = null;
			}
			objectController.player.setDisplace(new Vector2(0,0));
		}

	}

	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	/** Unused ContactListener method */
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	/**
	 * Loop update when the genre switch occurs. Only objects affected by genre
	 * switching should
	 * be updated.
	 *
	 */
	public void updateGenreSwitch() {
		soundController.setGenre(genre);
		// update to Synth
		if (genre == Genre.SYNTH) {
			world.setGravity(new Vector2(0, objectController.defaultConstants.get("genre_gravity").getFloat("synth", 0)));
		}
		// update to Jazz
		else {
			world.setGravity(new Vector2(0, objectController.defaultConstants.get("genre_gravity").getFloat("jazz", 0)));
		}

		for (IGenreObject g : objectController.genreObjects) {
			g.genreUpdate(genre);
		}
	}

	/**
	 * Processes physics
	 *
	 * Once the update phase is over, but before we draw, we are ready to handle
	 * physics. The primary method is the step() method in world. This
	 * implementation
	 * works for all applications and should not need to be overwritten.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void postUpdate(float dt) {
		// Add any objects created by actions
		while (!objectController.addQueue.isEmpty()) {
			instantiate(objectController.addQueue.poll());
		}

		// Turn the physics engine crank.
		world.step(WORLD_STEP, WORLD_VELOC, WORLD_POSIT);
		//set position, then call a world step of zeroz
		// Garbage collect the deleted objects.
		// Note how we use the linked list nodes to delete O(1) in place.
		// This is O(n) without copying.
		Iterator<PooledList<GameObject>.Entry> iterator = objectController.objects.entryIterator();
		while (iterator.hasNext()) {
			PooledList<GameObject>.Entry entry = iterator.next();
			GameObject obj = entry.getValue();
			if (obj.isRemoved()) {
				obj.deactivatePhysics(world);
				entry.remove();
			} else {
				// Note that update is called last!
				obj.update(dt);
			}
		}
	}

	/**
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself. It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void draw(float dt) {
		canvas.clear();

		// Draw background unscaled.
		canvas.begin();
		canvas.draw(objectController.backgroundTexture, 0, 0);
		canvas.end();

		canvas.begin();
		for (GameObject obj : objectController.objects) {
			if (!objectController.foreground.contains(obj)){
				obj.draw(canvas);
			}
		}
		canvas.end();

		// Draw the player on top
		canvas.begin();
		objectController.player.draw(canvas);
		canvas.end();

		// Draw the foreground on top of everything
		canvas.begin();
		for (GameObject obj : objectController.foreground) {
			obj.draw(canvas);
		}
		canvas.end();

		if (debug) {
			canvas.beginDebug();
			for (GameObject obj : objectController.objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}

		// Final message
		if (complete && !failed) {
			objectController.displayFont.setColor(Color.YELLOW);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("VICTORY!", objectController.displayFont, 0.0f);
			canvas.end();
		} else if (failed) {
			objectController.displayFont.setColor(Color.RED);
			canvas.begin(); // DO NOT SCALE
			// TODO: Remove this failure text with something more appropriate for our game
			// canvas.drawTextCentered("FAILURE!", objectController.displayFont, 0.0f);
			canvas.end();
		}
	}

	/**
	 * Called when the Screen is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// IGNORE FOR NOW
	}

	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw(). However, it is VERY
	 * important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			if (preUpdate(delta)) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);
			}
			canvas.updateCamera(objectController.player, worldWidth, worldHeight);
			draw(delta);
		}
	}

	/**
	 * Called when the Screen is paused.
	 *
	 * We need this method to stop all sounds when we pause.
	 * Pausing happens when we switch game modes.
	 */
	public void pause() {
		// TODO: Stop all sounds here
	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub
	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

	/**
	 * Switches the genre depending on what the current genre is.
	 */
	public void switchGenre() {
		switch (genre) {
			case SYNTH:
				genre = Genre.JAZZ;
				break;
			case JAZZ:
				genre = Genre.SYNTH;
				break;
		}
	}

	public void setSpawn(Vector2 spawn) {
		respawnPoint = spawn;
	}

	public Player getPlayer() {
		return objectController.player;
	}

	public void createJoint(GameObject bd1, GameObject bd2){
		Vector2 anchor1 = new Vector2();
		Vector2 anchor2 = new Vector2();

//		Vector2 anchor1 = bd1.getPosition();
//		Vector2 anchor2 = bd2.getPosition();

		// Definition for a revolute joint
		JointDef jointDef = new PrismaticJointDef();

		// Initial joint
		jointDef.bodyA = bd1.getBody();
		jointDef.bodyB = bd2.getBody();

		jointDef.collideConnected = true;
		Joint joint = world.createJoint(jointDef);
	}

	//TODO: destroy joint!
//	public void breakJoint(Joint joint){
//		world.destroyJoint(joint);
//	}

}
