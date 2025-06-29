package com.schurke.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class OrbManager {
    private ArrayList<Orb> orbs;
    private int maxOrbs;
    private int currentOrbCount;
    private boolean orbUnlocked;
    private boolean showUnlockMessage;
    private float unlockMessageTimer;
    private static final float UNLOCK_MESSAGE_DURATION = 3.0f; // Show message for 3 seconds

    // Orb configuration
    private static final float BASE_ORBIT_RADIUS = 120f;
    private static final float BASE_ORBIT_SPEED = 150f; // Faster orbit speed
    private static final float BASE_DAMAGE = 15f; // Reduced damage for area effect
    private static final float BASE_SIZE = 25f; // Slightly larger for shield appearance

    // Level thresholds for orb upgrades
    private static final int[] ORB_LEVEL_THRESHOLDS = {5, 10, 15, 20, 25};
    private static final float[] DAMAGE_MULTIPLIERS = {1.0f, 1.5f, 2.0f, 2.5f, 3.0f};
    private static final int ORB_UNLOCK_LEVEL = 15;

    public OrbManager() {
        this.orbs = new ArrayList<>();
        this.maxOrbs = 1; // Only one orb
        this.currentOrbCount = 0;
        this.orbUnlocked = false;
        this.showUnlockMessage = false;
        this.unlockMessageTimer = 0f;
    }

    public void update(float delta, Vector2 playerPosition, ArrayList<Enemy> enemies) {
        // Update unlock message timer
        if (showUnlockMessage) {
            unlockMessageTimer -= delta;
            if (unlockMessageTimer <= 0) {
                showUnlockMessage = false;
            }
        }

        // Update all orbs
        for (Orb orb : orbs) {
            orb.update(delta, playerPosition);
            orb.checkEnemyCollisions(enemies);
        }
    }

    public void render(ShapeRenderer shape) {
        for (Orb orb : orbs) {
            orb.render(shape);
        }
    }

    public void checkLevelUpgrade(int playerLevel) {
        // Check if orb should be unlocked
        if (!orbUnlocked && playerLevel >= ORB_UNLOCK_LEVEL) {
            unlockOrb();
        }

        // Only upgrade damage if orb exists
        if (orbUnlocked) {
            upgradeOrbs(playerLevel);
        }
    }

    private void unlockOrb() {
        orbUnlocked = true;
        showUnlockMessage = true;
        unlockMessageTimer = UNLOCK_MESSAGE_DURATION;
        addOrb();
    }

    private int calculateMaxOrbs(int playerLevel) {
        return 1; // Always return 1 orb
    }

    private void upgradeOrbs(int playerLevel) {
        float damageMultiplier = calculateDamageMultiplier(playerLevel);

        for (Orb orb : orbs) {
            orb.setDamage(BASE_DAMAGE * damageMultiplier);
        }
    }

    private float calculateDamageMultiplier(int playerLevel) {
        float multiplier = 1.0f;

        for (int i = 0; i < ORB_LEVEL_THRESHOLDS.length; i++) {
            if (playerLevel >= ORB_LEVEL_THRESHOLDS[i]) {
                multiplier = DAMAGE_MULTIPLIERS[i];
            }
        }

        return multiplier;
    }

    private void addOrb() {
        if (orbs.size() >= maxOrbs) return;

        // Single orb configuration
        float orbitRadius = BASE_ORBIT_RADIUS;
        float orbitSpeed = BASE_ORBIT_SPEED;
        float damage = BASE_DAMAGE;
        float size = BASE_SIZE;

        Orb newOrb = new Orb(orbitRadius, orbitSpeed, damage, size);
        orbs.add(newOrb);
        currentOrbCount++;
    }

    public int getOrbCount() {
        return orbs.size();
    }

    public int getMaxOrbs() {
        return maxOrbs;
    }

    public ArrayList<Orb> getOrbs() {
        return orbs;
    }

    public boolean isOrbUnlocked() {
        return orbUnlocked;
    }

    public boolean shouldShowUnlockMessage() {
        return showUnlockMessage;
    }
}
