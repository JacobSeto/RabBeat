package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

/**
 * Hedgehog enemy avatar for the platform game.
 * Hedgehogs roll back and forth along a platform and are lethal if they collide
 * with the player.
 */
public class HedgehogEnemy extends Enemy implements ISynced, IGenreObject {

    /** The endpoint equivalent to the hedgehog's starting position */
    private final float point1 = getX();

    /** The other endpoint */
    private final float point2;

    /** The distance the hedgehog moves every update frame */
    private float distance = 0.1f;

    /** The boolean that represents the direction that the hedgehog is rolling in  */
    private boolean rollingRight = false;

    /** The boolean for whether the hedgehog is in rolling mode */
    private boolean roll = false;

    /**
     * Creates a new enemy avatar with the given physics data
     *
     * @param data                  The JsonValue storing information about the
     *                              hedgehog
     * @param startX                The starting x position of the enemy
     * @param startY                The starting y position of the enemy
     * @param rollingDistance       The distance which the hedgehog will roll
     * @param width                 The object width in physics units
     * @param height                The object width in physics units
     * @param enemyScale            The scale of the hedgehog
     * @param faceRight             The direction the hedgehog is facing in
     * @param hedgehogIdleAnimation The idle animation for the hedgehog
     * @param beatList              The list of beats that the enemy reacts to
     */
    public HedgehogEnemy(JsonValue data, float startX, float startY, int rollingDistance, float width, float height,
            float enemyScale, boolean faceRight, int[] beatList,
            Animation<TextureRegion> hedgehogIdleAnimation) {
        super(data, startX, startY, width, height, enemyScale, faceRight, hedgehogIdleAnimation, beatList);
        setAnimation(hedgehogIdleAnimation);
        point2 = getX() - rollingDistance;
    }
  
    public void switchState() {
        switch (enemyState) {
            case IDLE:
                if (horizontalDistanceBetweenEnemyAndPlayer() < 8) {
                    enemyState = EnemyState.ATTACKING;
                }
                break;
            case ATTACKING:
                if (horizontalDistanceBetweenEnemyAndPlayer() > 8) {
                    enemyState = EnemyState.IDLE;
                }
                // TODO: make bear shoot
                break;
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (roll) {
            if (rollingRight) {
                setPosition(getX() + distance, getY());
                // setAngle(angle);
            } else {
                setPosition(getX() - distance, getY());
                // setAngle(angle);
            }
        }

        if (getX() >= point1) {

            rollingRight = false;
            roll = false;
        } else if (getX() <= point2) {
            rollingRight = true;
            roll = false;
        }

        if (GameController.getInstance().genre == Genre.SYNTH) {
            distance = 0.1f;
        } else {
            distance = 0.05f;
        }
    }

    @Override
    public void Attack() {
        roll = true;
    }

}
