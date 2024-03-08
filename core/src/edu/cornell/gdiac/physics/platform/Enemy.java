package edu.cornell.gdiac.physics.platform;

import edu.cornell.gdiac.physics.obstacle.CapsuleObstacle;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * Enemy avatar for the platform game.
 */
public class Enemy extends CapsuleObstacle {

    /** The scale of the enemy */
    private float enemyScale;

    /** The physics shape of this object */
    private PolygonShape sensorShape;

    /** Whether the enemy is facing right */
    private boolean faceRight;

    /**
     * Creates a new enemy avatar with the given physics data
     *
     * @param width	The object width in physics units
     * @param height	The object width in physics units
     */
    public Enemy(JsonValue data, float width, float height, float enemyScale) {
        // The shrink factors fit the image to a tigher hitbox
        super(  data.get("pos").getFloat(0),
                data.get("pos").getFloat(1),
                width*data.get("shrink").getFloat( 0 ),
                height*data.get("shrink").getFloat( 1 ));

        setDensity(data.getFloat("density", 0));
        setFriction(data.getFloat("friction", 0));
        setFixedRotation(true);

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
        canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
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
}