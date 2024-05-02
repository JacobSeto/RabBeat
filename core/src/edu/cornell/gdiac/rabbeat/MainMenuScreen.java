package edu.cornell.gdiac.rabbeat;

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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.gdiac.rabbeat.GameController;
import edu.cornell.gdiac.rabbeat.LoadingMode;
import edu.cornell.gdiac.rabbeat.ObjectController;
import edu.cornell.gdiac.util.ScreenListener;

public class MainMenuScreen extends ScreenAdapter {
    private Game game;
    private Stage stage;
    private Skin skin;
    private Texture buttonTexture;

    /** Reference to the numberOfLevels variable in GameController */
    private int numberOfLevels = GameController.getInstance().getNumberOfLevels();

    private ScreenListener listener;

    public MainMenuScreen(Game game) {
        this.game = game;
    }

    /** Displays the button UI for each level and adds a clickListener that detects whether
     * the button has been clicked and takes the player to the desired level
     */
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Background
        Texture background = new Texture(Gdx.files.internal("backgrounds/test-bg.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(background));
        Image bg = new Image(backgroundDrawable);
        bg.setPosition(0, 0);
        stage.addActor(bg);

        buttonTexture = GameController.getInstance().objectController.getUnlockedButtonTexture(1);

        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        BitmapFont font = new BitmapFont();
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = buttonDrawable;
        textButtonStyle.font = font;
        TextButton levelButton = new TextButton("", textButtonStyle);

        levelButton.setPosition(200, 200);

        levelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(MainMenuScreen.this, GameController.getInstance().GO_TO_LEVEL_SELECT);
            }
        });
        stage.addActor(levelButton);
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
