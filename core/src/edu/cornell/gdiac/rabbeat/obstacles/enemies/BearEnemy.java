package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.Bullet;
import edu.cornell.gdiac.rabbeat.sync.ISynced;
import com.badlogic.gdx.graphics.g2d.Animation;

public class BearEnemy extends Enemy implements ISynced, IGenreObject {

    private float synthBeat = 1;
    private float jazzBeat = .5f;
    private float beat = .25f;

    /** The bullet the bear will be shooting */
    public Bullet bullet;

    /** Number of beats the bullet exists in synth*/
    private int synthBulletTime = 3;

    /** Number of beats the bullet exists in jazz */
    private int jazzBulletTime = 8;

    /** Current genre that the game is on */
    public Genre curGenre = Genre.SYNTH;

    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();

    /** Tells whether the bear was facing right or not when they shot */
    private boolean shotDirection;

    /** The idle animation for the bear */
    public Animation<TextureRegion> bearIdleAnimation;

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
            boolean faceRight, Animation<TextureRegion> bearIdleAnimation) {
        super(data, width, height, enemyScale, faceRight, bearIdleAnimation);
        setAnimation(bearIdleAnimation);
    }

    /**
     * Switches the attack state of the bear depending on the player's position.
     */
    public void switchState() {
        switch(enemyState) {
            case IDLE:
                if(horizontalDistanceBetweenEnemyAndPlayer()<8) {
                    enemyState = EnemyState.ATTACKING;
                    System.out.println("ATTACKING");
                }
                break;
            case ATTACKING:
                if(horizontalDistanceBetweenEnemyAndPlayer()>8) {
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

        ObjectController oc = GameController.getInstance().objectController;
        float offset =  oc.defaultConstants.get("bullet").getFloat("offset",0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = oc.bulletTexture.getRegionWidth()/(2.0f*scale.x);
        bullet = new Bullet(getX()+offset, getY(), radius, oc.defaultConstants.get("bullet").getFloat("synth speed", 0),
                oc.defaultConstants.get("bullet").getFloat("jazz speed", 0), isFaceRight());

        bullet.setName(getName() + "_bullet");
        bullet.setDensity(oc.defaultConstants.get("bullet").getFloat("density", 0));
        bullet.setDrawScale(scale);
        bullet.setTexture(oc.bulletTexture);
        bullet.setGravityScale(0);
        shotDirection = isFaceRight();

        //Compute position and velocity
        float speed;
        int beatcount;
        if (curGenre == Genre.SYNTH){
            speed = oc.defaultConstants.get("bullet").getFloat("synth speed", 0);
            beatcount = synthBulletTime;
        }
        else {
            speed = oc.defaultConstants.get("bullet").getFloat("jazz speed", 0);
            beatcount = jazzBulletTime;
        }
        speed *= (isFaceRight() ? 1 : -1);
        bullet.setVX(speed);
        bullet.beatCount = beatcount;
        GameController.getInstance().instantiateQueue(bullet);
    }
    public float getBeat() {
        return beat;
    }

    public void beatAction() {
        if (enemyState == EnemyState.ATTACKING) {
            makeBullet();
            System.out.println("shoot");
        }
        setFaceRight(GameController.getInstance().getPlayer().getPosition().x - getPosition().x > 0);
    }

    public void genreUpdate(Genre genre) {
        curGenre = genre;
    }


}
