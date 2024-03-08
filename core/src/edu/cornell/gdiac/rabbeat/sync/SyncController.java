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

    public void setSync(Array<ISynced> syncedObjects, Music _synth, Music _jazz){
        synth = _synth;
        jazz = _jazz;

        //create an interval for each sync
        createIntervals(syncedObjects);

        synth.play();
        jazz.play();
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
     * After all synced objects have been loaded, create a list of intervals of each synced object
     * @param syncedObjects The list of synced objects loaded in the world
     */
    public void createIntervals(Array<ISynced> syncedObjects){
        for(ISynced s : syncedObjects){
            Interval interval = new Interval(s);
            intervals.add(interval);
        }
    }


    /**
     * An Interval is an object that is synced to a beat.  The interval of time it takes for each
     * beat to occur relative to the bpm is checked every update.  When enough time elapses for
     * the interval to be elapsed, a new interval is set and the synced object reacts.
     * */
    public class Interval{
        private float beat;
        private ISynced s;
        private int lastInterval;


        /**Constructor for Interval.  Takes in an ISynced and gets the beat from ISynced
         * @param _s ISynced object
         * */
        public Interval(ISynced _s){
            s = _s;
            beat = s.getBeat();
        }

        /**Returns the length of the interval in seconds
         * @param bpm The bpm of the soundtrack
         */
        public float getIntervalLength(float bpm){
            return 60f / (bpm * beat);
        }

        /**
         * Checks if the interval length has passed by checking if the current interval
         * is not equal to the last interval.  If true, call Beat() from sync object and set
         * the last interval to the current.
         * @param interval the interval length
         */
        public void checkForNewInterval (float interval){
            System.out.println(lastInterval);
            if ((int)Math.floor(interval) != lastInterval){
                if(lastInterval < interval){
                    s.Beat();
                }
                lastInterval = (int)Math.floor(interval);
            }
        }
    }

}
