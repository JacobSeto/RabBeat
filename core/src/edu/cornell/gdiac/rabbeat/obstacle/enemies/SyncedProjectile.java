package edu.cornell.gdiac.rabbeat.obstacle.enemies;

import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacle.WheelObstacle;

abstract public class SyncedProjectile extends WheelObstacle {
    public SyncedProjectile(float x, float y, float radius){
        super(x, y, radius);
    }

    public abstract void genreUpdate(Genre genre);
}
