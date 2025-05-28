package com.schurke.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.schurke.game.map.TileMap;

public class Player {
    private Vector2 position;
    private float size;
    private float health;
    private float maxHealth;
    
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
    private static final float ANIMATION_FRAME_DURATION = 0.2f; // Switch animation every 0.2 seconds
    
    private OrthographicCamera camera;
    private float lastAngle;

    public Player(Vector2 startPosition, float health, float size, OrthographicCamera camera) {
        this.position = new Vector2(startPosition);
        this.maxHealth = health;
        this.health = health;
        this.size = size;
        this.isWalking = false;
        this.animationTimer = 0;
        this.camera = camera;
        
        // Load textures
        frontTexture = new Texture(Gdx.files.internal("characters/front.png"));
        backTexture = new Texture(Gdx.files.internal("characters/back.png"));
        leftTexture = new Texture(Gdx.files.internal("characters/left.png"));
        rightTexture = new Texture(Gdx.files.internal("characters/right.png"));
        leftWalkingTexture = new Texture(Gdx.files.internal("characters/left_walking.png"));
        rightWalkingTexture = new Texture(Gdx.files.internal("characters/right_walking.png"));
        
        // Set initial texture
        currentTexture = frontTexture;
    }

    private void updateFacingDirection() {
        // Get mouse position in screen coordinates
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        
        // Convert mouse position to world coordinates
        Vector3 mousePos = new Vector3(mouseX, mouseY, 0);
        camera.unproject(mousePos);
        
        // Calculate angle between player and mouse
        float dx = mousePos.x - position.x;
        float dy = mousePos.y - position.y;
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        
        // Store the angle for potential use in other methods
        lastAngle = angle;
        
        // Update the current texture based on angle and walking state
        if (isWalking && (lastAngle >= -45 && lastAngle < 45 || lastAngle >= 135 || lastAngle < -135)) {
            // Only show walking animation for left/right movement
            animationTimer += Gdx.graphics.getDeltaTime();
            if (animationTimer >= ANIMATION_FRAME_DURATION) {
                animationTimer = 0;
                // Switch between walking and standing textures
                if (lastAngle >= -45 && lastAngle < 45) {
                    currentTexture = (currentTexture == rightTexture) ? rightWalkingTexture : rightTexture;
                } else {
                    currentTexture = (currentTexture == leftTexture) ? leftWalkingTexture : leftTexture;
                }
            }
        } else {
            // Not walking or moving up/down, use standard directional textures
            if (angle >= -45 && angle < 45) {
                currentTexture = rightTexture;
            } else if (angle >= 45 && angle < 135) {
                currentTexture = backTexture;
            } else if (angle >= 135 || angle < -135) {
                currentTexture = leftTexture;
            } else {
                currentTexture = frontTexture;
            }
            animationTimer = 0;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentTexture, position.x - size/2, position.y - size/2, size, size);
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void update(TileMap map) {
        float speed = 200f;
        float delta = Gdx.graphics.getDeltaTime();

        float xNew = position.x;
        float yNew = position.y;

        isWalking = false;

        // Handle movement
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
            yNew += speed * delta;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            yNew -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            xNew -= speed * delta;
            isWalking = true;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            xNew += speed * delta;
            isWalking = true;
        }

        // Update facing direction based on mouse position
        updateFacingDirection();

        float margin = size/2f;
        if (map.isInsideMap(xNew, position.y, margin)){
            position.x = xNew;
        }
        if (map.isInsideMap(position.x, yNew, margin)){
            position.y = yNew;
        }
    }

    public void setPosition(float x, float y){
        this.position.set(x,y);
    }

    public float getSize(){
        return size;
    }

    public void takeDamage(float amount){
        health -= amount;
        if (health < 0){
            health = 0;
        }
    }

    public float getHealth(){
        return health;
    }

    public float getMaxHealth(){
        return maxHealth;
    }

    public boolean isDead(){
        return health <= 0;
    }
    
    public void dispose() {
        frontTexture.dispose();
        backTexture.dispose();
        leftTexture.dispose();
        rightTexture.dispose();
        leftWalkingTexture.dispose();
        rightWalkingTexture.dispose();
    }
}
