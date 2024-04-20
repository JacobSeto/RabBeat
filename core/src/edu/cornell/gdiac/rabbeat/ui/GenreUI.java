package edu.cornell.gdiac.rabbeat.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;

/** Class for all in-game UI elements that depend on the current genre. */
public class GenreUI implements IGenreObject, ISyncedAnimated {

    /** The texture for the indicator. */
    private TextureRegion texture;
    /** The texture for synth */
    private final TextureRegion synthTexture;
    /** The texture for jazz */
    private final TextureRegion jazzTexture;

    /** Holds the genre of the ANIMATION. Doesn't specifically detect genre. */
    private Genre animationGenre;

    /**
     * Creates a new genre indicator.
     */
    public GenreUI(TextureRegion synthTexture, TextureRegion jazzTexture) {
        this.synthTexture = synthTexture;
        this.jazzTexture = jazzTexture;
        texture = synthTexture;
        animationGenre = Genre.SYNTH;
    }

    /**
     * Gets the current texture of the genre indicator.
     */
    public TextureRegion getTexture() {
        return texture;
    }

    /**
     * Sets the current texture of the genre indicator.
     *
     * @param texture   The texture being set.
     */
    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    @Override
    public void genreUpdate(Genre genre) {
        animationGenre = genre;
        switch (genre) {
            case SYNTH:
                setTexture(synthTexture);
                break;
            case JAZZ:
                setTexture(jazzTexture);
                break;
        }
    }

    @Override
    public float getBeat() {
        return 0;
    }

    @Override
    public void beatAction() { }

    @Override
    public void setAnimation(Animation<TextureRegion> animation) { }

    @Override
    public void updateAnimationFrame() { }
}
