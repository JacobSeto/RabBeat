package edu.cornell.gdiac.rabbeat.obstacles.platforms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.BoxGameObject;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.obstacles.PolygonGameObject;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */

public class WeightedPlatform extends BoxGameObject implements IGenreObject {
    /** Position for the weighted platform when the game is in Synth mode **/
    private Vector2 synthPosition;

    /** Position for the weighted platform when the game is in Jazz mode **/
    private Vector2 jazzPosition;
    private TextureRegion synthTexture;
    private TextureRegion jazzTexture;
    private int currentGenre;
    /** The speed at which the platform moves at**/
    private float platformSpeed;
    /** Whether or not the platform is moving**/
    boolean moving;
    private Vector2 velocity;

    /**The distance the platform should 'lock on' to its destination */
    private float LOCKDIST = 0.1f;
    /**
     * Creates a new weighted platform with the given physics data and current genre.
     *
     * @param width     The weighted platform's width.
     * @param height    The weighted platform's height.
     * @param synthPos  The array with index 0 and 1 holding the x and y coordinates for the platform's position in
     *                  synth mode
     * @param jazzPos	The array with index 0 and 1 holding the x and y coordinates for the platform's position in jazz
     *                  mode
     * @param speed     The speed of the platform
     * @param synthTexture The weighted platform's texture region used in synth mode.
     * @param jazzTexture The weighted platform's texture region used in jazz mode.
     */
    public WeightedPlatform(float width, float height, float[] synthPos, float[] jazzPos, float speed,
                            TextureRegion synthTexture, TextureRegion jazzTexture) {

        super(synthPos[0], synthPos[0], width, height);

        jazzPosition = new Vector2(jazzPos[0], jazzPos[1]);
        synthPosition = new Vector2(synthPos[0], synthPos[1]);
        this.synthTexture = synthTexture;
        this.jazzTexture = jazzTexture;
        setTexture(synthTexture);
        setPosition(synthPosition);
        moving = false;
        platformSpeed = speed;

        float magnitude1 = magnitude(jazzPosition, synthPosition);

        velocity = new Vector2((jazzPosition.x - synthPosition.x)*platformSpeed/magnitude1,
                (jazzPosition.y-synthPosition.y)*platformSpeed/magnitude1);

        currentGenre = 0;
    }
    /** */
    public Vector2 currentVelocity(){
        if (!moving){
            return new Vector2(0,0);
        }
        if (currentGenre==0){
            return new Vector2(velocity.x *-1, velocity.y*-1);
        }
        else{
            return new Vector2(velocity.x , velocity.y );
        }
    }

    /** updates the platform to determine what direction it should be moving in */
    public void update(float delta){
        if(moving){
            if (currentGenre == 0){
                move(delta, synthPosition, -1);
            }
            else{
                move(delta, jazzPosition, 1);
            }
        }
    }
    /** Moves the platforms, and sets it into place if it is close enough to its destination**/
    public void move(float delta, Vector2 destination, int direction){
        if (magnitude(getPosition(), destination)<LOCKDIST){
            moving = false;
            setPosition(destination);
        }
        else{
            setPosition(getPosition().x + velocity.x*delta*direction, getPosition().y + velocity.y*delta*direction);
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
    public float magnitude(Vector2 pos1, Vector2 pos2){
        double magnitude = Math.sqrt(Math.pow((pos1.x - pos2.x),2)+
                Math.pow((pos1.y-pos2.y),2));
        return (float) magnitude;
    }

    public Vector2 direction(Vector2 pos1, Vector2 pos2, float speed){
        float magnitude = magnitude(pos1, pos2);
        return new Vector2((pos1.x - pos2.x)*speed/magnitude,
                (pos1.y-pos2.y)*speed/magnitude);
    }
}