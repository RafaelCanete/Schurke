package com.schurke.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.schurke.game.Main;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameOverScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Stage stage;
    private final GlyphLayout layout;
    private final ShapeRenderer shapeRenderer;
    private final GameScreen gameScreen;

    public GameOverScreen(Main game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(3.0f);
        this.font.setColor(Color.RED);
        this.stage = new Stage(new ScreenViewport());
        this.layout = new GlyphLayout();
        this.shapeRenderer = new ShapeRenderer();

        // Calculate center position
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float spacing = 100f;

        // Create button style with just the font and color, no background
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.RED;
        // Add a slightly brighter color for hover effect
        textButtonStyle.overFontColor = Color.PINK;

        // Create label style for "Game Over" text
        LabelStyle labelStyle = new LabelStyle(font, Color.RED);

        // Create table for layout
        Table table = new Table();
        table.setFillParent(true);

        // Create "Game Over" label
        Label gameOverLabel = new Label("Game Over", labelStyle);

        // Create buttons
        TextButton restartButton = new TextButton("Restart Game", textButtonStyle);
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        TextButton menuButton = new TextButton("Back to Menu", textButtonStyle);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new StartScreen(game));
                dispose();
            }
        });

        TextButton exitButton = new TextButton("Exit Game", textButtonStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Add buttons to table with spacing
        table.add(gameOverLabel).padBottom(spacing).row();
        table.add(restartButton).padBottom(20).row();
        table.add(menuButton).padBottom(20).row();
        table.add(exitButton).row();

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Render the game screen in the background
        if (gameScreen != null) {
            gameScreen.renderWithoutUpdate();
        }

        // Add semi-transparent overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw the stage with buttons
        stage.act(delta);
        stage.draw();
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
        batch.dispose();
        font.dispose();
        stage.dispose();
        shapeRenderer.dispose();
    }
}
