package edu.cornell.gdiac.physics.platform;

import edu.cornell.gdiac.physics.obstacle.CapsuleObstacle;
//package edu.cornell.gdiac.physics.platform;

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
}