package com.schurke.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.schurke.game.map.TileMap;

public class Player {
    private Vector2 position;
    private float size;
    private float health;
    private float maxHealth;

    private int score = 0;
    private int level = 1;
    private int xp = 0;
    private int xpForNextLevel = 200;

    private boolean invincible;
    private float invincibleTimer;

    // Direction textures
    private Texture frontTexture;
    private Texture backTexture;
    private Texture leftTexture;
    private Texture rightTexture;
    private Texture leftWalkingTexture;
    private Texture rightWalkingTexture;
    private Texture currentTexture;
    private boolean isWalking;
    private float animationTimer;

    private static final float ANIMATION_FRAME_DURATION = 0.2f;
    private static final float INVINCIBLE_DURATION = 10f;

    private OrthographicCamera camera;
    private float lastAngle;

    public Player(Vector2 startPosition, float health, float size, OrthographicCamera camera) {
        this.position = new Vector2(startPosition);
        this.maxHealth = health;
        this.health = health;
        this.size = size;
        this.camera = camera;
        this.isWalking = false;
        this.animationTimer = 0f;

        this.invincible = false;
        this.invincibleTimer = 0f;

        frontTexture = new Texture(Gdx.files.internal("characters/front.png"));
        backTexture = new Texture(Gdx.files.internal("characters/back.png"));
        leftTexture = new Texture(Gdx.files.internal("characters/left.png"));
        rightTexture = new Texture(Gdx.files.internal("characters/right.png"));
        leftWalkingTexture = new Texture(Gdx.files.internal("characters/left_walking.png"));
        rightWalkingTexture = new Texture(Gdx.files.internal("characters/right_walking.png"));

        currentTexture = frontTexture;
    }

    public void update(TileMap map) {
        float delta = Gdx.graphics.getDeltaTime();
        float speed = 200f;

        float xNew = position.x;
        float yNew = position.y;

        isWalking = false;

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) yNew += speed * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) yNew -= speed * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            xNew -= speed * delta;
            isWalking = true;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            xNew += speed * delta;
            isWalking = true;
        }

        updateFacingDirection();

        float margin = size / 2f;
        if (map.isInsideMap(xNew, position.y, margin)) position.x = xNew;
        if (map.isInsideMap(position.x, yNew, margin)) position.y = yNew;

        // Update invincibility
        if (invincible) {
            invincibleTimer -= delta;
            if (invincibleTimer <= 0f) {
                invincible = false;
                invincibleTimer = 0f;
            }
        }
    }

    private void updateFacingDirection() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        Vector3 mousePos = new Vector3(mouseX, mouseY, 0);
        camera.unproject(mousePos);

        float dx = mousePos.x - position.x;
        float dy = mousePos.y - position.y;
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        lastAngle = angle;

        if (isWalking && (angle >= -45 && angle < 45 || angle >= 135 || angle < -135)) {
            animationTimer += Gdx.graphics.getDeltaTime();
            if (animationTimer >= ANIMATION_FRAME_DURATION) {
                animationTimer = 0;
                currentTexture = (angle >= -45 && angle < 45)
                        ? (currentTexture == rightTexture ? rightWalkingTexture : rightTexture)
                        : (currentTexture == leftTexture ? leftWalkingTexture : leftTexture);
            }
        } else {
            if (angle >= -45 && angle < 45) currentTexture = rightTexture;
            else if (angle >= 45 && angle < 135) currentTexture = backTexture;
            else if (angle >= 135 || angle < -135) currentTexture = leftTexture;
            else currentTexture = frontTexture;

            animationTimer = 0;
        }
    }

    public void render(SpriteBatch batch) {
        // Optional flashing when invincible (can be toggled every few frames)
        // if (invincible && ((int)(invincibleTimer * 10) % 2 == 0)) return;

        batch.draw(currentTexture, position.x - size / 2, position.y - size / 2, size, size);
    }

    public void takeDamage(float amount) {
        if (invincible) return;
        health -= amount;
        if (health < 0) health = 0;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
        this.invincibleTimer = invincible ? INVINCIBLE_DURATION : 0f;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x - size / 2f, position.y - size / 2f, size, size);
    }

    public float getSize() {
        return size;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void addHealth(float healthPoints) {
        this.health = Math.min(this.health + healthPoints, this.maxHealth);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void dispose() {
        frontTexture.dispose();
        backTexture.dispose();
        leftTexture.dispose();
        rightTexture.dispose();
        leftWalkingTexture.dispose();
        rightWalkingTexture.dispose();
    }

    public void addXP(int amount) {
        this.xp += amount;
        while (xp >= xpForNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        xp -= xpForNextLevel;
        level++;
        xpForNextLevel = 200 + (level - 1) * 100;
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getXpForNextLevel() {
        return xpForNextLevel;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
