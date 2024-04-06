package edu.cornell.gdiac.rabbeat.sync;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface ISyncedAnimated extends ISynced{
    /**
     * Sets the animation of the synced animated object.
     * NOTE: The animation state must have frame durations of 1 in order for this implementation to work
     */
    public void setAnimation(Animation<TextureRegion> animation);

    /**
     * Called in updateBeat from {@code SyncController} to update the current frame that should be
     * played in the animation
     */
    public void updateAnimationFrame();
}
