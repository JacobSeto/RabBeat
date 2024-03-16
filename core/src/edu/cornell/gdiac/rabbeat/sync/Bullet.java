package edu.cornell.gdiac.rabbeat.sync;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.SyncedProjectile;

public class Bullet extends WheelGameObject implements ISynced {
    private int beatCount = 0;

    private float synthSpeed;
    private float jazzSpeed;



    public Bullet(float x, float y, float radius, float synthVX, float jazzVX, boolean fr) {
        super(x, y, radius);
        float dir = (fr ? 1 : -1);
        synthSpeed = synthVX * dir;
        jazzSpeed = jazzVX * dir;
    }

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

    public JsonValue getBulletJV(){
        //TODO implement this
        return null;
    }

    public TextureRegion getBulletTR(){
        //TODO  implement this
        return null;
    }

}