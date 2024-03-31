package edu.cornell.gdiac.rabbeat.obstacles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Checkpoint extends BoxGameObject {

    /** Index of the checkpoint in the checkpoints json */
    private final int index;
    /** The texture for the checkpoint when it is active has already been reached */
    private final TextureRegion activeTexture;

    /** Indicates whether the checkpoint is active */
    public boolean isActive;

    /**
     * Creates a new checkpoint.
     *
     * @param index         The index of the checkpoint in the checkpoints json
     * @param activeTexture The texture for the checkpoint when it is active
     * @param x             Initial x position of the box center in Box2D units
     * @param y  		    Initial y position of the box center in Box2D units
     * @param width         The width of the checkpoint
     * @param height        The height of the checkpoint
     */
    public Checkpoint(int index, TextureRegion activeTexture, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.index = index;
        this.activeTexture = activeTexture;
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
        isActive = true;
        setTexture(activeTexture);
    }

}
