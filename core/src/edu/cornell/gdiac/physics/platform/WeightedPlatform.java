package edu.cornell.gdiac.physics.platform;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.Genre;
import edu.cornell.gdiac.physics.SyncedPlatform;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */

public class WeightedPlatform extends SyncedPlatform {
    /** Position for the weighted platform when the game is in Synth mode **/
    private Vector2 synthPosition;

    /** Position for the weighted platform when the game is in Jazz mode **/
    private Vector2 jazzPosition;

    /**
     * Creates a new weighted platform with the given physics data and current genre.
     *
     * @param points  	The polygon vertices
     * @param synthPos  The array with index 0 and 1 holding the x and y coordinates for the platform's position in
     *                  jazz mode
     * @param jazzPos	The array with index 0 and 1 holding the x and y coordinates for the platform's position in jazz
     *                  mode
     */
    public WeightedPlatform(float[] points, float[] synthPos, float[] jazzPos){
        super(points);
        jazzPosition = new Vector2(jazzPos[0], jazzPos[1]);
        synthPosition = new Vector2(synthPos[0], synthPos[1]);
        setPosition(synthPosition);
    }

    @Override
    public void genreUpdate(Genre genre) {
        move(genre);
    }

    /**
     * Sets the weighted platform's position based on the given genre.
     * @param genre     The genre that the game is currently in
     */
    public void move(Genre genre) {
        switch(genre) {
            case JAZZ:
                setPosition(jazzPosition);
                break;
            case SYNTH:
                setPosition(synthPosition);
                break;
        }
    }
}