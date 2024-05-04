package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.Input;

/** Class that represents the main menu screen for the game
 * Displays a play button, options button, and quit button
 */
public class MainMenuScreen extends ScreenAdapter {
    private Game game;
    private Stage stage;

    private ScreenListener listener;

    /** The texture for the select */
    private Texture selectTexture;

    /** String that represents what button the select is behind */
    private String buttonSelected = "play";

    /** Images for the buttons */
    private Image playSelectImage;
    private Image optionsSelectImage;
    private Image quitSelectImage;

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
        Texture background = GameController.getInstance().objectController.mainMenuBackground;
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(background));
        Image bg = new Image(backgroundDrawable);
        bg.setPosition(0, 0);
        stage.addActor(bg);

        Texture selectTexture = GameController.getInstance().objectController.select;
        TextureRegionDrawable selectDrawable = new TextureRegionDrawable(new TextureRegion(selectTexture));
        playSelectImage = new Image(selectDrawable);
        optionsSelectImage = new Image(selectDrawable);
        quitSelectImage = new Image(selectDrawable);


        // play button
        /** The texture for the play button */
        Texture playButtonTexture = GameController.getInstance().objectController.playButton;
        BitmapFont font = new BitmapFont();
        TextButton.TextButtonStyle playTextButtonStyle = new TextButton.TextButtonStyle();
        playTextButtonStyle.up = new TextureRegionDrawable(new TextureRegion(playButtonTexture));
        playTextButtonStyle.font = font;
        TextButton playButton = new TextButton("", playTextButtonStyle);

        playButton.setPosition((float) background.getWidth()/2 - playButton.getWidth()/2, 375);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(MainMenuScreen.this, GameController.GO_TO_LEVEL_SELECT);
            }
        });

        playSelectImage.setPosition((float) background.getWidth()/2 - playSelectImage.getWidth()/2, playButton.getY() - 10);

        stage.addActor(playSelectImage);
        stage.addActor(playButton);


        // options button
        /** The texture for the options button */
        Texture optionsButtonTexture = GameController.getInstance().objectController.optionsButton;
        TextButton.TextButtonStyle optionsTextButtonStyle = new TextButton.TextButtonStyle();
        optionsTextButtonStyle.up = new TextureRegionDrawable(new TextureRegion(
                optionsButtonTexture));
        optionsTextButtonStyle.font = font;
        TextButton optionsButton = new TextButton("", optionsTextButtonStyle);

        optionsButton.setPosition((float) background.getWidth()/2 - optionsButton.getWidth()/2, 300);

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO: GO TO OPTIONS SCREEN
            }
        });

        optionsSelectImage.setPosition((float) background.getWidth()/2 - optionsSelectImage.getWidth()/2, optionsButton.getY() - 10);
        stage.addActor(optionsSelectImage);
        stage.addActor(optionsButton);

        // quit button
        /** The texture for the quit button */
        Texture quitButtonTexture = GameController.getInstance().objectController.quitButton;
        TextButton.TextButtonStyle quitTextButtonStyle = new TextButton.TextButtonStyle();
        quitTextButtonStyle.up = new TextureRegionDrawable(new TextureRegion(quitButtonTexture));
        quitTextButtonStyle.font = font;
        TextButton quitButton = new TextButton("", quitTextButtonStyle);

        quitButton.setPosition((float) background.getWidth()/2 - quitButton.getWidth()/2, 175);

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(MainMenuScreen.this, GameController.EXIT_QUIT);
            }
        });

        quitSelectImage.setPosition((float) background.getWidth()/2 - quitSelectImage.getWidth()/2, quitButton.getY() + 35);

        stage.addActor(quitSelectImage);
        stage.addActor(quitButton);
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        playSelectImage.setVisible(buttonSelected.equals("play"));
        optionsSelectImage.setVisible(buttonSelected.equals("options"));
        quitSelectImage.setVisible(buttonSelected.equals("quit"));

        handleInput();
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


    /** reads the data from the input keys and changes the buttonSelected String accordingly */
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            switch (buttonSelected) {
                case "play":
                    buttonSelected = "quit";
                    break;
                case "options":
                    buttonSelected = "play";
                    break;
                case "quit":
                    buttonSelected = "options";
                    break;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)|| Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            switch (buttonSelected) {
                case "quit":
                    buttonSelected = "play";
                    break;
                case "play":
                    buttonSelected = "options";
                    break;
                case "options":
                    buttonSelected = "quit";
                    break;
            }
        } else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            switch (buttonSelected) {
                case "play":
                    listener.exitScreen(this, GameController.GO_TO_LEVEL_SELECT);
                    break;
                case "options":
                    //TODO: add an options screen!

                    break;
                case "quit":
                    listener.exitScreen(this, GameController.EXIT_QUIT);
                    break;
            }
        }
    }

}
