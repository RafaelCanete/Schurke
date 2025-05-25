package com.schurke.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

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
        shape.setColor(1f, 1f, 0f, 1f);
        shape.circle(position.x, position.y, size);
    }

    public boolean collidesWith(Enemy enemy) {
        return enemy.getPosition().dst(position) < (Enemy.getSize() / 2f);
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
