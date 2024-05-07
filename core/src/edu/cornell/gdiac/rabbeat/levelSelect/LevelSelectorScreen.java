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

    private TextButton levelButton2;

    /** Returns whether a button is being hovered over */
    private boolean hover = false;

    private Texture hoverRegion;
    private Image hoverImage;

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
        hover = false;
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

            hoverRegion = GameController.getInstance().objectController.levelButtonHover1;
            hoverImage = new Image(hoverRegion);

            hoverImage.addListener(new InputListener() {
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    //if (!hitbox.contains(x+100, y)) {
                        hover = false; // Update hover state
                    //}
                }
            });


            switch(i) {
                case(1):
                    hoverImage.setPosition(418, background.getHeight()-hoverImage.getHeight()-135);
                    levelButton.setPosition(478, background.getHeight()-levelButton.getHeight()-200);
                    break;
                case(2):
                    levelButton.setPosition(464, background.getHeight()-levelButton.getHeight()-266);
                    break;
                case(3):
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

            levelButton.addListener(new InputListener() {
                @Override
                public boolean mouseMoved(InputEvent event, float x, float y) {
                    hover = true;
                    return true; // Returning true means that this event is handled
                }

            });

            if(i==1) {
                //HOVER OVER BUTTON!
                Texture buttonTexture2 = GameController.getInstance().objectController.levelButtonHover1;
                TextureRegionDrawable buttonDrawable2 = new TextureRegionDrawable(new TextureRegion(buttonTexture2));
                BitmapFont font2 = new BitmapFont();
                TextButton.TextButtonStyle textButtonStyle2 = new TextButton.TextButtonStyle();
                textButtonStyle2.up = buttonDrawable2;
                textButtonStyle2.font = font2;
                levelButton2 = new TextButton("", textButtonStyle2);
                levelButton2.setPosition(418, background.getHeight()-levelButton2.getHeight()-135);
                stage.addActor(levelButton2);

                
                levelButton2.addListener(new InputListener() {
                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        //if (!hitbox.contains(x+100, y)) {
                        hover = false; // Update hover state
                        //}
                    }
                });
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
//            stage.addActor(hoverImage);


        }


    }

    public void render(float delta) {

        levelButton2.setVisible(hover);

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
