package com.schurke.game.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

public class DashParticle {
    private Vector2 position;
    private Vector2 velocity;
    private float size;
    private float lifetime;
    private float maxLifetime;
    private Color color;

    public DashParticle(float x, float y, float size, float lifetime) {
        this.position = new Vector2(x, y);
        this.size = size;
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.color = new Color(1, 1, 1, 0.8f); // Weiß mit leichter Transparenz
        
        // Gib dem Partikel eine zufällige, leichte Bewegung
        float angle = MathUtils.random(0, MathUtils.PI2);
        float speed = MathUtils.random(5f, 15f);
        this.velocity = new Vector2(
            MathUtils.cos(angle) * speed,
            MathUtils.sin(angle) * speed
        );
    }

    public void update(float delta) {
        lifetime -= delta;
        
        // Bewege den Partikel
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        
        // Verlangsame die Bewegung
        velocity.scl(0.95f);
        
        // Sanfteres Ausblenden mit einer nicht-linearen Funktion
        float alpha = (lifetime / maxLifetime);
        alpha = alpha * alpha; // Quadratische Funktion für sanfteres Ausblenden
        color.a = alpha * 0.8f;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(position.x, position.y, size);
    }

    public boolean isAlive() {
        return lifetime > 0;
    }
} 