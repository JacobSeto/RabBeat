package edu.cornell.gdiac.rabbeat.objects.art;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class PulsingArtObject extends ArtObject implements ISynced {
    /** This class is an artobject that pulses to the beat by increasing its scale to the beat
     * and decreasing the scale over time*/


    /** The number of pulses per beat*/
    float pulsePerBeat;

    /** The scale that it is multiplied by on beat*/
    float pulseScale;
    /** The speed at which the scale shrinks per frame update*/
    float shrinkRate;

    /** The scale bonus multiplied to the current scale. Decrements by the shrinkRate until it reaches 1*/
    float pulseScaleAmount = 1;

    /**
     * Creates a new art object with the given texture region and x and y coordinates.
     *
     * @param textureRegion The art object's texture region.
     * @param x             The art object's x coordinate in world coordinates.
     * @param y             The art object's y coordinate in world coordinates.
     * @param pulseScale    The art object's pulse scale
     */
    public PulsingArtObject(TextureRegion textureRegion, float x,
            float y, float pulsePerBeat, float pulseScale, float shrinkRate) {
        super(textureRegion, x, y);
        this.pulsePerBeat = pulsePerBeat;
        this.pulseScale = pulseScale;
        this.shrinkRate = shrinkRate;
    }

    @Override
    public float getBeat() {
        return pulsePerBeat;
    }

    @Override
    public void beatAction() {
        pulseScaleAmount = pulseScale;
    }
    @Override
    public void update(float delta){
        if(pulseScaleAmount > 1 ){
            pulseScaleAmount -= shrinkRate;
        }
    }

    @Override
    public void draw(GameCanvas canvas) {
        if (textureRegion != null) {
            canvas.draw(textureRegion, Color.WHITE,origin.x,origin.y,
                    getX()*drawScale.x ,
                    getY()*drawScale.x ,getAngle(), pulseScaleAmount,pulseScaleAmount);
        }
    }
}
