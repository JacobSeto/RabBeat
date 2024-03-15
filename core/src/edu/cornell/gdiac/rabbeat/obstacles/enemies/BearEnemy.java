package edu.cornell.gdiac.rabbeat.obstacles.enemies;

import com.badlogic.gdx.utils.JsonValue;

public class BearEnemy extends Enemy {

    /**
     * Creates a new enemy avatar with the given physics data
     *
     * @param data
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param enemyScale
     * @param faceRight
     */
    public BearEnemy(JsonValue data, float width, float height, float enemyScale,
            boolean faceRight) {
        super(data, width, height, enemyScale, faceRight);
    }

    /**
     * Switches the attack state of the bear depending on the player's position.
     */
    public void switchState() {
        switch(enemyState) {
            case IDLE:
                if(horizontalDistanceBetweenEnemyAndPlayer()<5) {
                    enemyState = EnemyState.ATTACKING;
                    System.out.println("ATTACKING");
                }
                break;
            case ATTACKING:
                if(horizontalDistanceBetweenEnemyAndPlayer()>5) {
                    enemyState = EnemyState.IDLE;
                    System.out.println("not ATTACKING");
                }
                //TODO: make bear shoot
                break;
        }
    }
}
