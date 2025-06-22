package com.schurke.game.combat;

import com.badlogic.gdx.Gdx;
import com.schurke.game.entities.EnemyManager;
import com.schurke.game.entities.Player;
import java.util.Random;

public class RoundManager {
    private EnemyManager enemyManager;
    private float spawnTimer = 0f;
    private float currentSpawnInterval = 2.5f; // Start with 2.5 seconds between spawns
    private Random random;
    
    // Scaling parameters
    private static final float BASE_SPAWN_INTERVAL = 2.5f;
    private static final float INTERVAL_DECREASE_PER_LEVEL = 0.04f; // Slower decrease per level
    private static final float MIN_SPAWN_INTERVAL = 0.5f; // The fastest spawn rate possible

    public RoundManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
        this.random = new Random();
        // Spawn initial enemies
        enemyManager.spawnEnemy(2);
    }

    public void update(Player player) {
        float delta = Gdx.graphics.getDeltaTime();
        spawnTimer += delta;
        
        // Update spawn rate based on player level
        updateSpawnRate(player.getLevel());
        
        // Spawn enemies when timer is ready
        if (spawnTimer >= currentSpawnInterval) {
            // Spawn more enemies at higher levels, but slower progression
            int spawnCount = 1 + random.nextInt(1 + player.getLevel() / 4);
            enemyManager.spawnEnemy(spawnCount);
            spawnTimer = 0f;
        }
    }
    
    private void updateSpawnRate(int level) {
        // Decrease spawn interval based on player level
        float newInterval = BASE_SPAWN_INTERVAL - (level - 1) * INTERVAL_DECREASE_PER_LEVEL;
        currentSpawnInterval = Math.max(MIN_SPAWN_INTERVAL, newInterval);
    }

    public boolean isRoundStarting() {
        return false; // No more round system
    }

    public int getCurrentRound() {
        // Convert game time to "rounds" for display (every 30 seconds = 1 round)
        return (int)(spawnTimer / 30f) + 1;
    }

    public int getCountdownNumber() {
        return 0; // No more countdown
    }
    
    public float getGameTime() {
        return spawnTimer;
    }
    
    public boolean isWaveActive() {
        return false; // No more wave system
    }
    
    public int getEnemiesInWave() {
        return 0; // No more wave system
    }
    
    public int getEnemiesSpawned() {
        return 0; // No more wave system
    }
}
