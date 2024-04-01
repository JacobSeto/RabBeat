package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

/**
 * Bat enemy avatar for the platform game.
 * Bats send echos synced to the beat and players will be hurt if they enter its radius.
 */
public class BatEnemy extends Enemy implements ISynced {
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    /** Current genre that the game is on */
    public Genre curGenre = Genre.SYNTH;

    /** the radius of attack of the bat's echo */
    private float echoRadius = 2f;

    /** The idle animation for the bear */
    public Animation<TextureRegion> batIdleAnimation;

    /**
     * Creates a bat enemy avatar with the given physics data
     *
     * @param data
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param enemyScale
     * @param faceRight
     */
    public BatEnemy(JsonValue data, float width, float height, float enemyScale,
            boolean faceRight, Animation<TextureRegion> batIdleAnimation) {
        super(data, width, height, enemyScale, faceRight, batIdleAnimation);
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
                if(beatCount == 4) {
                    enemyState = EnemyState.ATTACKING;
                    //System.out.println("ATTACKING");
                }
                break;
            case ATTACKING:
                if(beatCount != 4) {
                    enemyState = EnemyState.IDLE;
                    System.out.println("IDLE");
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

        //System.out.println("BEAT: " + beatCount);
    }

    /** Checks whether the player is within the echo radius */
    public void sendEcho() {
        if(horizontalDistanceBetweenEnemyAndPlayer() <= echoRadius && verticalDistanceBetweenEnemyAndPlayer() <= echoRadius) {
            GameController.getInstance().setFailure(true);
        }
        System.out.println("Echo sent!");
        //Rectangle echoBox = new Rectangle(getX() - echoRadius, getY() - echoRadius, echoRadius*2, echoRadius*2);

//        echoBox.setFill(Color.TRANSPARENT);
//        echoBox.setStroke(Color.RED);
//        echoBox.setStrokeWidth(2);

        shapeRenderer.begin(ShapeType.Line); // Use Line type for circle outline
        shapeRenderer.setColor(Color.RED); // Set the color of the echo shape
        shapeRenderer.circle(getX(), getY(), echoRadius+100); // Draw the circle
        shapeRenderer.end();
    }



}
