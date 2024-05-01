package edu.cornell.gdiac.rabbeat.sync;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

public class SyncController {
    /**
     * The purpose of this class is to track all synced objects in the game and update them appropriately.
     * This is achieved by taking in all synced objects and creating a list of intervals representing
     * each synced object. in the update loop of {@link edu.cornell.gdiac.rabbeat.GameController},
     * each interval is checked and updated when the tracked beat has passed.
     */

    /** The bpm of the soundtrack*/
    public int BPM = 120;

    /** The synth soundtrack*/
    Music synth;
    /** The jazz soundtrack*/
    Music jazz;

    /**The audio delay of the audio  in seconds*/
    private float delay = 0f;
    /** The intervals of each of the synced objects in the game */
    private Array<Interval> intervals = new Array<>();
    /** The interval that represents the animation update */
    private AnimationSync animationSync = new AnimationSync();

    public SyncController(int bpm) {
        this.BPM = bpm;
    }
    public SyncController() {}

    /** TODO: Create description and use SoundController instead.  Maybe even delete this function*/
    public void setSync(Music _synth, Music _jazz){
        synth = _synth;
        jazz = _jazz;
        addSync(animationSync);
    }

    /**Adds _delay to the delay field
     * @param _delay A float value that represents the added delay
     * */
    public void addDelay(float _delay){
        delay += _delay;
    }

    /**The update function for everything synced in the world*/
    public void updateBeat(){

        for(Interval i : intervals){
            float sample = synth.getPosition() / i.getIntervalLength(BPM) + delay * i.syncedObject.getBeat();
            i.checkForNewInterval(sample);
        }
    }

    /**The calibration for audio delay.  The audio delay is dependent on the audio output source
     * the player is using.  Delay is calculated by the average delay of a player clicking an input
     * to when they hear the beat.  The average delay is then stored to be used for beat calculation*/
    public void calibrate(){
        //TODO: Calibrate for audio delay
        float averageDelay = 0f;
        delay = averageDelay;
    }


    /**
     * Creates an {@link Interval} object from {@param syncedObject} and adds it to the intervals
     * in order to be synced. If the synced object is animated, add to the list of animated synced objects
     * @param syncedObject A synced object
     */
    public void addSync(ISynced syncedObject){
            Interval interval = new Interval(syncedObject);
            intervals.add(interval);
            if(syncedObject instanceof ISyncedAnimated){
                animationSync.animatedObjects.add((ISyncedAnimated)(syncedObject));
            }
        }
    }
