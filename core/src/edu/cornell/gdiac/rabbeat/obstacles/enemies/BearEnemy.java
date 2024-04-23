package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.obstacles.projectiles.Bullet;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Bear enemy avatar for the platform game.
 * Bears shoot bullets that are synced to the beat.
 */
public class BearEnemy extends Enemy {

    private final float beat = .25f;

    /** The bullet the bear will be shooting */
    public Bullet bullet;

    /** Number of beats the bullet exists in synth */
    private int synthBulletTime = 3;

    /** Number of beats the bullet exists in jazz */
    private int jazzBulletTime = 8;

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
            boolean faceRight, int[] beatList) {
        super(data, startX, startY, width, height, enemyScale, faceRight, beatList);
    }

    /**
     * Switches the attack state of the bear depending on the player's position.
     */
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
        animationUpdate();
    }

    /** Creates a bullet in front of the bear */
    public void makeBullet() {
        // TODO: create a bullet using object controller default values. instantiate the
        // copy using gamecontroller

        ObjectController oc = GameController.getInstance().objectController;
        float offset = oc.defaultConstants.get("bullet").getFloat("offset", 0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = (animationGenre == Genre.SYNTH ?
                (oc.bulletTexture.getRegionWidth() / (0.3f * scale.x)) :
                (oc.bulletTexture.getRegionWidth() / (0.24f * scale.x)));
        bullet = new Bullet(getX() + offset, getY(), radius,
                oc.defaultConstants.get("bullet").getFloat("synth speed", 0),
                oc.defaultConstants.get("bullet").getFloat("jazz speed", 0), isFaceRight(),
                animationGenre);

        bullet.setName(getName() + "_bullet");
        bullet.setDensity(oc.defaultConstants.get("bullet").getFloat("density", 0));
        bullet.setDrawScale(scale);
        //bullet.setTexture(oc.bulletTexture);
        bullet.setGravityScale(0);
        bullet.setAnimation(animationGenre == Genre.SYNTH ? oc.bulletSynthAnimation : oc.bulletJazzAnimation);
        shotDirection = isFaceRight();
        int beatcount;

        // Compute position and velocity
        float speed;
        if (GameController.getInstance().genre == Genre.SYNTH) {
            speed = oc.defaultConstants.get("bullet").getFloat("synth speed", 0);
            beatcount = synthBulletTime;
        } else {
            speed = oc.defaultConstants.get("bullet").getFloat("jazz speed", 0);
            beatcount = jazzBulletTime;
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

    /**
     * Updates the animation based on the physics state.
     */
    private void animationUpdate() {
        if (animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
        switch (animationGenre) {
            case SYNTH:
                setAnimation(attackSynthAnimation);
                break;
            case JAZZ:
                setAnimation(attackJazzAnimation);
                break;
        }
    }
}
