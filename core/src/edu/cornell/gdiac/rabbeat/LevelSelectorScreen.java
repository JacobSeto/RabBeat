package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

    private Texture hoverRegion;

    /** Images for the button hover images when hovering over a button */
    private Image hoverImage1;
    private Image hoverImage2;
    private Image hoverImage3;
    private Image hoverImage4;

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



//
//            hoverImage.addListener(new InputListener() {
//                @Override
//                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
//                    //if (!hitbox.contains(x+100, y)) {
//                        hover = false; // Update hover state
//                    //}
//                }
//            });

//            Texture buttonTexture2 = GameController.getInstance().objectController.getLevelButtonHoverTexture(finalI);
//            TextureRegionDrawable buttonDrawable2 = new TextureRegionDrawable(new TextureRegion(buttonTexture2));
//            BitmapFont font2 = new BitmapFont();
//            TextButton.TextButtonStyle textButtonStyle2 = new TextButton.TextButtonStyle();
//            textButtonStyle2.up = buttonDrawable2;
//            textButtonStyle2.font = font2;
//            hoverButton = new TextButton("", textButtonStyle2);
//            //hoverButton.setPosition(415, background.getHeight()-hoverButton.getHeight()-161);
//            stage.addActor(hoverButton);


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
                    levelButton.setPosition(483, background.getHeight()-levelButton.getHeight()-311);
                    break;
                case(4):
                    levelButton.setPosition(475, background.getHeight()-levelButton.getHeight()-374);
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

            //have a separate for loop for the hover images
            if(i <= 4) {
                levelButton.addListener(new InputListener() {
                    @Override
                    public boolean mouseMoved(InputEvent event, float x, float y) {
                        switch(finalI) {
                            case(1):
                                hover1 = true;
                                break;
                            case(2):
                                hover2 = true;
                                break;
                            case(3):
                                hover3 = true;
                                break;
                            case(4):
                                hover4 = true;
                                break;

                        }

                        return true; // Returning true means that this event is handled
                    }


                });



                if(i==1) {
                    hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(1);
                    hoverImage1 = new Image(hoverRegion);
                    hoverImage1.setPosition(411+62-15, background.getHeight()-hoverImage1.getHeight()-161-37+15);

                    hoverImage1.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                            //if (!hitbox.contains(x+100, y)) {
                            hover1 = false; // Update hover state
                            //}
                        }

                    });
                } else if(i==2) {
                    hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(2);
                    hoverImage2 = new Image(hoverRegion);
                    hoverImage2.setPosition(464-20, background.getHeight()-hoverImage1.getHeight()-270+30);


                    hoverImage2.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                            //if (!hitbox.contains(x+100, y)) {
                            hover2 = false; // Update hover state
                            //}
                        }

                    });
                } else if(i == 3){
                    hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(3);
                    hoverImage3 = new Image(hoverRegion);
                    hoverImage3.setPosition(485.5f-19, background.getHeight()-hoverImage3.getHeight()-315.5f+20);


                    hoverImage3.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                            hover3 = false;
                        }

                    });
                } else if(i == 4){
                    hoverRegion = GameController.getInstance().objectController.getLevelButtonHoverTexture(4);
                    hoverImage4 = new Image(hoverRegion);
                    hoverImage4.setPosition(478f-19, background.getHeight()-hoverImage4.getHeight()-378+20);


                    hoverImage4.addListener(new InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                            hover4 = false;
                        }

                    });
                }

            }


        }


    }

    public void render(float delta) {
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

}
