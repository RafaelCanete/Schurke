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
    private static final float BAR_WIDTH = 420f;
    private static final float BAR_HEIGHT = 22f;
    private float x;
    private float y;

    // Colors
    private final Color bgColor = new Color(0.13f, 0.13f, 0.13f, 0.92f);
    private final Color borderColor = new Color(0.25f, 0.25f, 0.25f, 1f);
    private final Color xpColorStart = new Color(1f, 0.7f, 0.2f, 1f);
    private final Color xpColorEnd = new Color(1f, 0.45f, 0.0f, 1f);

    public LevelBar(Player player, BitmapFont font) {
        this.player = player;
        this.font = font;
        this.layout = new GlyphLayout();
        // Center the bar at the top of the screen
        this.x = (Gdx.graphics.getWidth() - BAR_WIDTH) / 2f;
        this.y = Gdx.graphics.getHeight() - BAR_HEIGHT - 24f;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // --- Draw with ShapeRenderer ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw background bar (square)
        shapeRenderer.setColor(bgColor);
        shapeRenderer.rect(x, y, BAR_WIDTH, BAR_HEIGHT);

        // Draw XP progress bar with gradient (square)
        float progress = (float) player.getXp() / player.getXpForNextLevel();
        float progressWidth = BAR_WIDTH * progress;
        drawGradientRect(shapeRenderer, x, y, progressWidth, BAR_HEIGHT, xpColorStart, xpColorEnd);

        shapeRenderer.end();

        // Draw border (square)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        shapeRenderer.rect(x, y, BAR_WIDTH, BAR_HEIGHT);
        shapeRenderer.end();

        // --- Draw with SpriteBatch (for text) ---
        batch.begin();

        // Draw Level text (bolder, larger)
        String levelText = "LEVEL " + player.getLevel();
        font.getData().setScale(1.25f);
        layout.setText(font, levelText);
        font.setColor(1f, 0.95f, 0.7f, 1f);
        font.draw(batch, layout, x - layout.width - 22, y + BAR_HEIGHT / 2 + layout.height / 2 + 2);
        font.getData().setScale(1.0f);
        font.setColor(1, 1, 1, 1);

        // Draw Score text (right, minimal, no leading zeros)
        String scoreText = String.valueOf(player.getScore());
        layout.setText(font, scoreText);
        font.setColor(0.9f, 0.9f, 0.9f, 1f);
        font.draw(batch, layout, x + BAR_WIDTH + 22, y + BAR_HEIGHT / 2 + layout.height / 2 + 2);
        font.setColor(1, 1, 1, 1);
        batch.end();
    }

    // Helper: Draw a horizontal gradient rectangle (no rounded corners)
    private void drawGradientRect(ShapeRenderer shape, float x, float y, float w, float h, Color left, Color right) {
        float mid = x + w / 2f;
        shape.setColor(left);
        shape.rect(x, y, w / 2f, h);
        shape.setColor(right);
        shape.rect(mid, y, w / 2f, h);
    }

    public void resize(int width, int height) {
        // Recalculate position on screen resize
        this.x = (width - BAR_WIDTH) / 2f;
        this.y = height - BAR_HEIGHT - 24f;
    }
}
