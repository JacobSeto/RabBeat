package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
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

    private TextButton hoverButton;

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

    private Texture hoverRegion;

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

    /** Reference to the numberOfLevels variable in GameController */
    private final int numberOfLevels = GameController.getInstance().getNumberOfLevels();

    /** Texture for the level button */
    Texture buttonTexture;

    public LevelSelectorScreen(Game game) {
        this.game = game;
        Preferences prefs = Gdx.app.getPreferences("SavedLevelsUnlocked");
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
        Texture background = GameController.getInstance().getObjectController().levelSelectBackground;
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
                //TODO: Comment this out:
//                buttonTexture = GameController.getInstance().objectController.getLockedButtonTexture(finalI);
                buttonTexture = GameController.getInstance().objectController.getUnlockedButtonTexture(finalI);
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
//                    hoverButton.setPosition(415, background.getHeight()-hoverButton.getHeight()-161);
                    levelButton.setPosition(478, background.getHeight()-levelButton.getHeight()-200);
                    break;
                case(2):
//                    hoverButton.setPosition(414, background.getHeight()-hoverButton.getHeight()-213);
                    levelButton.setPosition(464, background.getHeight()-levelButton.getHeight()-266);
                    break;
                case(3):
//                    hoverButton.setPosition(420, background.getHeight()-hoverButton.getHeight()-227);
                    levelButton.setPosition(483, background.getHeight()-levelButton.getHeight()-311-3);
                    break;
                case(4):
                    levelButton.setPosition(475, background.getHeight()-levelButton.getHeight()-374-10);
                    break;
                case(5):
                    levelButton.setPosition(750, background.getHeight()-levelButton.getHeight()-214);
                    break;
                case(6):
                    levelButton.setPosition(789, background.getHeight()-levelButton.getHeight()-274);
                    break;
                case(7):
                    levelButton.setPosition(737, background.getHeight()-levelButton.getHeight()-322);
                    break;
                case(8):
                    levelButton.setPosition(734, background.getHeight()-levelButton.getHeight()-390);
                    break;
                case(9):
                    levelButton.setPosition(1024, background.getHeight()-levelButton.getHeight()-209);
                    break;
                case(10):
                    levelButton.setPosition(1030, background.getHeight()-levelButton.getHeight()-278);
                    break;
                case(11):
                    levelButton.setPosition(1010, background.getHeight()-levelButton.getHeight()-324);
                    break;
                case(12):
                    levelButton.setPosition(1029, background.getHeight()-levelButton.getHeight()-387);
                    break;
                  //OLD BUTTONS
//                case(1):
//                    levelButton.setPosition(483, background.getHeight()-levelButton.getHeight()-206);
//                    break;
//                case(2):
//                    levelButton.setPosition(469, background.getHeight()-levelButton.getHeight()-271);
//                    break;
//                case(3):
//                    levelButton.setPosition(491, background.getHeight()-levelButton.getHeight()-329);
//                    break;
//                case(4):
//                    levelButton.setPosition(485, background.getHeight()-levelButton.getHeight()-384);
//                    break;
//                case(5):
//                    levelButton.setPosition(760, background.getHeight()-levelButton.getHeight()-230);
//                    break;
//                case(6):
//                    levelButton.setPosition(789, background.getHeight()-levelButton.getHeight()-288);
//                    break;
//                case(7):
//                    levelButton.setPosition(750, background.getHeight()-levelButton.getHeight()-342);
//                    break;
//                case(8):
//                    levelButton.setPosition(750, background.getHeight()-levelButton.getHeight()-397);
//                    break;
//                case(9):
//                    levelButton.setPosition(1041, background.getHeight()-levelButton.getHeight()-228);
//                    break;
//                case(10):
//                    levelButton.setPosition(1036, background.getHeight()-levelButton.getHeight()-281);
//                    break;
//                case(11):
//                    levelButton.setPosition(1024, background.getHeight()-levelButton.getHeight()-337);
//                    break;
//                case(12):
//                    levelButton.setPosition(1041, background.getHeight()-levelButton.getHeight()-397);
//                    break;
            }


//            levelButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
//                        GameController.getInstance().setCurrentLevelInt(finalI);
//                        listener.exitScreen(LevelSelectorScreen.this, 0);
//                    }
//                }
//            });

            stage.addActor(levelButton);

            //have a separate for loop for the hover images
            if(i <= 4) {
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
                                //TODO: ENTER
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

                if (i == 1) {
                    hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                            1);
                    hoverImage1 = new Image(hoverRegion);
                    hoverImage1.setPosition(411 + 62 - 15,
                            background.getHeight() - hoverImage1.getHeight() - 161 - 37 + 15);

                    hoverImage1.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover1 = false;
                        }

                    });

                    hoverImage1.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                                GameController.getInstance().setCurrentLevelInt(1);
                                listener.exitScreen(LevelSelectorScreen.this, 0);
                            }
                        }
                    });

                } else if (i == 2) {
                    hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                            2);
                    hoverImage2 = new Image(hoverRegion);
                    hoverImage2.setPosition(464 - 20,
                            background.getHeight() - hoverImage1.getHeight() - 270 + 30);

                    hoverImage2.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover2 = false;
                        }

                    });

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
                    hoverImage3.setPosition(485.5f - 19,
                            background.getHeight() - hoverImage3.getHeight() - 315.5f + 20);

                    hoverImage3.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover3 = false;
                        }

                    });

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
                    hoverImage4.setPosition(478f - 19,
                            background.getHeight() - hoverImage4.getHeight() - 378 + 10);

                    hoverImage4.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover4 = false;
                        }

                    });

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
                    hoverImage5.setPosition(478f - 19,
                            background.getHeight() - hoverImage5.getHeight() - 378 + 10);

                    hoverImage5.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover5 = false;
                        }

                    });

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
                    hoverImage6.setPosition(478f - 19,
                            background.getHeight() - hoverImage6.getHeight() - 378 + 10);

                    hoverImage6.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover6 = false;
                        }

                    });

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
                    hoverImage7.setPosition(411 + 62 - 15,
                            background.getHeight() - hoverImage7.getHeight() - 161 - 37 + 15);

                    hoverImage7.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover7 = false;
                        }

                    });

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
                    hoverImage8.setPosition(464 - 20,
                            background.getHeight() - hoverImage8.getHeight() - 270 + 30);

                    hoverImage8.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover8 = false;
                        }

                    });

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
                    hoverImage9.setPosition(485.5f - 19,
                            background.getHeight() - hoverImage9.getHeight() - 315.5f + 20);

                    hoverImage9.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover9 = false;
                        }

                    });

                    hoverImage9.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (finalI <= GameController.getInstance().getLevelsUnlocked()) {
                                GameController.getInstance().setCurrentLevelInt(9);
                                listener.exitScreen(LevelSelectorScreen.this, 0);
                            }
                        }
                    });
                } else if (i == 10) {
                    hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(
                            10);
                    hoverImage10 = new Image(hoverRegion);
                    hoverImage10.setPosition(478f - 19,
                            background.getHeight() - hoverImage10.getHeight() - 378 + 10);

                    hoverImage10.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover10 = false;
                        }

                    });

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
                    hoverImage11.setPosition(478f - 19,
                            background.getHeight() - hoverImage11.getHeight() - 378 + 10);

                    hoverImage11.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover11 = false;
                        }

                    });

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
                    hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(6);
                    hoverImage12 = new Image(hoverRegion);
                    hoverImage12.setPosition(478f - 19,
                            background.getHeight() - hoverImage12.getHeight() - 378 + 10);

                    hoverImage12.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer,
                                Actor toActor) {
                            hover12 = false;
                        }

                    });

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
        //TODO: make sure to add condition testing whether the level is unlocked!!

    }

    public void render(float delta) {
        handleInput();

        hoverImage1.setVisible(hover1);
        hoverImage2.setVisible(hover2);
        hoverImage3.setVisible(hover3);
        hoverImage4.setVisible(hover4);

        if(hover1)
            stage.addActor(hoverImage1);
        if(hover2)
            stage.addActor(hoverImage2);
        if(hover3)
            stage.addActor(hoverImage3);
        if(hover4)
            stage.addActor(hoverImage4);

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
        if(!hover1 && !hover2 && !hover3 && !hover4 && !hover5 && !hover6 && !hover7 && !hover8 && !hover9 && !hover10 && !hover11 && !hover12)
            hover1 = true;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if(hover1) {
                hover1 = false;
                hover2 = true;
            } else if(hover2) {
                hover2 = false;
                hover3 = true;
            } else if(hover3) {
                hover3 = false;
                hover4 = true;
            } else if(hover4) {
                hover4 = false;
                hover5 = true;
            } if(hover5) {
                hover5 = false;
                hover6 = true;
            } else if(hover6) {
                hover6 = false;
                hover7 = true;
            } else if(hover7) {
                hover7 = false;
                hover8 = true;
            } else if(hover8) {
                hover8 = false;
                hover9 = true;
            } if(hover9) {
                hover9 = false;
                hover10 = true;
            } else if(hover10) {
                hover10 = false;
                hover11 = true;
            } else if(hover11) {
                hover11 = false;
                hover12 = true;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if(hover12) {
                hover12 = false;
                hover11 = true;
            } else if(hover11) {
                hover11 = false;
                hover10 = true;
            } else if(hover10) {
                hover10 = false;
                hover9 = true;
            } else if(hover9) {
                hover9 = false;
                hover8 = true;
            } if(hover8) {
                hover8 = false;
                hover7 = true;
            } else if(hover7) {
                hover7 = false;
                hover6 = true;
            } else if(hover6) {
                hover6 = false;
                hover5 = true;
            } else if(hover5) {
                hover5 = false;
                hover4 = true;
            } if(hover4) {
                hover4 = false;
                hover3 = true;
            } else if(hover3) {
                hover3 = false;
                hover2 = true;
            } else if(hover2) {
                hover2 = false;
                hover1 = true;
            }
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if(hover1) {
                GameController.getInstance().setCurrentLevelInt(1);
            } else if(hover2) {
                GameController.getInstance().setCurrentLevelInt(2);
            } else if(hover3) {
                GameController.getInstance().setCurrentLevelInt(3);
            } else if(hover4) {
                GameController.getInstance().setCurrentLevelInt(4);
            } if(hover5) {
                GameController.getInstance().setCurrentLevelInt(5);
            } else if(hover6) {
                GameController.getInstance().setCurrentLevelInt(6);
            } else if(hover7) {
                GameController.getInstance().setCurrentLevelInt(7);
            } else if(hover8) {
                GameController.getInstance().setCurrentLevelInt(8);
            } if(hover9) {
                GameController.getInstance().setCurrentLevelInt(9);
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
