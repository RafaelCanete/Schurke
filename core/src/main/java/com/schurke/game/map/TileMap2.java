package com.schurke.game.map;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class TileMap2 extends BaseTileMap {
    private static int tileSize = 64;
    private static int mapWidth = 80; // Größere Map
    private static int mapHeight = 60; // Größere Map

    private Texture mapTexture;

    public TileMap2(){
        // Verwende map2.png für die zweite Map
        mapTexture = new Texture(Gdx.files.internal("textures/map2.png"));
        mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        mapTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    @Override
    public void render(SpriteBatch batch){
        int tileW = mapTexture.getWidth();
        int tileH = mapTexture.getHeight();
        for (int y = 0; y < mapHeight * tileSize; y += tileH) {
            for (int x = 0; x < mapWidth * tileSize; x += tileW) {
                batch.draw(mapTexture, x, y, tileW, tileH);
            }
        }
    }

    @Override
    public Vector2 getCenter(){
        return new Vector2((mapWidth/2f)*tileSize,(mapHeight/2f)*tileSize);
    }

    @Override
    public boolean isInsideMap(float x, float y,float margin){
        return x >= margin && y >= margin && x <= mapWidth *tileSize - margin && y <= mapHeight*tileSize - margin;
    }

    @Override
    public void dispose() {
        mapTexture.dispose();
    }

    @Override
    public int getMapHeight(){
        return mapHeight;
    }

    @Override
    public int getTileSize() {
        return tileSize;
    }

    @Override
    public int getMapWidth() {
        return mapWidth;
    }
} 