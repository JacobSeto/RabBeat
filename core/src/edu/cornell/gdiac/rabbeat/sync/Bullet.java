package edu.cornell.gdiac.rabbeat.sync;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;

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