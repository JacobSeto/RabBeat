package edu.cornell.gdiac.rabbeat.objects.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.objects.IGenreObject;
import edu.cornell.gdiac.rabbeat.objects.Type;
import edu.cornell.gdiac.rabbeat.objects.WheelGameObject;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;


public class Bee extends WheelGameObject implements ISyncedAnimated, IGenreObject {

    public int beatCount = 0;
    private Genre hiveGenre;
    private boolean isFaceRight;
    public Animation<TextureRegion> animation;
    private float beeBeat;
    /** The elapsed time for animationUpdate */
    private float stateTime = 0;

    public Bee(float x, float y, float radius, Genre genre, boolean faceRight, Animation<TextureRegion> beeAttackAnimation, float beet) {
        super(x, y, radius);
        hiveGenre = genre;
        isFaceRight = faceRight;
        setAnimation(beeAttackAnimation);
        setType(Type.LETHAL);
        setSensor(true);
        beeBeat = beet;
    }
    public void update(float dt) {
        stateTime += dt;
        super.update(dt);
    }

    @Override
    public float getBeat() {
        return beeBeat;
    }

    @Override
    public void beatAction() {
        beatCount++;
        if (hiveGenre == Genre.SYNTH){
            setVY(getVY() * -1);
        }
        else {
            if (beatCount % 2 == 0){
                setVY(getVY() * -1);
            }
        }
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
        float effect = (isFaceRight) ? -1.0f : 1.0f;
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        canvas.draw(currentFrame, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(), 0.4f * effect,0.4f);
    }
}
