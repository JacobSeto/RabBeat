package edu.cornell.gdiac.rabbeat.objects.platforms;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.objects.BoxGameObject;
import edu.cornell.gdiac.rabbeat.objects.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */

public class WeightedPlatform extends BoxGameObject implements IGenreObject, ISynced {
    /** Position for the weighted platform when the game is in Synth mode **/
    private Vector2 synthPosition;

    /** Position for the weighted platform when the game is in Jazz mode **/
    private Vector2 jazzPosition;
    /** Texture of the weighted platform in synth mode **/
    private TextureRegion synthTexture;
    /** Texture of the weighted platform in jazz mode **/
    private TextureRegion jazzTexture;
    private int currentGenre;

    /** Whether or not the platform is moving**/
    boolean moving;
    private Vector2 velocity;

    /**The distance the platform should 'lock on' to its destination */
    private float LOCKDIST = 0.01f;
/**The current speed  of the platform*/
    private float currentSpeed = 0;
/** The current beat we are on*/
    private int beat = 0;
/** The internal BPM*/
    private int BPM = 180;
    /**two times the amount of intervals that the platform has to wait before it moves*/
    private int platformIntervals = 0;
    /**Determines how quickly the platform moves between each 'node'. This divides the default sixteenth notes movement by powers of 2. */
    private int moveTime;
    /**The number of 4 beat intervals the platform waits at each 'node'. Note that weighted platforms don't have nodes like moving platforms,
     * so the 'nodes' are defined by platformbeatintervals. */
    private int waitTime;

    /**Determines the speed of the platform on each frame */
    private float SPEEDBEAT6 = (float) 3*1 /10;
    private float SPEEDBEAT7 = (float) 2*3 /10;
    private float SPEEDBEAT8 = (float) 2*6 /10;
    /**
     * Creates a new weighted platform with the given physics data and current genre.
     *
     * @param width     The weighted platform's width.
     * @param height    The weighted platform's height.
     * @param synthPos  The array with index 0 and 1 holding the x and y coordinates for the platform's position in
     *                  synth mode
     * @param jazzPos	The array with index 0 and 1 holding the x and y coordinates for the platform's position in jazz
     *                  mode
     * @param platformInterval  Twice the amount of 8- beat intervals the platform has to move before it reaches its final destination-Ex. If beatIntervals were 2, the platform would take 32 sixteenth notes to move to the final position
     *
     * @param synthTexture The weighted platform's texture region used in synth mode.
     * @param jazzTexture The weighted platform's texture region used in jazz mode.
     */
    public WeightedPlatform(float width, float height, float[] synthPos, float[] jazzPos, int platformInterval, int beatWaitTime,int beatMoveTime,
                            TextureRegion synthTexture, TextureRegion jazzTexture) {
        super(synthPos[0], synthPos[1], width, height);

        jazzPosition = new Vector2(jazzPos[0], jazzPos[1]);
        synthPosition = new Vector2(synthPos[0], synthPos[1]);
        this.synthTexture = synthTexture;
        this.jazzTexture = jazzTexture;
        setTexture(synthTexture);
        setPosition(synthPosition);
        moving = false;
        float magnitude1 = magnitude(jazzPosition, synthPosition);
        velocity = new Vector2((jazzPosition.x - synthPosition.x)/magnitude1,
                (jazzPosition.y-synthPosition.y)/magnitude1);
        currentGenre = 0;
        platformIntervals = platformInterval-1;
        moveTime = (int) Math.pow(2, beatMoveTime);
        waitTime = beatWaitTime;
        BPM = GameController.getInstance().getBPM();
    }
    /** */
    public Vector2 currentVelocity(){
        if (!moving){
            return new Vector2(0,0);
        }
        if (currentGenre==0){
            return new Vector2(velocity.x *-1*currentSpeed , velocity.y*-1*currentSpeed);
        }
        else{
            return new Vector2(velocity.x *currentSpeed, velocity.y*currentSpeed );
        }
    }

    /** updates the platform to determine what direction it should be moving in */
    public void update(float delta){
        if(moving){
            if (currentGenre == 0){
                move(delta, synthPosition, -1*currentSpeed);
            }
            else{
                move(delta, jazzPosition, 1*currentSpeed);
            }
        }
    }
    /** Moves the platforms, and sets it into place if it is close enough to its destination**/
    public void move(float delta, Vector2 destination, float speed){
        if (solveDelta(currentSpeed, destination)-delta <LOCKDIST){
            setPosition(destination.x,destination.y);
            moving = false;
            currentSpeed = 0;
        }
        else{
            setPosition(getPosition().x + velocity.x*delta*speed, getPosition().y + velocity.y*delta*speed);
        }
    }
    /**Determines the distance between two vectors */
    public float magnitude(Vector2 pos1, Vector2 pos2){
        double magnitude = Math.sqrt(Math.pow((pos1.x - pos2.x),2)+
                Math.pow((pos1.y-pos2.y),2));
        return (float) magnitude;
    }
    /**Determines the normalized direction vector bewteen two vectors, with a a float multiplier*/
    public Vector2 direction(Vector2 pos1, Vector2 pos2, float speed){
        float magnitude = magnitude(pos1, pos2);
        return new Vector2((pos1.x - pos2.x)*speed/magnitude,
                (pos1.y-pos2.y)*speed/magnitude);
    }
    /**Calculates the time it takes to reach the next platform given the current speed*/
    public float solveDelta(float velocity, Vector2 destination){
        return magnitude(getPosition(), destination)/velocity;
    }
    @Override
    public float getBeat() {
        /** 4 pulses every quarter note*/
        return ((float) 4/ moveTime);
    }

    @Override
    public void beatAction() {
        /**Renable moving after reaching destination and incredments beat, as well as resetting the speed*/
        float BeatLength = (float) (60* moveTime) /BPM;
        beat+= 1;
        if (beat==(1)){
            currentSpeed = 0;
        }
        else if (beat==(2+4* waitTime)){
            if (moving){
                currentSpeed = (magnitude(jazzPosition, synthPosition)*(1/BeatLength)*SPEEDBEAT6*((float) 1 /(1+ (float) platformIntervals /2)));
            }
        }
        else if (beat==(3+4* waitTime) && currentSpeed>0){
            currentSpeed = (magnitude(jazzPosition, synthPosition)*(1/BeatLength)*SPEEDBEAT7*((float) 1 /(1+ (float) platformIntervals /2)));
        }
        else if (beat == (4+4* waitTime)){
            if (currentSpeed>0){
                currentSpeed = (magnitude(jazzPosition, synthPosition)*(1/BeatLength)*SPEEDBEAT8*((float) 1 /(1+ (float) platformIntervals /2)));
            }
            beat = 0;
        }
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
                //setPosition(jazzPosition);
                setTexture(jazzTexture);
                moving = true;
                currentGenre = 1;
                break;
            case SYNTH:
                //setPosition(synthPosition);
                setTexture(synthTexture);
                moving = true;
                currentGenre = 0;
                break;
        }
    }
}