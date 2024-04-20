package edu.cornell.gdiac.rabbeat.sync;

import com.badlogic.gdx.utils.Array;

public class AnimationSync implements ISynced{
    /**
     * The purpose of this class is to sync all animations to the beat. When beatAction is called,
     * all the animations will update to the next animation frame.
     */

    /** The animated objects that need to be synced with the game*/
    public Array<ISyncedAnimated> animatedObjects = new Array<>();
    /** Number of frames that play per beat */
    float ANIMATION_FPB = 3.0f;

    @Override
    public float getBeat() {
        return ANIMATION_FPB;
    }

    @Override
    public void beatAction() {
        for(ISyncedAnimated a : animatedObjects){
            a.updateAnimationFrame();
        }
    }
}
