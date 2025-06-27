package com.schurke.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.schurke.game.Main;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;


public class PauseScreen implements Screen {
    private Main game;
    private GameScreen gameScreen;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private float countdownTime;
    private boolean isCountingDown;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;

    public PauseScreen(Main game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3.0f); // Make countdown numbers bigger
        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();

        // Create button style
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.RED;
        // Add hover effect - text becomes pink when hovering
        textButtonStyle.overFontColor = Color.PINK;

        // Create table for button layout
        Table table = new Table();
        table.setFillParent(true);

        // Create continue button
        TextButton continueButton = new TextButton("Continue", textButtonStyle);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startCountdown();
            }
        });

        // Create exit button
        TextButton backToMenu = new TextButton("Back To Menu", textButtonStyle);
        backToMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new StartScreen(game));
            }
        });

        // Add buttons to table
        table.add(continueButton).pad(10).row();
        table.add(backToMenu).pad(10);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

        countdownTime = 2; // Changed to 2 seconds
        isCountingDown = false;
    }

    private void startCountdown() {
        isCountingDown = true;
        countdownTime = 2; // Changed to 2 seconds
        Gdx.input.setInputProcessor(null); // Disable input during countdown
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the static game state
        renderGameState();

        if (isCountingDown) {
            // Check for space key to skip countdown
            if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                game.setScreen(gameScreen);
                return;
            }

            // Render countdown number and hint
            batch.begin();
            font.setColor(1, 0, 0, 1); // Red color for countdown
            String countText = String.valueOf((int)Math.ceil(countdownTime));
            
            // Center the countdown number using GlyphLayout
            layout.setText(font, countText);
            float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float textY = Gdx.graphics.getHeight()/2f + font.getLineHeight()/2;
            font.draw(batch, layout, textX, textY);
            
            // Draw hint text below using GlyphLayout for proper centering
            font.getData().setScale(1.5f); // Smaller text for hint
            String hintText = "Press SPACE to skip";
            layout.setText(font, hintText);
            float hintX = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float hintY = textY - font.getLineHeight() * 2;
            font.draw(batch, layout, hintX, hintY);
            font.getData().setScale(3.0f); // Reset scale for next frame
            
            batch.end();

            countdownTime -= delta;
            if (countdownTime <= 0) {
                game.setScreen(gameScreen);
                return;
            }
        } else {
            // Add semi-transparent overlay for pause menu
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.7f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            // Draw the pause menu
            stage.act(0); // Use 0 delta to prevent animations
            stage.draw();
        }
    }

    private void renderGameState() {
        // Render the game state without updating it
        gameScreen.renderWithoutUpdate();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
