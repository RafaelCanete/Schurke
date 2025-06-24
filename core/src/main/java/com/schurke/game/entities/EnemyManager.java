package com.schurke.game.entities;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.map.BaseTileMap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.schurke.game.effects.BloodEffectManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private Random random;
    private BaseTileMap map;
    private BloodEffectManager bloodEffectManager;
    private ArrayList<ScorePopup> scorePopups = new ArrayList<>();
    private BitmapFont popupFont;

    public EnemyManager(BaseTileMap map) {
        this.map = map;
        this.enemies = new ArrayList<>();
        this.random = new Random();
        this.bloodEffectManager = new BloodEffectManager();
        this.popupFont = new BitmapFont();
        this.popupFont.getData().setScale(2.2f, 2.2f);
    }

    public void spawnEnemy(int count, Player player, OrthographicCamera camera) {
        int level = player.getLevel();
        ArrayList<Enemy.EnemyType> allowedTypes = new ArrayList<>();
        if (level < 3) {
            allowedTypes.add(Enemy.EnemyType.BAT);
        } else if (level < 5) {
            allowedTypes.add(Enemy.EnemyType.BAT);
            allowedTypes.add(Enemy.EnemyType.BABY_SPIDER);
        } else {
            // Ab Level 5: 40% BAT, 40% BABY_SPIDER, 20% SPIDER
            for (int i = 0; i < 4; i++) allowedTypes.add(Enemy.EnemyType.BAT);
            for (int i = 0; i < 4; i++) allowedTypes.add(Enemy.EnemyType.BABY_SPIDER);
            for (int i = 0; i < 2; i++) allowedTypes.add(Enemy.EnemyType.SPIDER);
        }
        for (int i = 0; i < count; i++) {
            Vector2 spawnPosition = getRandomPositionAtViewportEdge(camera);
            Enemy.EnemyType type = allowedTypes.get(random.nextInt(allowedTypes.size()));
            enemies.add(new Enemy(spawnPosition, type, 100f, 1f, 20f));
        }
    }

    private Vector2 getRandomPositionAtViewportEdge(OrthographicCamera camera) {
        float margin = 256f; // 4 Tiles Abstand zum Rand
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
        // Stelle sicher, dass der Spawnpunkt nicht zu nah am Rand ist
        x = Math.max(margin, Math.min(x, mapWidth - margin));
        y = Math.max(margin, Math.min(y, mapHeight - margin));
        return new Vector2(x, y);
    }

    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }

    public void update(Player player) {
        Iterator<Enemy> iterator = enemies.iterator();
        java.util.List<Enemy> toAdd = new ArrayList<>();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.update(enemies, player);
            enemy.updateHitAnimation(Gdx.graphics.getDeltaTime());
            if (enemy.isDead()) {
                player.addXP(enemy.getScoreValue());
                player.addScore(enemy.getScoreValue());
                bloodEffectManager.addBloodStain(enemy.getPosition());
                scorePopups.add(new ScorePopup(enemy.getPosition().cpy(), "+" + enemy.getScoreValue()));
                // Ab Level 10: Große Spinne spawnt 3 Baby-Spinnen
                if (enemy.getType() == Enemy.EnemyType.SPIDER && player.getLevel() >= 10) {
                    for (int j = 0; j < 3; j++) {
                        Vector2 offset = new Vector2((random.nextFloat()-0.5f)*30f, (random.nextFloat()-0.5f)*30f);
                        toAdd.add(new Enemy(enemy.getPosition().cpy().add(offset), Enemy.EnemyType.BABY_SPIDER, 100f, 1f, 20f));
                    }
                }
                iterator.remove();
            }
        }
        enemies.addAll(toAdd);
        bloodEffectManager.update(Gdx.graphics.getDeltaTime());
        // Update Popups
        Iterator<ScorePopup> popupIt = scorePopups.iterator();
        while (popupIt.hasNext()) {
            ScorePopup popup = popupIt.next();
            popup.update(Gdx.graphics.getDeltaTime());
            if (popup.isDead()) popupIt.remove();
        }
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

    public void renderPopups(SpriteBatch batch) {
        for (ScorePopup popup : scorePopups) {
            popup.render(batch, popupFont);
        }
    }
}
