package com.schurke.game.entities;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ScorePopup {
    private Vector2 position;
    private String text;
    private float alpha = 1f;
    private float lifetime = 2f;
    private float timer = 0f;
    private static final Color BLUE = new Color(0.2f, 0.5f, 1f, 1f);

    public ScorePopup(Vector2 position, String text) {
        this.position = position;
        this.text = text;
    }

    public void update(float delta) {
        timer += delta;
        alpha = Math.max(0f, 1f - timer / lifetime);
        // Optional: Popup leicht nach oben bewegen
        position.y += 20f * delta;
    }

    public boolean isDead() {
        return timer >= lifetime;
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        font.setColor(BLUE.r, BLUE.g, BLUE.b, alpha);
        // Fake-Bold: Text mehrfach leicht versetzt zeichnen
        float x = position.x, y = position.y;
        font.draw(batch, text, x, y);
        font.draw(batch, text, x+1, y);
        font.draw(batch, text, x, y+1);
        font.draw(batch, text, x+1, y+1);
        font.setColor(1,1,1,1); // Reset
    }
} 