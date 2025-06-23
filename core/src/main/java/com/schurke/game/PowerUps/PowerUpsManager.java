package com.schurke.game.PowerUps;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.entities.Player;
import com.schurke.game.map.BaseTileMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PowerUpsManager {
    private ArrayList<HealthPowerUp> activePowerUps;
    private BaseTileMap map;
    private float spawnTimer;
    private float timeSinceLastSpawn = 0f;
    private Random random;

    // Spawn control
    private static final float BASE_SPAWN_CHANCE = 0.1f; // 10% base chance
    private static final float SPAWN_CHANCE_INCREASE_PER_LEVEL = 0.02f; // 2% increase per level
    private static final float SPAWN_CHECK_INTERVAL = 5f; // Check to spawn a power-up every 5 seconds

    public PowerUpsManager(BaseTileMap map) {
        this.map = map;
        this.activePowerUps = new ArrayList<>();
        this.random = new Random();
        this.spawnTimer = 0f;
    }

    public void update(float delta, Player player) {
        // Only start spawning from level 3 onwards
        if (player.getLevel() < 3) {
            return;
        }

        spawnTimer += delta;
        timeSinceLastSpawn += delta;

        // Check if it's time to try spawning a power-up
        if (spawnTimer >= SPAWN_CHECK_INTERVAL) {
            spawnTimer = 0f;
            trySpawnPowerUp(player.getLevel());
        }

        // Check pickup collisions
        Iterator<HealthPowerUp> iterator = activePowerUps.iterator();
        while (iterator.hasNext()) {
            HealthPowerUp p = iterator.next();
            if (p.isPickedUp(player)) {
                p.applyEffect(player);
                iterator.remove();
            }
        }
    }

    private void trySpawnPowerUp(int playerLevel) {
        float currentSpawnChance = BASE_SPAWN_CHANCE + (playerLevel - 3) * SPAWN_CHANCE_INCREASE_PER_LEVEL;
        if (random.nextFloat() < currentSpawnChance) {
            spawnHealthPowerUp();
        }
    }

    private void spawnHealthPowerUp() {
        float margin = 30f;
        float x = margin + random.nextFloat() * (map.getMapWidth() * map.getTileSize() - 2 * margin);
        float y = margin + random.nextFloat() * (map.getMapHeight() * map.getTileSize() - 2 * margin);
        activePowerUps.add(new HealthPowerUp(new Vector2(x, y)));
    }

    public void render(SpriteBatch batch) {
        for (HealthPowerUp p : activePowerUps) {
            p.render(batch);
        }
    }

    public void dispose() {
        for (HealthPowerUp p : activePowerUps) {
            p.dispose();
        }
    }
}
