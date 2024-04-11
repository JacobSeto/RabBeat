package edu.cornell.gdiac.rabbeat.obstacles.platforms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.BoxGameObject;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */

public class MovingPlatform extends BoxGameObject implements IGenreObject, ISynced {
    /** Position for the weighted platform when the game is in Synth mode **/
    private Vector2[] positionNodes;
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

    private int beat = 0;

    private float currentSpeed=1.0f;

    /**
     * Creates a new moving platform with the given physics data and current genre.
     *
     * @param width The width of the moving platform
     * @param height The height of the moving platform
     * @param nodes The points where the platform goes to, must be of even length
     * @param speed The speed of the platform
     * @param texture The texture region for the platform
     */
    public MovingPlatform(float width, float height, Vector2[] nodes, float speed, TextureRegion texture) {
        super(nodes[0].x, nodes[0].y, width, height);
        platformSpeed = speed;
        positionNodes = nodes;
        destination = 1;
        home = 0;
        setTexture(texture);
        setPosition(positionNodes[0]);
        velocity = direction(positionNodes[home], positionNodes[destination], platformSpeed);
        moving = true;
    }

    /** updates the platform to determine what direction it should be moving in */
    public void update(float delta){
        if(moving){
            if ((magnitude(getPosition(), positionNodes[destination])<LOCKDIST)){
                home = destination;
                if (destination == positionNodes.length-1){
                    destination = 0;
                }
                else{
                    destination+=1;
                }
                velocity = direction(positionNodes[home], positionNodes[destination], 1);
                move(delta);
            }
            else{
                move(delta);
            }
        }
    }
    /** Moves the platforms, and sets it into place if it is close enough to its destination**/
    public void move(float delta){
        //setLinearVelocity(velocity);
        setPosition(getPosition().x + velocity.x*delta*-1*currentSpeed, getPosition().y + velocity.y*delta*-1*currentSpeed);
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
        return 4;
    }

    @Override
    public void beatAction() {
        beat+= 1;
        if (beat== 6){
            currentSpeed = 4; //magnitude(positionNodes[home], positionNodes[destination])/4;

        }
        else if (beat == 8 ){
            currentSpeed = 1;
            beat = 0;
        }
        System.out.println("speed is "+currentSpeed);
        System.out.println("Beat is "+beat);
    }
}