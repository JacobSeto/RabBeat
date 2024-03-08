package edu.cornell.gdiac.rabbeat.sync;

public interface ISynced {
    /**
     * Returns how often the beat/pulse occurs based on a quarter note
     * Assume in common time: 4/4
     * Ex: 1 represents a pulse every quarter note, .5 represents a pulse every half note
     */
    public float getBeat();

    /**
     * Called when an update should occur on the beat
     */
    public void beatAction();
}
