package com.schurke.game.entities;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.map.TileMap;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private Random random;
    private TileMap map;

    public EnemyManager(TileMap map) {
        this.map = map;
        this.enemies = new ArrayList<>();
        this.random = new Random();
    }

    public void spawnEnemy(int count) {
        for (int i = 0; i < count; i++) {
            Vector2 spawnPosition = getRandomEdgePosition();
            enemies.add(new Enemy(spawnPosition, 100f, 1f, 20f));
        }
    }

    private Vector2 getRandomEdgePosition() {
        float margin = 30f;
        float mapWidth = map.getMapWidth() * map.getTileSize();
        float mapHeight = map.getMapHeight() * map.getTileSize();
        
        // Choose a random edge (0=top, 1=right, 2=bottom, 3=left)
        int edge = random.nextInt(4);
        float x, y;
        
        switch (edge) {
            case 0: // Top edge
                x = margin + random.nextFloat() * (mapWidth - 2 * margin);
                y = mapHeight - margin;
                break;
            case 1: // Right edge
                x = mapWidth - margin;
                y = margin + random.nextFloat() * (mapHeight - 2 * margin);
                break;
            case 2: // Bottom edge
                x = margin + random.nextFloat() * (mapWidth - 2 * margin);
                y = margin;
                break;
            default: // Left edge
                x = margin;
                y = margin + random.nextFloat() * (mapHeight - 2 * margin);
                break;
        }
        
        return new Vector2(x, y);
    }

    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }

    public void update(Player player) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.update(enemies, player);
            if (enemy.isDead()) {
                player.addXP(enemy.getScoreValue());
                player.addScore(enemy.getScoreValue());
                iterator.remove();
            }
        }
    }

    // ✅ Draw only enemy textures
    public void render(SpriteBatch batch) {
        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }
    }

    // ✅ Draw only health bars
    public void renderHealthBars(ShapeRenderer shape) {
        for (Enemy enemy : enemies) {
            enemy.renderHealthBar(shape);
        }
    }

    public boolean hasNoEnemies() {
        return enemies.isEmpty();
    }
}
