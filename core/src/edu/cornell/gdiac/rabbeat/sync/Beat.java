package edu.cornell.gdiac.rabbeat.sync;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.rabbeat.GameController;

public class Beat implements ISynced {
    int beatCount = 0;
    float beat = 1;

    public Interval beatInterval;

    public Array<Float> beatLatencyList = new Array<>();
    /** The amount of time that has passed between each beat*/
    public float beatDT;

    SyncController syncController;

    public Beat(SyncController syncController){
        this.syncController = syncController;
    }

    /**
     * Adds the delta in update into beatDT
     * @param dt Number of seconds since last animation frame
     */
    public void updateBeatDT(float dt){
        beatDT += dt;
    }

    @Override
    public float getBeat() {
        return beat;
    }

    @Override
    public void beatAction() {;
        beatCount++;
        if(beatCount == 9){
            beatCount = 1;
        }
        System.out.println(beatCount);
        if(GameController.getInstance().inCalibration){
            AddDelay(beatDT);
        }
    }


    void AddDelay(float dt){
        beatLatencyList.add(dt);
    }

    /** Returns the beat count*/
    public int getBeatCount(){
        return beatCount;
    }

    /** Returns the beat in string form out of 4*/
    public int getBeatFour(){
            int modCount = beatCount % 4;
            return modCount == 0 ? 4 : modCount;
    }

}
