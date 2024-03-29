package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.Genre;
import edu.cornell.gdiac.rabbeat.sync.ISynced;

public class BatEnemy extends Enemy implements ISynced {

    /** Current genre that the game is on */
    public Genre curGenre = Genre.SYNTH;

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
    }

    /** Updates the variable curGenre to the current genre of the game */
    public void genreUpdate(Genre genre) {
        curGenre = genre;
    }

    @Override
    public void switchState() {
        switch(enemyState) {
            case IDLE:
                if(beatCount == 4) {
                    enemyState = EnemyState.ATTACKING;
                    System.out.println("ATTACKING");
                }
                break;
            case ATTACKING:
                if(beatCount != 4) {
                    enemyState = EnemyState.IDLE;
                    System.out.println("IDLE");
                }
                //TODO: make bear shoot
                break;
        }
    }

    private int beatCount = 1;

    @Override
    public float getBeat() {
        return beatCount;
    }

    @Override
    public void beatAction() {
        beatCount++;
        if(beatCount >= 5){
            beatCount = 1;
        }

    }


}
