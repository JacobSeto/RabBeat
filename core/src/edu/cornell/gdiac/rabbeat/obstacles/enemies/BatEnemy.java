package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.math.Path2;
import edu.cornell.gdiac.math.Poly2;
import edu.cornell.gdiac.rabbeat.GameCanvas;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.obstacles.IGenreObject;
import edu.cornell.gdiac.rabbeat.obstacles.WheelGameObject;
import edu.cornell.gdiac.rabbeat.sync.Bullet;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

/**
 * Bat enemy avatar for the platform game.
 * Bats send echos synced to the beat and players will be hurt if they enter its radius.
 */
public class BatEnemy extends Enemy implements ISynced, IGenreObject {
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    /** the radius of attack of the bat's echo */
    private float echoRadius;


    /** The idle animation for the bat */
    public Animation<TextureRegion> batIdleAnimation;

    /**
     * Creates a bat enemy avatar with the given physics data
     *
     * @param data                  The JsonValue storing information about the bat
     * @param startX                The starting x position of the bat
     * @param startY	            The starting y position of the bat
     * @param width                 The object width in physics units
     * @param height                The object width in physics units
     * @param enemyScale            The scale of the bat
     * @param faceRight             The direction the bat is facing in
     * @param batIdleAnimation      The idle animation for the bat
     */
    public BatEnemy(JsonValue data, float startX, float startY, float width, float height, float enemyScale,
            boolean faceRight, Animation<TextureRegion> batIdleAnimation) {
        super(data, startX, startY, width, height, enemyScale, faceRight, batIdleAnimation);
        setAnimation(batIdleAnimation);
    }

    /** Updates the variable curGenre to the current genre of the game */
    public void genreUpdate(Genre genre) {
        curGenre = genre;
    }

    /**
     * Switches the attack state of the bat depending on the beat count.
     */
    @Override
    public void switchState() {
        switch(enemyState) {
            case IDLE:
                if(curGenre.equals(Genre.JAZZ)) {
                    if(beatCount == 4) {
                        enemyState = EnemyState.ATTACKING;
                    }
                } else if(curGenre.equals(Genre.SYNTH)) {
                    if(beatCount == 4 || beatCount == 2) {
                        enemyState = EnemyState.ATTACKING;
                    }
                }
                break;
            case ATTACKING:
                if(curGenre.equals(Genre.JAZZ)) {
                    if(beatCount != 4) {
                        enemyState = EnemyState.IDLE;
                    }
                } else if(curGenre.equals(Genre.SYNTH)) {
                    if(beatCount != 4 && beatCount != 2) {
                        enemyState = EnemyState.IDLE;
                    }
                }

                break;
        }
    }

    private int beatCount = 1;
    private float beat = 1;

    @Override
    public float getBeat() {
        return beat;
    }

    @Override
    public void beatAction() {
        beatCount++;
        if(beatCount >= 5){
            beatCount = 1;
        }

        if(enemyState.equals(EnemyState.ATTACKING)) {
            sendEcho();
        }
    }

    ObjectController oc = GameController.getInstance().objectController;
    /** Scale of the world */
    private Vector2 scale = GameController.getInstance().getScale();

    private Bullet bullet;
    /** Tells whether the bear was facing right or not when they shot */
    private boolean shotDirection;



    /** Checks whether the player is within the echo radius */
    public void sendEcho() {
        if(Math.sqrt(Math.pow(horizontalDistanceBetweenEnemyAndPlayer(),2) + Math.pow(verticalDistanceBetweenEnemyAndPlayer(),2)) <= echoRadius) {
            GameController.getInstance().setFailure(true);
        }
        //TODO: visualize the echo
        
//        WheelGameObject w = new WheelGameObject(getX(), getY(), (float) 0.06);
//        GameController.getInstance().instantiateQueue(w);

//        ObjectController oc = GameController.getInstance().objectController;
//        float offset =  oc.defaultConstants.get("bullet").getFloat("offset",0);
//        offset *= (isFaceRight() ? 1 : -1);
//
//        float radius = oc.bulletTexture.getRegionWidth()/(2.0f*scale.x);
//        System.out.println("RADIUS: " + radius);
//        System.out.println("ECHORADIUS: " + echoRadius);
//        bullet = new Bullet(getX()+offset, getY(), radius, oc.defaultConstants.get("bullet").getFloat("synth speed", 0),
//                oc.defaultConstants.get("bullet").getFloat("jazz speed", 0), isFaceRight());
//
//        bullet.setName(getName() + "_bullet");
//        bullet.setDensity(oc.defaultConstants.get("bullet").getFloat("density", 0));
//        bullet.setDrawScale(scale);
//        bullet.setTexture(oc.bulletTexture);
//        bullet.setGravityScale(0);
//        shotDirection = isFaceRight();
//
//        //Compute position and velocity
//        float speed;
//        int beatcount;
//        if (curGenre == Genre.SYNTH){
//            speed = oc.defaultConstants.get("bullet").getFloat("synth speed", 0);
//            beatcount = 3;
//        }
//        else {
//            speed = oc.defaultConstants.get("bullet").getFloat("jazz speed", 0);
//            beatcount = 8;
//        }
//        speed *= (isFaceRight() ? 1 : -1);
//        bullet.setVX(speed);
//        bullet.beatCount = beatcount;
//        GameController.getInstance().instantiateQueue(bullet);
//
    }

    /**
     * Returns the number of segments necessary for the given tolerance
     *
     * This function is used to compute the number of segments to approximate
     * a radial curve at the given level of tolerance.
     *
     * @param rad   The circle radius
     * @param arc   The arc in radians
     * @param tol   The error tolerance
     *
     * @return the number of segments necessary for the given tolerance
     */
    private int curveSegs(float rad, float arc, float tol) {
        float da = (float) (Math.acos(rad / (rad + tol)) * 2.0f);
        return Math.max(2,(int)(Math.ceil(arc/da)));
    }

    /** The curve tolerance for rounded shapes */
    private float tolerance = 0.5f;


    public Poly2 makeEllipse(Poly2 poly, float cx, float cy, float sx, float sy) {
        int segments = curveSegs(Math.max(sx/2.0f,sy/2.0f),2.0f*(float)Math.PI, tolerance);
        float coef = 2.0f * (float)Math.PI/segments;
        int offset = poly.vertices.length/2;

        float[] copy = new float[poly.vertices.length+segments*2+2];
        System.arraycopy(poly.vertices, 0, copy, 0, poly.vertices.length);
        for (int i=0; i<segments; i++) {
            float rads = i*coef;
            copy[poly.vertices.length+i*2] = (float) (0.5f*sx*Math.cos(rads) + cx);
            copy[poly.vertices.length+i*2+1] =(float) (0.5f*sy*Math.sin(rads) + cy);
        }
        copy[poly.vertices.length+segments*2] = cx;
        copy[poly.vertices.length+segments*2+1] = cy;
        poly.vertices = copy;

        short[] copyI = new short[poly.indices.length+3*segments];
        System.arraycopy(poly.indices, 0, copyI, 0, poly.indices.length);
        for (int i=0; i<segments-1; i++) {
            copyI[poly.indices.length+i*3] = (short) (i+offset);
            copyI[poly.indices.length+i*3+1] = (short) (i+offset+1);
            copyI[poly.indices.length+i*3+2] = (short) (segments+offset);
        }
        copyI[poly.indices.length+3*segments-3] = (short) (segments+offset-1);
        copyI[poly.indices.length+3*segments-2] = (short) offset;
        copyI[poly.indices.length+3*segments-1] = (short) (segments+offset);
        poly.indices = copyI;
        return poly;
    }
    /**
     * Stores a circle in the provided buffer.
     *
     * The circle will be appended to the buffer.  You should clear the buffer
     * first if you do not want to preserve the original data.
     *
     * @param poly      The polygon to store the result
     * @param cx        The x-coordinate of the center point
     * @param cy        The y-coordinate of the center point
     * @param radius    The circle radius
     *
     * @return a reference to the buffer for chaining.
     */
    public Poly2 makeCircle(Poly2 poly, float cx, float cy, float radius) {
        return makeEllipse(poly, cx, cy, 2*radius, 2*radius);
    }

    private Poly2 temp;
    private PolygonRegion circle;

    @Override
    public void update(float dt) {
        super.update(dt);
//        makeEllipse(getX(), getY(), 2*5, 2*5);
//        temp = makeCircle(225, 50,40);
//        circle = temp.makePolyRegion(region);
//        //makeCircle(getX(), getY(), echoRadius);

        if(curGenre.equals(Genre.SYNTH)) {
            echoRadius = 2f;
        } else if(curGenre.equals(Genre.JAZZ)) {
            echoRadius = 4f;
        }
    }

}
