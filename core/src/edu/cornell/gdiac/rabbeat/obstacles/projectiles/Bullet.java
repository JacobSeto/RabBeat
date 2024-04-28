package edu.cornell.gdiac.rabbeat.obstacles.projectiles;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.obstacles.Type;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;
import edu.cornell.gdiac.rabbeat.sync.ISynced;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;
import edu.cornell.gdiac.rabbeat.Genre;

public class Bullet extends WheelGameObject implements ISynced, ISyncedAnimated {
    public int beatCount = 0;

    private float stateTime = 0;
    public Genre bulletGenre;

    private Animation animation;

    public Bullet(float x, float y, float radius, float synthVX, float jazzVX, boolean fr, Genre genre) {
        super(x, y, radius);
        float dir = (fr ? 1 : -1);
        bulletGenre = genre;
        setType(Type.LETHAL);
        setSensor(true);
    }

    @Override
    public float getBeat() {
        return 1;
    }

    @Override
    public void setAnimation(Animation<TextureRegion> anim) {
        animation = anim;
    }

    @Override
    public void updateAnimationFrame() {
        stateTime++;
        if (animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
    }

    @Override
    public void beatAction() {
        beatCount--;
        if (beatCount <= 0) {
            markRemoved(true);
        }
    }

    public void draw(GameCanvas canvas) {
        TextureRegion currentFrame = (TextureRegion) animation.getKeyFrame(stateTime, true);
        canvas.draw(currentFrame, Color.WHITE,origin.x + 30,origin.y + (bulletGenre == Genre.SYNTH ? 35 : 50),getX()*drawScale.x,getY()*drawScale.y,getAngle(), 1.2f,1.2f);
    }

}