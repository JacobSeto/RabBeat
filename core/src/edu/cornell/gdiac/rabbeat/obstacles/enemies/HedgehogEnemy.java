package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

/**
 * Hedgehog enemy avatar for the platform game.
 * Hedgehogs roll back and forth along a platform and are lethal if they collide with the player.
 */
public class HedgehogEnemy extends Enemy implements ISynced, IGenreObject {

    /** The distance that the hedgehog rolls */
    private final float rollingDistance;

    /** The endpoint equivalent to the hedgehog's starting position */
    private final float point1 = getX();

    /** The other endpoint */
    private final float point2;

    /** The distance the hedgehog moves every update frame */
    private float distance = 0.1f;

    //TODO: delete?
    /** The angle orientation of the hedgehog */
    private float angle = 5;

    /** The boolean that represents the direction that the hedgehog is rolling in  */
    private boolean rollingRight = false;

    /** The boolean for whether the hedgehog is in rolling mode */
    private boolean roll = false;

    /**
     * Creates a new enemy avatar with the given physics data
     *
     * @param data                    The JsonValue storing information about the hedgehog
     * @param width                   The object width in physics units
     * @param height                  The object width in physics units
     * @param enemyScale              The scale of the enemy
     * @param faceRight               The direction the bat is facing in
     * @param hedgehogIdleAnimation   The idle animation for the hedgehog
     */
    public HedgehogEnemy(JsonValue data, float width, float height,
            float enemyScale, boolean faceRight,
            Animation<TextureRegion> hedgehogIdleAnimation) {
        super(data, width, height, enemyScale, faceRight, hedgehogIdleAnimation);
        setAnimation(hedgehogIdleAnimation);
        rollingDistance = data.getFloat("rollingDistance");
        point2 = getX() - rollingDistance;
    }

    /** Updates the variable curGenre to the current genre of the game */
    public void genreUpdate(Genre genre) {
        curGenre = genre;
    }

    @Override
    public void switchState() {
        switch(enemyState) {
            case IDLE:
                if(curGenre.equals(Genre.JAZZ)) {
                    if(beatCount == 4) {
                        enemyState = EnemyState.ATTACKING;
                        System.out.println("ATTACKING Jazz");
                    }
                } else if(curGenre.equals(Genre.SYNTH)) {
                    if(beatCount == 4 || beatCount == 2) {
                        enemyState = EnemyState.ATTACKING;
                        System.out.println("ATTACKING Synth");
                    }
                }
                break;
            case ATTACKING:
                if(curGenre.equals(Genre.JAZZ)) {
                    if(beatCount != 4) {
                        enemyState = EnemyState.IDLE;
                        System.out.println("IDLE Jazz");
                    }
                } else if(curGenre.equals(Genre.SYNTH)) {
                    if(beatCount != 4 && beatCount != 2) {
                        enemyState = EnemyState.IDLE;
                        System.out.println("IDLE Synth");
                    }
                }
                break;
        }
    }

    private int beatCount = 1;
    private float beat = 1;

    @Override
    public float getBeat() {
        return beat;
    }

    @Override
    public void beatAction() {
        beatCount++;
        if(beatCount >= 5){
            beatCount = 1;
        }

        if(enemyState.equals(EnemyState.ATTACKING)) {
           roll = true;
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        //setVX(-1);

        if(roll) {
            if(rollingRight) {
                setPosition(getX()+distance, getY());
                //setAngle(angle);
            } else {
                setPosition(getX()-distance, getY());
                System.out.println("X:" + getX());
                System.out.println("point2" + point2);
                //setAngle(angle);
            }
        }


        if(getX() >= point1) {
            rollingRight = false;
            roll = false;
        } else if (getX() <= point2) {
            rollingRight = true;
            roll = false;
        }

        if(curGenre == Genre.SYNTH) {
            distance = 0.1f;
        } else {
            distance = 0.05f;
        }
    }
}
