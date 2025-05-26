package com.schurke.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.schurke.game.Main;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class GameOverScreen implements Screen {
    private Main game;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;

    public GameOverScreen(Main game) {
        try {
            this.game = game;
            this.batch = new SpriteBatch();
            stage = new Stage(new ScreenViewport());

            // Create fonts
            font = new BitmapFont();
            font.getData().setScale(2.0f);
            titleFont = new BitmapFont();
            titleFont.getData().setScale(3.0f);
            titleFont.setColor(Color.RED);

            // Create button style
            TextButtonStyle textButtonStyle = new TextButtonStyle();
            textButtonStyle.font = font;
            textButtonStyle.fontColor = Color.WHITE;
            textButtonStyle.downFontColor = Color.LIGHT_GRAY;

            // Create label style for title
            LabelStyle labelStyle = new LabelStyle(titleFont, Color.RED);

            // Create table for layout
            Table table = new Table();
            table.setFillParent(true);

            // Create title label
            Label titleLabel = new Label("Game Over!", labelStyle);

            // Create restart button
            TextButton restartButton = new TextButton("Restart Game", textButtonStyle);
            restartButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        dispose();
                        game.startGame();
                    } catch (Exception e) {
                        Gdx.app.error("GameOverScreen", "Error restarting game", e);
                    }
                }
            });

            // Create exit button
            TextButton exitButton = new TextButton("Exit Game", textButtonStyle);
            exitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        dispose();
                        game.setScreen(new StartScreen(game));
                    } catch (Exception e) {
                        Gdx.app.error("GameOverScreen", "Error returning to start screen", e);
                    }
                }
            });

            // Add widgets to table with spacing
            table.add(titleLabel).padBottom(50).row();
            table.add(restartButton).pad(10).row();
            table.add(exitButton).pad(10);

            stage.addActor(table);
            Gdx.input.setInputProcessor(stage);

        } catch (Exception e) {
            Gdx.app.error("GameOverScreen", "Error initializing game over screen", e);
            throw e; // Rethrow to ensure the error is not silently swallowed
        }
    }

    @Override
    public void render(float delta) {
        try {
            Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            stage.act(Math.min(delta, 1/30f)); // Cap delta time to prevent huge jumps
            stage.draw();

        } catch (Exception e) {
            Gdx.app.error("GameOverScreen", "Error in render", e);
        }
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        try {
            if (stage != null) stage.dispose();
            if (batch != null) batch.dispose();
            if (font != null) font.dispose();
            if (titleFont != null) titleFont.dispose();
        } catch (Exception e) {
            Gdx.app.error("GameOverScreen", "Error disposing resources", e);
        }
    }
}
