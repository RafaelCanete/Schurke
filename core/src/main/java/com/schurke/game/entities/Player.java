package com.schurke.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.schurke.game.map.TileMap;
import com.badlogic.gdx.audio.Sound;
import com.schurke.game.core.GameConfig;
import com.schurke.game.effects.DashEffectManager;

public class Player {
    private Vector2 position;
    private float size;
    private float health;
    private float maxHealth;

    // Dash-bezogene Eigenschaften
    private boolean isDashing;
    private float dashTimer;
    private float dashCooldownTimer;
    private Vector2 dashDirection;
    private DashEffectManager dashEffectManager;

    private int score = 0;
    private int level = 1;
    private int xp = 0;
    private int xpForNextLevel = 200;

    private boolean invincible;
    private float invincibleTimer;

    // Direction textures
    private Texture playerTexture;
    private boolean isWalking;
    private float animationTimer;

    private static final float ANIMATION_FRAME_DURATION = 0.2f;
    private static final float INVINCIBLE_DURATION = 1f;

    private OrthographicCamera camera;
    private float lastAngle;
    private float rotation = 0f;

    private static final Vector3 tmpMouse = new Vector3();
    
    // Sounds
    private Sound damageTakenSound;
    private Sound deathSound;
    private boolean deathSoundPlayed = false;

    public Player(Vector2 startPosition, float health, float maxHealth, OrthographicCamera camera) {
        this.position = new Vector2(startPosition);
        this.health = health;
        this.maxHealth = maxHealth;
        this.camera = camera;
        this.playerTexture = new Texture(Gdx.files.internal("characters/new/player.png"));
        this.size = 170f;
        this.isWalking = false;
        this.animationTimer = 0f;

        // Initialisiere Dash-Eigenschaften
        this.isDashing = false;
        this.dashTimer = 0f;
        this.dashCooldownTimer = 0f;
        this.dashDirection = new Vector2();
        this.dashEffectManager = new DashEffectManager(camera);

        this.invincible = false;
        this.invincibleTimer = 0f;
        
        // Load sounds
        this.damageTakenSound = Gdx.audio.newSound(Gdx.files.internal("sounds/player/damage_taken.mp3"));
        this.deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/player/death.mp3"));
    }

    public void update(TileMap map) {
        float delta = Gdx.graphics.getDeltaTime();

        // Update Dash-Cooldown
        if (dashCooldownTimer > 0) {
            dashCooldownTimer -= delta;
        }

        // Bewegungslogik
        if (!isDashing) {
            float moveX = 0;
            float moveY = 0;
            float speed = 300;

            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) moveY += speed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) moveY -= speed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) moveX -= speed;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) moveX += speed;

            // Normalisiere die Bewegung für diagonales Laufen
            if (moveX != 0 && moveY != 0) {
                float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
                moveX = moveX / length * speed;
                moveY = moveY / length * speed;
            }

            // Aktualisiere die Position
            float newX = position.x + moveX * delta;
            float newY = position.y + moveY * delta;

            // Kollisionsprüfung mit der Karte
            if (map.isInsideMap(newX, position.y, size/2)) {
                position.x = newX;
            }
            if (map.isInsideMap(position.x, newY, size/2)) {
                position.y = newY;
            }

            // Dash-Aktivierung
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE) && dashCooldownTimer <= 0) {
                isDashing = true;
                dashTimer = GameConfig.DASH_DURATION;
                dashCooldownTimer = GameConfig.DASH_COOLDOWN;
                setInvincible(true);
                
                // Berechne Bewegungsrichtung für Dash
                dashDirection.set(moveX, moveY);
                if (dashDirection.len() == 0) {
                    dashDirection.set(1, 0); // Standard-Richtung nach rechts, wenn keine Bewegung
                }
                dashDirection.nor();
            }
        } else {
            // Dash-Bewegung
            dashTimer -= delta;
            
            // Bewege in Dash-Richtung
            float newX = position.x + dashDirection.x * GameConfig.DASH_SPEED * delta;
            float newY = position.y + dashDirection.y * GameConfig.DASH_SPEED * delta;
            
            // Erstelle Dash-Partikel
            dashEffectManager.createDashEffect(position.x, position.y);
            
            // Kollisionsprüfung mit der Karte
            if (map.isInsideMap(newX, position.y, size/2)) {
                position.x = newX;
            }
            if (map.isInsideMap(position.x, newY, size/2)) {
                position.y = newY;
            }
            
            if (dashTimer <= 0) {
                isDashing = false;
                setInvincible(false);
            }
        }

        // Update Dash-Effekte
        dashEffectManager.update(delta);

        // Update Unverwundbarkeit
        if (invincible) {
            invincibleTimer -= delta;
            if (invincibleTimer <= 0) {
                invincible = false;
            }
        }
    }

    public void render(SpriteBatch batch) {
        // Beende den aktuellen Batch für die Partikel
        batch.end();
        
        // Render dash effects
        dashEffectManager.render();
        
        // Starte den Batch wieder für den Player
        batch.begin();
        
        // Berechne den Winkel zur Maus
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        tmpMouse.set(mouseX, mouseY, 0);
        camera.unproject(tmpMouse);
        float dx = tmpMouse.x - position.x;
        float dy = tmpMouse.y - position.y;
        rotation = (float)Math.toDegrees(Math.atan2(dy, dx)) - 90f;

        // Zeichne die Textur rotiert um die Mitte
        batch.draw(
            playerTexture,
            position.x - size / 2, position.y - size / 2,
            size / 2, size / 2, // Origin (Mitte)
            size, size,
            1f, 1f, // scale
            rotation,
            0, 0,
            playerTexture.getWidth(), playerTexture.getHeight(),
            false, false
        );
    }

    public void dispose() {
        playerTexture.dispose();
        if (damageTakenSound != null) {
            damageTakenSound.dispose();
        }
        if (deathSound != null) {
            deathSound.dispose();
        }
        dashEffectManager.dispose();
    }

    public void takeDamage(float amount) {
        if (invincible) return;
        health -= amount;
        if (health < 0) health = 0;
        
        // Play damage sound if player is not dead
        if (!isDead() && damageTakenSound != null) {
            damageTakenSound.play(0.5f);
        }
        
        // Play death sound if this damage killed the player
        if (isDead() && !deathSoundPlayed && deathSound != null) {
            deathSound.play(0.7f);
            deathSoundPlayed = true;
        }
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
            }
        }
    }

    public float getDashCooldownTimer() {
        return dashCooldownTimer;
    }
}
