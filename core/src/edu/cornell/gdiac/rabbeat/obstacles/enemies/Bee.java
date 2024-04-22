package edu.cornell.gdiac.rabbeat.obstacles.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;
import edu.cornell.gdiac.rabbeat.sync.ISynced;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;


public class Bee extends WheelGameObject implements ISynced, IGenreObject {

    public int beatCount = 0;
    private float hiveY;
    private boolean switched = false;
    public Animation<TextureRegion> animation;
    public Genre curGenre = Genre.SYNTH;
    /** The elapsed time for animationUpdate */
    private float stateTime = 0;

    public Bee(float x, float y, float radius, Animation<TextureRegion> beeAttackAnimation) {
        super(x, y, radius);
        hiveY = y;
        setAnimation(beeAttackAnimation);
    }
    public void update(float dt) {
        stateTime += dt;
        if (getY() == hiveY){
            if (switched == true){
                if (curGenre == Genre.SYNTH){
                    if (getVY() < 0){
                        setVY(-4);
                    }
                    else{
                        setVY(4);
                    }
                }
                else{
                    if (getVY() < 0){
                        setVY(-2);
                    }
                    else{
                        setVY(2);
                    }
                }
            }
        }
        super.update(dt);
    }

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void beatAction() {
        beatCount++;
        setVY(getVY() * -1);
    }

    @Override
    public void genreUpdate(Genre genre) {
        curGenre = genre;
        switched = true;
//        if (genre == Genre.SYNTH){
//            if (getVY() < 0){
//                setVY(-2);
//            }
//            else {
//                setVY(2);
//            }
//        }
//        else {
//            if (getVY() < 0){
//                setVY(-1);
//            }
//            else {
//                setVY(1);
//            }
//        }
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
