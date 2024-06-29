package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
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
import edu.cornell.gdiac.rabbeat.sync.SyncController;
import edu.cornell.gdiac.util.ScreenListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

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

    private Image[] musicVolumeBoxes;

    private Image[] sfxVolumeBoxes;

    private Image indicatorStar;

    /** TextButtons that represents the buttons */
    private Image playButton;
    private Image optionsButton;
    private Image quitButton;

    private Image creditsButton;

    private Image volumeLowerImage;

    private Image volumeHigherImage;
    private Image volumeLowerImage2;

    private Image volumeHigherImage2;

    private Image backButton;

    /** Images for the buttons */
    private Image playSelectImage;
    private Image optionsSelectImage;
    private Image quitSelectImage;

    private Image creditsSelectImage;

    private Image creditsScreen;
    private Image musicText;

    private Image sfxText;

    private Image volumeBox;
    private Texture background;
    private Texture blurredBackground;

    private Image backgroundImage;
    private Image blurredBackgroundImage;

    private boolean downPressed;
    private boolean downPrevious;

    private boolean upPressed;
    private boolean upPrevious;

    private boolean enterPressed;
    private boolean enterPrevious;

    private boolean leftPressed;
    private boolean leftPrevious;

    private boolean rightPressed;
    private boolean rightPrevious;

    private boolean oPressed;
    private boolean oPrevious;

    private Music mus;

    private int musPref;
    private int sfxPref;

    private int optMenuSel = 0;

    private static int NUM_OPTS = 2;
    private boolean inOptionsMenu = false;

    private boolean inCredits = false;

    private float sfxVolume;

    public MainMenuScreen(Game game) {
        this.game = game;
        musicVolumeBoxes = new Image[10];
        sfxVolumeBoxes = new Image[10];
    }
    public void setMusic(Music m) {mus = m;}
    public void setButtonClickedSound(Sound s) {buttonClicked = s;}

    public void setButtonTransitionSound(Sound s) {buttonTransition = s;}

    public void setMusicPreference(int vol) {musPref = vol;}

    public void setSFXPreference(int vol) {sfxPref = vol;}
    public void setSFXVolume(float vol) {sfxVolume = vol;}

    /** Displays the button UI for each level and adds a clickListener that detects whether
     * the button has been clicked and takes the player to the desired level
     */
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Background
        background = GameController.getInstance().objectController.mainMenuBackground;
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(background));
        backgroundImage = new Image(backgroundDrawable);
        backgroundImage.setPosition(0, 0);
        stage.addActor(backgroundImage);

        blurredBackground = GameController.getInstance().objectController.blurredBackground;
        TextureRegionDrawable blurredBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(blurredBackground));
        blurredBackgroundImage = new Image(blurredBackgroundDrawable);
        blurredBackgroundImage.setPosition(0, 0);
        blurredBackgroundImage.setScale(0.5f);
        stage.addActor(blurredBackgroundImage);

        Texture playSelectTexture = GameController.getInstance().objectController.playSelect;
        TextureRegionDrawable playSelectDrawable = new TextureRegionDrawable(new TextureRegion(playSelectTexture));
        playSelectImage = new Image(playSelectDrawable);

        Texture optionsSelectTexture = GameController.getInstance().objectController.optionsSelect;
        TextureRegionDrawable optionsSelectDrawable = new TextureRegionDrawable(new TextureRegion(optionsSelectTexture));
        optionsSelectImage = new Image(optionsSelectDrawable);

        Texture quitSelectTexture = GameController.getInstance().objectController.quitSelect;
        TextureRegionDrawable quitSelectDrawable = new TextureRegionDrawable(new TextureRegion(quitSelectTexture));
        quitSelectImage = new Image(quitSelectDrawable);

        Texture creditsSelectTexture = GameController.getInstance().objectController.creditsGlow;
        TextureRegionDrawable creditsSelectDrawable = new TextureRegionDrawable(new TextureRegion(creditsSelectTexture));
        creditsSelectImage = new Image(creditsSelectDrawable);

        TextureRegion star = GameController.getInstance().objectController.indicatorStarTexture;
        TextureRegionDrawable starDrawable = new TextureRegionDrawable(star);
        indicatorStar = new Image(starDrawable);
        indicatorStar.setVisible(false);

        stage.addActor(indicatorStar);

        TextureRegion minusSign = GameController.getInstance().objectController.unhoverLowerSoundTexture;
        TextureRegionDrawable minusDrawable = new TextureRegionDrawable(minusSign);
        volumeLowerImage = new Image(minusDrawable);
        volumeLowerImage.setPosition(560, 375);

        volumeLowerImage.setScale(0.8f);

        stage.addActor(volumeLowerImage);

        TextureRegion plusSign = GameController.getInstance().objectController.unhoverUpSoundTexture;
        TextureRegionDrawable plusDrawable = new TextureRegionDrawable(plusSign);
        volumeHigherImage = new Image(plusDrawable);

        volumeHigherImage.setPosition(1020, 375);

        volumeHigherImage.setScale(0.8f);

        stage.addActor(volumeHigherImage);

        TextureRegion minusSign2 = GameController.getInstance().objectController.unhoverLowerSoundTexture;
        TextureRegionDrawable minusDrawable2 = new TextureRegionDrawable(minusSign);
        volumeLowerImage2 = new Image(minusDrawable2);

        volumeLowerImage2.setPosition(560, 325);

        stage.addActor(volumeLowerImage2);

        volumeLowerImage2.setScale(0.8f);


        TextureRegion plusSign2 = GameController.getInstance().objectController.unhoverUpSoundTexture;
        TextureRegionDrawable plusDrawable2 = new TextureRegionDrawable(plusSign2);
        volumeHigherImage2 = new Image(plusDrawable2);

        volumeHigherImage2.setPosition(1020, 325);

        stage.addActor(volumeHigherImage2);

        volumeHigherImage2.setScale(0.8f);

        volumeLowerImage.setVisible(false);
        volumeLowerImage2.setVisible(false);
        volumeHigherImage.setVisible(false);
        volumeHigherImage2.setVisible(false);

        volumeLowerImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (musPref > 0) {
                    musicVolumeBoxes[musPref - 1].setVisible(false);
                    musPref--;
                    mus.setVolume(musPref / 10f);
                }
                optMenuSel = 0;
            }
        });

        volumeHigherImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (musPref < 10) {
                    musPref++;
                    mus.setVolume(musPref / 10f);
                }
                optMenuSel = 0;
            }
        });

        volumeLowerImage2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (sfxPref > 0) {
                    sfxVolumeBoxes[sfxPref - 1].setVisible(false);
                    sfxPref--;
                    sfxVolume = sfxPref;
                    buttonClicked.play(sfxVolume / 10f);
                }
                optMenuSel = 1;
            }
        });

        volumeHigherImage2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (sfxPref < 10) {
                    sfxPref++;
                    sfxVolume = sfxPref;
                    buttonClicked.play(sfxVolume / 10f);
                }
                optMenuSel = 1;
            }
        });

        Texture creditsTexture = GameController.getInstance().objectController.creditsScreen;
        creditsScreen = new Image(creditsTexture);
        creditsScreen.setScale(0.5f);
        creditsScreen.setPosition(0, 0);
        creditsScreen.setVisible(false);
        stage.addActor(creditsScreen);

        Texture backButtonTexture = GameController.getInstance().objectController.levelSelectBackButton;
        backButton = new Image(backButtonTexture);
        backButton.setScale(1.0f);
        backButton.setPosition(27f, background.getHeight() - backButton.getHeight() - 25);
        stage.addActor(backButton);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inOptionsMenu = false;
                inCredits = false;
                Preferences prefs = Gdx.app.getPreferences("MusicVolume");
                prefs.putInteger("musicVolume", musPref);
                prefs.flush();
                prefs = Gdx.app.getPreferences("SFXVolume");
                prefs.putInteger("sfxVolume", sfxPref);
                prefs.flush();
                sfxVolume = sfxPref;
                buttonClicked.play(sfxVolume / 10f);
            }
        });

        // play button
        Texture playButtonTexture = GameController.getInstance().objectController.playButton;
        TextureRegionDrawable playButtonDrawable = new TextureRegionDrawable(new TextureRegion(playButtonTexture));
        playButton = new Image(playButtonDrawable);

        TextureRegion musictxt = GameController.getInstance().objectController.musicTexture;
        TextureRegionDrawable trd = new TextureRegionDrawable(musictxt);
        musicText = new Image(trd);
        musicText.setScale(0.5f);

        TextureRegion sfxtxt = GameController.getInstance().objectController.SFXTexture;
        TextureRegionDrawable tad = new TextureRegionDrawable(sfxtxt);
        sfxText = new Image(tad);
        sfxText.setScale(0.5f);

        musicText.setVisible(false);
        sfxText.setVisible(false);
        TextureRegion v = GameController.getInstance().objectController.volumeBoxTexture;
        TextureRegionDrawable tdd = new TextureRegionDrawable(v);
        for (int i = 0; i < 10; i++) {
            volumeBox = new Image(tdd);
            volumeBox.setPosition(background.getWidth() / 2 - 30 + 40 * i, 375);
            stage.addActor(volumeBox);
            musicVolumeBoxes[i] = volumeBox;
            volumeBox.setVisible(false);
        }
        for (int i = 0; i < 10; i++) {
            volumeBox = new Image(tdd);
            volumeBox.setPosition(background.getWidth() / 2 - 30 + 40 * i, 325);
            stage.addActor(volumeBox);
            sfxVolumeBoxes[i] = volumeBox;
            volumeBox.setVisible(false);
        }




        playButton.setPosition((float) background.getWidth()/2 - playButton.getWidth()/2, 420);

        /** Listens for when the mouse moves over the button to switch to the hover state  for the play button*/
        playButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                buttonTransition.play(sfxVolume / 10f);
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

        optionsButton.setPosition((float) background.getWidth()/2 - optionsButton.getWidth()/2, 365);

        /** Listens for when the mouse moves over the button to switch to the hover state  for the options button*/
        optionsButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                buttonTransition.play(sfxVolume / 10f);
                buttonSelected = "options";
                return true;
            }
        });

        optionsSelectImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO: GO TO OPTIONS SCREEN
                inOptionsMenu = true;
                buttonClicked.play(sfxVolume / 10f);
                Preferences prefs = Gdx.app.getPreferences("MusicVolume");
                musPref = prefs.getInteger("musicVolume", 10);
                prefs = Gdx.app.getPreferences("SFXVolume");
                sfxPref = prefs.getInteger("sfxVolume", 10);
            }
        });

        optionsSelectImage.setPosition((float) background.getWidth()/2 - optionsSelectImage.getWidth()/2, optionsButton.getY()-15);
        stage.addActor(optionsSelectImage);
        stage.addActor(optionsButton);

        //options button
        Texture creditsButtonTexture = GameController.getInstance().objectController.creditsNonGlow;
        TextureRegionDrawable creditsButtonDrawable = new TextureRegionDrawable(new TextureRegion(creditsButtonTexture));
        creditsButton = new Image(creditsButtonDrawable);

        creditsButton.setPosition((float) background.getWidth()/2 - creditsButton.getWidth()/2, 310);

        /** Listens for when the mouse moves over the button to switch to the hover state  for the options button*/
        creditsButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                buttonTransition.play(sfxVolume / 10f);
                buttonSelected = "credits";
                return true;
            }
        });

        creditsSelectImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inCredits = true;
                buttonClicked.play(sfxVolume / 10f);
            }
        });

        creditsSelectImage.setPosition((float) background.getWidth()/2 - creditsSelectImage.getWidth()/2, creditsButton.getY() - 15);
        stage.addActor(creditsSelectImage);
        stage.addActor(creditsButton);

        // quit button
        /** The texture for the quit button */
        Texture quitButtonTexture = GameController.getInstance().objectController.quitButton;
        TextureRegionDrawable quitButtonDrawable = new TextureRegionDrawable(new TextureRegion(quitButtonTexture));
        quitButton = new Image(quitButtonDrawable);

        quitButton.setPosition((float) background.getWidth()/2 - quitButton.getWidth()/2, 250);

        /** Listens for when the mouse moves over the button to switch to the hover state  for the options button*/
        quitButton.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                buttonTransition.play(sfxVolume / 10f);
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

        quitSelectImage.setPosition((float) background.getWidth()/2 - quitSelectImage.getWidth()/2, quitButton.getY()-12);

        stage.addActor(quitSelectImage);
        stage.addActor(quitButton);

        // Options Menu
        musicText.setPosition(400, 375);
        sfxText.setPosition(420, 325);

        stage.addActor(musicText);
        stage.addActor(sfxText);

    }

    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        SyncController syncController = GameController.getInstance().syncController;
        syncController.update(true);
        float pulseScale = syncController.uiSyncPulse.uiPulseScale;

        backButton.setScale(pulseScale, pulseScale);
        backButton.setOrigin( (backButton.getWidth() / 2), backButton.getHeight() / 2);



        if (!inOptionsMenu && !inCredits) {
            backButton.setVisible(false);
            creditsScreen.setVisible(false);
            indicatorStar.setVisible(false);
            musicText.setVisible(false);
            sfxText.setVisible(false);
            volumeLowerImage.setVisible(false);
            volumeLowerImage2.setVisible(false);
            volumeHigherImage.setVisible(false);
            volumeHigherImage2.setVisible(false);
            for (Image i : musicVolumeBoxes) {
                i.setVisible(false);
            }
            for (Image i : sfxVolumeBoxes) {
                i.setVisible(false);
            }
            playButton.setVisible(true);
            optionsButton.setVisible(true);
            quitButton.setVisible(true);
            creditsButton.setVisible(true);
        }

        if (inOptionsMenu) {
            backButton.setVisible(true);
            creditsScreen.setVisible(false);
            switch (optMenuSel) {
                case 0:
                    indicatorStar.setPosition(350, 375);
                    break;
                case 1:
                    indicatorStar.setPosition(370, 325);
                    break;
                default:
                    break;
            }
            indicatorStar.setScale(pulseScale / 2);
            indicatorStar.setVisible(true);
            musicText.setVisible(true);
            sfxText.setVisible(true);
            volumeLowerImage.setVisible(true);
            volumeLowerImage2.setVisible(true);
            volumeHigherImage.setVisible(true);
            volumeHigherImage2.setVisible(true);
            for (int i = 0; i < 10; i++) { // change 10 to musicVolume from prefs. same below for sfx
                musicVolumeBoxes[i].setVisible(i < musPref);
            }
            for (int i = 0; i < sfxPref; i++) {
                sfxVolumeBoxes[i].setVisible(i < sfxPref);
            }
            playButton.setVisible(false);
            optionsButton.setVisible(false);
            quitButton.setVisible(false);
            creditsButton.setVisible(false);
        }
        else if (inCredits) {
            backButton.setVisible(true);
            creditsScreen.setVisible(true);
            playButton.setVisible(false);
            optionsButton.setVisible(false);
            quitButton.setVisible(false);
            creditsButton.setVisible(false);
        }
        else if(buttonSelected.equals("play")) {
            playButton.setVisible(false);
            optionsButton.setVisible(true);
            quitButton.setVisible(true);
            creditsButton.setVisible(true);
        } else if(buttonSelected.equals("options")) {
            playButton.setVisible(true);
            optionsButton.setVisible(false);
            quitButton.setVisible(true);
            creditsButton.setVisible(true);
        } else if(buttonSelected.equals("quit")) {
            playButton.setVisible(true);
            optionsButton.setVisible(true);
            quitButton.setVisible(false);
            creditsButton.setVisible(true);
        } else if (buttonSelected.equals("credits")) {
            playButton.setVisible(true);
            optionsButton.setVisible(true);
            quitButton.setVisible(true);
            creditsButton.setVisible(false);
        }

        playSelectImage.setVisible(buttonSelected.equals("play"));
        optionsSelectImage.setVisible(buttonSelected.equals("options"));
        quitSelectImage.setVisible(buttonSelected.equals("quit"));
        creditsSelectImage.setVisible(buttonSelected.equals("credits"));

        handleInput();

        playSelectImage.setScale(pulseScale, pulseScale);
        playSelectImage.setOrigin( (playSelectImage.getWidth() / 2), playSelectImage.getHeight() / 2);

        playSelectImage.setScale(pulseScale, pulseScale);
        playSelectImage.setOrigin( (playSelectImage.getWidth() / 2), playSelectImage.getHeight() / 2);

        optionsSelectImage.setScale(pulseScale, pulseScale);
        optionsSelectImage.setOrigin( (optionsSelectImage.getWidth() / 2), optionsSelectImage.getHeight() / 2);

        quitSelectImage.setScale(pulseScale, pulseScale);
        quitSelectImage.setOrigin( (quitSelectImage.getWidth() / 2), quitSelectImage.getHeight() / 2);

        creditsSelectImage.setScale(pulseScale, pulseScale);
        creditsSelectImage.setOrigin( (creditsSelectImage.getWidth() / 2), creditsSelectImage.getHeight() / 2);

        if (inOptionsMenu || inCredits) {
            playSelectImage.setVisible(false);
            optionsSelectImage.setVisible(false);
            quitSelectImage.setVisible(false);
            creditsSelectImage.setVisible(false);
            backgroundImage.setVisible(false);
            blurredBackgroundImage.setVisible(true);
        } else {
            blurredBackgroundImage.setVisible(false);
            backgroundImage.setVisible(true);
        }


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
        enterPrevious = enterPressed;
        downPrevious = downPressed;
        upPrevious = upPressed;
        oPrevious = oPressed;
        leftPrevious = leftPressed;
        rightPrevious = rightPressed;

        enterPressed = Gdx.input.isKeyPressed(Input.Keys.ENTER);
        downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        upPressed = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        oPressed = Gdx.input.isKeyPressed(Input.Keys.O);
        rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);

        if (upPressed && !upPrevious) {
            if (!inOptionsMenu && !inCredits) {
                switch (buttonSelected) {
                    case "play":
                        buttonSelected = "quit";
                        break;
                    case "options":
                        buttonSelected = "play";
                        break;
                    case "quit":
                        buttonSelected = "credits";
                        break;
                    case "credits":
                        buttonSelected = "options";
                        break;
                }
            }
            else if (inOptionsMenu) {
                optMenuSel--;
                if (optMenuSel < 0) {
                    optMenuSel = NUM_OPTS - 1;

                }
            }
            buttonTransition.play(sfxVolume / 10f);
        } else if (downPressed && !downPrevious) {
            if (!inOptionsMenu && !inCredits) {
                switch (buttonSelected) {
                    case "quit":
                        buttonSelected = "play";
                        break;
                    case "play":
                        buttonSelected = "options";
                        break;
                    case "options":
                        buttonSelected = "credits";
                        break;
                    case "credits":
                        buttonSelected = "quit";
                        break;
                }
            }
            else if (inOptionsMenu){
                optMenuSel = ((optMenuSel + 1) % NUM_OPTS);
                }
            buttonTransition.play(sfxVolume / 10f);
        } else if (rightPressed && !rightPrevious && inOptionsMenu) {
            switch (optMenuSel) {
                case 0: // MUSIC
                    if (musPref < 10) {
                        musPref++;
                        mus.setVolume(musPref / 10f);
                    }
                    break;
                case 1: // SFX
                    if (sfxPref < 10) {
                        sfxPref++;
                        sfxVolume = sfxPref;
                        buttonClicked.play(sfxVolume / 10f);
                    }
                    break;
                default:
                    break;
            }
        }else if (leftPressed && !leftPrevious && inOptionsMenu) {
            switch (optMenuSel) {
                case 0: // MUSIC
                    if (musPref > 0) {
                        musicVolumeBoxes[musPref - 1].setVisible(false);
                        musPref--;
                        mus.setVolume(musPref / 10f);
                    }
                    break;
                case 1: // SFX
                    if (sfxPref > 0) {
                        sfxVolumeBoxes[sfxPref - 1].setVisible(false);
                        sfxPref--;
                        sfxVolume = sfxPref;
                        buttonClicked.play(sfxVolume / 10f);
                    }
                    break;
                default:
                    break;
            }
        }else if (enterPressed && !enterPrevious) {
                if (!inOptionsMenu && !inCredits) {
                    switch (buttonSelected) {
                        case "play":
                            listener.exitScreen(this, GameController.GO_TO_LEVEL_SELECT);
                            break;
                        case "options":
                            inOptionsMenu = true;
                            Preferences prefs = Gdx.app.getPreferences("MusicVolume");
                            musPref = prefs.getInteger("musicVolume", 10);
                            prefs = Gdx.app.getPreferences("SFXVolume");
                            sfxPref = prefs.getInteger("sfxVolume", 10);
                            break;
                        case "credits":
                            inCredits = true;
                            break;
                        case "quit":
                            listener.exitScreen(this, GameController.EXIT_QUIT);
                            break;
                    }
                }
                else if (inCredits) {
                    inCredits = false;
                }
                else {
                    inOptionsMenu = false;
                    Preferences prefs = Gdx.app.getPreferences("MusicVolume");
                    prefs.putInteger("musicVolume", musPref);
                    prefs.flush();
                    prefs = Gdx.app.getPreferences("SFXVolume");
                    prefs.putInteger("sfxVolume", sfxPref);
                    prefs.flush();

                    sfxVolume = sfxPref;
                }
            buttonClicked.play(sfxVolume / 10f);
        }
    }

}
