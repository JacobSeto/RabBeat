package edu.cornell.gdiac.rabbeat.sync;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

public class SyncController {

    /** The bpm of the soundtrack*/
    private final int BPM = 120;

    /** The synth soundtrack*/
    Music synth;
    /** The jazz soundtrack*/
    Music jazz;

    /**The audio delay of the audio  in seconds*/
    float delay = 0;
    private Array<Interval> intervals = new Array<>();

    /** TODO: Create description and use SoundController instead.  Maybe even delete this function*/
    public void setSync(Music _synth, Music _jazz){
        synth = _synth;
        jazz = _jazz;
    }

    /**The update function for everything synced in the world*/
    public void updateBeat(){
        for(Interval i : intervals){
            float sample = synth.getPosition() / i.getIntervalLength(BPM) + delay;
            i.checkForNewInterval(sample);
        }

    }

    /**The calibration for audio delay.  The audio delay is dependent on the audio output source
     * the player is using.  Delay is calculated by the average delay of a player clicking an input
     * to when they hear the beat.  The average delay is then stored to be used for beat calculation*/
    public void calibration(){
        //TODO: Calibrate for audio delay
        float averageDelay = 0f;
        delay = averageDelay;
    }


    /**
     * Creates an {@link Interval} object from {@param syncedObject} and adds it to the intervals
     * in order to be synced
     * @param syncedObject A synced object
     */
    public void addSync(ISynced syncedObject){
            Interval interval = new Interval(syncedObject);
            intervals.add(interval);
        }
    }
