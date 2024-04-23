package edu.cornell.gdiac.rabbeat.obstacles.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.BoxGameObject;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;


public class Echo extends BoxGameObject implements ISyncedAnimated {

    public int beatCount = 4;

    public Animation<TextureRegion> animation;
    /** The elapsed time for animationUpdate */
    private float stateTime = 0;
    public boolean flipX = false;

    public boolean flipY = false;

    public boolean vertical = false;

    public Echo(float x, float y, float width, float height, Animation _animation) {
        super(x, y, width, height);
        setAnimation(_animation);
    }

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void beatAction() {
        beatCount--;
        if (beatCount <= 0) {
            markRemoved(true);
        }
    }
    public void setAnimation(Animation<TextureRegion> animation){
        this.animation = animation;
    }
    public void updateAnimationFrame(){
        stateTime++;
    }
    public void draw(GameCanvas canvas) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        float scaleX = flipX ? -1 : 1;
        float scaleY = flipY ? -1 : 1;
        float rotate = vertical ? (float)(Math.PI/2) : 0;
        canvas.draw(currentFrame, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,
                getAngle() + rotate, scaleX,scaleY);
    }
}
