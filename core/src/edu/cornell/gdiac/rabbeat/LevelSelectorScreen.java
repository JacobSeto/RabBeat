package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
        Preferences prefs = Gdx.app.getPreferences("Saved Levels Unlocked");

        //COMMENT OUT THE FOLLOWING LINE IF YOU ONLY WANT 1ST LEVEL TO BE UNLOCKED
        //prefs.putInteger("levelsUnlocked", 1);

        GameController.getInstance().setLevelsUnlocked(prefs.getInteger("levelsUnlocked", 1));
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

        /** Loops through all buttons */
        for(int i=1; i<= numberOfLevels; i++) {
            int finalI = i;
            if(i <= GameController.getInstance().getLevelsUnlocked()) {
                buttonTexture = GameController.getInstance().objectController.getUnlockedButtonTexture(finalI);
            } else {
                buttonTexture = GameController.getInstance().objectController.getLockedButtonTexture(finalI);
            }

            TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
            BitmapFont font = new BitmapFont();
            TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.up = buttonDrawable;
            textButtonStyle.font = font;
            TextButton levelButton = new TextButton("", textButtonStyle);

            float xPos = 100 + 300*((i-1)%4);
            float yPos = 0;

            if(i <= 4) {
                yPos = 475;
            } else if (i <= 8) {
                yPos = 275;
            } else if (i <= 12) {
                yPos = 75;
            }

            levelButton.setPosition(xPos, yPos);

            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                        GameController.getInstance().setCurrentLevelInt(finalI);
                        listener.exitScreen(LevelSelectorScreen.this, 0);
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
