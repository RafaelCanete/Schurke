package com.schurke.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class BaseTileMap {
    protected static int tileSize = 64;
    
    public abstract void render(SpriteBatch batch);
    public abstract Vector2 getCenter();
    public abstract boolean isInsideMap(float x, float y, float margin);
    public abstract void dispose();
    public abstract int getMapHeight();
    public abstract int getMapWidth();
    public abstract int getTileSize();
} 