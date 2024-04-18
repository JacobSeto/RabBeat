package edu.cornell.gdiac.rabbeat.levelSelect;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.LoadingMode;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.util.ScreenListener;

public class LevelSelectorScreen extends ScreenAdapter {
    private Game game;
    private Stage stage;
    private Skin skin;
    private Texture buttonTexture;

    /** Reference to the numberOfLevels variable in GameController */
    private int numberOfLevels = GameController.getInstance().getNumberOfLevels();

    private ScreenListener listener;

    public LevelSelectorScreen(Game game) {
        this.game = game;
    }

    public String currentLevel = "";

    /** Displays the button UI for each level and adds a clickListener that detects whether
     * the button has been clicked and takes the player to the desired level
     */
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);


        /** Loops through all buttons */
        for(int i=1; i<= numberOfLevels; i++) {
            int finalI = i;

            if(i <= GameController.getInstance().getLevelsUnlocked()) {
                buttonTexture = new Texture(Gdx.files.internal("ui/unlockedLevels/unlockedLevel" + finalI + ".png"));
            } else {
                buttonTexture = new Texture(Gdx.files.internal("ui/lockedLevels/lockedLevel" + finalI + ".png"));
            }

            TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
            BitmapFont font = new BitmapFont();
            TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.up = buttonDrawable;
            textButtonStyle.font = font;
            TextButton levelButton = new TextButton("", textButtonStyle);

            float xPos = 250 + 300*((i-1)%4);
            float yPos = 0;

            if(i <= 4) {
                yPos = 800;
            } else if (i <= 8) {
                yPos = 600;
            } else if (i <= 12) {
                yPos = 400;
            }

            levelButton.setPosition(xPos, yPos);

            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                        listener.exitScreen(LevelSelectorScreen.this, 0);
                        GameController.getInstance().setCurrentLevelInt(finalI);
                    }
                }
            });
            stage.addActor(levelButton);

        }
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    public void setListener (ScreenListener listener) {
        this.listener = listener;
    }

}
