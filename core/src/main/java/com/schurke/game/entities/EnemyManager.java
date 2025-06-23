package com.schurke.game.entities;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.map.TileMap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.schurke.game.effects.BloodEffectManager;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private Random random;
    private TileMap map;
    private BloodEffectManager bloodEffectManager;

    public EnemyManager(TileMap map) {
        this.map = map;
        this.enemies = new ArrayList<>();
        this.random = new Random();
        this.bloodEffectManager = new BloodEffectManager();
    }

    public void spawnEnemy(int count, Player player, OrthographicCamera camera) {
        int level = player.getLevel();
        for (int i = 0; i < count; i++) {
            Vector2 spawnPosition = getRandomPositionAtViewportEdge(camera);
            Enemy.EnemyType type;
            if (level < 3) {
                type = Enemy.EnemyType.BAT;
            } else {
                type = random.nextBoolean() ? Enemy.EnemyType.BAT : Enemy.EnemyType.SPIDER;
            }
            enemies.add(new Enemy(spawnPosition, type, 100f, 1f, 20f));
        }
    }

    private Vector2 getRandomPositionAtViewportEdge(OrthographicCamera camera) {
        float margin = 30f;
        float mapWidth = map.getMapWidth() * map.getTileSize();
        float mapHeight = map.getMapHeight() * map.getTileSize();
        float left = Math.max(margin, camera.position.x - camera.viewportWidth / 2f);
        float right = Math.min(mapWidth - margin, camera.position.x + camera.viewportWidth / 2f);
        float bottom = Math.max(margin, camera.position.y - camera.viewportHeight / 2f);
        float top = Math.min(mapHeight - margin, camera.position.y + camera.viewportHeight / 2f);

        int edge = random.nextInt(4); // 0=top, 1=right, 2=bottom, 3=left
        float x, y;
        switch (edge) {
            case 0: // Top
                x = left + random.nextFloat() * (right - left);
                y = top + margin;
                if (y > mapHeight - margin) y = mapHeight - margin;
                break;
            case 1: // Right
                x = right + margin;
                if (x > mapWidth - margin) x = mapWidth - margin;
                y = bottom + random.nextFloat() * (top - bottom);
                break;
            case 2: // Bottom
                x = left + random.nextFloat() * (right - left);
                y = bottom - margin;
                if (y < margin) y = margin;
                break;
            default: // Left
                x = left - margin;
                if (x < margin) x = margin;
                y = bottom + random.nextFloat() * (top - bottom);
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
            enemy.updateHitAnimation(Gdx.graphics.getDeltaTime());
            if (enemy.isDead()) {
                player.addXP(enemy.getScoreValue());
                player.addScore(enemy.getScoreValue());
                bloodEffectManager.addBloodStain(enemy.getPosition());
                iterator.remove();
            }
        }
        bloodEffectManager.update(Gdx.graphics.getDeltaTime());
    }

    public void hitEnemy(Enemy enemy) {
        enemy.hit();
        bloodEffectManager.createBloodEffect(enemy.getPosition());
    }

    // ✅ Draw only enemy textures
    public void render(SpriteBatch batch, Player player) {
        for (Enemy enemy : enemies) {
            enemy.render(batch, player);
        }
    }

    // ✅ Draw only health bars
    public void renderHealthBars(ShapeRenderer shape) {
        for (Enemy enemy : enemies) {
            enemy.renderHealthBar(shape);
        }
    }

    public void renderBloodEffects(ShapeRenderer shape) {
        bloodEffectManager.render(shape);
    }

    public boolean hasNoEnemies() {
        return enemies.isEmpty();
    }
}
