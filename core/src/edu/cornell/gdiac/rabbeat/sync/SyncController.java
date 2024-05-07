package edu.cornell.gdiac.rabbeat.sync;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.rabbeat.GameController;
import java.text.DecimalFormat;

public class SyncController {
    /**
     * The purpose of this class is to track all synced objects in the game and
     * update them appropriately.
     * This is achieved by taking in all synced objects and creating a list of
     * intervals representing
     * each synced object. in the update loop of
     * {@link edu.cornell.gdiac.rabbeat.GameController},
     * each interval is checked and updated when the tracked beat has passed.
     */

    /** The bpm of the soundtrack */
    public int BPM;

    /** The synth soundtrack */
    Music synth;
    /** The jazz soundtrack */
    Music jazz;

    /** The audio delay of the audio in seconds */
    public float audioDelay = 0f;
    /** The visual delay of the animations in seconds */
    public float visualDelay = 0f;
    /** The intervals of each of the synced objects in the game */
    private Array<Interval> intervals = new Array<>();

    /** The beat of the game */
    public Beat beat = new Beat();
    /** The beat interval of the game */
    private Interval beatInterval;

    /** The interval that represents the animation update */
    private AnimationSync animationSync = new AnimationSync();
    /** *The interval for animationSync */
    private Interval animationInterval;

    private float calibrateDT = 0f;

    Array<Float> beatLatencyList = new Array<>();
    int calibrationCount = 0;
    final int NUM_CALIBRATION_STEPS = 12;

    public SyncController(int bpm) {
        this.BPM = bpm;
        animationInterval = new Interval(animationSync, (visualDelay) / bpm);
        beatInterval = new Interval(beat, (audioDelay) / bpm);
        Preferences prefs = Gdx.app.getPreferences("delays");
        audioDelay = prefs.getFloat("audioDelay", 0);
        visualDelay = prefs.getFloat("visualDelay", 0);
    }

    /**
     * TODO: Create description and use SoundController instead. Maybe even delete
     * this function
     */
    public void setSync(Music _synth, Music _jazz) {
        synth = _synth;
        jazz = _jazz;
    }

    /**
     * Adds delay to visualDelay
     *
     * @param delay A float value that represents the added delay
     */
    public void addVisualDelay(float delay) {
        visualDelay = (float)(Math.round((visualDelay + delay)*100)) / 100;
        Preferences prefs = Gdx.app.getPreferences("delays");
        prefs.putFloat("visualDelay", visualDelay);
        prefs.flush();
    }
    /**
     * Adds delay to audioDelay
     *
     * @param delay A float value that represents the added delay
     */
    public void addAudioDelay(float delay) {
        audioDelay = (float)(Math.round((audioDelay + delay)*100)) / 100;
        Preferences prefs = Gdx.app.getPreferences("delays");
        prefs.putFloat("audioDelay", audioDelay);
        prefs.flush();
    }

    /**
     * The update function for everything synced in the world
     * 
     * @param isPaused if the game is currently paused
     */
    public void update(boolean isPaused) {
        beatInterval.checkForNewInterval(
                (synth.getPosition() - audioDelay) / beatInterval.getIntervalLength(BPM), true);

        animationInterval.checkForNewInterval(
                (synth.getPosition() - visualDelay) / animationInterval.getIntervalLength(BPM), !isPaused);
        for (Interval i : intervals) {
            float sample = (synth.getPosition() - audioDelay) / i.getIntervalLength(BPM);
            i.checkForNewInterval(sample, !isPaused);
        }

    }

    /**
     * Adds delta values to the local delta values in this class and {@link Beat}
     */
    public void updateCalibrate(float dt) {
        calibrateDT += dt;
        beat.updateBeatDT(dt);
    }

    /**
     * The calibration for audio delay. The audio delay is dependent on the audio
     * output source
     * the player is using. Delay is calculated by the average delay of a player
     * clicking an input
     * to when they hear the beat. The average delay is then stored to be used for
     * beat calculation
     */
    public void calibrate() {
        beatLatencyList.add(calibrateDT);
        calibrationCount++;
        if (calibrationCount >= NUM_CALIBRATION_STEPS) {
            GameController.getInstance().inCalibration = false;
            float averageDelay = 0;
            int numCalibrations = Math.min(beatLatencyList.size, beat.beatLatencyList.size);
            for (int i = 0; i < numCalibrations; i++) {
                System.out.println("actual: " + beatLatencyList.get(i) + ", sunc: " + beat.beatLatencyList.get(i));
                averageDelay += (beatLatencyList.get(i) - beat.beatLatencyList.get(i));
            }
            beatLatencyList.clear();
            beat.beatLatencyList.clear();
            audioDelay =  (float)(Math.round((averageDelay / numCalibrations)*100)) / 100  ;
            System.out.println("delay: " + (audioDelay*100) + "ms");
            calibrationCount = 0;
            calibrateDT = 0;
            beat.beatDT = 0;
            Preferences prefs = Gdx.app.getPreferences("delays");
            prefs.putFloat("audioDelay", audioDelay);
            prefs.flush();
        }
    }

    /**
     * Creates an {@link Interval} object from {@param syncedObject} and adds it to
     * the intervals
     * in order to be synced. If the synced object is animated, add to the list of
     * animated synced objects
     * 
     * @param syncedObject A synced object
     */
    public void addSync(ISynced syncedObject) {
        Interval interval = new Interval(
                syncedObject, ((synth.getPosition() + audioDelay) / (60f / (BPM * syncedObject.getBeat()))));
        intervals.add(interval);
        if (syncedObject instanceof ISyncedAnimated) {
            animationSync.animatedObjects.add((ISyncedAnimated) (syncedObject));
        }
    }

    /**
     * Called to start the first beatAction when the game is initialized. The only
     * synced object that
     * ignores this is the animationSync because the first frame should not be
     * skipped
     */
    public void initializeSync() {
        beatInterval.syncedObject.beatAction();
        for (Interval i : intervals) {
            i.syncedObject.beatAction();
        }
    }
}
