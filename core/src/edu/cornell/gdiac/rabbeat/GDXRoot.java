/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
 package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.assets.*;

/**
 * Root class for a LibGDX.  
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * plaforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	AssetDirectory directory;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;

	/** Player mode for the initial loading screen asset (CONTROLLER CLASS) */
	private LoadingMode initialLoading;

	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Player mode for the game proper (CONTROLLER CLASS) */
	private int current;
	/** List of all WorldControllers */
	private GameController controller;

	/** Variable that represents the level selector screen */
	private LevelSelectorScreen levelSelectorScreen;

	/** Variable that represents the main menu screen */
	private MainMenuScreen mainMenuScreen;

	/** Main menu music */

	private Music mainMenuMusic;


	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() { controller = new GameController(); }

	/** 
	 * Called when the Application is first created.
	 * 
	 * This method initializes the canvas and loading screen as well as creates and displays
	 * the levelSelector screen
	 */
	public void create() {
		canvas = new GameCanvas();
		initialLoading = new LoadingMode("assets.json", canvas, 1);
		initialLoading.setScreenListener(this);
		setScreen(initialLoading);

		levelSelectorScreen = new LevelSelectorScreen(this);
		levelSelectorScreen.setListener(this);

		mainMenuScreen = new MainMenuScreen(this);
		mainMenuScreen.setListener(this);
	}

	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		controller.dispose();

		canvas.dispose();
		canvas = null;
	
		// Unload all of the resources
		if (directory != null) {
			directory.unloadAssets();
			directory.dispose();
			directory = null;
		}
		super.dispose();
	}
	
	/**
	 * Called when the Application is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}
	
	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if(screen == initialLoading) {
			directory = initialLoading.getAssets();
			mainMenuMusic = directory.getEntry("music:mainmenu", Music.class);
			controller.gatherAssets(directory);
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			InputController.getInstance().setPaused(true);
			setScreen(mainMenuScreen);
			mainMenuMusic.play();
		} else if (screen == levelSelectorScreen || exitCode == GameController.NEXT_LEVEL) {
			mainMenuMusic.stop();
			controller = new GameController();
			InputController.getInstance().setPaused(false);
			GameController.getInstance().setPaused(false);
			controller.gatherAssets(directory);
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			controller.resume();
			controller.initialize();
			setScreen(controller);
		}else if (screen == controller || exitCode == GameController.GO_TO_LEVEL_SELECT) {
			createLevelSelectorScreen();
		} else if (exitCode == GameController.EXIT_QUIT) {
			Gdx.app.exit();
		}
	}

	/** Creates the level selector screen */
	public void createLevelSelectorScreen() {
		levelSelectorScreen = new LevelSelectorScreen(this);
		levelSelectorScreen.setListener(this);
		setScreen(levelSelectorScreen);
	}

}
