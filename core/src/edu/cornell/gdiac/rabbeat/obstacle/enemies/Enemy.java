package edu.cornell.gdiac.rabbeat.obstacle.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacle.CapsuleObstacle;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
//package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.utils.JsonValue;

/**
 * Enemy avatar for the platform game.
 */
public abstract class Enemy extends CapsuleObstacle {

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
        //playerScale = playerScale1;
        //maxspeed = data.get("max_speed").getFloat("synth");

//        damping = data.getFloat("damping", 0);
//        force = data.getFloat("force", 0);
//        jump_force = data.getFloat( "jump_force", 0 );
//        jumpLimit = data.getInt( "jump_cool", 0 );
//        shotLimit = data.getInt( "shot_cool", 0 );
//        sensorName = "DudeGroundSensor";
//        this.data = data;

        setName("enemy");
    }
//
//    /**
//     * Creates the physics Body(s) for this object, adding them to the world.
//     *
//     * This method overrides the base method to keep your ship from spinning.
//     *
//     * @param world Box2D world to store body
//     *
//     * @return true if object allocation succeeded
//     */
//    public boolean activatePhysics(World world) {
//        // create the box from our superclass
//        if (!super.activatePhysics(world)) {
//            return false;
//        }
//
//        // Ground Sensor
//        // -------------
//        // We only allow the dude to jump when he's on the ground.
//        // Double jumping is not allowed.
//        //
//
//        // To determine whether or not the dude is on the ground,
//        // we create a thin sensor under his feet, which reports
//        // collisions with the world but has no collision response.
//        Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
//        FixtureDef sensorDef = new FixtureDef();
//        sensorDef.density = data.getFloat("density",0);
//        sensorDef.isSensor = true;
//        sensorShape = new PolygonShape();
//        JsonValue sensorjv = data.get("sensor");
//        sensorShape.setAsBox(sensorjv.getFloat("shrink",0)*getWidth()/2.0f,
//                sensorjv.getFloat("height",0), sensorCenter, 0.0f);
//        sensorDef.shape = sensorShape;
//
//        // Ground sensor to represent our feet
//        Fixture sensorFixture = body.createFixture( sensorDef );
//        sensorFixture.setUserData(getSensorName());
//
//        return true;
//    }

    public Bullet bulletMaker(JsonValue bulletjv, TextureRegion bullettr, Vector2 scale, Genre genre){
        float offset = bulletjv.getFloat("offset",0);
        float radius = bullettr.getRegionWidth()/(2.0f*scale.x);
        Bullet bullet = new Bullet(getX()+offset, getY(), radius, bulletjv.getFloat("synth speed", 0), bulletjv.getFloat("jazz speed", 0));

        bullet.setName("bullet");
        bullet.setDensity(bulletjv.getFloat("density", 0));
        bullet.setDrawScale(scale);
        bullet.setTexture(bullettr);
        bullet.setGravityScale(0);

        // Compute position and velocity
        float speed;
        if (genre == Genre.SYNTH){
            speed = bulletjv.getFloat("synth speed", 0);
        }
        else {
            speed = bulletjv.getFloat("jazz speed", 0);
        }
        bullet.setVX(speed);
        return bullet;
    }

    /**
     * Updates the object's physics state (NOT GAME LOGIC).
     *
     * We use this method to reset cooldowns.
     *
     * @param dt	Number of seconds since last animation frame
     */
    public void update(float dt) {
        // shoot bullets here?
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
        return Math.abs(GameController.getPlayer().getPosition().x - getPosition().x);
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