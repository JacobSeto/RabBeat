package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.CapsuleGameObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
//package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.utils.JsonValue;

/**
 * Enemy avatar for the platform game.
 */
public abstract class Enemy extends CapsuleGameObject {

    /** Enum containing the state of the enemy */
    protected enum EnemyState{
        IDLE,
        ATTACKING
    }

    /** The scale of the enemy */
    private float enemyScale;

    /** The physics shape of this object */
    private PolygonShape sensorShape;

    /** Whether the enemy is facing right */
    private boolean faceRight;

    /** Initializes the state of the enemy to idle */
    protected EnemyState enemyState = EnemyState.IDLE;

    //range: how far away player is --> beat action called whenever an action is supposed to hapepn on beat
    //create switch states (wandering, shooting, etc). ENUM


    /**
     * Creates a new enemy avatar with the given physics data
     *
     * @param width	The object width in physics units
     * @param height	The object width in physics units
     */
    public Enemy(JsonValue data, float width, float height, float enemyScale, boolean faceRight) {
        // The shrink factors fit the image to a tigher hitbox
        super(  data.get("pos").getFloat(0),
                data.get("pos").getFloat(1),
                width*data.get("shrink").getFloat( 0 ),
                height*data.get("shrink").getFloat( 1 ));

        setDensity(data.getFloat("density", 0));
        setFriction(data.getFloat("friction", 0));
        setFixedRotation(true);

        this.faceRight = faceRight; // should face the direction player is in?
        this.enemyScale = enemyScale;

        setName("enemy");
    }

    /**
     * Getter for faceRight
     */
    public boolean isFaceRight() {
        return faceRight;
    }

    /**
     * Implement this with any updates necessary after the genre switches.
     */
    public abstract void genreUpdate(Genre genre);

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     * @param dt	Number of seconds since last animation frame
     */
    public void update(float dt) {
        switchState();
        super.update(dt);
    }

    /**
     * Draws the physics object of the enemy.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        float effect = faceRight ? 1.0f : -1.0f;
        //System.out.println(drawScale.x);
        //System.out.println(drawScale.y);
        canvas.draw(texture,Color.RED,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
                enemyScale*effect,enemyScale);
    }

    /**
     * Draws the outline of the physics body.
     *
     * This method can be helpful for understanding issues with collisions.
     *
     * @param canvas Drawing context
     */
    public void drawDebug(GameCanvas canvas) {
        super.drawDebug(canvas);
        canvas.drawPhysics(sensorShape,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
    }

    /** Sets the direction that the enemy is facing in */
    public void setFaceRight(boolean faceRight) {
        this.faceRight = faceRight;
    }

    /** Returns whether the enemy is facing right */
    public boolean getFaceRight() {
        return faceRight;
    }

    /** Returns the distance between the enemy and the player */
    public float horizontalDistanceBetweenEnemyAndPlayer(){
        return Math.abs(GameController.getInstance().getPlayer().getPosition().x - getPosition().x);
    }

    /** Switches enemy attacking state depending on its current state */
    public abstract void switchState();


    /** Returns the x position of the player */
    public float playerXPosition(){
        if(GameController.getInstance() !=  null) {
            return GameController.getInstance().getPlayer().getPosition().x;
        }

        return 0;
    }


}