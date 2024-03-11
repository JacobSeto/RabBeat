package edu.cornell.gdiac.rabbeat.obstacle.enemies;

import com.badlogic.gdx.graphics.Color;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacle.WheelObstacle;

public class Bullet extends SyncedProjectile {
    private float synthSpeed;
    private float jazzSpeed;



    public Bullet(float x, float y, float radius, float synthVX, float jazzVX, boolean fr) {
        super(x, y, radius);
        float dir = (fr ? 1 : -1);
        synthSpeed = synthVX * dir;
        jazzSpeed = jazzVX * dir;
    }

    @Override
    public void genreUpdate(Genre genre) {
        move(genre);
    }

    public void move(Genre genre) {
        switch(genre) {
            case JAZZ:
                setVX(jazzSpeed);
                break;
            case SYNTH:
                setVX(synthSpeed);
                break;
        }
    }

}
