package com.schurke.game.map;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class TileMap {
    private static int tileSize = 64;
    private static int mapWidth = 80;
    private static int mapHeight = 60;

    private Texture mapTexture;

    public TileMap(){
        mapTexture = new Texture(Gdx.files.internal("textures/map1.png"));
        mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        mapTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    public void render(SpriteBatch batch){
        int tileW = mapTexture.getWidth();
        int tileH = mapTexture.getHeight();
        for (int y = 0; y < mapHeight * tileSize; y += tileH) {
            for (int x = 0; x < mapWidth * tileSize; x += tileW) {
                batch.draw(mapTexture, x, y, tileW, tileH);
            }
        }
    }

    public Vector2 getCenter(){
        return new Vector2((mapWidth/2f)*tileSize,(mapHeight/2f)*tileSize);
    }

    public boolean isInsideMap(float x, float y,float margin){
        return x >= margin && y >= margin && x <= mapWidth *tileSize - margin && y <= mapHeight*tileSize - margin;
    }

    public void dispose() {
        mapTexture.dispose();
    }

    public  int getMapHeight(){
        return mapHeight;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getMapWidth() {
        return mapWidth;
    }
}
