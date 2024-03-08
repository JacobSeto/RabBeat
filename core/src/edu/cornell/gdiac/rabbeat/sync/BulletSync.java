package edu.cornell.gdiac.sync;

import edu.cornell.gdiac.sync.ISynced;

public class BulletSync implements ISynced {
    private int beatCount = 0;
    private boolean isBeatOne = false;

    public boolean getIsBeatOne() {
        return isBeatOne;
    }
    @Override
    public int getBeat() {
        return 1;
    }

    @Override
    public void Beat() {
        beatCount++;
        if(beatCount >= 5){
            beatCount = 1;
        }
        if (beatCount == 1) {
            isBeatOne = true;
        }
        else {
            isBeatOne = false;
        }
    }

}