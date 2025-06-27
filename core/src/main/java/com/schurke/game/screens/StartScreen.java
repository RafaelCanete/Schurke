package com.schurke.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.schurke.game.Main;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Cursor;

public class StartScreen implements Screen {
    private Main game;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;
    private TextButton startButton;
    private TextButton closeButton;
    private float startButtonYOffset = 230f; // Noch weiter nach unten
    private float exitButtonYOffset = 40f; // Wieder etwas höher
    private float startButtonXOffset = 400f; // Nach links verschoben (erhöht von 300f)
    private float exitButtonXOffset = 400f; // Nach rechts verschoben (erhöht von 300f)
    private float buttonYOffset = 40f; // Gemeinsame Y-Position für beide Buttons
    private Music menuMusic;

    public StartScreen(Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3.0f); // Make the font 3 times bigger
        font.setColor(Color.RED); // Set font color to red

        // Calculate center position
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float spacing = 200f;

        // Load background texture
        backgroundTexture = new Texture(Gdx.files.internal("textures/BackgroundStart.png"));

        // Create button style
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = new Color(0.7f, 1f, 0.7f, 1f); // Hellgrün

        // Lila-Grün-Button-Hintergrund erzeugen
        int bw = 320, bh = 80, border = 8;
        Pixmap pixmap = new Pixmap(bw, bh, Pixmap.Format.RGBA8888);
        // Lila Fläche
        pixmap.setColor(0.45f, 0.2f, 0.6f, 0.8f); // Lila, 80% Opazität
        pixmap.fillRectangle(0, 0, bw, bh);
        // Grüner Rand
        pixmap.setColor(0.2f, 1f, 0.4f, 0.8f); // Grün, 80% Opazität
        pixmap.drawRectangle(0, 0, bw, bh);
        for (int i = 1; i < border; i++) {
            pixmap.drawRectangle(i, i, bw - 2 * i, bh - 2 * i);
        }
        Texture buttonTex = new Texture(pixmap);
        Drawable buttonBg = new TextureRegionDrawable(new TextureRegion(buttonTex));

        // Hover-Effekt: 100% Opazität
        Pixmap pixmapHover = new Pixmap(bw, bh, Pixmap.Format.RGBA8888);
        pixmapHover.setColor(0.45f, 0.2f, 0.6f, 1f); // Lila, 100%
        pixmapHover.fillRectangle(0, 0, bw, bh);
        pixmapHover.setColor(0.2f, 1f, 0.4f, 1f); // Grün, 100%
        pixmapHover.drawRectangle(0, 0, bw, bh);
        for (int i = 1; i < border; i++) {
            pixmapHover.drawRectangle(i, i, bw - 2 * i, bh - 2 * i);
        }
        Texture buttonTexHover = new Texture(pixmapHover);
        Drawable buttonBgHover = new TextureRegionDrawable(new TextureRegion(buttonTexHover));

        textButtonStyle.up = buttonBg;
        textButtonStyle.down = buttonBgHover;
        textButtonStyle.over = buttonBgHover;
        pixmap.dispose();
        pixmapHover.dispose();

        // Create start button (wieder mittig, oben)
        this.startButton = new TextButton("Start Game", textButtonStyle);
        startButton.setSize(bw, bh);
        startButton.setPosition(centerX - startButton.getWidth() / 2f - startButtonXOffset,
                        centerY - startButton.getHeight() / 2f - spacing / 2f - buttonYOffset);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.startGame();
            }
        });

        // Create close Game button (wieder mittig, unten)
        this.closeButton = new TextButton("Exit Game", textButtonStyle);
        closeButton.setSize(bw, bh);
        closeButton.setPosition(centerX - closeButton.getWidth() / 2f + exitButtonXOffset,
                        centerY - closeButton.getHeight() / 2f - spacing / 2f - buttonYOffset);

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        stage.addActor(startButton);
        stage.addActor(closeButton);

        // Stelle sicher, dass der Stage als Input Processor gesetzt ist
        Gdx.input.setInputProcessor(stage);

        // Lade und starte die Menü-Musik
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background_music/start_screen_music.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f); // 50% Lautstärke
        menuMusic.play();
    }

    @Override
    public void show() {
        // Stelle sicher, dass der Input Processor korrekt gesetzt ist
        Gdx.input.setInputProcessor(stage);
        // Normalen Cursor anzeigen
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        // Starte die Musik wenn der Screen angezeigt wird
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        // Stelle sicher, dass der Input Processor während des Renderns gesetzt ist
        if (Gdx.input.getInputProcessor() != stage) {
            Gdx.input.setInputProcessor(stage);
        }

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // Input Processor freigeben wenn Screen versteckt wird
        Gdx.input.setInputProcessor(null);
        // Stoppe die Musik wenn der Screen versteckt wird
        if (menuMusic != null) {
            menuMusic.stop();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose();
        // Musik-Ressourcen freigeben
        if (menuMusic != null) {
            menuMusic.dispose();
        }
        // Button-Textur entsorgen
        if (startButton.getStyle().up instanceof TextureRegionDrawable) {
            TextureRegion region = ((TextureRegionDrawable) startButton.getStyle().up).getRegion();
            if (region.getTexture() != null) region.getTexture().dispose();
        }
        if (startButton.getStyle().over instanceof TextureRegionDrawable) {
            TextureRegion region = ((TextureRegionDrawable) startButton.getStyle().over).getRegion();
            if (region.getTexture() != null) region.getTexture().dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update viewport and re-center camera if needed

        // Re-center die Buttons mit Offset
        float spacing = 200f;
        startButton.setPosition(
            width / 2f - startButton.getWidth() / 2f - startButtonXOffset,
            height / 2f - startButton.getHeight() / 2f - spacing / 2f - buttonYOffset
        );
        closeButton.setPosition(
            width / 2f - closeButton.getWidth() / 2f + exitButtonXOffset,
            height / 2f - closeButton.getHeight() / 2f - spacing / 2f - buttonYOffset
        );
    }
}
