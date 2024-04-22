package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.obstacles.projectiles.BeeProjectile;
import java.util.ArrayList;

public class BeeHive extends Enemy {

    ObjectController oc = GameController.getInstance().objectController;

    /** Number of beats the bee exists in synth */
    private int synthBeeTime = 1;

    /** Number of beats the bee exists in jazz */
    private int jazzBeeTime = 1;

    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();

    /** Tells whether the hive should shoot bees to the left or right */
    private boolean shotDirection;

    /** The attack animation for the bee */
    public Animation<TextureRegion> attackAnimation;
    /** The attack animation in synth for the bee */
    public Animation<TextureRegion> beeAttackSynthAnimation;
    /**
     * Creates a new bee hive avatar with the given physics data
     *
     * @param data       The JsonValue storing information about the beehive
     * @param startX     The starting x position of the enemy
     * @param startY     The starting y position of the enemy
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param enemyScale The scale of the beehive
     * @param faceRight  The direction the beehive is facing in
     * @param beatList   The list of beats that the enemy reacts to
     */
    public BeeHive(JsonValue data, float startX, float startY, float width, float height, float enemyScale,
            boolean faceRight, int[] beatList) {
        super(data, startX, startY, width, height, enemyScale, faceRight, beatList);
        enemyState = EnemyState.ATTACKING;
        isFlippable = false;
    }

    /** Creates a bee in front of the hive */
    public void makeBee() {
        // TODO: create a bullet using object controller default values. instantiate the
        // copy using gamecontroller
        
        float offset = oc.defaultConstants.get("bullet").getFloat("offset", 0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = oc.bulletTexture.getRegionWidth() / (2.0f * scale.x);
        BeeProjectile bee = new BeeProjectile(getX() + offset, getY(), radius, beeAttackSynthAnimation);

        bee.setName(getName() + "_bee");
        bee.setDensity(oc.defaultConstants.get("bullet").getFloat("density", 0));
        bee.setDrawScale(scale);
        bee.setTexture(oc.bulletTexture);
        bee.setGravityScale(0);
        shotDirection = isFaceRight();

        // Compute position and velocity
        float speed = 2.5f;
        int beatcount;
        if (GameController.getInstance().genre == Genre.SYNTH) {
            beatcount = synthBeeTime;
            bee.setVY(2);
        } else {
            beatcount = jazzBeeTime;
            bee.setVY(1);
        }
        speed *= (isFaceRight() ? 1 : -1);
        bee.setVX(speed);
        bee.beatCount = beatcount;
        GameController.getInstance().instantiateQueue(bee);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        animationUpdate();
    }

    @Override
    public void switchState() {

    }

    @Override
    public void Attack() {
        makeBee();
        setFaceRight(playerXPosition() - getPosition().x > 0);
    }

    /**
     * Updates the animation based on the physics state.
     */
    private void animationUpdate() {
        setAnimation(attackAnimation);
        if (animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
    }
}
