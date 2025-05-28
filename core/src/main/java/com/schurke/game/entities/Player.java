package com.schurke.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
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
    private Texture currentTexture;

    public Player(Vector2 startPosition, float health, float size) {
        this.position = new Vector2(startPosition);
        this.maxHealth = health;
        this.health = health;
        this.size = size;
        
        // Load textures
        frontTexture = new Texture(Gdx.files.internal("Charachters/front.png"));
        backTexture = new Texture(Gdx.files.internal("Charachters/back.png"));
        leftTexture = new Texture(Gdx.files.internal("Charachters/left.png"));
        rightTexture = new Texture(Gdx.files.internal("Charachters/right.png"));
        
        // Set initial texture
        currentTexture = frontTexture;
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

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
            yNew += speed * delta;
            currentTexture = backTexture;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            yNew -= speed * delta;
            currentTexture = frontTexture;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            xNew -= speed * delta;
            currentTexture = leftTexture;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            xNew += speed * delta;
            currentTexture = rightTexture;
        }

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
    }
}
