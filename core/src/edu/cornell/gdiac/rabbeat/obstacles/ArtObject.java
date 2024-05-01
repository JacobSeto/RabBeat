package edu.cornell.gdiac.rabbeat.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.rabbeat.GameCanvas;

/**
 * ArtObject.java
 *
 * This class provides an art object which is not interact-able by the player.
 */

public class ArtObject extends GameObject{

    /** The art object's texture */
    protected TextureRegion textureRegion;
    /** The texture origin for drawing */
    protected Vector2 origin;

    /**
     * Creates a new art object with the given texture region and x and y coordinates.
     *
     * @param textureRegion The art object's texture region.
     * @param x The art object's x coordinate in world coordinates.
     * @param y The art object's y coordinate in world coordinates.
     */
    public ArtObject(TextureRegion textureRegion, float x, float y){
        this.textureRegion = textureRegion;
        setX(x);
        setY(y);
        origin = new Vector2(textureRegion.getRegionWidth()/2.0f, textureRegion.getRegionHeight()/2.0f);
    }

    @Override
    public boolean activatePhysics(World world) {
        return true;
    }

    @Override
    public void deactivatePhysics(World world) {}

    @Override
    public void draw(GameCanvas canvas) {
        if (textureRegion != null) {
            canvas.draw(textureRegion,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
        }
    }

    @Override
    public void drawDebug(GameCanvas canvas) {}
}
