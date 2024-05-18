package edu.cornell.gdiac.rabbeat.objects.art;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.objects.IGenreObject;
import edu.cornell.gdiac.rabbeat.objects.art.ArtObject;
import edu.cornell.gdiac.rabbeat.objects.art.PulsingArtObject;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;

/** Class for all in-game UI elements that depend on the current genre. */
public class GenreUI extends PulsingArtObject implements IGenreObject {
    /** The texture for synth */
    private TextureRegion synthTexture;
    /** The texture for jazz */
    private TextureRegion jazzTexture;

    private TextureRegion texture;

    /**
     * Creates a new art object with the given texture region and x and y coordinates.
     *
     * @param textureRegion The art object's texture region.
     * @param x             The art object's x coordinate in world coordinates.
     * @param y             The art object's y coordinate in world coordinates.
     * @param pulseScale    The art object's pulse scale
     * @param
     */
    public GenreUI(TextureRegion textureRegion, float x,
            float y, float pulsePerBeat, float pulseScale, float shrinkRate, TextureRegion synthTexture, TextureRegion jazzTexture, Genre genre) {
        super(textureRegion, x, y, pulsePerBeat, pulseScale, shrinkRate);
        this.pulsePerBeat = pulsePerBeat;
        this.pulseScale = pulseScale;
        this.shrinkRate = shrinkRate;
        this.synthTexture = synthTexture;
        this.jazzTexture = jazzTexture;
        texture = (genre == Genre.SYNTH? synthTexture : jazzTexture);
    }

    @Override
    public void genreUpdate(Genre genre) {
        texture = (genre == Genre.SYNTH ? synthTexture : jazzTexture);
    }

    public void draw(GameCanvas canvas, float x, float y) {
        if (texture != null) {
            canvas.draw(texture, Color.WHITE, origin.x, origin.y,
                    x , y, 0, pulseScaleAmount,pulseScaleAmount);
        }
    }

    @Override
    public void update(float delta){
        if(pulseScaleAmount > 1 ){
            pulseScaleAmount -= shrinkRate;
        }
    }
}
