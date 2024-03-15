package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;

abstract public class SyncedProjectile extends WheelGameObject {
    public SyncedProjectile(float x, float y, float radius){
        super(x, y, radius);
    }

    public abstract void genreUpdate(Genre genre);
}
