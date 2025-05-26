package com.schurke.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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


public class PauseScreen implements Screen {
    private Main game;
    private GameScreen gameScreen;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private float countdownTime;
    private boolean isCountingDown;
    private ShapeRenderer shapeRenderer;

    public PauseScreen(Main game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3.0f); // Make countdown numbers bigger
        shapeRenderer = new ShapeRenderer();

        // Create button style
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.RED;

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
        TextButton exitButton = new TextButton("Exit Game", textButtonStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new StartScreen(game));
            }
        });

        // Add buttons to table
        table.add(continueButton).pad(10).row();
        table.add(exitButton).pad(10);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

        countdownTime = 3;
        isCountingDown = false;
    }

    private void startCountdown() {
        isCountingDown = true;
        countdownTime = 3;
        Gdx.input.setInputProcessor(null); // Disable input during countdown
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the static game state
        renderGameState();

        if (isCountingDown) {
            // Render just the countdown number
            batch.begin();
            font.setColor(1, 0, 0, 1); // Red color for countdown
            String countText = String.valueOf((int)Math.ceil(countdownTime));
            // Center the text
            float textX = Gdx.graphics.getWidth()/2f - font.getData().spaceXadvance * countText.length() / 2;
            float textY = Gdx.graphics.getHeight()/2f + font.getLineHeight()/2;
            font.draw(batch, countText, textX, textY);
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
