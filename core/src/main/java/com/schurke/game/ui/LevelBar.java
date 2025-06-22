package com.schurke.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.schurke.game.entities.Player;

public class LevelBar {
    private Player player;
    private BitmapFont font;
    private GlyphLayout layout;

    // Bar dimensions and position
    private static final float BAR_WIDTH = 400f;
    private static final float BAR_HEIGHT = 20f;
    private float x;
    private float y;

    // Colors
    private final Color bgColor = new Color(0.2f, 0.2f, 0.2f, 0.8f);
    private final Color xpColor = new Color(1f, 0.5f, 0f, 0.8f);

    public LevelBar(Player player, BitmapFont font) {
        this.player = player;
        this.font = font;
        this.layout = new GlyphLayout();
        // Center the bar at the top of the screen
        this.x = (Gdx.graphics.getWidth() - BAR_WIDTH) / 2f;
        this.y = Gdx.graphics.getHeight() - BAR_HEIGHT - 20f;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // --- Draw with ShapeRenderer ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw background bar
        shapeRenderer.setColor(bgColor);
        shapeRenderer.rect(x, y, BAR_WIDTH, BAR_HEIGHT);

        // Draw XP progress bar
        float progress = (float) player.getXp() / player.getXpForNextLevel();
        float progressWidth = BAR_WIDTH * progress;
        shapeRenderer.setColor(xpColor);
        shapeRenderer.rect(x, y, progressWidth, BAR_HEIGHT);

        shapeRenderer.end();

        // --- Draw with SpriteBatch (for text) ---
        batch.begin();

        // Draw Level text
        String levelText = "LEVEL " + player.getLevel();
        layout.setText(font, levelText);
        font.draw(batch, layout, x - layout.width - 15, y + BAR_HEIGHT / 2 + layout.height / 2);

        // Draw Score text
        String scoreText = String.format("%08d", player.getScore());
        layout.setText(font, scoreText);
        font.draw(batch, layout, x + BAR_WIDTH + 15, y + BAR_HEIGHT / 2 + layout.height / 2);
        
        batch.end();
    }

    public void resize(int width, int height) {
        // Recalculate position on screen resize
        this.x = (width - BAR_WIDTH) / 2f;
        this.y = height - BAR_HEIGHT - 20f;
    }
} 