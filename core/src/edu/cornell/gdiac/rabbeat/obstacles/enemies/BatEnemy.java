package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.math.Poly2;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.rabbeat.obstacles.projectiles.Bullet;

/**
 * Bat enemy avatar for the platform game.
 * Bats send echos synced to the beat and players will be hurt if they enter its
 * radius.
 */
public class BatEnemy extends Enemy {
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    /** the radius of attack of the bat's echo */
    private float echoRadius;

    /** The idle animation for the bat */
    public Animation<TextureRegion> batIdleAnimation;

    /**
     * Creates a bat enemy avatar with the given physics data
     *
     * @param data             The JsonValue storing information about the bat
     * @param startX           The starting x position of the bat
     * @param startY           The starting y position of the bat
     * @param width            The object width in physics units
     * @param height           The object width in physics units
     * @param enemyScale       The scale of the bat
     * @param faceRight        The direction the bat is facing in
     * @param batIdleAnimation The idle animation for the bat
     * @param beatList         The list of beats that the enemy reacts to
     */
    public BatEnemy(JsonValue data, float startX, float startY, float width, float height, float enemyScale,
            boolean faceRight, Animation<TextureRegion> batIdleAnimation, int[] beatList) {
        super(data, startX, startY, width, height, enemyScale, faceRight, batIdleAnimation, beatList);
        setAnimation(batIdleAnimation);
    }

    /**
     * Switches the attack state of the bat depending on the beat count.
     */
    @Override
    public void switchState() {
        switch (enemyState) {
            case IDLE:
                if (GameController.getInstance().genre == Genre.JAZZ) {
                    if (beatCount == 4) {
                        enemyState = EnemyState.ATTACKING;
                    }
                } else {
                    if (beatCount == 4 || beatCount == 2) {
                        enemyState = EnemyState.ATTACKING;
                    }
                }
                break;
            case ATTACKING:
                if (GameController.getInstance().genre == Genre.JAZZ) {
                    if (beatCount != 4) {
                        enemyState = EnemyState.IDLE;
                    }
                } else {
                    if (beatCount != 4 && beatCount != 2) {
                        enemyState = EnemyState.IDLE;
                    }
                }

                break;
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
        if (Math.sqrt(Math.pow(horizontalDistanceBetweenEnemyAndPlayer(), 2)
                + Math.pow(verticalDistanceBetweenEnemyAndPlayer(), 2)) <= echoRadius) {
            GameController.getInstance().setFailure(true);
        }
        // TODO: visualize the echo
    }

    /**
     * Returns the number of segments necessary for the given tolerance
     *
     * This function is used to compute the number of segments to approximate
     * a radial curve at the given level of tolerance.
     *
     * @param rad The circle radius
     * @param arc The arc in radians
     * @param tol The error tolerance
     *
     * @return the number of segments necessary for the given tolerance
     */
    private int curveSegs(float rad, float arc, float tol) {
        float da = (float) (Math.acos(rad / (rad + tol)) * 2.0f);
        return Math.max(2, (int) (Math.ceil(arc / da)));
    }

    /** The curve tolerance for rounded shapes */
    private float tolerance = 0.5f;

    public Poly2 makeEllipse(Poly2 poly, float cx, float cy, float sx, float sy) {
        int segments = curveSegs(Math.max(sx / 2.0f, sy / 2.0f), 2.0f * (float) Math.PI, tolerance);
        float coef = 2.0f * (float) Math.PI / segments;
        int offset = poly.vertices.length / 2;

        float[] copy = new float[poly.vertices.length + segments * 2 + 2];
        System.arraycopy(poly.vertices, 0, copy, 0, poly.vertices.length);
        for (int i = 0; i < segments; i++) {
            float rads = i * coef;
            copy[poly.vertices.length + i * 2] = (float) (0.5f * sx * Math.cos(rads) + cx);
            copy[poly.vertices.length + i * 2 + 1] = (float) (0.5f * sy * Math.sin(rads) + cy);
        }
        copy[poly.vertices.length + segments * 2] = cx;
        copy[poly.vertices.length + segments * 2 + 1] = cy;
        poly.vertices = copy;

        short[] copyI = new short[poly.indices.length + 3 * segments];
        System.arraycopy(poly.indices, 0, copyI, 0, poly.indices.length);
        for (int i = 0; i < segments - 1; i++) {
            copyI[poly.indices.length + i * 3] = (short) (i + offset);
            copyI[poly.indices.length + i * 3 + 1] = (short) (i + offset + 1);
            copyI[poly.indices.length + i * 3 + 2] = (short) (segments + offset);
        }
        copyI[poly.indices.length + 3 * segments - 3] = (short) (segments + offset - 1);
        copyI[poly.indices.length + 3 * segments - 2] = (short) offset;
        copyI[poly.indices.length + 3 * segments - 1] = (short) (segments + offset);
        poly.indices = copyI;
        return poly;
    }

    /**
     * Stores a circle in the provided buffer.
     *
     * The circle will be appended to the buffer. You should clear the buffer
     * first if you do not want to preserve the original data.
     *
     * @param poly   The polygon to store the result
     * @param cx     The x-coordinate of the center point
     * @param cy     The y-coordinate of the center point
     * @param radius The circle radius
     *
     * @return a reference to the buffer for chaining.
     */
    public Poly2 makeCircle(Poly2 poly, float cx, float cy, float radius) {
        return makeEllipse(poly, cx, cy, 2 * radius, 2 * radius);
    }

    private Poly2 temp;
    private PolygonRegion circle;

    @Override
    public void update(float dt) {
        super.update(dt);
        if (GameController.getInstance().genre == Genre.SYNTH) {
            echoRadius = 2f;
        } else {
            echoRadius = 4f;
        }
    }

    @Override
    public void Attack() {
        sendEcho();
    }
}
