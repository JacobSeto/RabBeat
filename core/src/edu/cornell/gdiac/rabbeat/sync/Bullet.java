package edu.cornell.gdiac.rabbeat.sync;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.SyncedProjectile;
/**
 * Bullets shoot out and disappear on certain beats.
 */
public class Bullet extends WheelGameObject implements ISynced {
    public int beatCount = 0;

    public Bullet(float x, float y, float radius, float synthVX, float jazzVX, boolean fr) {
        super(x, y, radius);
        float dir = (fr ? 1 : -1);
    }

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void beatAction() {
        beatCount--;
        if (beatCount <= 0) {
            markRemoved(true);
        }
    }

}