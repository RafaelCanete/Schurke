package com.schurke.game.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;

public class BloodParticle {
    private Vector2 position;
    private Vector2 velocity;
    private float size;
    private float lifetime;
    private float maxLifetime;
    private float alpha;

    public BloodParticle(Vector2 startPosition) {
        this.position = new Vector2(startPosition);
        this.velocity = new Vector2(
            MathUtils.random(-50f, 50f),  // Random horizontal velocity
            MathUtils.random(-100f, -50f) // Downward velocity
        );
        this.size = MathUtils.random(2f, 6f);
        this.maxLifetime = MathUtils.random(8f, 12f);
        this.lifetime = maxLifetime;
        this.alpha = 1f;
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
        lifetime -= delta;
        alpha = lifetime / maxLifetime;
        
        // Slow down over time
        velocity.scl(0.98f);
    }

    public void render(ShapeRenderer shape) {
        if (alpha > 0) {
            Color c = new Color(BloodEffectManager.BLOOD_COLOR);
            c.a = alpha;
            shape.setColor(c);
            shape.circle(position.x, position.y, size);
        }
    }

    public boolean isDead() {
        return lifetime <= 0;
    }

    public Vector2 getPosition() {
        return position;
    }
} 