package com.schurke.game.weapons;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.combat.Bullet;
import com.schurke.game.core.GameConfig;

public class LaserGun implements Weapon {
    private final Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pistol/shoot.wav"));
    private final Texture laserTexture = new Texture(Gdx.files.internal("projectile/projectile_laser.png"));

    private final float cooldown = 0.3f;
    private final float damage = 50f;

    @Override
    public List<Bullet> shoot(Vector2 position, Vector2 direction) {
        List<Bullet> bullets = new ArrayList<>();

        float speed = 800f;
        float size = 32f;
        float lifetime = 2f;

        bullets.add(new Bullet(position, direction, speed, damage, size, lifetime, laserTexture));
        shootSound.play();
        return bullets;
    }

    @Override
    public boolean hasAmmo() {
        return true;
    }

    @Override
    public int getAmmo() {
        return -1;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public void dispose() {
        shootSound.dispose();
        laserTexture.dispose();
    }

    @Override
    public void update(float delta) {
        // No reload logic
    }
} 