package edu.cornell.gdiac.rabbeat.obstacles.platforms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.obstacles.PolygonGameObject;

/**
 * WeightedPlatform.java
 *
 * This class provides a weighted platform which changes location depending on the genre.
 */

public class MovingPlatform extends PolygonGameObject implements IGenreObject {
    /** Position for the weighted platform when the game is in Synth mode **/
    private Array<Vector2> positionNodes;
    /** The moving platform's current tint color **/
    private Color tint;
    /** The moving platform's tint in synth mode **/
    private Color synthTint = new Color(255/255f, 0, 189/255f, 1);
    /** The moving platform's tint in jazz mode **/
    private Color jazzTint = new Color(200/255f, 0, 0, 1);
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
     * Creates a new moving platform with the given physics data and current genre.
     *
     * @param points  	The polygon vertices
     * @param nodes The points where the platform goes to, must be of even length
     * @param speed The speed of the paltforms
     */
    public MovingPlatform(float[] points, float[] nodes, float speed){
        super(points);
        platformSpeed = speed;
        positionNodes = new Array<Vector2>();
        for (int i = 0; i < nodes.length; i++) {
            if ((i%2) == 1){

                positionNodes.add(new Vector2(nodes[i-1], nodes[i]));
            }
        }
        destination = 1;
        home = 0;

        setPosition(positionNodes.get(0));
        velocity = direction(positionNodes.get(home), positionNodes.get(destination), platformSpeed);
        moving = true;
        tint = synthTint;
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
                home = destination;
                if (destination == positionNodes.size-1){
                    destination = 0;
                }
                else{
                    destination+=1;
                }
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
        //setLinearVelocity(velocity);
        setPosition(getPosition().x + velocity.x*delta*-1, getPosition().y + velocity.y*delta*-1);
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
                tint = jazzTint;
                break;
            case SYNTH:
                tint = synthTint;
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
            return new Vector2(velocity.x*-1 , velocity.y*-1 );
        }
    }
}