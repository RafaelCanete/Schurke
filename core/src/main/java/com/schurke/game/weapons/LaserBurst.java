package com.schurke.game.weapons;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.combat.Bullet;
import com.schurke.game.core.GameConfig;

public class LaserBurst implements Weapon {
    private final Sound burstSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pistol/shoot.mp3"));
    private final Texture laserTexture = new Texture(Gdx.files.internal("projectile/projectile_laser.png"));

    private final float cooldown = 30.0f; // 30 second cooldown
    private final float damage = 100f; // Much higher damage than normal laser
    private final int projectileCount = 16; // Fire in 16 directions (22.5 degrees apart)
    private float currentCooldown = 0f;
    private boolean isReloading = false;

    @Override
    public List<Bullet> shoot(Vector2 position, Vector2 direction) {
        if (currentCooldown > 0 || isReloading) {
            return new ArrayList<>(); // Return empty list if on cooldown or reloading
        }

        List<Bullet> bullets = new ArrayList<>();
        float speed = 600f; // Slightly slower than normal laser
        float size = 40f; // Larger projectiles
        float lifetime = 1.5f; // Shorter lifetime

        // Fire projectiles in all directions
        for (int i = 0; i < projectileCount; i++) {
            float angle = (360f / projectileCount) * i;
            float radians = (float) Math.toRadians(angle);
            Vector2 shootDir = new Vector2(
                (float) Math.cos(radians),
                (float) Math.sin(radians)
            );

            bullets.add(new Bullet(position, shootDir, speed, damage, size, lifetime, laserTexture, true, 0, true));
        }

        burstSound.play(0.7f);
        currentCooldown = cooldown;
        isReloading = true;
        return bullets;
    }

    @Override
    public boolean hasAmmo() {
        return currentCooldown <= 0 && !isReloading;
    }

    @Override
    public int getAmmo() {
        return (currentCooldown <= 0 && !isReloading) ? 1 : 0;
    }

    @Override
    public float getCooldown() {
        return currentCooldown;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public void cancelReload() {
        isReloading = false;
        currentCooldown = 0f;
    }

    @Override
    public void dispose() {
        burstSound.dispose();
        laserTexture.dispose();
    }

    @Override
    public void update(float delta) {
        if (currentCooldown > 0) {
            currentCooldown -= delta;
            if (currentCooldown <= 0) {
                isReloading = false;
            }
        }
    }
}
