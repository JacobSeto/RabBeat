package edu.cornell.gdiac.rabbeat.obstacle.platforms;

import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacle.PolygonObstacle;

abstract public class SyncedPlatform extends PolygonObstacle {

    public SyncedPlatform(float[] points) {
        super(points);
    }

    /**
     * Implement this with any updates necessary after the genre switches.
     */
    public abstract void genreUpdate(Genre genre);
}
