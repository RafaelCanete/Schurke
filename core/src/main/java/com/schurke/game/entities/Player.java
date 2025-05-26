package com.schurke.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.map.TileMap;

public class Player {
    private Vector2 position;
    private float size;
    private float health;
    private float maxHealth;

    public Player(Vector2 startPosition, float health, float size){
        this.position = new Vector2(startPosition);
        this.maxHealth = health;
        this.health = health;
        this.size = size;
    }

    public void render(ShapeRenderer shape){
        shape.circle(this.position.x, this.position.y, this.size/2f);
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void update(TileMap map) {
        // Bewegungsgeschwindigkeit in Einheiten pro Sekunde
        float speed = 200f;
        // Zeit seit letztem Frame
        float delta = com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        float xNew = position.x; //position speichern
        float yNew = position.y;

        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) {
            yNew += speed * delta;
        }
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) {
            yNew -= speed * delta;
        }
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            xNew -= speed * delta;
        }
        if (com.badlogic.gdx.Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            xNew += speed * delta;
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
}
