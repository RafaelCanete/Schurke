package com.schurke.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.schurke.game.Main;

public class GameOverScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final GlyphLayout layout;

    public GameOverScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(2);
        this.font.setColor(Color.RED);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float centerY = Gdx.graphics.getHeight() / 2f;
        float spacing = 60;

        drawCentered("Game Over", centerY + spacing * 2);
        drawCentered("Press R to Restart", centerY + spacing);
        drawCentered("Press ENTER to return to Menu", centerY);
        drawCentered("Press ESC to Exit", centerY - spacing);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            game.setScreen(new GameScreen(game));
            dispose();
        } else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            game.setScreen(new StartScreen(game));
            dispose();
        } else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void drawCentered(String text, float y) {
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.draw(batch, layout, x, y);
    }

    @Override public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
