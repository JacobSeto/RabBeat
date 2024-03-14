package edu.cornell.gdiac.rabbeat.obstacle.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.WorldController;
import edu.cornell.gdiac.rabbeat.sync.BulletSync;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class BearEnemy extends Enemy implements ISynced {

    /** Value for current beat that the game is on */
    private int beatCount = 0;

    /** The bullet the bear will be shooting */
    private BulletSync bullet;

    /** Speed of the bullet when the game is in synth mode */
    private float synthSpeed;

    /** Speed of the bullet when the game is in jazz mode */
    private float jazzSpeed;

    /** Current genre that the game is on */
    private Genre curGenre = Genre.SYNTH;

    /** JsonValue for bullet */
    private JsonValue bulletjv =  WorldController.getInstance().getBulletJV();

    /** Texture for bullet */
    private TextureRegion bullettr = WorldController.getInstance().getBulletTR();

    /** Scale of the world */
    private Vector2 scale = WorldController.getInstance().getScale();

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
            boolean faceRight, float synthVX, float jazzVX) {
        super(data, width, height, enemyScale, faceRight);
        float dir = (faceRight ? 1 : -1);
        synthSpeed = synthVX * dir;
        jazzSpeed = jazzVX * dir;
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
        float offset = bulletjv.getFloat("offset",0);
        offset *= (isFaceRight() ? 1 : -1);
        float radius = bullettr.getRegionWidth()/(2.0f*scale.x);
        bullet = new BulletSync(getX()+offset, getY(), radius, bulletjv.getFloat("synth speed", 0),
                bulletjv.getFloat("jazz speed", 0), isFaceRight());

        bullet.setName("bullet");
        bullet.setDensity(bulletjv.getFloat("density", 0));
        bullet.setDrawScale(scale);
        bullet.setTexture(bullettr);
        bullet.setGravityScale(0);

        // Compute position and velocity
        float speed;
        if (curGenre == Genre.SYNTH){
            speed = bulletjv.getFloat("synth speed", 0);
        }
        else {
            speed = bulletjv.getFloat("jazz speed", 0);
        }
        speed *= (isFaceRight() ? 1 : -1);
        bullet.setVX(speed);
        WorldController.getInstance().instantiate(bullet);
    }
    public float getBeat() {
        return 1;
    }

    @Override
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
    }

    @Override
    public void genreUpdate(Genre genre) {
        changeSpeed(genre);
    }

    public void changeSpeed(Genre genre) {
        switch(genre) {
            case JAZZ:
                if (bullet != null){
                    bullet.setVX(jazzSpeed);
                }
                curGenre = Genre.JAZZ;
                break;
            case SYNTH:
                if (bullet != null){
                    bullet.setVX(synthSpeed);
                }
                curGenre = Genre.SYNTH;
                break;
        }
    }
}
