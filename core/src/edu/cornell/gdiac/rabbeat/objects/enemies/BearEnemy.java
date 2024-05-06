package edu.cornell.gdiac.rabbeat.objects.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.objects.projectiles.Bullet;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Bear enemy avatar for the platform game.
 * Bears shoot bullets that are synced to the beat.
 */
public class BearEnemy extends Enemy {

    private final float beat = .25f;

    /** The bullet the bear will be shooting */
    public Bullet bullet;

    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();

    /** Tells whether the bear was facing right or not when they shot */
    private boolean shotDirection;

    /** The attack animation in synth for the bear */
    public Animation<TextureRegion> attackSynthAnimation;
    /** The attack animation in jazz for the bear */
    public Animation<TextureRegion> attackJazzAnimation;

    /**
     * Creates a bear enemy avatar with the given physics data
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
    public BearEnemy(JsonValue data, float startX, float startY, float width, float height, float enemyScale,
            boolean faceRight, int[] beatList, Genre genre) {
        super(data, startX, startY, width, height, enemyScale, faceRight, beatList, genre);
    }

    /**
     * Switches the attack state of the bear depending on the player's position.
     */
    public void switchState() {
//        switch (enemyState) {
//            case IDLE:
//                if (horizontalDistanceBetweenEnemyAndPlayer() < 20) {
//                    enemyState = EnemyState.ATTACKING;
//                }
//                break;
//            case ATTACKING:
//                if (horizontalDistanceBetweenEnemyAndPlayer() > 20) {
//                    enemyState = EnemyState.IDLE;
//                }
//                // TODO: make bear shoot
//                break;
//        }
//        System.out.println(enemyState);
    }

    /** Creates a bullet in front of the bear */
    public void makeBullet() {
        ObjectController oc = GameController.getInstance().objectController;
        float offset = oc.defaultConstants.get("bullet").getFloat("offset", 0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = (genre == Genre.SYNTH ?
                (oc.bulletTexture.getRegionWidth() / (0.3f * scale.x)) :
                (oc.bulletTexture.getRegionWidth() / (0.24f * scale.x)));
        float width  = oc.bulletTexture.getRegionWidth()*0.1f;
        float height = oc.bulletTexture.getRegionHeight()*0.05f;
        bullet = new Bullet(getX()+offset, getY()+0.1f, width, height,
                oc.defaultConstants.get("bullet").getFloat("synth speed", 0),
                oc.defaultConstants.get("bullet").getFloat("jazz speed", 0), isFaceRight(),
                genre);

        bullet.setDensity(oc.defaultConstants.get("bullet").getFloat("density", 0));
        bullet.setDrawScale(scale);
        //bullet.setTexture(oc.bulletTexture);
        bullet.setGravityScale(0);
        bullet.setAnimation(genre == Genre.SYNTH ? oc.bulletSynthAnimation : oc.bulletJazzAnimation);
        shotDirection = isFaceRight();
        int beatcount;

        // Compute position and velocity
        float speed;
        if (genre == Genre.SYNTH) {
            speed = oc.defaultConstants.get("bullet").getFloat("synth speed", 0);
            beatcount = oc.defaultConstants.get("bullet").getInt("synth bullet time", 0);
        } else {
            speed = oc.defaultConstants.get("bullet").getFloat("jazz speed", 0);
            beatcount = oc.defaultConstants.get("bullet").getInt("jazz bullet time", 0);
        }
        speed *= (isFaceRight() ? 1 : -1);
        bullet.setVX(speed);
        bullet.beatCount = beatcount;
        GameController.getInstance().instantiateQueue(bullet);
    }

    public void beatAction() {
        super.beatAction();
        flipEnemy();
    }

    @Override
    public void Attack() {
        makeBullet();
    }
}
