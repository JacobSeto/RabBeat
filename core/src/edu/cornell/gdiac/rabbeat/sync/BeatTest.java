package edu.cornell.gdiac.rabbeat.sync;

public class BeatTest implements ISynced {

    int beatCount = 0;
    float beat = 1;
    @Override
    public float getBeat() {
        return beat;
    }

    @Override
    public void beatAction() {
        beatCount++;
        if(beatCount >= 5){
            beatCount = 1;
        }
    }
}
