package com.schurke.game.entities;

import java.util.ArrayList;
import java.util.Random;

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
            float margin = 30f;
            float x = margin + random.nextFloat() * (map.getMapWidth() * map.getTileSize() - 2 * margin);
            float y = margin + random.nextFloat() * (map.getMapHeight() * map.getTileSize() - 2 * margin);
            enemies.add(new Enemy(new Vector2(x, y), 100f, 1f, 20f));
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }

    public void update(Player player) {
        for (Enemy enemy : enemies) {
            enemy.update(enemies, player);
        }
        enemies.removeIf(Enemy::isDead);
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
