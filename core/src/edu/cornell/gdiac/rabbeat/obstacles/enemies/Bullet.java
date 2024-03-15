package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import edu.cornell.gdiac.rabbeat.Genre;

public class Bullet extends SyncedProjectile {
    private float synthSpeed;
    private float jazzSpeed;


    public Bullet(float x, float y, float radius, float synthVX, float jazzVX) {
        super(x, y, radius);
        synthSpeed = synthVX;
        jazzSpeed = jazzVX;
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
