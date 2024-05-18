package edu.cornell.gdiac.rabbeat.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;

/**
 * Class for checkpoints, which determines if the player has reached a checkpoint and
 * the spawn points of the player.
 */
public class Checkpoint extends BoxGameObject implements ISyncedAnimated {

    /** Index of the checkpoint in the checkpoints json */
    private final int index;
    /** Indicates whether the checkpoint is active */
    public boolean isActive;
    /** Indicates whether the checkpoint is rising */
    private boolean isRising = false;

    /** The active animation */
    private Animation<TextureRegion> activeAnimation;
    /** The rise animation */
    private Animation<TextureRegion> riseAnimation;
    /** The inactive animation */
    private Animation<TextureRegion> inactiveAnimation;
    /** The current animation */
    public Animation<TextureRegion> animation;
    /** The elapsed time for animationUpdate */
    private float stateTime = 0;

    /** The animation playing is rising */
    private boolean animationIsRising = false;

    /**
     * Creates a new checkpoint.
     *
     * @param index             The index of the checkpoint in the checkpoints json
     * @param inactiveAnimation The inactive animation for checkpoints
     * @param activeAnimation   The active animation for checkpoints
     * @param riseAnimation     The rise animation for checkpoints
     * @param x                 Initial x position of the box center in Box2D units
     * @param y  		        Initial y position of the box center in Box2D units
     * @param width             The width of the checkpoint
     * @param height            The height of the checkpoint
     */
    public Checkpoint(int index, Animation<TextureRegion> inactiveAnimation, Animation<TextureRegion> activeAnimation, Animation<TextureRegion> riseAnimation, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.index = index;
        isActive = false;
        this.inactiveAnimation = inactiveAnimation;
        this.activeAnimation = activeAnimation;
        this.riseAnimation = riseAnimation;
        setAnimation(inactiveAnimation);
    }

    /**
     * Updates the object's physics state.
     *
     * @param dt	Number of seconds since last animation frame
     */
    public void update(float dt) {
        if (animationIsRising) {
            if (riseAnimation.isAnimationFinished(stateTime)) {
                isRising = false;
                animationIsRising = false;
            }
        }

        if (!isActive) {
            setAnimation(inactiveAnimation);
        } else if (!isRising) {
            setAnimation(activeAnimation);
        }

        super.update(dt);
    }

    public void draw(GameCanvas canvas) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        canvas.draw(currentFrame, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
                1,1);
    }

    /**
     * Returns the index of the checkpoint in the checkpoints json.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the checkpoint as active and changes the texture of the checkpoint.
     */
    public void setActive() {
        isRising = true;
        isActive = true;
        setAnimation(riseAnimation);
        animationIsRising = true;
        stateTime = 0;
    }

    public void setActive(boolean act) {
        if (act) {
            isRising = true;
            isActive = true;
            setAnimation(riseAnimation);
            animationIsRising = true;
            stateTime = 0;
        } else {
            isActive = false;
        }
    }

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void beatAction() { }

    @Override
    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    @Override
    public void updateAnimationFrame() {
        stateTime++;
    }
}
