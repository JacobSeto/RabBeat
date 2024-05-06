package edu.cornell.gdiac.rabbeat.objects.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.objects.projectiles.Echo;

/**
 * Bat enemy avatar for the platform game.
 * Bats send echos synced to the beat and players will be hurt if they enter its
 * radius.
 */
public class BatEnemy extends Enemy {
    /** The echo attack the bat will be shooting */
    public Echo echo;

    /** Number of beats the bullet exists in synth */
    private int synthBulletTime = 3;

    /** Number of beats the bullet exists in jazz */
    private int jazzBulletTime = 8;

    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();


    /** The attack animation in synth for the bear */
    public Animation<TextureRegion> attackSynthAnimation;
    /** The attack animation in jazz for the bear */
    public Animation<TextureRegion> attackJazzAnimation;

    /** The echo animation for the bat*/
    public Animation<TextureRegion> echoAnimation;

    /**
     * Creates a bat enemy avatar with the given physics data
     *
     * @param data       The physics constants for this enemy
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param startX     The starting x position of the enemy
     * @param startY     The starting y position of the enemy
     * @param enemyScale The scale of the enemy
     * @param faceRight  The direction the enemy is facing in
     * @param beatList   The list of beats that the enemy reacts to
     */
    public BatEnemy(JsonValue data, float startX, float startY, float width, float height, float enemyScale,
            boolean faceRight, int[] beatList, Genre genre) {
        super(data, startX, startY, width, height, enemyScale, faceRight, beatList, genre);
        isFlippable = false;
    }

    /**
     * Switches the attack state of the bear depending on the player's position.
     */
    public void switchState() {
        switch (enemyState) {
            case IDLE:
                if (horizontalDistanceBetweenEnemyAndPlayer() < 16) {
                    enemyState = EnemyState.ATTACKING;
                }
                break;
            case ATTACKING:
                if (horizontalDistanceBetweenEnemyAndPlayer() > 16) {
                    enemyState = EnemyState.IDLE;
                }
                break;
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    /** Creates a bullet in front of the bear */
    public void makeEcho() {

        ObjectController oc = GameController.getInstance().objectController;
        float offset = oc.defaultConstants.get("echo").getFloat("offset", 0);
        offset *= (isFaceRight() ? 1 : -1);
        for(int i = 0; i < 2; i++){
            if(genre == Genre.SYNTH){
                echo = new Echo(getX() + offset, getY(),
                        2, .75f,  echoAnimation);
            }
            else{
                echo = new Echo(getX(), getY()+ offset,
                        .75f, 2.5f,  echoAnimation);
                echo.vertical = true;
            }
            if(i == 1) echo.flipX = !echo.flipX;
            echo.setSensor(true);
            echo.setDensity(oc.defaultConstants.get("echo").getFloat("density", 0));
            echo.setDrawScale(scale);
            echo.setTexture(oc.echoTexture);
            echo.setGravityScale(0);
            GameController.getInstance().instantiateQueue(echo);
            offset *= -1;
        }

    }
    @Override
    public void Attack() {
        makeEcho();
    }
}
