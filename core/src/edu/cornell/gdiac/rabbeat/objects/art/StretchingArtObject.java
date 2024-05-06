package edu.cornell.gdiac.rabbeat.objects.art;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class StretchingArtObject extends ArtObject implements ISynced {
    /** This class stretches an art object to the beat*/

    /** The number of pulses per beat*/
    float pulsePerBeat;

    /** The horizontal grow that it is added to the horizontal scale*/
    float horizontalGrowRate;
    /** The vertical scale that it is multiplied by on beat*/
    float verticalGrowRate;

    /** The scale bonus multiplied to the horizontal scale. Decrements by the shrinkRate until it reaches 1*/
    float horizontalScaleAmount = 1;
    /** The scale bonus multiplied to the vertical scale. Decrements by the shrinkRate until it reaches 1*/
    float verticalScaleAmount = 1;

    /**
     * Creates a new art object with the given texture region and x and y coordinates.
     *
     * @param textureRegion The art object's texture region.
     * @param x             The art object's x coordinate in world coordinates.
     * @param y             The art object's y coordinate in world coordinates.
     * @param horizontalGrowRate   The art object's horizontal scale
     * @param verticalGrowRate     The art object's vertical scale
     */
    public StretchingArtObject(TextureRegion textureRegion, float x,
            float y, float pulsePerBeat, float horizontalGrowRate, float verticalGrowRate) {
        super(textureRegion, x, y);
        this.pulsePerBeat = pulsePerBeat;
        this.horizontalGrowRate = horizontalGrowRate;
        this.verticalGrowRate = verticalGrowRate;
    }

    @Override
    public float getBeat() {
        return pulsePerBeat;
    }

    @Override
    public void beatAction() {
        horizontalScaleAmount = 1;
        verticalScaleAmount = 1;
    }
    @Override
    public void update(float delta){
            horizontalScaleAmount += horizontalGrowRate;
            verticalScaleAmount += verticalGrowRate;

    }

    @Override
    public void draw(GameCanvas canvas) {
        if (textureRegion != null) {
            canvas.draw(textureRegion, Color.WHITE,origin.x,origin.y,
                    getX()*drawScale.x ,
                    getY()*drawScale.x ,getAngle(), horizontalScaleAmount, verticalScaleAmount);
        }
    }
}
