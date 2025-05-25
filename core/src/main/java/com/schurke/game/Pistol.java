package com.schurke.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Pistol implements Weapon {
    private final Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pistol/shoot.wav"));
    private final Sound reloadSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pistol/reload.wav"));

    private boolean isReloading = false;
    private float reloadTimer = 0f;
    private final float reloadDuration = 4.0f;

    private final float cooldown = 0.3f;
    private final float damage = 50f;
    private final int magazineSize = 10;

    private int currentAmmo = magazineSize;
    private int reserveAmmo = 90;

    @Override
    public List<Bullet> shoot(Vector2 position, Vector2 direction) {
        List<Bullet> bullets = new ArrayList<>();
        if (!hasAmmo())
            return bullets;

        float speed = 800f;
        float size = 5f;
        float lifetime = 2f;

        bullets.add(new Bullet(position, direction, speed, damage, size, lifetime));
        currentAmmo--;
        shootSound.play();
        return bullets;
    }

    @Override
    public boolean hasAmmo() {
        return GameConfig.isUnlimitedAmmo() || currentAmmo > 0;
    }

    @Override
    public int getAmmo() {
        return GameConfig.isUnlimitedAmmo() ? -1 : currentAmmo;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public void reload() {
        if (GameConfig.isUnlimitedAmmo()) {
            currentAmmo = magazineSize;
            return;
        }

        if (isReloading || currentAmmo == magazineSize || reserveAmmo == 0)
            return;

        isReloading = true;
        reloadTimer = reloadDuration;
        reloadSound.play();
    }

    @Override
    public int getCurrentAmmo() {
        return GameConfig.isUnlimitedAmmo() ? -1 : currentAmmo;
    }

    @Override
    public int getReserveAmmo() {
        return GameConfig.isUnlimitedAmmo() ? -1 : reserveAmmo;
    }

    @Override
    public void dispose() {
        shootSound.dispose();
        reloadSound.dispose();
    }

    @Override
    public void update(float delta) {
        if (isReloading) {
            reloadTimer -= delta;
            if (reloadTimer <= 0f) {
                isReloading = false;

                int missing = magazineSize - currentAmmo;
                int toReload = Math.min(missing, reserveAmmo);
                currentAmmo += toReload;
                reserveAmmo -= toReload;
            }
        }
    }

    @Override
    public boolean isReloading() {
        return isReloading;
    }
}
