/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.*;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.util.*;


/**
 * Class for reading player input.
 *
 * This supports both a keyboard and X-Box controller. In previous solutions, we only
 * detected the X-Box controller on start-up.  This class allows us to hot-swap in
 * a controller via the new XBox360Controller class.
 */
public class InputController {
	// Sensitivity for moving crosshair with gameplay
	private static final float GP_ACCELERATE = 1.0f;
	private static final float GP_MAX_SPEED  = 10.0f;
	private static final float GP_THRESHOLD  = 0.01f;

	/** The singleton instance of the input controller */
	private static InputController theController = null;


	/**
	 * Return the singleton instance of the input controller
	 *
	 * @return the singleton instance of the input controller
	 */
	public static InputController getInstance() {
		if (theController == null) {
			theController = new InputController();
		}
		return theController;
	}

	// Fields to manage buttons
	/** Whether the reset button was pressed. */
	private boolean resetPressed;
	private boolean resetPrevious;
	/** Whether the button to advanced worlds was pressed. */
	private boolean nextPressed;
	private boolean nextPrevious;
	/** Whether the button to step back worlds was pressed. */
	private boolean prevPressed;
	private boolean prevPrevious;
	/** Whether the primary action button was pressed. */
	private boolean primePressed;
	private boolean primePrevious;
	/** Whether the secondary action button was pressed. */
	private boolean secondPressed;
	private boolean secondPrevious;
	/** Whether the teritiary action button was pressed. */
	private boolean tertiaryPressed;
	/** Whether the debug toggle was pressed. */
	private boolean debugPressed;
	private boolean debugPrevious;
	/** Whether the exit button was pressed. */
	private boolean exitPressed;
	private boolean exitPrevious;

	/** Whether or not the pause button was pressed */
	private boolean pausePressed;

	private boolean enterPressed;

	private boolean enterPrevious;

	private boolean pausePrevious;

	private boolean pauseUpPressed;
	private boolean pauseUpPrevious;

	private boolean pauseDownPressed;
	private boolean pauseDownPrevious;

	private boolean pauseRightPressed;
	private boolean pauseRightPrevious;

	private boolean pauseLeftPressed;
	private boolean pauseLeftPrevious;

	private boolean levelSelectPressed;

	private boolean levelSelectPrevious;

	/** How much did we move horizontally? */
	private float horizontal;
	/** How much did we move vertically? */
	private float vertical;
	/** The crosshair position (for raddoll) */
	private Vector2 crosshair;
	/** The crosshair cache (for using as a return value) */
	private Vector2 crosscache;
	/** For the gamepad crosshair control */
	private float momentum;

	/** Whether genre has been changed in the instance that the space key was pressed */
	private boolean genreSwitched = false;

	/** Whether space bar has been pressed to switch genre */
	private boolean switchGenre;

	/** Whether the delay buttons have been pressed*/
	private float delay;

	/** Whether or not the game is paused. Set by GameController */
	private boolean paused;

	/** An X-Box controller (if it is connected) */
	XBoxController xbox;

	/**
	 * Returns the amount of sideways movement.
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways movement.
	 */
	public float getHorizontal() {
		return horizontal;
	}

	/**
	 * Returns the amount of vertical movement.
	 *
	 * -1 = down, 1 = up, 0 = still
	 *
	 * @return the amount of vertical movement.
	 */
	public float getVertical() {
		return vertical;
	}

	/**
	 * Returns the current position of the crosshairs on the screen.
	 *
	 * This value does not return the actual reference to the crosshairs position.
	 * That way this method can be called multiple times without any fair that
	 * the position has been corrupted.  However, it does return the same object
	 * each time.  So if you modify the object, the object will be reset in a
	 * subsequent call to this getter.
	 *
	 * @return the current position of the crosshairs on the screen.
	 */
	public Vector2 getCrossHair() {
		return crosscache.set(crosshair);
	}

	/**
	 * Returns true if the primary action button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the primary action button was pressed.
	 */
	public boolean didPrimary() {
		return primePressed && !primePrevious;
	}

	/**
	 * Returns true if the secondary action button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didSecondary() {
		return secondPressed && !secondPrevious;
	}

	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didTertiary() {
		return tertiaryPressed;
	}

	/**
	 * Returns true if the reset button was pressed.
	 *
	 * @return true if the reset button was pressed.
	 */
	public boolean didReset() {
		return resetPressed && !resetPrevious;
	}

	/**
	 * Returns true if the player wants to go to the next level.
	 *
	 * @return true if the player wants to go to the next level.
	 */
	public boolean didAdvance() {
		return nextPressed && !nextPrevious;
	}

	public boolean didPressRightWhilePaused() {return pauseRightPressed && !pauseRightPrevious;}

	public boolean didPressLeftWhilePaused() {return pauseLeftPressed && !pauseLeftPrevious;}

	public boolean didPressUpWhilePaused() {return pauseUpPressed && !pauseUpPrevious;}

	public boolean didPressDownWhilePaused() {return pauseDownPressed && !pauseDownPrevious;}

	public boolean didPressLevelSelect() {return levelSelectPressed && !levelSelectPrevious;}

	/**
	 * Returns true if the player wants to go to the previous level.
	 *
	 * @return true if the player wants to go to the previous level.
	 */
	public boolean didRetreat() {
		return prevPressed && !prevPrevious;
	}

	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didDebug() {
		return debugPressed && !debugPrevious;
	}

	/**
	 * Returns true if the exit button was pressed.
	 *
	 * @return true if the exit button was pressed.
	 */
	public boolean didExit() {
		return exitPressed && !exitPrevious;
	}

	/**
	 * Creates a new input controller
	 *
	 * The input controller attempts to connect to the X-Box controller at device 0,
	 * if it exists.  Otherwise, it falls back to the keyboard control.
	 */

	/**
	 * Returns true if the pause button was pressed.
	 * @return true if the pause button was pressed.
	 */
	public boolean didPause() { return pausePressed && !pausePrevious; }

	public boolean didPressEnter() { return enterPressed && !enterPrevious;}

	/** Sets whether or not the game is currently paused.
	 *
	 * @param p whether or not the game is currently paused.
	 */
	public void setPaused(boolean p) { paused = p;}

	public InputController() {
		// If we have a game-pad for id, then use it.
		Array<XBoxController> controllers = Controllers.get().getXBoxControllers();
		if (controllers.size > 0) {
			xbox = controllers.get( 0 );
		} else {
			xbox = null;
		}
		crosshair = new Vector2();
		crosscache = new Vector2();
	}

	/**
	 * Reads the input for the player and converts the result into game logic.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the crosshair.  They cannot go outside of this zone.
	 *
	 * @param bounds The input bounds for the crosshair.
	 * @param scale  The drawing scale
	 */
	public void readInput(Rectangle bounds, Vector2 scale) {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down
		primePrevious  = primePressed;
		secondPrevious = secondPressed;
		resetPrevious  = resetPressed;
		debugPrevious  = debugPressed;
		exitPrevious = exitPressed;
		nextPrevious = nextPressed;
		prevPrevious = prevPressed;
		pausePrevious = pausePressed;
		enterPrevious = enterPressed;
		levelSelectPrevious = levelSelectPressed;
		if (paused) {
			pauseUpPrevious = pauseUpPressed;
			pauseDownPrevious = pauseDownPressed;
			pauseLeftPrevious = pauseLeftPressed;
			pauseRightPrevious = pauseRightPressed;
		}

		// Check to see if a GamePad is connected
		if (xbox != null && xbox.isConnected()) {
			readGamepad(bounds, scale);
			readKeyboard(bounds, scale, true); // Read as a back-up
		} else {
			readKeyboard(bounds, scale, false);
		}
	}

	/**
	 * Reads input from an X-Box controller connected to this computer.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the crosshair.  They cannot go outside of this zone.
	 *
	 * @param bounds The input bounds for the crosshair.
	 * @param scale  The drawing scale
	 */
	private void readGamepad(Rectangle bounds, Vector2 scale) {
		resetPressed = xbox.getStart();
		exitPressed  = xbox.getBack();
		nextPressed  = xbox.getRBumper();
		prevPressed  = xbox.getLBumper();
		primePressed = xbox.getA();
		debugPressed  = xbox.getY();

		// Increase animation frame, but only if trying to move
		horizontal = xbox.getLeftX();
		vertical   = xbox.getLeftY();
		secondPressed = xbox.getRightTrigger() > 0.6f;

		// Move the crosshairs with the right stick.
		tertiaryPressed = xbox.getA();
		crosscache.set(xbox.getLeftX(), xbox.getLeftY());
		if (crosscache.len2() > GP_THRESHOLD) {
			momentum += GP_ACCELERATE;
			momentum = Math.min(momentum, GP_MAX_SPEED);
			crosscache.scl(momentum);
			crosscache.scl(1/scale.x,1/scale.y);
			crosshair.add(crosscache);
		} else {
			momentum = 0;
		}
		clampPosition(bounds);
	}

	/**
	 * Reads input from the keyboard.
	 *
	 * This controller reads from the keyboard regardless of whether or not an X-Box
	 * controller is connected.  However, if a controller is connected, this method
	 * gives priority to the X-Box controller.
	 *
	 * @param secondary true if the keyboard should give priority to a gamepad
	 */
	private void readKeyboard(Rectangle bounds, Vector2 scale, boolean secondary) {
		// Give priority to gamepad results
		resetPressed = (secondary && resetPressed) || (Gdx.input.isKeyPressed(Input.Keys.R));
		debugPressed = (secondary && debugPressed) || (Gdx.input.isKeyPressed(Input.Keys.B));
		primePressed = (secondary && primePressed) || (Gdx.input.isKeyPressed(Input.Keys.UP)
				|| Gdx.input.isKeyPressed(Input.Keys.W));
		exitPressed = (secondary && exitPressed) || (Gdx.input.isKeyPressed(Input.Keys.ESCAPE));
		pausePressed = (secondary && pausePressed) || (Gdx.input.isKeyPressed(Input.Keys.P));
		levelSelectPressed = (secondary && levelSelectPressed) || (Gdx.input.isKeyPressed(Input.Keys.L));


		// Directional controls
		if (!paused) {
			horizontal = (secondary ? horizontal : 0.0f);
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
				horizontal += 1.0f;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
				horizontal -= 1.0f;
			}

			vertical = (secondary ? vertical : 0.0f);
			if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
				vertical += 1.0f;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
				vertical -= 1.0f;
			}

			if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !genreSwitched) {
				genreSwitched = true;
				switchGenre = true;
			} else if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
				genreSwitched = false;
			}
		}
		// When the game IS paused
		else {
			pauseRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
			pauseLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
			pauseUpPressed = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
			pauseDownPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
			enterPressed = Gdx.input.isKeyPressed(Input.Keys.ENTER);

			if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !genreSwitched) {
				genreSwitched = true;
				switchGenre = true;
			} else if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
				genreSwitched = false;
			}
			//TODO: This is temporary code to add artificial delay to the syncing
			delay = 0;
			if (Gdx.input.isKeyPressed(Keys.EQUALS)) {
				delay = .05f;
			} else if (Gdx.input.isKeyPressed(Keys.MINUS)) {
				delay = -.05f;
			}

			if (Gdx.input.isKeyPressed(Input.Keys.L)) {
				levelSelectPressed = true;
			}

			if (Gdx.input.isKeyPressed(Keys.TAB) && GameController.getInstance()
					.getPlayerCompletedLevel()) {
				GameController gc = GameController.getInstance();
				gc.exitScreen(1);
				gc.setPlayerCompletedLevel(false);
				gc.setCurrentlLevel(gc.getCurrentLevel() + 1);
			}

			if (Gdx.input.isKeyPressed(Keys.C)) {
				GameController.getInstance().setComplete(true);
				GameController.getInstance().setPlayerCompletedLevel(false);
			}


			// Mouse results
			tertiaryPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
			crosshair.set(Gdx.input.getX(), Gdx.input.getY());
			crosshair.scl(1 / scale.x, -1 / scale.y);
			crosshair.y += bounds.height;
			clampPosition(bounds);
		}
		//TODO: This is temporary code to add artificial delay to the syncing
		delay = 0;
		if (Gdx.input.isKeyPressed(Keys.EQUALS)){
			delay = .05f;
		}
		else if(Gdx.input.isKeyPressed(Keys.MINUS)){
			delay = -.05f;
		}

		if(GameController.getInstance().getPlayerCompletedLevel()) {
			GameController gc = GameController.getInstance();

			//Press tab to go to the next level (only if level has been completed)
			if (Gdx.input.isKeyPressed(Keys.TAB)) {
				gc.exitScreen(1);
				gc.setPlayerCompletedLevel(false);
				gc.setCurrentLevelInt(gc.getCurrentLevelInt()+1);
			}

			//Switch between the texts nextLevel (0) and levelSelect (1)
			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
				gc.setVictoryScreenItemSelected(1);
			} else if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)){
				gc.setVictoryScreenItemSelected(0);
			}

			//Click enter/return once selection has been chosen
			if(Gdx.input.isKeyPressed(Keys.ENTER)) {
				gc.setPlayerCompletedLevel(false);
				gc.setCurrentLevelInt(gc.getCurrentLevelInt()+1);

				if(gc.getVictoryScreenItemSelected() == 0) {
					//GO TO NEXT LEVEL
					gc.exitScreen(1);
				} else if(gc.getVictoryScreenItemSelected() == 1) {
					//GO BACK TO LEVEL SELECT
					gc.exitScreen(0);
				}
			}


		}

		//C = shortcut to complete the level
		if (Gdx.input.isKeyPressed(Keys.C)) {
			GameController.getInstance().setComplete(true);
			GameController.getInstance().setPlayerCompletedLevel(false);
		}





		// Mouse results
        	tertiaryPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		crosshair.set(Gdx.input.getX(), Gdx.input.getY());
		crosshair.scl(1/scale.x,-1/scale.y);
		crosshair.y += bounds.height;
		clampPosition(bounds);
	}

	/**
	 * Clamp the cursor position so that it does not go outside the window
	 *
	 * While this is not usually a problem with mouse control, this is critical
	 * for the gamepad controls.
	 */
	private void clampPosition(Rectangle bounds) {
		crosshair.x = Math.max(bounds.x, Math.min(bounds.x+bounds.width, crosshair.x));
		crosshair.y = Math.max(bounds.y, Math.min(bounds.y+bounds.height, crosshair.y));
	}


	/** Sets the boolean value for switchGenre */
	public void setSwitchGenre(boolean switchGenre){
		this.switchGenre = switchGenre;
	}

	/** Returns the boolean value of switchGenre
	 * Returns true if genre is switching
	 * */
	public boolean getSwitchGenre(){
		return switchGenre;
	}

	/** returns the delay value*/
	public float getDelay(){
		return delay;
	}
}