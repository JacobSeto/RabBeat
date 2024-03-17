package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.Bullet;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class BearEnemy extends Enemy implements ISynced, IGenreObject {

    /** Value for current beat that the game is on */
    private int beatCount = 0;

    /** The bullet the bear will be shooting */
    public Bullet bullet;

    /** Speed of the bullet when the game is in synth mode */
    private float synthSpeed;

    /** Speed of the bullet when the game is in jazz mode */
    private float jazzSpeed;

    /** Current genre that the game is on */
    public Genre curGenre = Genre.SYNTH;

    ObjectController oc = ObjectController.getInstance();

    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();

    /** Tells whether the bear was facing right or not when they shot */
    private boolean shotDirection;

    /**
     * Creates a new enemy avatar with the given physics data
     *
     * @param data
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param enemyScale
     * @param faceRight
     */
    public BearEnemy(JsonValue data, float width, float height, float enemyScale,
            boolean faceRight) {
        super(data, width, height, enemyScale, faceRight);
        synthSpeed = data.get("max_speed").getFloat("synth");
        jazzSpeed = data.get("max_speed").getFloat("jazz");
    }

    /**
     * Switches the attack state of the bear depending on the player's position.
     */
    public void switchState() {
        switch(enemyState) {
            case IDLE:
                if(horizontalDistanceBetweenEnemyAndPlayer()<5) {
                    enemyState = EnemyState.ATTACKING;
                    System.out.println("ATTACKING");
                }
                break;
            case ATTACKING:
                if(horizontalDistanceBetweenEnemyAndPlayer()>5) {
                    enemyState = EnemyState.IDLE;
                    System.out.println("not ATTACKING");
                }
                //TODO: make bear shoot
                break;
        }
    }

    /** Creates a bullet in front of the bear */
    public void makeBullet(){
          //TODO: create a bullet using object controller default values.  instantiate the copy using gamecontroller

        float offset = oc.constants.get("bullet").getFloat("offset",0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = oc.bulletTexture.getRegionWidth()/(2.0f*scale.x);
        bullet = new Bullet(getX()+offset, getY(), radius, oc.constants.get("bullet").getFloat("synth speed", 0),
                oc.constants.get("bullet").getFloat("jazz speed", 0), isFaceRight());

        bullet.setName(getName() + "_bullet");
        bullet.setDensity(oc.constants.get("bullet").getFloat("density", 0));
        bullet.setDrawScale(scale);
        bullet.setTexture(oc.bulletTexture);
        bullet.setGravityScale(0);
        shotDirection = isFaceRight();

        //Compute position and velocity
        float speed;
        if (curGenre == Genre.SYNTH){
            speed = oc.constants.get("bullet").getFloat("synth speed", 0);
        }
        else {
            speed = oc.constants.get("bullet").getFloat("jazz speed", 0);
        }
        speed *= (isFaceRight() ? 1 : -1);
        bullet.setVX(speed);
        System.out.println("shoot" + (int) bullet.getX() + " " +  (int) bullet.getY());
        GameController.getInstance().instantiateQueue(bullet);
    }
    public float getBeat() {
        return 1;
    }

    public void beatAction() {
        beatCount++;
        if(beatCount >= 5){
            beatCount = 1;
        }
        if (beatCount == 1 && enemyState == EnemyState.ATTACKING) {
            makeBullet();
        }
        if (beatCount == 4){
        }

        setFaceRight(GameController.getInstance().getPlayer().getPosition().x - getPosition().x > 0);
    }

    public void genreUpdate(Genre genre) {
        changeSpeed(genre);
    }

    public void changeSpeed(Genre genre) {
        switch(genre) {
            case JAZZ:
                if (bullet != null){
                    if (shotDirection) {
                        bullet.setVX(jazzSpeed);
                    } else {
                        bullet.setVX(jazzSpeed * -1);
                    }
                }
                curGenre = Genre.JAZZ;
                break;
            case SYNTH:
                if (bullet != null){
                    if (shotDirection) {
                        bullet.setVX(synthSpeed);
                    } else {
                        bullet.setVX(synthSpeed * -1);
                    }
                }
                curGenre = Genre.SYNTH;
                break;
        }
    }


}
