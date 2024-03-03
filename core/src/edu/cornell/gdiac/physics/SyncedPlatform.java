package edu.cornell.gdiac.physics;

import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;

abstract public class SyncedPlatform extends PolygonObstacle {

    public SyncedPlatform(float[] points) {
        super(points);
    }

    /**
     * Implement this with any updates necessary after the genre switches.
     */
    public abstract void genreUpdate(Genre genre);
}
