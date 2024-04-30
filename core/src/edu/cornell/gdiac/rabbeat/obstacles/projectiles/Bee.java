package edu.cornell.gdiac.rabbeat.obstacles.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.obstacles.Type;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;


public class Bee extends WheelGameObject implements ISyncedAnimated, IGenreObject {

    public int beatCount = 0;
    private Genre hiveGenre;
    public Animation<TextureRegion> animation;
    /** The elapsed time for animationUpdate */
    private float stateTime = 0;

    public Bee(float x, float y, float radius, Genre genre, Animation<TextureRegion> beeAttackAnimation) {
        super(x, y, radius);
        hiveGenre = genre;
        setAnimation(beeAttackAnimation);
        setType(Type.LETHAL);
        setSensor(true);
    }
    public void update(float dt) {
        stateTime += dt;
        super.update(dt);
    }

    @Override
    public float getBeat() {
        if (hiveGenre == Genre.SYNTH){
            return 1;
        }
        else {
            return 0.5f;
        }
    }

    @Override
    public void beatAction() {
        beatCount++;
        setVY(getVY() * -1);
    }

    @Override
    public void genreUpdate(Genre genre) {
    }
    public void setAnimation(Animation<TextureRegion> animation){
        this.animation = animation;
    }
    public void updateAnimationFrame(){
        stateTime++;
    }
    public void draw(GameCanvas canvas) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        canvas.draw(currentFrame, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(), 0.4f,0.4f);
    }
}
