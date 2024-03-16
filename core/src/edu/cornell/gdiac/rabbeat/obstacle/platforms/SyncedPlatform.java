package edu.cornell.gdiac.rabbeat.obstacle.platforms;

import com.badlogic.gdx.math.Vector2;
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
    public float magnitude(Vector2 pos1, Vector2 pos2){
        double magnitude = Math.sqrt(Math.pow((pos1.x - pos2.x),2)+
                Math.pow((pos1.y-pos2.y),2));
        return (float) magnitude;
    }

    public Vector2 direction(Vector2 pos1, Vector2 pos2, float speed){
        float magnitude = magnitude(pos1, pos2);

        return new Vector2((pos1.x - pos2.x)*speed/magnitude,
                (pos1.y-pos2.y)*speed/magnitude);
    }
}
