package com.schurke.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Portal {
    private Vector2 position;
    private float size;
    private Texture texture;
    private float animationTimer;
    private float pulseSpeed = 2f;
    private float pulseScale = 1f;
    private boolean isActive;
    
    public Portal(Vector2 position) {
        this.position = new Vector2(position);
        this.size = 80f;
        this.animationTimer = 0f;
        this.isActive = true;
        
        // Verwende eine einfache Textur oder erstelle eine programmatisch
        // Für jetzt verwenden wir eine einfache Form
    }
    
    public void update(float delta) {
        if (!isActive) return;
        
        animationTimer += delta;
        // Pulsierender Effekt
        pulseScale = 1f + 0.2f * (float)Math.sin(animationTimer * pulseSpeed);
    }
    
    public void render(SpriteBatch batch) {
        if (!isActive) return;
        
        // Für jetzt zeichnen wir ein einfaches Portal mit ShapeRenderer
        // Das wird in der GameScreen-Klasse gehandhabt
    }
    
    public void renderShape(ShapeRenderer shape) {
        if (!isActive) return;
        
        // Äußerer Ring (blau)
        shape.setColor(0.2f, 0.6f, 1f, 0.8f);
        shape.circle(position.x + size/2, position.y + size/2, size/2 * pulseScale);
        
        // Innerer Ring (heller blau)
        shape.setColor(0.4f, 0.8f, 1f, 0.9f);
        shape.circle(position.x + size/2, position.y + size/2, size/3 * pulseScale);
        
        // Zentrum (weiß)
        shape.setColor(1f, 1f, 1f, 0.7f);
        shape.circle(position.x + size/2, position.y + size/2, size/6 * pulseScale);
    }
    
    public boolean isPlayerInPortal(Player player) {
        if (!isActive) return false;
        
        Rectangle portalBounds = new Rectangle(position.x, position.y, size, size);
        Rectangle playerBounds = player.getBounds();
        
        return portalBounds.overlaps(playerBounds);
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public float getSize() {
        return size;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
} 