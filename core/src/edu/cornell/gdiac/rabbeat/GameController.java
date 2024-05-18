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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation.SwingOut;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer.Task;
import edu.cornell.gdiac.rabbeat.objects.enemies.Enemy;
import edu.cornell.gdiac.rabbeat.objects.platforms.MovingPlatform;
import edu.cornell.gdiac.rabbeat.objects.platforms.WeightedPlatform;
import edu.cornell.gdiac.rabbeat.objects.projectiles.Bee;
import edu.cornell.gdiac.rabbeat.objects.projectiles.Bullet;
import edu.cornell.gdiac.rabbeat.objects.projectiles.Echo;
import edu.cornell.gdiac.rabbeat.sync.ISynced;
import edu.cornell.gdiac.rabbeat.sync.SyncController;
import edu.cornell.gdiac.rabbeat.ui.GenreUI;

import java.util.Iterator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.rabbeat.objects.*;

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

	/**
	 * The genre state of the game
	 */
	private Genre genre = Genre.SYNTH;
	/**
	 * The Sync object that will sync the world to the beat
	 */
	public SyncController syncController;

	/**
	 * The SoundController object to handle audio
	 */
	public SoundController soundController;
	public ObjectController objectController;

	/**
	 * Exit code for quitting the game
	 */
	public static final int EXIT_QUIT = 0;


	/**
	 * Exit code for going back to the level select menu
	 */
	public static final int BACK_TO_LEVEL_SELECT = 1;


	public static final int GO_TO_LEVEL_SELECT = 1;

	/**
	 * Exit code for going to the next level
	 */
	public static final int NEXT_LEVEL = 2;


	/** Exit code for going to the main menu */
	public static final int MAIN_MENU = 3;

	/**
	 * The integer that represents the number of levels that the player has unlocked
	 */

	private static int levelsUnlocked;

	/**
	 * The integer that represents the current level number the player selected from the
	 * LevelSelectorScreen
	 */
	private static int currentLevelInt = 1;

	/**
	 * The String that represents the JSON file for the current level the player selected from the
	 * LevelSelectorScreen
	 */
	private static String currentLevel = "level" + currentLevelInt;

	/**
	 * How many frames after winning/losing do we continue?
	 */
	public static final int EXIT_COUNT = 35;

	/**
	 * The number of levels in the game
	 */
	private final int numberOfLevels = 12;

	/**
	 * The amount of time for a physics engine step.
	 */
	public static final float WORLD_STEP = 1 / 60.0f;
	/**
	 * Number of velocity iterations for the constrain solvers
	 */
	public static final int WORLD_VELOC = 6;
	/**
	 * Number of position iterations for the constrain solvers
	 */
	public static final int WORLD_POSIT = 2;
	/**
	 * Width of the screen in Box2d units
	 */
	protected static final float DEFAULT_WIDTH = 38.4f;
	/**
	 * Height of the screen in Box2d units
	 */
	protected static final float DEFAULT_HEIGHT = 21.64f;
	/**
	 * The default value of gravity (going down)
	 */
	protected static final float DEFAULT_GRAVITY = -18f;

	/**
	 * The boolean representing whether the player has completed the level
	 */
	private boolean playerCompletedLevel = false;

	private boolean collidedWithCrusher = false;

	/**
	 * Reference to the game canvas
	 */
	protected GameCanvas canvas;
	/**
	 * Listener that will update the player mode when we are done
	 */
	private ScreenListener listener;

	/**
	 * The Box2D world
	 */
	protected World world;
	/**
	 * The boundary of the world
	 */
	protected Rectangle bounds;
	/**
	 * The world scale
	 */
	protected Vector2 scale;
	/**
	 * Width of the game world in Box2d units
	 */
	protected float worldWidth;
	/**
	 * Height of the game world in Box2d units
	 */
	protected float worldHeight;

	/**
	 * Whether or not this is an active controller
	 */
	private boolean active;
	/**
	 * Whether we have completed this level
	 */
	private boolean complete = false;

	/**
	 * Whether we have failed at this world (and need a reset)
	 */
	private boolean failed = false;
	/**
	 * Whether or not the game is paused
	 */
	private boolean paused = false;

	/** Whether or not the player is currently colliding with a wall
	/**
	 * Whether or not the game is in calibration screen
	 */
	public boolean calibrateScreen = false;
	/**
	 * Whether calibration is happening
	 */
	public boolean inCalibration = false;
	/**
	 * Whether the genre switch mechanic is locked
	 */
	private boolean isGenreSwitchLocked = true;

	/**
	 * Whether or not the cutscene sound effect has been played
	 */
	private boolean cutscenePlayed = false;

	/**
	 * Whether or not debug mode is active
	 */
	private boolean debug;
	/**
	 * Stores the bpm after it's loaded in. Don't use this for anything, use getBPM() instead.
	 */
	private int levelBPM;
	/**
	 * synth soundtrack of game
	 */
	private Music synthSoundtrack;
	/**
	 * jazz soundtrack of game
	 */
	private Music jazzSoundtrack;

	/**
	 * Pause tint synth color
	 */
	private Color pauseTintSynthColor;

	/**
	 * Pause tint jazz color
	 */

	private Color pauseTintJazzColor;

	/**
	 * Current item selected in the pause menu
	 */
	private int pauseItemSelected = 0;

	/**
	 * Current item selected in the victory screen menu
	 */
	private int victoryScreenItemSelected = 0;

	/**
	 * Global music volume, which can be changed in pause menu
	 */

	private int musicVolume = 10;

	/**
	 * Global SFX volume, which can be changed in pause menu
	 */

	private int SFXVolume = 10;

	/**
	 * lIST of enemies that are 'bounded' to a moving or weighted platform
	 */
	private Enemy[] boundedEnemies;
	/**
	 * lIST of platforms that are 'bounded' to an enemy
	 */
	private BoxGameObject[] boundedPlatforms;

	/**
	 * last platform collided with
	 */
	private WeightedPlatform lastCollideWith;

	private MovingPlatform lastMCollideWith;
	/**
	 * the spawnpoint location of the player
	 */

	private Vector2 respawnPoint = null;

	/**
	 * Mark set to handle more sophisticated collision callbacks
	 */
	protected ObjectSet<Fixture> sensorFixtures;

	/**
	 * Jump buffer time
	 */
	private final float jumpBuffer = 0.02f;

	private static GameController theController = null;

	public static synchronized GameController getInstance() {
		if (theController == null) {
			theController = new GameController();
		}
		return theController;
	}

	/**
	 * Returns true if debug mode is active.
	 * <p>
	 * If true, all objects will display their physics bodies.
	 *
	 * @return true if debug mode is active.
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Sets whether debug mode is active.
	 * <p>
	 * If true, all objects will display their physics bodies.
	 *
	 * @param value whether debug mode is active.
	 */
	public void setDebug(boolean value) {
		debug = value;
	}

	/**
	 * Returns true if the level is completed.
	 * <p>
	 * If true, the level will advance after a countdown
	 *
	 * @return true if the level is completed.
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets whether the level is completed.
	 * <p>
	 * If true, the level will advance after a countdown
	 *
	 * @param value whether the level is completed.
	 */
	public void setComplete(boolean value) {
		complete = value;
	}

	/**
	 * Returns true if the level is failed.
	 * <p>
	 * If true, the level will reset after a countdown
	 *
	 * @return true if the level is failed.
	 */
	public boolean isFailure() {
		return failed;
	}

	/**
	 * Sets whether the level is failed.
	 * <p>
	 * If true, the level will reset after a countdown
	 *
	 * @param value whether the level is failed.
	 */
	public void setFailure(boolean value) {
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
	 * Returns true if the genre switch is locked, disabling the SHIFT key
	 *
	 * @return true if the genre switch is locked
	 */
	public boolean isGenreSwitchLocked() {
		return isGenreSwitchLocked;
	}

	/**
	 * Returns true if the game is paused
	 *
	 * @return true if the game is paused
	 */
	public boolean getPaused() {
		return paused;
	}

	/**
	 * Sets whether the game is paused.
	 *
	 * @param value whether the game is paused.
	 */
	public void setPaused(boolean value) {
		paused = value;
	}

	/**
	 * Returns the canvas associated with this controller
	 * <p>
	 * The canvas is shared across all controllers
	 *
	 * @return the canvas associated with this controller
	 */
	public GameCanvas getCanvas() {
		return canvas;
	}

	/**
	 * Sets the canvas associated with this controller
	 * <p>
	 * The canvas is shared across all controllers. Setting this value will compute the drawing
	 * scale from the canvas size.
	 *
	 * @param canvas the canvas associated with this controller
	 */
	public void setCanvas(GameCanvas canvas) {
		this.canvas = canvas;
		this.scale.x = objectController.tileSize;
		this.scale.y = objectController.tileSize;
	}

	/**
	 * Creates a new game world with the default values.
	 * <p>
	 * The game world is scaled so that the screen coordinates do not agree with the Box2d
	 * coordinates. The bounds are in terms of the Box2d world, not the screen.
	 */
	protected GameController() {
		this(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT),
				new Vector2(0, DEFAULT_GRAVITY));
		setDebug(false);
		setComplete(false);
		setFailure(false);
		setPaused(false);
		pauseTintSynthColor = new Color(1, 0, 1, 0.55f);
		pauseTintJazzColor = new Color(1, 0, 1, 0.55f);
		world.setContactListener(this);
		sensorFixtures = new ObjectSet<Fixture>();
		objectController = new ObjectController();
		theController = this;
	}

	/**
	 * Creates a new game world
	 * <p>
	 * The game world is scaled so that the screen coordinates do not agree with the Box2d
	 * coordinates. The bounds are in terms of the Box2d world, not the screen.
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
		paused = false;
		cutscenePlayed = false;
	}

	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		for (GameObject obj : objectController.objects) {
			obj.deactivatePhysics(world);
		}
		objectController = null;
		world.dispose();
		objectController = null;
		bounds = null;
		scale = null;
		world = null;
		canvas = null;
		syncController = null;

	}

	/**
	 * Gather the assets for this controller.
	 * <p>
	 * This method extracts the asset variables from the given asset directory. It should only be
	 * called after the asset directory is completed.
	 *
	 * @param directory Reference to global asset manager.
	 */
	public void gatherAssets(AssetDirectory directory) {
		soundController = new SoundController();
		objectController.gatherAssets(directory);
		levelBPM = objectController.defaultConstants.get("music").get(getCurrentLevel())
				.getInt("bpm");
		syncController = new SyncController(levelBPM);

		Preferences prefs = Gdx.app.getPreferences("MusicVolume");
		musicVolume = prefs.getInteger("musicVolume", 10);

		prefs = Gdx.app.getPreferences("SFXVolume");
		SFXVolume = prefs.getInteger("sfxVolume", 10);

		// set the soundtrack
		setSoundtrack(directory);
		// set the sound effects
		initializeSFX(directory);
		syncController.setSync(synthSoundtrack, jazzSoundtrack);
		System.out.println(synthSoundtrack.getPosition());
	}

	/**
	 * Sets the synth and jazz soundtrack to the correct tracks. This function will be significant
	 * if there are multiple different soundtracks for different levels
	 *
	 * @param directory Reference to global asset manager.
	 */
	public void setSoundtrack(AssetDirectory directory) {
		synthSoundtrack = directory.getEntry(
				objectController.defaultConstants.get("music").get(getCurrentLevel())
						.getString("synth"), Music.class);
		jazzSoundtrack = directory
				.getEntry(objectController.defaultConstants.get("music").get(getCurrentLevel())
						.getString("jazz"), Music.class);
		soundController.setSynthTrack(synthSoundtrack);
		soundController.setJazzTrack(jazzSoundtrack);
		soundController.setGlobalMusicVolume(musicVolume / 10f);
		soundController.setGlobalSFXVolume(SFXVolume / 10f);
		soundController.resetMusic();
	}

	/**
	 * Initializes the sound effects, which are stored in the sound controller.
	 *
	 * @param directory Reference to global asset manager.
	 */
	public void initializeSFX(AssetDirectory directory) {
		soundController.addSound("genreSwitch", directory.getEntry("sfx:genreSwitch", Sound.class));
		String checkpointNum; // change this once tracks are finalized to match their key signatures. 1 = lab,
		// 2 = disco, 3 = penthouse
		switch (currentLevelInt) {
			case 1: case 2: case 3: case 4:
				checkpointNum = "1";
				break;
			case 5: case 6: case 7: case 8:
				checkpointNum = "2";
				break;
			case 9: case 10: case 11: case 12:
				checkpointNum = "3";
				break;
			default:
				checkpointNum = "3";
				break;
		}
		soundController.addSound("checkpoint",
				directory.getEntry("sfx:checkpoint" + checkpointNum, Sound.class));
		soundController.addSound("jump",
				directory.getEntry("sfx:jump", Sound.class));
		soundController.addSound("death", directory.getEntry("sfx:death", Sound.class));

		switch (currentLevelInt) {
			case 1: // JAZZ
				soundController.addSound("cutscene",
						directory.getEntry("sfx:jazzCutscene", Sound.class));
				break;
			case 4: // ROCK
				soundController.addSound("cutscene",
						directory.getEntry("sfx:rockCutscene", Sound.class));
				break;
			case 6: // POP
				soundController.addSound("cutscene",
						directory.getEntry("sfx:popCutscene", Sound.class));
				break;
			case 8: // CLASSICAL
				soundController.addSound("cutscene",
						directory.getEntry("sfx:classicalCutscene", Sound.class));
				break;
			case 10: // COUNTRY
				soundController.addSound("cutscene",
						directory.getEntry("sfx:countryCutscene", Sound.class));
				break;
			case 11: // HIP HOP
				soundController.addSound("cutscene",
						directory.getEntry("sfx:hiphopCutscene", Sound.class));
				break;
			default:
				break;
		}
		soundController.addSound("uiTransition", directory.getEntry("sfx:menutransition", Sound.class));
		soundController.addSound("glassShatter", directory.getEntry("sfx:glass", Sound.class));
	}

	public Vector2 getScale() {
		return scale;
	}

	public int getBPM() {
		return syncController.BPM;
	}

	/**
	 * Adds a physics object in to the insertion queue.
	 * <p>
	 * Objects on the queue are added just before collision processing. We do this to control object
	 * creation.
	 * <p>
	 * param obj The object to add
	 */
	public void instantiateQueue(GameObject obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objectController.addQueue.add(obj);
	}

	/**
	 * If the object is implements {@link ISynced}, add to the sync. If it is a
	 * {@link IGenreObject}, add to genreObstacles.
	 *
	 * @param object:      The object you are instantiating
	 * @param insertIndex: Where the object should be placed in the PooledList
	 */
	protected void instantiate(GameObject object, int insertIndex) {
		assert inBounds(object) : "Object is not in bounds";
		int index = objectController.insertIndexes[insertIndex];
		objectController.objects.add(index, object);
		for (int i = insertIndex; i < objectController.insertIndexes.length; i++) {
			objectController.insertIndexes[i]++;
		}
		if (object instanceof ISynced) {
			syncController.addSync((ISynced) object);
		}
		if (object instanceof IGenreObject) {
			objectController.genreObjects.add((IGenreObject) object);
		}
		object.activatePhysics(world);
	}

	/**
	 * If the object is implements {@link ISynced}, add to the sync. If it is a
	 * {@link IGenreObject}, add to genreObstacles.
	 *
	 * @param gui: The GUI element you are instantiating
	 */
	protected void instantiate(GenreUI gui) {
		syncController.addSync(gui);
		objectController.genreObjects.add(gui);
	}

	/**
	 * Returns true if the object is in bounds.
	 * <p>
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
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
		isGenreSwitchLocked = getCurrentLevelInt() <= 2;
		genre = getCurrentLevelInt() == 2 ? Genre.JAZZ : Genre.SYNTH;
		if (getCurrentLevelInt() == 2) {
			soundController.setGenre(Genre.JAZZ);
		}
		Vector2 gravity = new Vector2(world.getGravity());

		world = new World(gravity, false);
		populateLevel();
		worldWidth = DEFAULT_WIDTH * objectController.levelBackground.getRegionWidth()
				/ getCanvas().getWidth();
		worldHeight = DEFAULT_HEIGHT * objectController.levelBackground.getRegionHeight()
				/ getCanvas().getHeight();
		world.setContactListener(this);
		soundController.resetMusic();
		soundController.playMusic(genre);
		syncController.initializeSync();
		setComplete(false);
		setFailure(false);
		setPaused(false);
		syncController.update(false);
	}

	/**
	 * Resets the status of the game so that we can play again.
	 * <p>
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity());

		for (GameObject obj : objectController.objects) {
			obj.deactivatePhysics(world);
		}
		objectController.objects.clear();
		objectController.addQueue.clear();
		for (int i = 0; i < objectController.insertIndexes.length; i++) {
			objectController.insertIndexes[i] = 0;
		}
		world.dispose();

		world = new World(gravity, false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// world starts with Synth gravity
		world.setGravity(new Vector2(0,
				objectController.defaultConstants.get("defaults").getFloat("gravity", 0)));
		objectController.populateObjects(genre, scale, respawnPoint);
	}

	/**
	 * Returns whether to process the update loop
	 * <p>
	 * At the start of the update loop, we check if it is time to switch to a new game mode. If not,
	 * the update proceeds normally.
	 *
	 * @param dt Number of seconds since last animation frame
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		InputController input = InputController.getInstance();
		input.readInput(bounds, scale);
		soundController.update();
		syncController.update(getPaused());

		if(currentLevelInt == 1 && InputController.getInstance().didPressEnter() && !paused) {
			if(showLevel1FirstCutScene) {
				showLevel1SecondCutScene = true;
				showLevel1FirstCutScene = false;
			} else if(showLevel1SecondCutScene) {
				showLevel1ThirdCutScene = true;
				showLevel1SecondCutScene = false;
			} else if(showLevel1ThirdCutScene) {
				showLevel1FourthCutScene = true;
				showLevel1ThirdCutScene = false;
			} else if(showLevel1FourthCutScene) {
				displayStartCutScenes = false;
				if (!complete) {
					soundController.playSFX("glassShatter");
				}
			}
		} else if(currentLevelInt == 9 && InputController.getInstance().didPressEnter() && !paused) {
			if(showLevel9StartingScreen[0]) {
				showLevel9StartingScreen[1] = true;
				showLevel9StartingScreen[0] = false;
			} else if(showLevel9StartingScreen[1]) {
				displayStartCutScenes = false;
			}
		}

		//ADD: !showLevel1ThirdCutScene && !showLevel1FourthCutScene if more cutscenes
		if(currentLevelInt == 1 && !showLevel1FirstCutScene && !showLevel1SecondCutScene
				&& !showLevel1ThirdCutScene && !showLevel1FourthCutScene && displayStartCutScenes) {
			showLevel1FirstCutScene = true;
		} else if(currentLevelInt == 9 && !showLevel9StartingScreen[0] && !showLevel9StartingScreen[1]) {
			showLevel9StartingScreen[0] = true;
		}

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
			// if (input.didPressLevelSelect()) {
			// exitLevel();
			// }

			if (input.didPause()) {
				// If game is already paused, hitting pause again will unpause it.
				paused = !paused;
				if (paused) {
					pause();
				} else {
					resume();
					// Make sure that genre doesn't get switched while game is paused
				}
			} else if (paused) {
				//calibrating for audio delay
				syncController.calibrationCheck(inCalibration, dt);

				// If game is currently in the middle of the paused state, do all this. It won't
				// work the first frame of pausing but that should be fine
				if (input.didPressDownWhilePaused()) {
					pauseItemSelected = (pauseItemSelected + 1) % 6;
					soundController.playSFX("uiTransition");
				}
				if (input.didPressUpWhilePaused()) { // not using else if on purpose
					pauseItemSelected--;
					soundController.playSFX("uiTransition");
					if (pauseItemSelected == -1) {
						pauseItemSelected = 5;
					}
				}
				if (pauseItemSelected == 3) {
					if (input.didPressLeftWhilePaused()
							&& musicVolume > 0) { // change this to 1 if it causes bugs
						musicVolume--;
						soundController.setGlobalMusicVolumeImmediate(musicVolume / 10f, true);
						Preferences prefs = Gdx.app.getPreferences("MusicVolume");
						prefs.putInteger("musicVolume", musicVolume);
						prefs.flush();
					}
					if (input.didPressRightWhilePaused() && musicVolume < 10) {
						musicVolume++;
						soundController.setGlobalMusicVolumeImmediate(musicVolume / 10f, true);
						Preferences prefs = Gdx.app.getPreferences("MusicVolume");
						prefs.putInteger("musicVolume", musicVolume);
						prefs.flush();
					}
				} else if (pauseItemSelected == 4) {
					if (input.didPressLeftWhilePaused()
							&& SFXVolume > 0) { // again, change this to 1 if it causes bugs
						SFXVolume--;
						Preferences prefs = Gdx.app.getPreferences("SFXVolume");
						prefs.putInteger("sfxVolume", SFXVolume);
						prefs.flush();
					}
					if (input.didPressRightWhilePaused() && SFXVolume < 10) {
						SFXVolume++;
						Preferences prefs = Gdx.app.getPreferences("SFXVolume");
						prefs.putInteger("sfxVolume", SFXVolume);
						prefs.flush();
					}
				} else {
					if (input.didPressEnter()) {
						pauseAction(pauseItemSelected);
					}
				}
			}

			if (failed && getPlayer().playerAnimFinished()) {
				reset();
			} else if (GameController.getInstance().isComplete()) {
				pause();
				return false;
			}

		}
		if (!isFailure() && objectController.player.getY() < -1) {
			setFailure(true);
			return false;
		}
		return true;
	}

	/**
	 * The core gameplay loop of this world.
	 * <p>
	 * This method contains the specific update code for this mini-game. It does not handle
	 * collisions, as those are managed by the parent class WorldController. This method is called
	 * after input is read, but before collisions are resolved. The very last thing that it should
	 * do is apply forces to the appropriate objects.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void update(float dt) {


		if (InputController.getInstance().getSwitchGenre()) {
			if (!objectController.player.genreSwitchCooldown) {
				switchGenre();
				updateGenreSwitch();
			}
			InputController.getInstance().setSwitchGenre(false);
		}
		if (InputController.getInstance().didPrimary() && objectController.player.isGrounded) {
			soundController.playSFX("jump");
		}
		if (lastCollideWith != null) {
			Vector2 displace = lastCollideWith.currentVelocity();
			objectController.player.setDisplace(displace);
		}
		if (lastMCollideWith != null) {
			Vector2 displace = lastMCollideWith.currentVelocity();
			objectController.player.setDisplace(displace);
		}

	}

	/**
	 * Callback method for the start of a collision
	 * <p>
	 * This method is called when we first get a collision between two objects. We use this method
	 * to test if it is the "right" kind of collision. In particular, we use it to test if we made
	 * it to the win door.
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
			if ((objectController.player.getSensorName().equals(fd2)
					&& objectController.player != bd1) ||
					(objectController.player.getSensorName().equals(fd1)
							&& objectController.player != bd2)) {
				objectController.player.setGrounded(true);
				sensorFixtures.add(objectController.player == bd1 ? fix2
						: fix1); // Could have more than one ground
			}
			// Check for win condition
			if ((bd1 == objectController.player && bd2 == objectController.goalDoor) ||
					(bd1 == objectController.goalDoor && bd2 == objectController.player)) {
				setComplete(true);
			}

			// Bullet and Bee Collision checks
			if (bd2 instanceof Bullet && !(bd1 instanceof Enemy || bd1 instanceof Echo)) {
				bd2.markRemoved(true);
			}
			if (bd2 instanceof Bee) {
				assert bd1 != null;
				if (bd1.getWall()) {
					bd2.markRemoved(true);
				}
			}

			// player collision checks
			if (bd1.getType() == Type.Player || bd2.getType() == Type.Player) {
				if (bd2.getType() == Type.LETHAL || bd1.getType() == Type.LETHAL) {
					if (!getPlayer().getIsDying()) {
						getPlayer().setDying(true);
						soundController.playSFX("death");
					}
				}
				if (bd2.getType() == Type.CRUSHER || bd1.getType() == Type.CRUSHER) {
					collidedWithCrusher = true;
				}
				if (bd2 instanceof WeightedPlatform) {
					lastCollideWith = (WeightedPlatform) bd1;
				}
				if ((bd2.getWall() || bd1.getWall()) && collidedWithCrusher) {
					if (!getPlayer().getIsDying()) {
						getPlayer().setDying(true);
						soundController.playSFX("death");
					}
				}
			}
			if ((bd1 instanceof WeightedPlatform) && (bd2.getType() == Type.Player)) {
				lastCollideWith = (WeightedPlatform) bd1;
			}
			if ((bd1 instanceof MovingPlatform) && (bd2.getType() == Type.Player)) {
				lastMCollideWith = (MovingPlatform) bd1;
			}
			// Check for collision with checkpoints and set new current checkpoint
			for (Checkpoint checkpoint : objectController.checkpoints) {
				if (!checkpoint.isActive && ((bd1 == objectController.player && bd2 == checkpoint)
						||
						(bd1 == checkpoint && bd2 == objectController.player))) {
					if (!checkpoint.isActive && checkpoint.getIndex() != 0) {
						soundController.playSFX("checkpoint");
					}
					if (getCurrentLevelInt() == 2 && checkpoint.getIndex() == 1) {
						isGenreSwitchLocked = false;
					}
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
	 * <p>
	 * This method is called when two objects cease to touch. The main use of this method is to
	 * determine when the character is NOT on the ground. This is how we prevent double jumping.
	 */
	public void endContact(Contact contact) {
		Fixture fix1 = contact.getFixtureA();
		Fixture fix2 = contact.getFixtureB();

		Body body1 = fix1.getBody();
		Body body2 = fix2.getBody();

		Object fd1 = fix1.getUserData();
		Object fd2 = fix2.getUserData();
		try {
			GameObject bd1 = (GameObject) body1.getUserData();
			GameObject bd2 = (GameObject) body2.getUserData();

			if (bd1.getType() == Type.CRUSHER || bd2.getType() == Type.CRUSHER) {
				collidedWithCrusher = false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		Object bd1 = body1.getUserData();
		Object bd2 = body2.getUserData();

		if ((objectController.player.getSensorName().equals(fd2) && objectController.player != bd1)
				||
				(objectController.player.getSensorName().equals(fd1)
						&& objectController.player != bd2)) {
			sensorFixtures.remove(objectController.player == bd1 ? fix2 : fix1);
			if (sensorFixtures.size == 0) {
				objectController.player.setGrounded(false);
			}
		}
		if ((bd1 instanceof WeightedPlatform) && (bd2 instanceof Player)) {
			if (bd1 == lastCollideWith) {
				lastCollideWith = null;
			}
			objectController.player.setDisplace(new Vector2(0, 0));
		}
		if ((bd1 instanceof MovingPlatform) && (bd2 instanceof Player)) {
			if (bd1 == lastMCollideWith) {
				lastMCollideWith = null;
			}
			objectController.player.setDisplace(new Vector2(0, 0));
		}
	}

	/**
	 * Unused ContactListener method
	 */
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	/**
	 * Unused ContactListener method
	 */
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	/**
	 * Loop update when the genre switch occurs. Only objects affected by genre switching should be
	 * updated.
	 */
	public void updateGenreSwitch() {
		soundController.setGenre(genre);
		soundController.playSFX("genreSwitch");

		for (IGenreObject g : objectController.genreObjects) {
			g.genreUpdate(genre);
		}
	}

	/**
	 * Processes physics
	 * <p>
	 * Once the update phase is over, but before we draw, we are ready to handle physics. The
	 * primary method is the step() method in world. This implementation works for all applications
	 * and should not need to be overwritten.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void postUpdate(float dt) {
		// Add any objects created by actions
		while (!objectController.addQueue.isEmpty()) {
			instantiate(objectController.addQueue.poll(), 0);
		}

		// Turn the physics engine crank.
		world.step(WORLD_STEP, WORLD_VELOC, WORLD_POSIT);
		// set position, then call a world step of zeroz
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
				for (int i = 0; i < objectController.insertIndexes.length; i++) {
					objectController.insertIndexes[i]--;
				}
			} else {
				// Note that update is called last!
				obj.update(dt);
			}
		}

		// Update checkpoints
		for (Checkpoint checkpoint : objectController.checkpoints) {
			checkpoint.update(dt);
		}
	}

	/** The boolean that represents whether the starting cutscenes in level 1 should be displayed */
	public static boolean displayStartCutScenes;

	/** The boolean that represents whether the first starting cut scene in level 1 should be displayed */
	public static boolean showLevel1FirstCutScene;

	/** The boolean that represents whether the second starting cut scene in level 1 should be displayed */
	public static boolean showLevel1SecondCutScene;

	/** The boolean that represents whether the third starting cut scene in level 1 should be displayed */
	public static boolean showLevel1ThirdCutScene;

	/** The boolean that represents whether the fourth starting cut scene in level 1 should be displayed */
	public static boolean showLevel1FourthCutScene;

	public static Boolean[] showLevel9StartingScreen = {false, false};

	/**
	 * Draw the physics objects to the canvas
	 * <p>
	 * For simple worlds, this method is enough by itself. It will need to be overriden if the world
	 * needs fancy backgrounds or the like.
	 * <p>
	 * The method draws all objects in the order that they were added.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void draw(float dt) {
		canvas.clear();

		// Draw background unscaled.
		canvas.begin(false);
		canvas.draw(objectController.levelBackground, 0, 0);
		canvas.end();

		canvas.begin(false);
		for (GameObject obj : objectController.objects) {
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

		// Draw genre indicator UI
		canvas.begin(true);
		canvas.draw(objectController.blackGradient, 0, 0);
		objectController.genreIndicator.draw(canvas, 50, 50);
		canvas.end();

		// Victory Screen
		if (complete && !failed) {if((currentLevelInt == 1 && !showFirstVictoryScreen && !showSecondVictoryScreen)
					|| (currentLevelInt == 12 && !showFirstVictoryScreen && !showSecondVictoryScreen
					&& !showThirdVictoryScreen && !showFourthVictoryScreen && !showFifthVictoryScreen)) {
				showFirstVictoryScreen = true;
			}

			if (!cutscenePlayed) {
				cutscenePlayed = true;
				switch (currentLevelInt) {
					case 1: case 4: case 6: case 8: case 10: case 11:
						soundController.playSFX("cutscene");
						break;
					default:
						break;
				}
			}

			if(currentLevelInt != 1 && currentLevelInt != 12) {
				readyToGoToNextLevel = true;
			}


			playerCompletedLevel = true;
			objectController.displayFont.setColor(Color.YELLOW);
			drawVictoryScreen();
			incrementLevelsUnlocked();
		} else if (failed) {
			objectController.displayFont.setColor(Color.RED);
		}

		canvas.begin(true);

		if(currentLevelInt == 1 && displayStartCutScenes){
			if(showLevel1FourthCutScene) {
				canvasDrawVictoryScreen(objectController.startScreens.get("lvl1Start-4"));
			} else if (showLevel1ThirdCutScene){
				canvasDrawVictoryScreen(objectController.startScreens.get("lvl1Start-3"));
			} else if(showLevel1SecondCutScene) {
				canvasDrawVictoryScreen(objectController.startScreens.get("lvl1Start-2"));
			} else if (showLevel1FirstCutScene){
				canvasDrawVictoryScreen(objectController.startScreens.get("lvl1Start-1"));
			}
		} else if(currentLevelInt == 9 && displayStartCutScenes){
			if(showLevel9StartingScreen[1]) {
				canvasDrawVictoryScreen(objectController.startScreens.get("lvl9Start-2"));
			} else if (showLevel9StartingScreen[0]){
				canvasDrawVictoryScreen(objectController.startScreens.get("lvl9Start-1"));
			}
		}

		canvas.end();

		// Put pause screen UI in this if statement
		if (paused) {
			float pulse = syncController.uiSyncPulse.uiPulseScale;
			objectController.displayFont.setColor(Color.CYAN);
			canvas.begin(true);

			canvas.draw(objectController.pauseWhiteOverlayTexture.getTexture(), (genre == Genre.SYNTH ? pauseTintSynthColor : pauseTintJazzColor), 0, 0, 0, 0, 0, 1, 1);
			canvas.draw(objectController.overlayTexture.getTexture(), Color.WHITE, 0, 0, 0, -10, 0,1.05f, 1.05f);
			if(calibrateScreen){

				//calibration beats
				if(inCalibration) {
					canvas.draw(objectController.tapText.getTexture(), Color.WHITE, 0, 0, 860, 390,
							0, 1f, 1f);
					canvas.draw(objectController.pressSpace.getTexture(), Color.WHITE, 0, 0, 860,
							310, 0, 0.75f, 0.75f);
					int beatX = 875;
					int xSpace = 75;
					int beatNum = syncController.calibrationCount % 4 + 1 == 0 ? 4
							: syncController.calibrationCount % 4 + 1;
					for (int i = 1; i < 5; i++) {
						if (i == beatNum) {
							canvas.draw(objectController.onBeatTexture.getTexture(), Color.WHITE, 0,
									0, beatX, 200, 0, 1.25f, 1.25f);
						} else {
							canvas.draw(objectController.offBeatTexture.getTexture(), Color.WHITE,
									0, 0, beatX, 200, 0, 1f, 1f);
						}
						beatX += xSpace;
					}
					//Delay Display
					canvas.drawText("Calibration: " +  (int)(((float)syncController.calibrationCount / syncController.NUM_CALIBRATION_STEPS)*100)  + "%", objectController.displayFont, 750, 175);
				}
				else{
					//counting beats

					canvas.draw(objectController.tapText.getTexture(), Color.WHITE, 0, 0, 860, 390, 0, 1f, 1f);
					canvas.draw(objectController.pressSpace.getTexture(), Color.WHITE, 0, 0, 860, 310, 0, 0.75f, 0.75f);
					int beatNum = syncController.beat.getBeatFour();
					int beatX = 875;
					int xSpace = 75;
					for(int i = 1; i < 5; i++){
						if(i == beatNum){
							canvas.draw(objectController.onBeatTexture.getTexture(), Color.WHITE, 0, 0, beatX, 200, 0, 1f * pulse, 1f * pulse);
						}
						else{
							canvas.draw(objectController.offBeatTexture.getTexture(), Color.WHITE, 0, 0, beatX, 200, 0, 1f, 1f);
						}
						beatX+=xSpace;
					}
					//Delay Display
					canvas.draw(objectController.audioAdjustLeft.getTexture(), Color.WHITE, 0, 0, 1075, 40, 0,1f, 1f);
					canvas.draw(objectController.audioAdjustLeft.getTexture(), Color.WHITE, 0, 0, 1255, 40, 0, -1f, 1f);
					canvas.drawText("Delay: " +(int)(syncController.audioDelay*100) + "ms", objectController.displayFont, 720, 80);
				}

			}
			else{
				canvas.draw(objectController.resumeTexture.getTexture(), Color.WHITE, 0, 0, 860, 400, 0, 0.5f, 0.5f);
				canvas.draw(objectController.restartLevelTexture.getTexture(), Color.WHITE, 0, 0, 860, 340, 0, 0.5f, 0.5f);
				canvas.draw(objectController.exitLevelTexture.getTexture(), Color.WHITE, 0, 0, 860, 280, 0, 0.5f, 0.5f);
				canvas.draw(objectController.musicTexture.getTexture(), Color.WHITE, 0, 0, 800, 200, 0, 0.5f, 0.5f);
				canvas.draw(objectController.SFXTexture.getTexture(), Color.WHITE, 0, 0, 850, 140, 0, 0.5f, 0.5f);
				canvas.draw(objectController.calibrateTextTexture.getTexture(), Color.WHITE, 0, 0, 850, 60, 0, 0.5f, 0.5f);
				for (int i = 0; i < musicVolume; i++) {
					canvas.draw(objectController.volumeBoxTexture.getTexture(), Color.WHITE, 0, 0, 970 + i * 20, 200, 0, 0.5f, 0.5f);
				}
				for (int i = 0; i < SFXVolume; i++) {
					canvas.draw(objectController.volumeBoxTexture.getTexture(), Color.WHITE, 0, 0, 970 + i * 20, 140, 0, 0.5f, 0.5f);
				}
				canvas.draw(objectController.unhoverLowerSoundTexture.getTexture(), Color.WHITE, 0, 0, 935, 200, 0, 0.5f, 0.5f);
				canvas.draw(objectController.unhoverLowerSoundTexture.getTexture(), Color.WHITE, 0, 0, 935, 140, 0, 0.5f, 0.5f);
				canvas.draw(objectController.unhoverUpSoundTexture.getTexture(), Color.WHITE, 0, 0, 1175, 200, 0, 0.5f, 0.5f);
				canvas.draw(objectController.unhoverUpSoundTexture.getTexture(), Color.WHITE, 0, 0, 1175, 140, 0, 0.5f, 0.5f);


				switch (pauseItemSelected) {
					case 0: // Resume Level
						canvas.draw(objectController.indicatorStarTexture.getTexture(), Color.WHITE, 0, 0,  800, 400, 0, 0.5f * pulse, 0.5f * pulse);
						break;
					case 1: // Restart Level
						canvas.draw(objectController.indicatorStarTexture.getTexture(), Color.WHITE, 0, 0,  800,340, 0, 0.5f * pulse, 0.5f * pulse);
						break;
					case 2: // Exit Level
						canvas.draw(objectController.indicatorStarTexture.getTexture(), Color.WHITE, 0, 0, 800, 280,0, 0.5f * pulse, 0.5f * pulse);
						break;
					case 3: // Music
						canvas.draw(objectController.indicatorStarTexture.getTexture(), Color.WHITE, 0, 0, 740,200, 0, 0.5f * pulse, 0.5f * pulse);
						break;
					case 4: // SFX
						canvas.draw(objectController.indicatorStarTexture.getTexture(), Color.WHITE, 0, 0, 780,140, 0, 0.5f * pulse, 0.5f * pulse);
						break;
					case 5: // Calibrate
						canvas.draw(objectController.indicatorStarTexture.getTexture(), Color.WHITE, 0, 0, 780,60, 0, 0.5f * pulse, 0.5f * pulse);
						break;
				}
			}

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
	public void resize ( int width, int height){
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
	public void render ( float delta){
		if (active) {
			if (preUpdate(delta) && !paused  && !displayStartCutScenes) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);
			}
			if (!paused) {
				canvas.updateCamera(objectController.player, worldWidth, worldHeight);
			}
			draw(delta);
		}



	}

	/**
	 * Called when the Screen is paused.
	 *
	 * We need this method to stop all sounds when we pause.
	 * Pausing happens when we switch game modes.
	 */
	public void pause () {

		InputController.getInstance().setPaused(true);
		soundController.setGlobalMusicVolumeImmediate(musicVolume / 10f, true);
	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume () {
		soundController.setGlobalMusicVolumeImmediate(musicVolume / 10f);
		soundController.setGlobalSFXVolume(SFXVolume / 10f);

		// soundController.resumeMusic();
		InputController.getInstance().setPaused(false);
		pauseItemSelected = 0; // delete this line if pause menu should "save" where you were last time
		InputController.getInstance().setSwitchGenre(false);
	}

	public void pauseAction ( int sel){
		switch (sel) {
			case 1: // Restart Level
				paused = false;
				for (Checkpoint checkpoint : objectController.checkpoints) {
					checkpoint.setActive(false);
				}
				if (objectController.checkpoints.size() > 0) {
					objectController.checkpoints.get(0).setActive(true);
				}
				respawnPoint = null;
				resume();
				reset();

				if(currentLevelInt == 1) {
					displayStartCutScenes = true;
					showLevel1FirstCutScene = false;
					showLevel1SecondCutScene = false;
					showLevel1ThirdCutScene = false;
					showLevel1FourthCutScene = false;
				} else if(currentLevelInt == 9) {
					displayStartCutScenes = true;
					showLevel9StartingScreen[0] = false;
					showLevel9StartingScreen[1] = false;
				}
				break;
			case 0: // Resume Level
				paused = false;
				resume();
				break;
			case 2: // Exit Level
				exitLevel();
				break;
			case 5: // Calibrate
				calibrateScreen = true;
			default:
				break;
		}
	}

	/**
	 * Returns to the level select screen and resets the SoundController
	 * accordingly.
	 */

	public void exitLevel () {
		soundController.resetMusic();
		soundController.pauseMusic();
		displayStartCutScenes = false;
		exitScreen(0);
	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show () {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide () {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener (ScreenListener listener){
		this.listener = listener;
	}

	/**
	 * Switches the genre depending on what the current genre is.
	 */
	public void switchGenre () {
		switch (genre) {
			case SYNTH:
				genre = Genre.JAZZ;
				break;
			case JAZZ:
				genre = Genre.SYNTH;
				break;
		}
	}

	public void setSpawn (Vector2 spawn){
		respawnPoint = spawn;
	}

	public Player getPlayer () {
		return objectController.player;
	}

	/** Return the currentLevel String variable */
	public String getCurrentLevel () {
		return currentLevel;
	}

	/** Set the currentLevel variable to the current level */
	public void setCurrentlLevel (String currentLevel){
		this.currentLevel = currentLevel;
	}

	public int getNumberOfLevels () {
		return numberOfLevels;
	}

	/** Called when the game screen needs to be exited out of */
	public void exitScreen ( int exitCode){
		soundController.pauseMusic();
		listener.exitScreen(this, exitCode);
	}

	/**
	 * Sets the currentLevelInt variable and concurrently change the currentLevel
	 * String
	 */
	public void setCurrentLevelInt ( int currentLevelInt){
		this.currentLevelInt = currentLevelInt;
		currentLevel = "level" + currentLevelInt;
	}

	/** Return the int variable currentLevelInt */
	public int getCurrentLevelInt () {
		return currentLevelInt;
	}

	/** Returns the number of levelsUnlocked */
	public int getLevelsUnlocked () {
		return levelsUnlocked;
	}

	/** Sets the integer levelsUnlocked */
	public void setLevelsUnlocked ( int levelsUnlocked){
		this.levelsUnlocked = levelsUnlocked;
	}

	/**
	 * Increments the integer levelsUnlocked if a player completes a level and the
	 * next level is locked
	 */
	public void incrementLevelsUnlocked () {
		if (currentLevelInt == levelsUnlocked && currentLevelInt < 12) {
			levelsUnlocked++;
			Preferences prefs = Gdx.app.getPreferences("SavedLevelsUnlocked");
			prefs.putInteger("levelsUnlocked", levelsUnlocked);
			prefs.flush();
		}
	}

	/** Returns whether player has completed the level */
	public boolean getPlayerCompletedLevel () {
		return playerCompletedLevel;
	}

	/** Sets the boolean playerCompletedLevel */
	public void setPlayerCompletedLevel ( boolean playerCompletedLevel){
		this.playerCompletedLevel = playerCompletedLevel;
	}

	/** Sets the integer victoryScreenItemSelected */
	public void setVictoryScreenItemSelected ( int victoryScreenItemSelected){
		this.victoryScreenItemSelected = victoryScreenItemSelected;
	}

	/** Returns the integer victoryScreenItemSelected */
	public int getVictoryScreenItemSelected () {
		return victoryScreenItemSelected;
	}

	/** Returns the object controller */
	public ObjectController getObjectController () {
		return objectController;
	}


	/** Boolean that represents whether all the cutscenes have been read and
	 * whether the next level should be loaded
	 */
	public boolean readyToGoToNextLevel = false;

	/** Boolean that represents whether the first victory screen is showing for level 1 and level 12 */
	public boolean showFirstVictoryScreen;

	/** Boolean that represents whether the second victory screen is showing for level 1 and level 12 */
	public boolean showSecondVictoryScreen;

	/** Boolean that represents whether the third victory screen is showing for level 12*/
	public boolean showThirdVictoryScreen;

	/** Boolean that represents whether the fourth victory screen is showing for level 12*/
	public boolean showFourthVictoryScreen;

	/** Boolean that represents whether the fifth victory screen is showing for level 12*/
	public boolean showFifthVictoryScreen;


	/** Displays the victory screen after player completes a level */
	public void drawVictoryScreen () {
		canvas.begin(true);
		if (currentLevelInt == 1) {

			if (InputController.getInstance().didPressEnter()) {
				if (showSecondVictoryScreen) {
					readyToGoToNextLevel = true;
					showSecondVictoryScreen = false;
				} else if (showFirstVictoryScreen) {
					showSecondVictoryScreen = true;
					showFirstVictoryScreen = false;
				}

			}
			if (showFirstVictoryScreen) {
				canvasDrawVictoryScreen(objectController.victoryScreens.get("level1VS-1"));
			} else if (showSecondVictoryScreen) {
				canvasDrawVictoryScreen(objectController.victoryScreens.get("level1VS-2"));
			}


		} else if (currentLevelInt == 4) {
			canvas.draw(objectController.victoryScreens.get("level4VS"), 0, 0);
		} else if (currentLevelInt == 6) {
			canvas.draw(objectController.victoryScreens.get("level6VS"), 0, 0);
		} else if (currentLevelInt == 8) {
			canvas.draw(objectController.victoryScreens.get("level8VS"), 0, 0);
		} else if (currentLevelInt == 10) {
			canvas.draw(objectController.victoryScreens.get("level10VS"), 0, 0);
		} else if (currentLevelInt == 11) {
			canvas.draw(objectController.victoryScreens.get("level11VS"), 0, 0);
		} else if (currentLevelInt == 12) {
			if(InputController.getInstance().didPressEnter()) {
				if (showFourthVictoryScreen) {
					showFifthVictoryScreen=true;
					showFourthVictoryScreen=false;
				} else if (showThirdVictoryScreen) {
					showFourthVictoryScreen = true;
					showThirdVictoryScreen = false;
				} else if(showSecondVictoryScreen) {
					showThirdVictoryScreen = true;
					showSecondVictoryScreen = false;
				} else if (showFirstVictoryScreen) {
					showSecondVictoryScreen = true;
					showFirstVictoryScreen = false;
				}
			}

			if(showFirstVictoryScreen) {
				canvasDrawVictoryScreen(objectController.victoryScreens.get("level12VS-1"));
			} else if(showSecondVictoryScreen) {
				//TODO: replace with 2nd victory screen (Level 12)
				canvasDrawVictoryScreen(objectController.victoryScreens.get("level12VS-2"));
			} else if(showThirdVictoryScreen) {
				//TODO: replace with 3nd victory screen (Level 12)
				canvasDrawVictoryScreen(objectController.victoryScreens.get("level12VS-3"));
			} else if(showFourthVictoryScreen) {
				//TODO: replace with 4th victory screen (Level 12)
				canvasDrawVictoryScreen(objectController.victoryScreens.get("level12VS-4"));
			}else if(showFifthVictoryScreen) {
				//TODO: replace with 4th victory screen (Level 12)
				canvasDrawVictoryScreen(objectController.victoryScreens.get("level12VS-5"));
			}

		} else {
			canvas.draw(objectController.victoryScreenBackground, 0, 0);
		}
		canvas.end();

	}

	/** Helper method for drawVictoryScreen that takes a TextureRegion parameter to
	 * pass into the method canvas.draw()
	 * */
	public void canvasDrawVictoryScreen(TextureRegion victoryScreen) {
		canvas.draw(victoryScreen, 0, 0);
	}
}
