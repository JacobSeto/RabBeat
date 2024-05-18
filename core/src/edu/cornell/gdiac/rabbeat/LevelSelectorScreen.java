package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import edu.cornell.gdiac.util.ScreenListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/** Class that represents the level select menu screen for the game
 * Displays 12 buttons that represent each level
 */

public class LevelSelectorScreen extends ScreenAdapter {
    private Game game;
    private Stage stage;
    private ScreenListener listener;

    /** Returns whether a button is being hovered over */
    private boolean hover1 = false;
    private boolean hover2 = false;
    private boolean hover3 = false;
    private boolean hover4 = false;
    private boolean hover5 = false;
    private boolean hover6 = false;
    private boolean hover7 = false;
    private boolean hover8 = false;
    private boolean hover9 = false;
    private boolean hover10 = false;
    private boolean hover11 = false;
    private boolean hover12 = false;

    public boolean finishedLoadingLevel = false;

    /** Images for the button hover images when hovering over a button */
    private Image hoverImage1;
    private Image hoverImage2;
    private Image hoverImage3;
    private Image hoverImage4;
    private Image hoverImage5;
    private Image hoverImage6;
    private Image hoverImage7;
    private Image hoverImage8;
    private Image hoverImage9;
    private Image hoverImage10;
    private Image hoverImage11;
    private Image hoverImage12;

    private boolean downPressed;
    private boolean downPrevious;

    private boolean upPressed;
    private boolean upPrevious;

    private boolean rightPressed;
    private boolean rightPrevious;

    private boolean leftPressed;
    private boolean leftPrevious;

    private boolean enterPressed;
    private boolean enterPrevious;

    private Sound menuTransitionSound;

    private float sfxVolume;

    /** Reference to the numberOfLevels variable in GameController */
    private final int numberOfLevels = GameController.getInstance().getNumberOfLevels();

    /** Texture for the level button */
    Texture buttonTexture;

    public LevelSelectorScreen(Game game) {
        this.game = game;
        Preferences prefs = Gdx.app.getPreferences("SavedLevelsUnlocked");
        GameController.getInstance().setLevelsUnlocked(prefs.getInteger("levelsUnlocked", 1));
    }

    public void setMenuTransitionSound(Sound s) {
        menuTransitionSound = s;
    }

    public void setSFXVolume(float vol) {sfxVolume = vol;}

    /** Displays the button UI for each level and adds a clickListener that detects whether
     * the button has been clicked and takes the player to the desired level
     */
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Background
        Texture background = GameController.getInstance().getObjectController().levelSelectBackground;
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(background));
        Image bg = new Image(backgroundDrawable);
        bg.setPosition(0, 0);
        stage.addActor(bg);

        Texture backButtonTexture = GameController.getInstance().objectController.levelSelectBackButton;
        Image backButton = new Image(backButtonTexture);
        backButton.setPosition(27f, background.getHeight() - backButton.getHeight() - 25);
        stage.addActor(backButton);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.exitScreen(LevelSelectorScreen.this, GameController.MAIN_MENU);
            }
        });

        /** Loops through all buttons */
        for(int i=1; i<= numberOfLevels; i++) {
            int finalI = i;

            if(i <= GameController.getInstance().getLevelsUnlocked()) {
                buttonTexture = GameController.getInstance().objectController.getUnlockedButtonTexture(finalI);
            } else {
                buttonTexture = GameController.getInstance().objectController.getLockedButtonTexture(finalI);
            }

            TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(
                    buttonTexture));
            BitmapFont font = new BitmapFont();
            TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.up = buttonDrawable;
            textButtonStyle.font = font;
            TextButton levelButton = new TextButton("", textButtonStyle);

            switch(i) {
                case(1):
                    levelButton.setPosition(476.5f, background.getHeight()-levelButton.getHeight()-200.5f);
                    break;
                case(2):
                    levelButton.setPosition(463.5f, background.getHeight()-levelButton.getHeight()-270);
                    break;
                case(3):
                    levelButton.setPosition(484.5f, background.getHeight()-levelButton.getHeight()-315);
                    break;
                case(4):
                    levelButton.setPosition(477.5f, background.getHeight()-levelButton.getHeight()-374.5f);
                    break;
                case(5):
                    levelButton.setPosition(748.5f, background.getHeight()-levelButton.getHeight()-215);
                    break;
                case(6):
                    levelButton.setPosition(788.5f, background.getHeight()-levelButton.getHeight()-276.5f);
                    break;
                case(7):
                    levelButton.setPosition(743, background.getHeight()-levelButton.getHeight()-326);
                    break;
                case(8):
                    levelButton.setPosition(734.5f, background.getHeight()-levelButton.getHeight()-390);
                    break;
                case(9):
                    levelButton.setPosition(1025.5f, background.getHeight()-levelButton.getHeight()-211);
                    break;
                case(10):
                    levelButton.setPosition(1031.5f, background.getHeight()-levelButton.getHeight()-280.5f);
                    break;
                case(11):
                    levelButton.setPosition(1014, background.getHeight()-levelButton.getHeight()-324.5f);
                    break;
                case(12):
                    levelButton.setPosition(1034.5f, background.getHeight()-levelButton.getHeight()-389.5f);
                    break;
            }


            stage.addActor(levelButton);

            if(i <= GameController.getInstance().getLevelsUnlocked()) {
                levelButton.addListener(new InputListener() {
                    @Override
                    public boolean mouseMoved(InputEvent event, float x, float y) {
                        hover1 = false;
                        hover2 = false;
                        hover3 = false;
                        hover4 = false;
                        hover5 = false;
                        hover6 = false;
                        hover7 = false;
                        hover8 = false;
                        hover9 = false;
                        hover10 = false;
                        hover11 = false;
                        hover12 = false;

                        menuTransitionSound.play(sfxVolume);

                        switch (finalI) {
                            case (1):
                                hover1 = true;
                                break;
                            case (2):
                                hover2 = true;
                                break;
                            case (3):
                                hover3 = true;
                                break;
                            case (4):
                                hover4 = true;
                                break;
                            case (5):
                                hover5 = true;
                                break;
                            case (6):
                                hover6 = true;
                                break;
                            case (7):
                                hover7 = true;
                                break;
                            case (8):
                                hover8 = true;
                                break;
                            case (9):
                                hover9 = true;
                                break;
                            case (10):
                                hover10 = true;
                                break;
                            case (11):
                                hover11 = true;
                                break;
                            case (12):
                                hover12 = true;
                                break;
                        }
                        return true;
                    }

                });
            }

            Texture hoverRegion;
            if (i == 1) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        1);
                hoverImage1 = new Image(hoverRegion);
                hoverImage1.setPosition(476.5f-16,
                        background.getHeight() - hoverImage1.getHeight() - 200.5f+16);

                hoverImage1.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(1);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                            GameController.displayStartCutScenes = true;
                            GameController.showLevel1FirstCutScene = false;
                            GameController.showLevel1SecondCutScene = false;
                            GameController.showLevel1ThirdCutScene = false;
                            GameController.showLevel1FourthCutScene = false;
                        }
                    }
                });

            } else if (i == 2) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        2);
                hoverImage2 = new Image(hoverRegion);
                hoverImage2.setPosition(463.5f-16,
                        background.getHeight() - hoverImage2.getHeight() - 270+16);

                hoverImage2.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(2);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });
            } else if (i == 3) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        3);
                hoverImage3 = new Image(hoverRegion);
                hoverImage3.setPosition(484.5f - 16,
                        background.getHeight() - hoverImage3.getHeight() - 315f + 16);


                hoverImage3.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(3);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });
            } else if (i == 4) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        4);
                hoverImage4 = new Image(hoverRegion);
                hoverImage4.setPosition(477.5f - 16,
                        background.getHeight() - hoverImage4.getHeight() - 374.5f + 16);

                hoverImage4.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(4);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });
            } else if (i == 5) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        5);
                hoverImage5 = new Image(hoverRegion);
                hoverImage5.setPosition(748.5f - 16,
                        background.getHeight() - hoverImage5.getHeight() - 215 + 16);

                hoverImage5.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(5);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });

            } else if (i == 6) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(6);
                hoverImage6 = new Image(hoverRegion);
                hoverImage6.setPosition(788.5f - 16,
                        background.getHeight() - hoverImage6.getHeight() - 276.5f + 16);


                hoverImage6.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(6);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });
            } else if (i == 7) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        7);
                hoverImage7 = new Image(hoverRegion);
                hoverImage7.setPosition(743 - 16,
                        background.getHeight() - hoverImage7.getHeight() - 326 + 16);


                hoverImage7.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(7);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });

            } else if (i == 8) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        8);
                hoverImage8 = new Image(hoverRegion);
                hoverImage8.setPosition(734.5f - 16,
                        background.getHeight() - hoverImage8.getHeight() - 390 + 16);


                hoverImage8.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(8);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });
            } else if (i == 9) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        9);
                hoverImage9 = new Image(hoverRegion);
                hoverImage9.setPosition(1025.5f - 16,
                        background.getHeight() - hoverImage9.getHeight() - 211 + 16);

                hoverImage9.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(9);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                            GameController.displayStartCutScenes = true;
                            GameController.showLevel9StartingScreen[0] = false;
                            GameController.showLevel9StartingScreen[1] = false;
                        }
                    }
                });
            } else if (i == 10) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        10);
                hoverImage10 = new Image(hoverRegion);
                hoverImage10.setPosition(1031.5f - 16,
                        background.getHeight() - hoverImage10.getHeight() - 280.5f + 16);


                hoverImage10.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(10);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });
            } else if (i == 11) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                        11);
                hoverImage11 = new Image(hoverRegion);
                hoverImage11.setPosition(1014 - 16,
                        background.getHeight() - hoverImage11.getHeight() - 324.5f + 16);

                hoverImage11.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(11);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });

            } else if (i == 12) {
                hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(12);
                hoverImage12 = new Image(hoverRegion);
                hoverImage12.setPosition(1034.5f - 16,
                        background.getHeight() - hoverImage12.getHeight() - 389.5f + 16);


                hoverImage12.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                            GameController.getInstance().setCurrentLevelInt(12);
                            listener.exitScreen(LevelSelectorScreen.this, 0);
                        }
                    }
                });
            }
        }

    }

    public void render(float delta) {


        handleInput();

        if (finishedLoadingLevel){
            return;
        }

        hoverImage1.setVisible(hover1);
        hoverImage2.setVisible(hover2);
        hoverImage3.setVisible(hover3);
        hoverImage4.setVisible(hover4);
        hoverImage5.setVisible(hover5);
        hoverImage6.setVisible(hover6);
        hoverImage7.setVisible(hover7);
        hoverImage8.setVisible(hover8);
        hoverImage9.setVisible(hover9);
        hoverImage10.setVisible(hover10);
        hoverImage11.setVisible(hover11);
        hoverImage12.setVisible(hover12);

        if(hover1)
            stage.addActor(hoverImage1);
        if(hover2)
            stage.addActor(hoverImage2);
        if(hover3)
            stage.addActor(hoverImage3);
        if(hover4)
            stage.addActor(hoverImage4);
        if(hover5)
            stage.addActor(hoverImage5);
        if(hover6)
            stage.addActor(hoverImage6);
        if(hover7)
            stage.addActor(hoverImage7);
        if(hover8)
            stage.addActor(hoverImage8);
        if(hover9)
            stage.addActor(hoverImage9);
        if(hover10)
            stage.addActor(hoverImage10);
        if(hover11)
            stage.addActor(hoverImage11);
        if(hover12)
            stage.addActor(hoverImage12);

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

    /** reads the data from the input keys and changes the buttonSelected String accordingly */
    public void handleInput() {
        enterPrevious = enterPressed;
        downPrevious = downPressed;
        upPrevious = upPressed;
        leftPrevious = leftPressed;
        rightPrevious = rightPressed;

        enterPressed = Gdx.input.isKeyPressed(Input.Keys.ENTER);
        downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        upPressed = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        int numberOfLevelsUnlocked = GameController.getInstance().getLevelsUnlocked();
        if(!hover1 && !hover2 && !hover3 && !hover4 && !hover5 && !hover6 && !hover7 && !hover8 && !hover9 && !hover10 && !hover11 && !hover12)
            switch(GameController.getInstance().getCurrentLevelInt()) {
                case (1):
                    hover1 = true;
                    break;
                case (2):
                    hover2 = true;
                    break;
                case (3):
                    hover3 = true;
                    break;
                case (4):
                    hover4 = true;
                    break;
                case (5):
                    hover5 = true;
                    break;
                case (6):
                    hover6 = true;
                    break;
                case (7):
                    hover7 = true;
                    break;
                case (8):
                    hover8 = true;
                    break;
                case (9):
                    hover9 = true;
                    break;
                case (10):
                    hover10 = true;
                    break;
                case (11):
                    hover11 = true;
                    break;
                case (12):
                    hover12 = true;
                    break;
            }
        else if (downPressed && !downPrevious) {
            menuTransitionSound.play(sfxVolume);
            if(hover1 && numberOfLevelsUnlocked > 1) {
                hover1 = false;
                hover2 = true;
            } else if(hover2 && numberOfLevelsUnlocked > 2) {
                hover2 = false;
                hover3 = true;
            } else if(hover3 && numberOfLevelsUnlocked > 3) {
                hover3 = false;
                hover4 = true;
            } else if(hover4 && numberOfLevelsUnlocked > 4) {
                hover4 = false;
                hover5 = true;
            } else if(hover5 && numberOfLevelsUnlocked > 5) {
                hover5 = false;
                hover6 = true;
            } else if(hover6 && numberOfLevelsUnlocked > 6) {
                hover6 = false;
                hover7 = true;
            } else if(hover7 && numberOfLevelsUnlocked > 7) {
                hover7 = false;
                hover8 = true;
            } else if(hover8 && numberOfLevelsUnlocked > 8) {
                hover8 = false;
                hover9 = true;
            } else if(hover9 && numberOfLevelsUnlocked > 9) {
                hover9 = false;
                hover10 = true;
            } else if(hover10 && numberOfLevelsUnlocked > 10) {
                hover10 = false;
                hover11 = true;
            } else if(hover11 && numberOfLevelsUnlocked > 11) {
                hover11 = false;
                hover12 = true;
            }
        } else if (upPressed && !upPrevious) {
            menuTransitionSound.play(sfxVolume);
            if (hover12) {
                hover12 = false;
                hover11 = true;
            } else if (hover11) {
                hover11 = false;
                hover10 = true;
            } else if (hover10) {
                hover10 = false;
                hover9 = true;
            } else if (hover9) {
                hover9 = false;
                hover8 = true;
            } else if (hover8) {
                hover8 = false;
                hover7 = true;
            } else if (hover7) {
                hover7 = false;
                hover6 = true;
            } else if (hover6) {
                hover6 = false;
                hover5 = true;
            } else if (hover5) {
                hover5 = false;
                hover4 = true;
            } else if (hover4) {
                hover4 = false;
                hover3 = true;
            } else if (hover3) {
                hover3 = false;
                hover2 = true;
            } else if (hover2) {
                hover2 = false;
                hover1 = true;
            }
        }
            else if (rightPressed && !rightPrevious) {
                menuTransitionSound.play(sfxVolume);
                if(hover8) {
                    hover8 = false;
                    hover12 = true;
                } else if(hover7) {
                    hover7 = false;
                    hover11 = true;
                } else if(hover6) {
                    hover6 = false;
                    hover10 = true;
                } else if(hover5) {
                    hover5 = false;
                    hover9 = true;
                } else if(hover4) {
                    hover4 = false;
                    hover8 = true;
                } else if(hover3) {
                    hover3 = false;
                    hover7 = true;
                } else if(hover2) {
                    hover2 = false;
                    hover6 = true;
                } else if(hover1) {
                    hover1 = false;
                    hover5 = true;
                }
            }
            else if (leftPressed && !leftPrevious) {
                menuTransitionSound.play(sfxVolume);
                if(hover8) {
                    hover8 = false;
                    hover4 = true;
                } else if(hover7) {
                    hover7 = false;
                    hover3 = true;
                } else if(hover6) {
                    hover6 = false;
                    hover2 = true;
                } else if(hover5) {
                    hover5 = false;
                    hover1 = true;
                } else if(hover12) {
                    hover12 = false;
                    hover8 = true;
                } else if(hover11) {
                    hover11 = false;
                    hover7 = true;
                } else if(hover10) {
                    hover10 = false;
                    hover6 = true;
                } else if(hover9) {
                    hover9 = false;
                    hover5 = true;
                }
            }
        else if (enterPressed && !enterPrevious) {
            if(hover1) {
                GameController.getInstance().setCurrentLevelInt(1);
                GameController.displayStartCutScenes = true;
                GameController.showLevel1FirstCutScene = false;
                GameController.showLevel1SecondCutScene = false;
                GameController.showLevel1ThirdCutScene = false;
                GameController.showLevel1FourthCutScene = false;
            } else if(hover2) {
                GameController.getInstance().setCurrentLevelInt(2);
            } else if(hover3) {
                GameController.getInstance().setCurrentLevelInt(3);
            } else if(hover4) {
                GameController.getInstance().setCurrentLevelInt(4);
            } else if(hover5) {
                GameController.getInstance().setCurrentLevelInt(5);
            } else if(hover6) {
                GameController.getInstance().setCurrentLevelInt(6);
            } else if(hover7) {
                GameController.getInstance().setCurrentLevelInt(7);
            } else if(hover8) {
                GameController.getInstance().setCurrentLevelInt(8);
            } else if(hover9) {
                GameController.getInstance().setCurrentLevelInt(9);
                GameController.displayStartCutScenes = true;
                GameController.showLevel9StartingScreen[0] = false;
                GameController.showLevel9StartingScreen[1] = false;
            } else if(hover10) {
                GameController.getInstance().setCurrentLevelInt(10);
            } else if(hover11) {
                GameController.getInstance().setCurrentLevelInt(11);
            } else if(hover12) {
                GameController.getInstance().setCurrentLevelInt(12);
            }
            listener.exitScreen(LevelSelectorScreen.this, 0);

        }
    }

}
