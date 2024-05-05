package edu.cornell.gdiac.rabbeat.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.objects.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;

/** Class for all in-game UI elements that depend on the current genre. */
public class GenreUI implements IGenreObject, ISyncedAnimated {

    /** The texture for the indicator. */
    private TextureRegion texture;
    /** The texture for synth */
    private final TextureRegion synthTexture;
    /** The texture for jazz */
    private final TextureRegion jazzTexture;

    /** The synth animation */
    private Animation<TextureRegion> synthAnimation;
    /** The jazz animation */
    private Animation<TextureRegion> jazzAnimation;
    /** The current animation */
    public Animation<TextureRegion> animation;
    /** The elapsed time for animationUpdate */
    private float stateTime = 0;

    /** Holds the genre of the ANIMATION. Doesn't specifically detect genre. */
    private Genre animationGenre;

    /**
     * Creates a new genre indicator.
     */
    public GenreUI(TextureRegion synthTexture, TextureRegion jazzTexture, Animation<TextureRegion> synthAnimation, Animation<TextureRegion> jazzAnimation) {
        this.synthTexture = synthTexture;
        this.jazzTexture = jazzTexture;
        this.synthAnimation = synthAnimation;
        this.jazzAnimation = jazzAnimation;
        texture = synthTexture;
        animationGenre = Genre.SYNTH;
        setAnimation(synthAnimation);
    }

    /**
     * Updates the UI's physics animation.
     **
     * @param dt	Number of seconds since last animation frame
     */
    public void update(float dt) {
        stateTime += dt;
        switch (animationGenre) {
            case SYNTH:
                if (animation.isAnimationFinished(stateTime)) {
                    stateTime = 0;
                    setAnimation(synthAnimation);
                }
                break;
            case JAZZ:
                if (animation.isAnimationFinished(stateTime)) {
                    stateTime = 0;
                    setAnimation(jazzAnimation);
                }
                break;
        }
    }

    /**
     * Draws the GenreUI.
     *
     * @param canvas    Drawing context
     * @param x         The x coordinate on the screen
     * @param y         The y coordinate on the screen
     */
    public void draw(GameCanvas canvas, float x, float y) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        canvas.draw(currentFrame,x,y);
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
    }

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void beatAction() { }

    @Override
    public void setAnimation(Animation<TextureRegion> animation) { this.animation = animation; }

    @Override
    public void updateAnimationFrame() { stateTime++; }
}
