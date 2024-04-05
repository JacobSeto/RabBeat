package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.Bullet;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class BeeHive extends Enemy implements ISynced, IGenreObject {

    public Genre curGenre = Genre.SYNTH;

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

    /**
     * Creates a new enemy avatar with the given physics data
     *
     * @param data
     * @param x The beehive's x coordinate
     * @param y The beehive's y coordinate
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param enemyScale
     * @param faceRight
     * @param animation
     */
    public BeeHive(JsonValue data, float x, float y, float width, float height, float enemyScale, boolean faceRight, Animation<TextureRegion> animation) {
        super(data, x, y, width, height, enemyScale, faceRight, animation);
        setAnimation(animation);
    }

    /** Creates a bee in front of the hive */
    public void makeBee(){
        //TODO: create a bullet using object controller default values.  instantiate the copy using gamecontroller

        float offset = oc.defaultConstants.get("bullet").getFloat("offset",0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = oc.bulletTexture.getRegionWidth()/(2.0f*scale.x);
        Bee bee = new Bee(getX()+offset, getY(), radius);

        bee.setName(getName() + "_bee");
        bee.setDensity(oc.defaultConstants.get("bullet").getFloat("density", 0));
        bee.setDrawScale(scale);
        bee.setTexture(oc.bulletTexture);
        bee.setGravityScale(0);
        shotDirection = isFaceRight();

        //Compute position and velocity
        float speed = 2.5f;
        int beatcount;
        if (curGenre == Genre.SYNTH){
            beatcount = synthBeeTime;
            bee.setVY(2);
        }
        else {
            beatcount = jazzBeeTime;
            bee.setVY(1);
        }
        speed *= (isFaceRight() ? 1 : -1);
        bee.setVX(speed);
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
        setFaceRight(GameController.getInstance().getPlayer().getPosition().x - getPosition().x > 0);
    }
}
