package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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

    private Sound buttonClicked;

    private Sound buttonTransition;

    private ScreenListener listener;

    /** The texture for the select */
    private Texture selectTexture;

    /** String that represents what button the select is behind */
    private String buttonSelected = "play";

    /** TextButtons that represents the buttons */
    private Image playButton;
    private Image optionsButton;
    private Image quitButton;

    /** Images for the buttons */
    private Image playSelectImage;
    private Image optionsSelectImage;
    private Image quitSelectImage;


    public MainMenuScreen(Game game) {
        this.game = game;
    }

    public MainMenuScreen(Game game, Sound buttonClicked, Sound buttonTransition) {
        this.game = game;
        this.buttonClicked = buttonClicked;
        this.buttonTransition = buttonTransition;
    }

    public void setButtonClickedSound(Sound s) {buttonClicked = s;}

    public void setButtonTransitionSound(Sound s) {buttonTransition = s;}


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

        Texture playSelectTexture = GameController.getInstance().objectController.playSelect;
        TextureRegionDrawable playSelectDrawable = new TextureRegionDrawable(new TextureRegion(playSelectTexture));
        playSelectImage = new Image(playSelectDrawable);

        Texture optionsSelectTexture = GameController.getInstance().objectController.optionsSelect;
        TextureRegionDrawable optionsSelectDrawable = new TextureRegionDrawable(new TextureRegion(optionsSelectTexture));
        optionsSelectImage = new Image(optionsSelectDrawable);

        Texture quitSelectTexture = GameController.getInstance().objectController.quitSelect;
        TextureRegionDrawable quitSelectDrawable = new TextureRegionDrawable(new TextureRegion(quitSelectTexture));
        quitSelectImage = new Image(quitSelectDrawable);


        // play button
        Texture playButtonTexture = GameController.getInstance().objectController.playButton;
        TextureRegionDrawable playButtonDrawable = new TextureRegionDrawable(new TextureRegion(playButtonTexture));
        playButton = new Image(playButtonDrawable);

        playButton.setPosition((float) background.getWidth()/2 - playButton.getWidth()/2, 400);

        /** Listens for when the mouse moves over the button to switch to the hover state  for the play button*/
        playButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                buttonSelected = "play";
                return true;
            }
        });

        playSelectImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(MainMenuScreen.this, GameController.GO_TO_LEVEL_SELECT);
            }
        });

        playSelectImage.setPosition((float) background.getWidth()/2 - playSelectImage.getWidth()/2, playButton.getY()-10);

        stage.addActor(playSelectImage);
        stage.addActor(playButton);

        //options button
        Texture optionsButtonTexture = GameController.getInstance().objectController.optionsButton;
        TextureRegionDrawable optionsButtonDrawable = new TextureRegionDrawable(new TextureRegion(optionsButtonTexture));
        optionsButton = new Image(optionsButtonDrawable);

        optionsButton.setPosition((float) background.getWidth()/2 - optionsButton.getWidth()/2, 330);

        /** Listens for when the mouse moves over the button to switch to the hover state  for the options button*/
        optionsButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                buttonSelected = "options";
                return true;
            }
        });

        optionsSelectImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO: GO TO OPTIONS SCREEN
            }
        });

        optionsSelectImage.setPosition((float) background.getWidth()/2 - optionsSelectImage.getWidth()/2, optionsButton.getY()-10);
        stage.addActor(optionsSelectImage);
        stage.addActor(optionsButton);

        // quit button
        /** The texture for the quit button */
        Texture quitButtonTexture = GameController.getInstance().objectController.quitButton;
        TextureRegionDrawable quitButtonDrawable = new TextureRegionDrawable(new TextureRegion(quitButtonTexture));
        quitButton = new Image(quitButtonDrawable);

        quitButton.setPosition((float) background.getWidth()/2 - quitButton.getWidth()/2, 260);

        /** Listens for when the mouse moves over the button to switch to the hover state  for the options button*/
        quitButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                buttonSelected = "quit";
                return true;
            }
        });

        quitSelectImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(MainMenuScreen.this, GameController.EXIT_QUIT);
            }
        });

        quitSelectImage.setPosition((float) background.getWidth()/2 - quitSelectImage.getWidth()/2, quitButton.getY()-10);

        stage.addActor(quitSelectImage);
        stage.addActor(quitButton);
    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if(buttonSelected.equals("play")) {
            playButton.setVisible(false);
            optionsButton.setVisible(true);
            quitButton.setVisible(true);
        } else if(buttonSelected.equals("options")) {
            playButton.setVisible(true);
            optionsButton.setVisible(false);
            quitButton.setVisible(true);
        } else if(buttonSelected.equals("quit")) {
            playButton.setVisible(true);
            optionsButton.setVisible(true);
            quitButton.setVisible(false);
        }

        playSelectImage.setVisible(buttonSelected.equals("play"));
        optionsSelectImage.setVisible(buttonSelected.equals("options"));
        quitSelectImage.setVisible(buttonSelected.equals("quit"));


        handleInput();

        GameController.getInstance().syncController.update(false);
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
            buttonTransition.play();
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
            buttonTransition.play();
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
            buttonClicked.play();
        }
    }

}
