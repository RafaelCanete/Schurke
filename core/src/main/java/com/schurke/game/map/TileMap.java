package com.schurke.game.map;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class TileMap {
    private static int tileSize = 64;
    private static int mapWidth = 20;
    private static int mapHeight = 15;

    private Texture grassTexture;

    public TileMap(){
        grassTexture = new Texture(Gdx.files.internal("textures/grass1.png"));
        grassTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        grassTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

public void render(SpriteBatch batch){
        for (int y=0; y<mapHeight;y++){
            for (int x=0; x<mapWidth;x++){
                batch.draw(grassTexture,x*tileSize,y*tileSize);
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
        grassTexture.dispose();
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
