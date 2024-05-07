package edu.cornell.gdiac.rabbeat.sync;

import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.rabbeat.GameController;

public class Beat implements ISynced {

    int beatCount = 0;
    float beat = 1;

    public Array<Float> beatLatencyList = new Array<>();
    /** The amount of time that has passed between each beat*/
    float beatDT;

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
    public void beatAction() {
        beatCount++;
        if(beatCount >= 9){
            beatCount = 1;
        }
        if(GameController.getInstance().inCalibration){
            AddDelay(beatDT);
            beatDT = 0;
        }
    }


    void AddDelay(float dt){
        beatLatencyList.add(dt);
    }

    /** Returns the beat count*/
    public int getBeatCount(){
        return beatCount;
    }

}
