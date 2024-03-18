package edu.cornell.gdiac.rabbeat.obstacles.platforms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.obstacles.PolygonGameObject;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */

public class WeightedPlatform extends PolygonGameObject implements IGenreObject {
    /** Position for the weighted platform when the game is in Synth mode **/
    private Vector2 synthPosition;

    /** Position for the weighted platform when the game is in Jazz mode **/
    private Vector2 jazzPosition;
    /** The weighted platform's current tint color **/
    private Color tint;
    /** The weighted platform's tint in synth mode **/
    private Color synthTint = new Color(255/255f, 0, 189/255f, 1);
    /** The weighted platform's tint in jazz mode **/
    private Color jazzTint = new Color(200/255f, 0, 0, 1);
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
     * @param points  	The polygon vertices
     * @param synthPos  The array with index 0 and 1 holding the x and y coordinates for the platform's position in
     *                  jazz mode
     * @param jazzPos	The array with index 0 and 1 holding the x and y coordinates for the platform's position in jazz
     *                  mode
     * @param speed     The speed of the platform
     */
    public WeightedPlatform(float[] points, float[] synthPos, float[] jazzPos, float speed){
        super(points);
        jazzPosition = new Vector2(jazzPos[0], jazzPos[1]);
        synthPosition = new Vector2(synthPos[0], synthPos[1]);
        tint = synthTint;
        setPosition(synthPosition);
        moving = false;
        platformSpeed = speed;

        /** calculates the difference between the two positions of the platforms, normalizes it, and then
         * converts that into the velocity**/
        float magnitude1 = magnitude(jazzPosition, synthPosition);

        velocity = new Vector2((jazzPosition.x - synthPosition.x)*platformSpeed/magnitude1,
                (jazzPosition.y-synthPosition.y)*platformSpeed/magnitude1);

        currentGenre = 0;
    }

    @Override
    public void draw(GameCanvas canvas) {
        if (region != null) {
            canvas.draw(region,tint,0,0,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1,1);
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
                tint = jazzTint;
                moving = true;
                currentGenre = 1;
                break;
            case SYNTH:
                //setPosition(synthPosition);
                tint = synthTint;
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