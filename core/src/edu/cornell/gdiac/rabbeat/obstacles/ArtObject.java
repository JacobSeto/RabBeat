package edu.cornell.gdiac.rabbeat.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.rabbeat.GameCanvas;

public class ArtObject extends GameObject{
    private TextureRegion textureRegion;
    protected Vector2 origin;

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
