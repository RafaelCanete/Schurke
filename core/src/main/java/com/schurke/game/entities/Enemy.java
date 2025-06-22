package com.schurke.game.entities;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private Vector2 position;
    private static float size = 70f;
    private float damageCooldown;
    private float health;
    private float maxHealth;
    private float attackDamage;
    private Texture texture;
    private int scoreValue = 10;

    public Enemy(Vector2 position, float health, float damageCooldown, float attackDamage) {
        this.position = new Vector2(position);
        this.health = health;
        this.maxHealth = health;
        this.damageCooldown = damageCooldown;
        this.attackDamage = attackDamage;
        this.texture = new Texture(Gdx.files.internal("characters/enemy.png"));
    }

  
    public void render(SpriteBatch batch) {
        if (position != null && texture != null) {
            batch.draw(texture, position.x, position.y, size, size);
        }
    }

  
    public void renderHealthBar(ShapeRenderer shape) {
        float healthBarWidth = size;
        float healthBarHeight = 4f;
        float healthPercentage = health / maxHealth;

        shape.setColor(0.3f, 0.3f, 0.3f, 1f);
        shape.rect(position.x, position.y + size + 5f, healthBarWidth, healthBarHeight);

        float r = 1 - healthPercentage;
        float g = healthPercentage;
        shape.setColor(r, g, 0, 1f);
        shape.rect(position.x, position.y + size + 5f, healthBarWidth * healthPercentage, healthBarHeight);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void update(ArrayList<Enemy> allEnemies, Player player) {
        Vector2 playerPosition = player.getPosition();
        Vector2 toPlayer = new Vector2(playerPosition).sub(position).nor();
        float speed = 100f;
        float delta = Gdx.graphics.getDeltaTime();
        damageCooldown -= delta;

        Vector2 separation = new Vector2();
        float separationDistance = 25f;
        float separationStrength = 100f;

        for (Enemy other : allEnemies) {
            if (other == this) continue;

            float distance = this.position.dst(other.position);
            if (distance < separationDistance && distance > 0.01f) {
                Vector2 push = new Vector2(position).sub(other.position).nor()
                        .scl((separationDistance - distance) / separationDistance);
                separation.add(push);
            }
        }

        Vector2 finalVelocity = new Vector2(toPlayer).scl(speed).add(separation.scl(separationStrength));
        position.add(finalVelocity.scl(delta));

        if (this.position.dst(playerPosition) < 20f) {
            if (damageCooldown <= 0f) {
                player.takeDamage(this.attackDamage);
                damageCooldown = 1.0f;
            }
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void takeDamage(float amount) {
        this.health -= amount;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public static float getSize() {
        return size;
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
