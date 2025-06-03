package com.schurke.game.PowerUps;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.entities.Player;
import com.schurke.game.map.TileMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PowerUpsManager {
    private ArrayList<PowerUps> powerUps;
    private TileMap map;
    private float spawnTimer;
    private static final float SPAWN_INTERVAL = 60f; // every 60 seconds
    private Random random;

    public PowerUpsManager(TileMap map) {
        this.map = map;
        this.powerUps = new ArrayList<>();
        this.spawnTimer = 0f;
        this.random = new Random();
    }

    public void update(float delta, Player player, int currentRound) {
        spawnTimer += delta;

        if (currentRound >= 3 && spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0f;
            spawnPowerUp();
        }

        // Check pickup collisions
        Iterator<PowerUps> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUps p = iterator.next();
            if (p.isPickedUp(player)) {
                p.applyEffect(player);
                iterator.remove();
            }
        }
    }

    private void spawnPowerUp() {
        float margin = 30f;
        float x = margin + random.nextFloat() * (map.getMapWidth() * map.getTileSize() - 2 * margin);
        float y = margin + random.nextFloat() * (map.getMapHeight() * map.getTileSize() - 2 * margin);
        powerUps.add(new PowerUps(new Vector2(x, y)));
    }

    public void render(SpriteBatch batch) {
        for (PowerUps p : powerUps) {
            p.render(batch);
        }
    }

    public void dispose() {
        for (PowerUps p : powerUps) {
            p.dispose();
        }
    }
}
