package edu.cornell.gdiac.rabbeat.obstacle.platforms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.Genre;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */

public class MovingPlatform extends SyncedPlatform {
    /** Position for the weighted platform when the game is in Synth mode **/
    private Array<Vector2> positionNodes;
    private Color tint;
    /** The speed at which the platform moves at**/
    private float platformSpeed;
    /** the direction the platform is moving in*/
    private Vector2 velocity;
    /**The next platform in positionNodes that the platform is moving to*/
    private int destination;
    /** The last platform the platform moved from*/
    private int home;
    /** Whether or not the platform is moving**/
    boolean moving;

    /**The distance the platform should 'lock on' to its destination */
    private float LOCKDIST = 0.1f;

    /**
     * Creates a new weighted platform with the given physics data and current genre.
     *
     * @param points  	The polygon vertices
     * @param nodes The points where the platform goes to, must be of even length
     */
    public MovingPlatform(float[] points, float[] nodes, float speed){
        super(points);
        platformSpeed = speed;

        for (int i = 0; i < 2*nodes.length; i++) {
            if ((i%2) == 1){
                positionNodes.add(new Vector2(nodes[i-1], nodes[i]));
            }
        }
        destination = 1;
        home = 0;

        setPosition(positionNodes.get(0));
        moving = true;
        tint = Color.RED;
    }
    /** Calculates the direction between vectors*/
    @Override
    public void draw(GameCanvas canvas) {
        if (region != null) {
            canvas.draw(region,tint,0,0,getX()*drawScale.x,getY()*drawScale.y,getAngle(),1,1);
        }
    }
    /** updates the platform to determine what direction it should be moving in */
    public void update(float delta){
        if(moving){
            if ((magnitude(getPosition(), positionNodes.get(destination))<LOCKDIST)){
                if (destination == positionNodes.size-1){
                    destination = 0;
                }
                else{
                    destination+=1;
                }
                home = destination;
                velocity = direction(positionNodes.get(home), positionNodes.get(destination), platformSpeed);
                move(delta);
            }
            else{
                move(delta);
            }
        }
    }
    /** Moves the platforms, and sets it into place if it is close enough to its destination**/
    public void move(float delta){
        setPosition(getPosition().x + velocity.x*delta, getPosition().y + velocity.y*delta);
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
                tint = Color.YELLOW;
                break;
            case SYNTH:
                tint = Color.RED;
                break;
        }
    }
}