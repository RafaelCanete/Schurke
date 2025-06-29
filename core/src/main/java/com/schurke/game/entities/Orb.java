package com.schurke.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class Orb {
    private Vector2 position;
    private float orbitRadius;
    private float orbitSpeed;
    private float orbitAngle;
    private float damage;
    private float size;
    private float damageCooldown;
    private float currentDamageCooldown;

    // Visual effects
    private float pulseTimer;
    private float pulseSpeed;
    private float baseSize;

    // Colors for shield appearance
    private float r, g, b, a;

    public Orb(float orbitRadius, float orbitSpeed, float damage, float size) {
        this.orbitRadius = orbitRadius;
        this.orbitSpeed = orbitSpeed;
        this.damage = damage;
        this.size = size;
        this.baseSize = size;
        this.orbitAngle = 0f;
        this.damageCooldown = 0.05f; // Very low cooldown for area damage
        this.currentDamageCooldown = 0f;

        // Visual effects
        this.pulseTimer = 0f;
        this.pulseSpeed = 3f; // Faster pulse

        // Set color (shield-like: metallic blue-grey)
        this.r = 0.4f;
        this.g = 0.6f;
        this.b = 0.8f;
        this.a = 0.9f;

        this.position = new Vector2();
    }

    public void update(float delta, Vector2 playerPosition) {
        // Update orbit angle
        orbitAngle += orbitSpeed * delta;
        if (orbitAngle >= 360f) {
            orbitAngle -= 360f;
        }

        // Calculate position based on player position and orbit
        float radians = MathUtils.degRad * orbitAngle;
        position.x = playerPosition.x + MathUtils.cos(radians) * orbitRadius;
        position.y = playerPosition.y + MathUtils.sin(radians) * orbitRadius;

        // Update damage cooldown
        if (currentDamageCooldown > 0) {
            currentDamageCooldown -= delta;
        }

        // Update pulse effect (subtle for shield)
        pulseTimer += delta * pulseSpeed;
        float pulseScale = 1f + 0.15f * MathUtils.sin(pulseTimer); // Slightly more pulse
        size = baseSize * pulseScale;
    }

    public void checkEnemyCollisions(ArrayList<Enemy> enemies) {
        if (currentDamageCooldown > 0) return;

        // Damage all enemies in range for area damage effect
        boolean hitAny = false;
        for (Enemy enemy : enemies) {
            if (collidesWith(enemy)) {
                enemy.takeDamage(damage);
                hitAny = true;
            }
        }

        // Only reset cooldown if we hit something
        if (hitAny) {
            currentDamageCooldown = damageCooldown;
        }
    }

    private boolean collidesWith(Enemy enemy) {
        float distance = position.dst(enemy.getPosition());
        float combinedRadius = size + enemy.getSize() / 2f;
        return distance <= combinedRadius;
    }

    public void render(ShapeRenderer shape) {
        // Draw shield center point only
        drawShieldDot(shape);
    }

    private void drawShieldDot(ShapeRenderer shape) {
        // Draw shield glow/outline
        shape.setColor(0.8f, 0.9f, 1.0f, a * 0.6f);
        shape.circle(position.x, position.y, size * 0.5f, 12);

        // Draw shield center point
        shape.setColor(0.8f, 0.9f, 1.0f, a);
        shape.circle(position.x, position.y, size * 0.3f, 8);

        // Draw inner core
        shape.setColor(r, g, b, a);
        shape.circle(position.x, position.y, size * 0.15f, 6);
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getSize() {
        return size;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setOrbitSpeed(float speed) {
        this.orbitSpeed = speed;
    }

    public void setOrbitRadius(float radius) {
        this.orbitRadius = radius;
    }
}
