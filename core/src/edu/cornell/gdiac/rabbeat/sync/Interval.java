package edu.cornell.gdiac.rabbeat.sync;

/**
 * An Interval is an object that is synced to a beat.  The interval of time it takes for each beat
 * to occur relative to the bpm is checked every update.  When enough time elapses for the interval
 * to be elapsed, a new interval is set and the synced object reacts.
 */
public class Interval {

    private float beat;
    private ISynced s;
    private int lastInterval;


    /**
     * Constructor for Interval.  Takes in an ISynced and gets the beat from ISynced
     *
     * @param _s ISynced object
     */
    public Interval(ISynced _s) {
        s = _s;
        beat = s.getBeat();
    }

    /**
     * Returns the length of the interval in seconds
     *
     * @param bpm The bpm of the soundtrack
     */
    public float getIntervalLength(float bpm) {
        return 60f / (bpm * beat);
    }

    /**
     * Checks if the interval length has passed by checking if the current interval is not equal to
     * the last interval.  If true, call Beat() from sync object and set the last interval to the
     * current.
     *
     * @param interval the interval length
     */
    public void checkForNewInterval(float interval) {
        if ((int) Math.floor(interval) != lastInterval) {
            if (lastInterval < interval) {
                s.beatAction();
            }
            lastInterval = (int) Math.floor(interval);
        }
    }
}