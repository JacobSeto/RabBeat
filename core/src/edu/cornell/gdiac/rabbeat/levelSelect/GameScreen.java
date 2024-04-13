package edu.cornell.gdiac.rabbeat.levelSelect;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;

public class GameScreen extends ScreenAdapter {
    private Game game;
    private int level;

    public GameScreen(Game game, int level) {
        this.game = game;
        this.level = level;
    }
}
