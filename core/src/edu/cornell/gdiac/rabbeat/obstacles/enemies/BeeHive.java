package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class BeeHive extends Enemy implements ISynced, IGenreObject {

    public ObjectController objectController;

    ObjectController oc = GameController.getInstance().objectController;

    /** Number of beats the bee exists in synth*/
    private int synthBeeTime = 1;

    /** Number of beats the bee exists in jazz */
    private int jazzBeeTime = 1;

    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();

    /** Tells whether the hive should shoot bees to the left or right */
    private boolean shotDirection;

    public Animation<TextureRegion> beeAttackAnimation;

    /**
     * Creates a new bee hive avatar with the given physics data
     *
     * @param data                  The JsonValue storing information about the beehive
     * @param startX                The starting x position of the enemy
     * @param startY                The starting y position of the enemy
     * @param width                 The object width in physics units
     * @param height                The object width in physics units
     * @param enemyScale            The scale of the beehive
     * @param faceRight             The direction the beehive is facing in
     * @param animation             The idle animation for the beehive
     */
    public BeeHive(JsonValue data, float startX, float startY, float width, float height, float enemyScale, boolean faceRight, Animation<TextureRegion> animation, Animation<TextureRegion> beeAnimation) {
        super(data, startX, startY, width, height, enemyScale, faceRight, animation);
        beeAttackAnimation = beeAnimation;
        setAnimation(animation);
    }

    /** Creates a bee in front of the hive */
    public void makeBee(){
        //TODO: create a bullet using object controller default values.  instantiate the copy using gamecontroller

        float offset = oc.defaultConstants.get("bullet").getFloat("offset",0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = oc.bulletTexture.getRegionWidth()/(2.0f*scale.x);
        Bee bee = new Bee(getX()+offset, getY(), radius, beeAttackAnimation);

        bee.setName(getName() + "_bee");
        bee.setDensity(oc.defaultConstants.get("bullet").getFloat("density", 0));
        bee.setDrawScale(scale);
        bee.setTexture(oc.bulletTexture);
        bee.setGravityScale(0);
        shotDirection = isFaceRight();

        //Compute position and velocity
        float speed = 2.5f;
        float vSpeed;
        int beatcount;
        if (curGenre == Genre.SYNTH){
            beatcount = synthBeeTime;
            vSpeed = 4;
        }
        else {
            beatcount = jazzBeeTime;
            vSpeed = 2;
        }
        speed *= (isFaceRight() ? 1 : -1);
        bee.setVX(speed);
        bee.setVY(vSpeed);
        bee.beatCount = beatcount;
        GameController.getInstance().instantiateQueue(bee);
    }

    @Override
    public void genreUpdate(Genre genre) {
        curGenre = genre;
    }

    @Override
    public void switchState() {

    }

    @Override
    public float getBeat() {
        return 0.25f;
    }

    @Override
    public void beatAction() {
        makeBee();
        setFaceRight(playerXPosition() - getPosition().x > 0);
    }
}
