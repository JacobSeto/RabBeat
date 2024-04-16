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

        buttonTexture = new Texture(Gdx.files.internal("ui/level1TempButton.png"));
        //TextureRegion buttonRegion = new TextureRegion(buttonTexture);

        // Calculate scale factor to fit the button within desired size
//        float maxWidth = 500;  // Max width for the button
//        float maxHeight = 500;  // Max height for the button
//        float scaleX = maxWidth / buttonTexture.getWidth();
//        float scaleY = maxHeight / buttonTexture.getHeight();
//        float scale = Math.min(scaleX, scaleY); // Use the smallest scale factor
//
//        buttonRegion.setRegionWidth((int)(buttonRegion.getRegionWidth() * scaleX));
//        buttonRegion.setRegionHeight((int)(buttonRegion.getRegionHeight() * scaleY));

        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        BitmapFont font = new BitmapFont();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = buttonDrawable;
        textButtonStyle.font = font;

        //TextButton level1Button = new TextButton("", textButtonStyle);
        /** Temporary! */
        //STYLING
//        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
//        BitmapFont font = new BitmapFont();
//        textButtonStyle.font = font; // Set your desired font
//        textButtonStyle.fontColor = Color.WHITE; // Set the font color

        /** Level 1 button! */
//        TextButton level1Button = new TextButton("Level 1", textButtonStyle);
//        level1Button.setPosition(300, 300);
//        level1Button.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                //game.setScreen(new GameScreen(game, 1));
//                System.out.print("HEY!");
//            }
//        });
//        stage.addActor(level1Button);


        /** Loops through all buttons */
        for(int i=1; i<= numberOfLevels; i++) {
            TextButton levelButton = new TextButton("Level " + i, textButtonStyle);
            levelButton.setPosition(300+(200*i), 300);
            int finalI = i;
            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    listener.exitScreen(LevelSelectorScreen.this, 0);
                    currentLevel = "level" + finalI;
                    GameController.getInstance().setCurrentlLevel("level" + finalI);
                    System.out.println(GameController.getInstance().getCurrentLevel());
                }
            });
            stage.addActor(levelButton);

        }

//        level1Button.setPosition(200, 200);
//        stage.addActor(level1Button);

        /**Add skin!! */
//TextButton level1Button = new TextButton("Level 1", skin); // Replace 'skin' with your skin instance
//        level1Button.setPosition(100, 200);
//        level1Button.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new GameScreen(game, 1)); // Start level 1
//            }
//        });
//        stage.addActor(level1Button);

        // Add more buttons for other levels
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
