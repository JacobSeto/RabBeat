package edu.cornell.gdiac.physics;

import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;

abstract class SyncedPlatform extends PolygonObstacle {

    public SyncedPlatform(float[] points) {
        super(points);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        genreUpdate();
    }

    /**
     * Implement this with any updates necessary after the genre switches.
     */
    abstract void genreUpdate();
}
