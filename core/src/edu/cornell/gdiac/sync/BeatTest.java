package edu.cornell.gdiac.sync;

public class BeatTest implements ISynced {

    int beatCount = 0;
    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void Beat() {
        beatCount++;
        beatCount= beatCount % 5;
        System.out.println(beatCount);
    }
}
