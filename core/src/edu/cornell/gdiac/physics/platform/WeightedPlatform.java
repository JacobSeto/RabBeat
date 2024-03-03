package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.Genre;
import edu.cornell.gdiac.physics.obstacle.SimpleObstacle;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */
public class WeightedPlatform extends BoxObstacle {
    /** The initializing data (to avoid magic numbers) */
    private final JsonValue data;
    /** Position for the weighted platform when the game is in Jazz mode **/
    private Vector2 jazzPosition;
    /** Position for the weighted platform when the game is in Synth mode **/
    private Vector2 synthPosition;

    /**
     * Creates a new weighted platform with the given physics data and current genre.
     *
     * @param data  	The physics constants for this weighted platform
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     * @param genre     The current genre the game is in
     */
    public WeightedPlatform(JsonValue data, float width, float height, Genre genre){
        //  calls BoxObstacle's (the parent) constructor
        super(width, height);
        this.data = data;
        jazzPosition = new Vector2(data.get("jazzPos").getFloat(0), data.get("jazzPos").getFloat(1));
        synthPosition = new Vector2(data.get("synthPos").getFloat(0), data.get("synthPos").getFloat(1));
        move(genre);
    }

    /**
     * Sets the weighted platform's position based on the given genre.
     * @param genre     The genre that the game is currently in
     */
    public void move(Genre genre) {
        switch(genre) {
            case JAZZ:
                setPosition(jazzPosition.x, jazzPosition.y);
                break;
            case SYNTH:
                setPosition(synthPosition.x, synthPosition.y);
                break;
        }
    }
}