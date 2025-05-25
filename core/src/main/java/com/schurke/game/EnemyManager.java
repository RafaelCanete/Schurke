package com.schurke.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;


import java.util.ArrayList;
import java.util.Random;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private Random random;
    private TileMap map;
    private int currentRound = 1;  // Start at round 1
    private boolean roundComplete = false;
    private float roundMessageTimer = 0;
    private static final float ROUND_MESSAGE_DURATION = 3.0f; // Duration to show round message

    public EnemyManager(TileMap map) {
        this.map = map;
        this.enemies = new ArrayList<>();
        this.random = new Random();
        spawnEnemy(10); // Initial round spawn
    }

    public void spawnEnemy (int count){
        for (int i=0;i< count; i++){
            float margin = 30f;
            float x = margin + random.nextFloat()*(map.getMapWidth()* map.getTileSize() -2 * margin);
            float y = margin + random.nextFloat()*(map.getMapHeight()* map.getTileSize() -2 * margin);
            enemies.add(new Enemy(new Vector2(x,y), 100f, 1f, 20f));
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }
    public void update(Player player) {
        // Update existing enemies
        for (Enemy enemy: enemies) {
            enemy.update(enemies, player);
        }

        enemies.removeIf(Enemy::isDead);

        // Check if round is complete and start new round
        if (enemies.isEmpty()) {
            currentRound++;
            roundMessageTimer = ROUND_MESSAGE_DURATION;
            // Spawn more enemies for next round (increase count with each round)
            spawnEnemy(10 + (currentRound - 1) * 2);
        }

        // Update round message timer
        if (roundMessageTimer > 0) {
            roundMessageTimer -= Gdx.graphics.getDeltaTime();
        }
    }

    public boolean shouldShowRoundMessage() {
        return roundMessageTimer > 0;
    }

    public int getCurrentRound() {
        return currentRound;
    }
    public void render(ShapeRenderer shape){
        for (Enemy enemy:enemies){
            enemy.render(shape);
        }
    }
}
