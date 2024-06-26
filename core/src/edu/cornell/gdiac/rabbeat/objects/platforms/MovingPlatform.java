package edu.cornell.gdiac.rabbeat.objects.platforms;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.objects.BoxGameObject;
import edu.cornell.gdiac.rabbeat.objects.IGenreObject;
import edu.cornell.gdiac.rabbeat.objects.Type;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */

public class MovingPlatform extends BoxGameObject implements IGenreObject, ISynced {
    /** Position for the weighted platform when the game is in Synth mode **/
    private Vector2[] positionNodes;
    /** How many 4 beat intervals the platform waits at each node**/
    private int beatWait;
    /**How long it takes the platform to move from node to node, changing the */
    private  int moveTime = 1;
    /** the direction the platform is moving in*/
    private Vector2 velocity;
    /**The next platform in positionNodes that the platform is moving to*/
    private int destination;
    /** The last platform the platform moved from*/
    private int home;
    /** Whether or not the platform is moving**/
    boolean moving;

    /**The distance the platform should 'lock on' to its destination */
    private float LOCKDIST = 0.05f;
    /**Keeps track of the which beat the platform is on*/
    private int beat = 0;
    /**How fast the platform should be currently moving*/
    private float currentSpeed=1.0f;
    /**Stores the BPM of the current level*/
    private int BPM;
    /**CrushSpeed*/
    private boolean crushSpeed;

    /**Determines the speed of the platform on each frame */
    private float SPEEDBEAT6 = (float) 3*1 /10;
    private float SPEEDBEAT7 = (float) 2*3 /10;
    private float SPEEDBEAT8 = (float) 2*6 /10;
    private BoxGameObject crusher;

    /**
     * Creates a new moving platform with the given physics data and current genre.
     *
     * @param width The width of the moving platform
     * @param height The height of the moving platform
     * @param nodes The points where the platform goes to, must be of even length
     * @param beatWaitTime The number of 4 beat intervals the platform waits at each position
     * @param texture The texture region for the platform
     * @param crushSpeedEnable Whether the platform should crush the player when it goes quickly
     */
    public MovingPlatform(float width, float height, Vector2[] nodes, int beatWaitTime, int beatMoveTime,
                          TextureRegion texture, BoxGameObject crushBody, boolean crushSpeedEnable) {
        super(nodes[0].x, nodes[0].y, width, height);
        beatWait = beatWaitTime;
        positionNodes = nodes;
        destination = 1;
        home = 0;
        setTexture(texture);
        setPosition(positionNodes[0]);
        velocity = direction(positionNodes[home], positionNodes[destination], 2);
        moving = true;
        moveTime = (int) Math.pow(2, beatMoveTime);
        BPM = GameController.getInstance().getBPM();

        crusher = crushBody;
        crushSpeed = crushSpeedEnable;
        //setType(Type.LETHAL);
    }

    /** updates the platform to determine what direction it should be moving in */
    public void update(float delta){
        if(moving){
            /**If it is predicted we reach the next platform in the current frame, teleport to it and recalculte velocity.*/
            if (solveDelta(currentSpeed)-delta <LOCKDIST){
                setPosition(positionNodes[destination].x,positionNodes[destination].y );
                crusher.setPosition(positionNodes[destination].x, positionNodes[destination].y);

                home = destination;
                if (destination == positionNodes.length-1){
                    destination = 0;
                }
                else{
                    destination+=1;
                }
                velocity = direction(positionNodes[home], positionNodes[destination], 2);
                moving = false;
                currentSpeed = 0;
                setType(Type.NONE);
            }
            else{
                move(delta);
            }
            if (currentSpeed>14 && crushSpeed){
                setType(Type.LETHAL);
            }
        }
    }
    /** Moves the platforms, and sets it into place if it is close enough to its destination**/
    public void move(float delta){
        setPosition(getPosition().x + velocity.x*delta*-1*currentSpeed, getPosition().y + velocity.y*delta*-1*currentSpeed);
        crusher.setPosition(crusher.getPosition().x + velocity.x*delta*-1*currentSpeed,
                crusher.getPosition().y + velocity.y*delta*-1*currentSpeed);
    }
    /**Calculates the time it takes to reach the next platform given the current speed*/
    public float solveDelta(float velocity){
        return magnitude(getPosition(), positionNodes[destination])/velocity;
    }
    @Override
    public void genreUpdate(Genre genre) {
        move(genre);
    }
    /**
     * Sets the weighted platform's position based on the given genre.
     * @param genre     The genre that the game is currently in
     */
    public void move(Genre genre) {
        switch(genre) {
            case JAZZ:
                break;
            case SYNTH:
                break;
        }
    }
    /**Determines the distance between two vectors */
    private float magnitude(Vector2 pos1, Vector2 pos2){
        double magnitude = Math.sqrt(Math.pow((pos1.x - pos2.x),2)+
                Math.pow((pos1.y-pos2.y),2));
        return (float) magnitude;
    }
    /**Determines the normalized direction vector bewteen two vectors, with a float multiplier*/
    private Vector2 direction(Vector2 pos1, Vector2 pos2, float speed){
        float magnitude = magnitude(pos1, pos2);

        return new Vector2((pos1.x - pos2.x)*speed/magnitude,
                (pos1.y-pos2.y)*speed/magnitude);
    }
    /**Returns the current velocity of the platform */
    public Vector2 currentVelocity(){
        if (!moving){
            return new Vector2(0,0);
        }
        else{
            return new Vector2(velocity.x*-1*currentSpeed, velocity.y*-1*currentSpeed);
        }
    }
    /**iMPLEMENTS THE syncing for the platforms */
    @Override
    public float getBeat() {
        /** 4 pulses every quarter note*/
        return  ((float) 4/moveTime);
    }

    @Override
    public void beatAction() {
        /**Renable moving after reaching destination and incredments beat, as well as resetting the speed*/
        moving = true;

        float BeatLength = (float) (60*moveTime) /BPM;
        beat+= 1;
        if (beat==1){
            currentSpeed = 0;
        }
        else if (beat==(2+4* beatWait)){
            currentSpeed = (magnitude(positionNodes[home], positionNodes[destination])*(1/BeatLength)*SPEEDBEAT6);
        }
        else if (beat== (3+4* beatWait)){
            currentSpeed = (magnitude(positionNodes[home], positionNodes[destination])*(1/BeatLength)*SPEEDBEAT7);
        }
        else if (beat == (4+4* beatWait) ){
            currentSpeed = (magnitude(positionNodes[home], positionNodes[destination])*(1/BeatLength)*SPEEDBEAT8);
            beat = 0;
        }
    }
}