package edu.cornell.gdiac.rabbeat.objects.art;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.sync.ISynced;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;

public class AnimatedArtObject extends ArtObject implements ISyncedAnimated {
    /** The animated art object's current animation */
    public Animation<TextureRegion> animation;
    /** The elapsed time for animationUpdate */
    private float stateTime = 0;

    /**
     * Creates a new animated art object with the given texture region and x and y coordinates.
     *
     * @param animation The animated art object's animation.
     * @param x             The art object's x coordinate in world coordinates.
     * @param y             The art object's y coordinate in world coordinates.
     */
    public AnimatedArtObject(Animation<TextureRegion> animation, float x, float y) {
        super(animation.getKeyFrame(0), x, y);
        setAnimation(animation);
        super.textureRegion = animation.getKeyFrame(stateTime);
    }

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void beatAction() {
        return;
    }

    @Override
    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    @Override
    public void updateAnimationFrame() {
        stateTime++;
        super.textureRegion = animation.getKeyFrame(stateTime);
    }
}
