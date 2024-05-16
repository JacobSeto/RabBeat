package edu.cornell.gdiac.rabbeat.objects.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.objects.projectiles.Bee;

public class BeeHive extends Enemy {

    ObjectController oc = GameController.getInstance().objectController;

    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();

    private float beeBeat;


    /** The idle animation for the beehive */
    public Animation<TextureRegion> idleAnimation;

    /** The attack animation for the bee */
    public Animation<TextureRegion> beeSynthAnimation;
    /** The attack animation for the bee */
    public Animation<TextureRegion> beeJazzAnimation;
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
            boolean faceRight, int[] beatList, float beet, Genre genre) {
        super(data, startX, startY, width, height, enemyScale, faceRight, beatList, genre);
        enemyState = EnemyState.ATTACKING;
        isFlippable = false;
        beeBeat = beet;
    }

    @Override
    public void switchState() {

    }

    /** Creates a bee in front of the hive */
    public void makeBee() {
        // TODO: create a bullet using object controller default values. instantiate the
        // copy using gamecontroller

        float offset = oc.defaultConstants.get("bullet").getFloat("offset", 0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = oc.beeTexture.getRegionWidth() / (5.0f * scale.x);
        Animation<TextureRegion> beeAnimation;
        if (genre == Genre.SYNTH){
            beeAnimation = beeSynthAnimation;
        }
        else{
            beeAnimation = beeJazzAnimation;
        }
        Bee bee = new Bee(getX() + offset, getY(), radius, genre, isFaceRight(), beeAnimation, beeBeat);

        bee.setName(getName() + "_bee");
        bee.setDensity(oc.defaultConstants.get("bullet").getFloat("density", 0));
        bee.setDrawScale(scale);

        bee.setSensor(true);
        bee.setTexture(oc.beeTexture);
        bee.setGravityScale(0);

        // Compute position and velocity
        float speed = 2.5f;
        float vSpeed = -4;
        speed *= (isFaceRight() ? 1 : -1);
        bee.setVX(speed);
        bee.setVY(vSpeed);
        GameController.getInstance().instantiateQueue(bee);
    }

    @Override
    public void Attack() {
        makeBee();
    }
}
