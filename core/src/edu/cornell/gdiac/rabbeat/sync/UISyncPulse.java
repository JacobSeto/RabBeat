package edu.cornell.gdiac.rabbeat.sync;

public class UISyncPulse implements  ISynced{
    /**
     * The purpose of this class is to sync ui objects that move to the beat.
     */

    /** The scale that it is multiplied by on beat*/
    float PULSE_SCALE = 1.1f;
    /** The speed at which the scale shrinks per frame update*/
    float SHRINK_RATE = .01f;

    /** The scale bonus multiplied to the current scale. Decrements by the shrinkRate until it reaches 1*/
    public float uiPulseScale = 1;

    /** The number of times the UI reacts to the beat*/

    float uiBeat = .5f;

    @Override
    public float getBeat() {
        return uiBeat;
    }

    @Override
    public void beatAction() {
        uiPulseScale = PULSE_SCALE;
    }

    public void update(){
        if(uiPulseScale > 1 ){
            uiPulseScale -= SHRINK_RATE;
        }
    }
}
