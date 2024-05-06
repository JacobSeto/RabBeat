package edu.cornell.gdiac.rabbeat.sync;

/**
 * An Interval is an object that is synced to a beat.  The interval of time it takes for each beat
 * to occur relative to the bpm is checked every update.  When enough time elapses for the interval
 * to be elapsed, a new interval is set and the synced object reacts.
 */
public class Interval {
    public ISynced syncedObject;

    private int lastInterval;


    /**
     * Constructor for Interval.  Takes in an ISynced and gets the beat from ISynced
     *
     * @param _s ISynced object
     */
    public Interval(ISynced _s, float startInterval) {
        syncedObject = _s;
        lastInterval = (int) Math.floor(startInterval);
    }

    /**
     * Returns the length of the interval in seconds
     *
     * @param bpm The bpm of the soundtrack
     */
    public float getIntervalLength(float bpm) {
        return 60f / (bpm * syncedObject.getBeat());
    }

    /**
     * Checks if the interval length has passed by checking if the current interval is not equal to
     * the last interval.  If true, call Beat() from sync object and set the last interval to the
     * current.
     *
     * @param interval the interval length
     * @param doAction if beatAction should be called when the next interval is updated
     */
    public void checkForNewInterval(float interval, boolean doAction) {
        if ((int) Math.floor(interval) != lastInterval) {
            if (lastInterval < interval && doAction) {
                syncedObject.beatAction();
            }
            lastInterval = (int) Math.floor(interval);
        }
    }
}
