package com.schurke.game.combat;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.entities.Enemy;

public class Bullet {
    private Vector2 position;
    private Vector2 velocity;
    private float damage;
    private float size;
    private float lifetime;
    private float speed;

    public Bullet(Vector2 position, Vector2 direction, float speed, float damage, float size, float lifetime) {
        this.position = new Vector2(position);
        this.velocity = new Vector2(direction).nor().scl(speed);
        this.damage = damage;
        this.size = size;
        this.lifetime = lifetime;
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
        lifetime -= delta;
    }

    public void render(ShapeRenderer shape) {
        // Berechne die Rotation in Grad
        float angle = (float)Math.toDegrees(Math.atan2(velocity.y, velocity.x));
        shape.identity();
        shape.translate(position.x, position.y, 0);
        shape.rotate(0, 0, 1, angle);
        // Outline
        shape.setColor(0.2f, 0.1f, 0f, 1f);
        shape.ellipse(-size * 1.2f, -size * 0.5f, size * 2.4f, size, 32);
        // Kern
        shape.setColor(1f, 0.8f, 0.2f, 1f);
        shape.ellipse(-size, -size * 0.35f, size * 2f, size * 0.7f, 32);
        shape.identity();
    }

    public boolean collidesWith(Enemy enemy) {
        float halfSize = enemy.getSize() / 2f;
        float ex = enemy.getPosition().x;
        float ey = enemy.getPosition().y;
        float dx = position.x - ex;
        float dy = position.y - ey;
        float distanceSquared = dx * dx + dy * dy;
        float combinedRadius = halfSize + size;
        return distanceSquared <= combinedRadius * combinedRadius;
    }

    public float getDamage() {
        return damage;
    }

    public boolean isExpired() {
        return lifetime <= 0f;
    }

    public Vector2 getPosition() {
        return position;
    }
}
