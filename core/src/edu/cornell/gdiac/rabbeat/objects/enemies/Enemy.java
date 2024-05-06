package edu.cornell.gdiac.rabbeat.objects.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.objects.CapsuleGameObject;
import com.badlogic.gdx.graphics.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.objects.IGenreObject;
import edu.cornell.gdiac.rabbeat.objects.Type;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;

/**
 * Enemy parent class for the platform game.
 */
public abstract class Enemy extends CapsuleGameObject implements ISyncedAnimated, IGenreObject {

    /** Enum containing the state of the enemy */
    protected enum EnemyState {
        IDLE,
        ATTACKING
    }
    /** The current genre*/
    Genre genre;

    /** Enum containing the state of the animation */
    protected enum AnimationState {
        IDLE,
        ANTI,
        ATTACK
    }

    /** The scale of the enemy */
    private float enemyScale;

    /** Whether the enemy is facing right */
    private boolean faceRight;

    /** Initializes the state of the enemy to idle */
    protected EnemyState enemyState;

    /** The enemy's current animation */
    public Animation<TextureRegion> animation;
    /** The elapsed time for animationUpdate */
    protected float stateTime = 0;
    /** Holds the genre of the ANIMATION. Doesn't specifically detect genre. */
    protected Genre animationGenre;
    /** Initializes the state of the animation */
    protected AnimationState animationState = AnimationState.IDLE;

    /** Whether the enemy is flippable */
    protected boolean isFlippable = true;

    /**
     * The beat counter for an enemy. The beat counter cycles integers values
     * starting from 1
     * and incrementing to 8 (2 measures)
     */

    private float beat = 1;
    public int beatCount = 0;
    /**
     * A list of beats in which the enemies act when called in beatAction. Default
     * for enemies is
     * acting on the downbeat (beatCount counts to 8 so the downbeats are 1 and 5)
     */
    public int[] beatList;

    /** The index to cycle through beatList */
    public int beatListIndex;

    /**
     * Creates a new enemy avatar with the given physics data
     *
     * @param data       The physics constants for this enemy
     * @param startX     The starting x position of the enemy
     * @param startY     The starting y position of the enemy
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param enemyScale The scale of the enemy
     * @param faceRight  The direction the enemy is facing in
     * @param beatList   The list of beats that the enemy reacts to
     * @param genre      The current genre of the game
     */
    public Enemy(JsonValue data, float startX, float startY,
            float width, float height, float enemyScale, boolean faceRight, int[] beatList, Genre genre) {
        // The shrink factors fit the image to a tigher hitbox
        super(startX, startY,
                width * data.get("shrink").getFloat(0),
                height * data.get("shrink").getFloat(1));

        setDensity(data.getFloat("density", 0));
        setFriction(data.getFloat("friction", 0));
        setFixedRotation(true);

        this.faceRight = faceRight; // should face the direction player is in?
        this.enemyScale = enemyScale;
        this.beatList = beatList;
        setType(Type.LETHAL);
        setName("enemy");
        this.genre = genre;
        enemyState = EnemyState.ATTACKING;
    }

    /**
     * Getter for faceRight
     */
    public boolean isFaceRight() {
        return faceRight;
    }

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        super.update(dt);
        switchState();
    }

    /**
     * Draws the physics object of the enemy.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        float effect = (faceRight && isFlippable) ? -1.0f : 1.0f;
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        canvas.draw(currentFrame, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y,
                getAngle(), enemyScale * effect, enemyScale);
    }

    /** Sets the direction that the enemy is facing in */
    public void setFaceRight(boolean faceRight) {
        this.faceRight = faceRight;
    }

    /** Returns the horizontal distance between the enemy and the player */
    public float horizontalDistanceBetweenEnemyAndPlayer() {
        return Math.abs(playerXPosition() - getPosition().x);
    }

    /** Switches enemy attacking state depending on its current state */
    public abstract void switchState();

    /** Returns the x position of the player */
    public float playerXPosition() {
        if (GameController.getInstance() != null) {
            return GameController.getInstance().getPlayer().getPosition().x;
        }

        return 0;
    }
    /** Flips the direction the enemy is facing based on the player's position */
    public void flipEnemy() {
        if (playerXPosition() - getPosition().x > 0 && !faceRight) {
            setFaceRight(true);
            setPosition(getX(), getY());
        } else if (playerXPosition() - getPosition().x < 0 && faceRight) {
            setFaceRight(false);
            setPosition(getX(), getY());
        }
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    public void updateAnimationFrame() {
        stateTime++;
    }

    public void genreUpdate(Genre genre) {
        this.genre = genre;
    }

    @Override
    public float getBeat() {
        return beat;
    }

    public void beatAction() {
        beatCount++;
        if (beatCount >= 9) {
            beatCount = 1;
        }
        if (beatList[beatListIndex] == beatCount) {
            animationState = AnimationState.ATTACK;
            if (enemyState == EnemyState.ATTACKING) {
                Attack();
                beatListIndex++;
                if (beatListIndex >= beatList.length) {
                    beatListIndex = 0;
                }
            }
        } else {
            if (beatList[beatListIndex] == 1f) {
                if (8f == beatCount) {
                    animationState = AnimationState.ANTI;
                } else {
                    animationState = AnimationState.IDLE;
                }
            } else if (beatList[beatListIndex] - 1f == beatCount) {
                animationState = AnimationState.ANTI;
            } else {
                animationState = AnimationState.IDLE;
            }
        }
    }

    /** The function called whenever the enemy is supposed to attack */
    public abstract void Attack();

}