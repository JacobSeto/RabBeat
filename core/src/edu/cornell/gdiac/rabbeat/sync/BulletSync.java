package edu.cornell.gdiac.rabbeat.sync;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacle.WheelObstacle;
import edu.cornell.gdiac.rabbeat.obstacle.enemies.SyncedProjectile;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class BulletSync extends SyncedProjectile implements ISynced {
    private int beatCount = 0;

    private float synthSpeed;
    private float jazzSpeed;



    public BulletSync(float x, float y, float radius, float synthVX, float jazzVX, boolean fr) {
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

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void beatAction() {
        beatCount++;
        if(beatCount >= 5){
            beatCount = 1;
        }
        if (beatCount == 4) {
            markRemoved(true);
        }
    }

}