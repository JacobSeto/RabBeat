package edu.cornell.gdiac.rabbeat.levelSelect;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LevelSelectorScreen extends ScreenAdapter {
    private Game game;
    private Stage stage;
    private Skin skin = new Skin();

    private int numberOfLevels = 3;

    public LevelSelectorScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        /** Temporary! */
        //STYLING
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        BitmapFont font = new BitmapFont();
        textButtonStyle.font = font; // Set your desired font
        textButtonStyle.fontColor = Color.WHITE; // Set the font color

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

        /** Looping through all buttons */
        for(int i=1; i<= numberOfLevels; i++) {
            TextButton levelButton = new TextButton("Level " + i, textButtonStyle);
            levelButton.setPosition(300+(100*i), 300);
            int finalI = i;
            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //game.setScreen(new GameScreen(game, finalI));
                    System.out.println("Level " + finalI);
                }
            });
            stage.addActor(levelButton);

        }

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


}
