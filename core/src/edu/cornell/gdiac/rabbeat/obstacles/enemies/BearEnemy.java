package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.sync.Bullet;
import edu.cornell.gdiac.rabbeat.sync.ISynced;
import com.badlogic.gdx.graphics.g2d.Animation;

public class BearEnemy extends Enemy implements ISynced {

    /** Value for current beat that the game is on */
    private int beatCount = 0;

    /** The bullet the bear will be shooting */
    private Bullet bullet;

    /** Speed of the bullet when the game is in synth mode */
    private float synthSpeed;

    /** Speed of the bullet when the game is in jazz mode */
    private float jazzSpeed;

    /** Current genre that the game is on */
    private Genre curGenre = Genre.SYNTH;

    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();

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
        float dir = (faceRight ? 1 : -1);
        synthSpeed = data.get("max_speed").getFloat("synth") * dir;
        jazzSpeed = data.get("max_speed").getFloat("jazz") * dir;
        setAnimation(bearIdleAnimation);
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

//        float offset = ObjectController.bullet.getFloat("offset",0);
//        offset *= (isFaceRight() ? 1 : -1);
//        float radius = bullettr.getRegionWidth()/(2.0f*scale.x);
//        bullet = new BulletSync(getX()+offset, getY(), radius, bulletjv.getFloat("synth speed", 0),
//                bulletjv.getFloat("jazz speed", 0), isFaceRight());
//
//        bullet.setName("bullet");
//        bullet.setDensity(bulletjv.getFloat("density", 0));
//        bullet.setDrawScale(scale);
//        bullet.setTexture(bullettr);
//        bullet.setGravityScale(0);

        // Compute position and velocity
//        float speed;
//        if (curGenre == Genre.SYNTH){
//            speed = bulletjv.getFloat("synth speed", 0);
//        }
//        else {
//            speed = bulletjv.getFloat("jazz speed", 0);
//        }
//        speed *= (isFaceRight() ? 1 : -1);
//        bullet.setVX(speed);
//        GameController.getInstance().instantiateQueue(bullet);
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
