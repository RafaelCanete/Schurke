package com.schurke.game.entities;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Enemy {
    private Vector2 position;
    private float size;
    private float damageCooldown;
    private float health;
    private float maxHealth;
    private float attackDamage;
    private Texture texture;
    private int scoreValue;
    private boolean isHit = false;
    private float hitTimer = 0f;
    private static final float HIT_DURATION = 0.2f;
    private static Texture sharedBatTexture;
    private static Texture sharedBabySpiderTexture;
    private static Texture sharedSpiderTexture;
    private EnemyType type;

    public enum EnemyType { BAT, BABY_SPIDER, SPIDER }

    public Enemy(Vector2 position, EnemyType type, float health, float damageCooldown, float attackDamage) {
        this.position = new Vector2(position);
        this.health = health;
        this.maxHealth = health;
        this.damageCooldown = damageCooldown;
        this.attackDamage = attackDamage;
        this.type = type;
        if (type == EnemyType.BAT) {
            if (sharedBatTexture == null) {
                sharedBatTexture = new Texture(Gdx.files.internal("characters/new/enemy_bat.png"));
            }
            this.texture = sharedBatTexture;
            this.scoreValue = 150;
            this.size = 60f;
        } else if (type == EnemyType.BABY_SPIDER) {
            if (sharedBabySpiderTexture == null) {
                sharedBabySpiderTexture = new Texture(Gdx.files.internal("characters/new/enemy_baby_spider.png"));
            }
            this.texture = sharedBabySpiderTexture;
            this.scoreValue = 100;
            this.size = 50f;
        } else {
            if (sharedSpiderTexture == null) {
                sharedSpiderTexture = new Texture(Gdx.files.internal("characters/new/enemy_spider.png"));
            }
            this.texture = sharedSpiderTexture;
            this.scoreValue = 250;
            this.size = 90f;
        }
    }

    public void hit() {
        isHit = true;
        hitTimer = HIT_DURATION;
    }

    public void updateHitAnimation(float delta) {
        if (isHit) {
            hitTimer -= delta;
            if (hitTimer <= 0f) {
                isHit = false;
            }
        }
    }

    public boolean isHit() {
        return isHit;
    }

    public void render(SpriteBatch batch, Player player) {
        float dx = player.getPosition().x - position.x;
        float dy = player.getPosition().y - position.y;
        float rotation = (float)Math.toDegrees(Math.atan2(dy, dx)) - 90f;
        batch.draw(
            texture,
            position.x, position.y,
            size / 2, size / 2,
            size, size,
            1f, 1f,
            rotation,
            0, 0,
            texture.getWidth(), texture.getHeight(),
            false, false
        );
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
        float speed = 150f;
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

    public float getSize() {
        return size;
    }

    public void dispose() {
        if (sharedBatTexture != null) {
            sharedBatTexture.dispose();
            sharedBatTexture = null;
        }
        if (sharedBabySpiderTexture != null) {
            sharedBabySpiderTexture.dispose();
            sharedBabySpiderTexture = null;
        }
        if (sharedSpiderTexture != null) {
            sharedSpiderTexture.dispose();
            sharedSpiderTexture = null;
        }
    }
}
