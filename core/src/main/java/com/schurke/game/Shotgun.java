package com.schurke.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Shotgun implements Weapon {
    private final Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shotgun/shoot.wav"));
    private final Sound reloadSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shotgun/reload.wav"));

    private boolean isReloading = false;
    private float reloadTimer = 0f;
    private final float reloadDuration = 1.0f;

    private final float cooldown = 0.6f;
    private final float damage = 20f;
    private final int pelletCount = 3;
    private final float spreadAngle = 20f;

    private final int magazineSize = 2;
    private int currentAmmo = magazineSize;
    private int reserveAmmo = 30;

    @Override
    public List<Bullet> shoot(Vector2 position, Vector2 direction) {
        List<Bullet> bullets = new ArrayList<>();

        if (!hasAmmo())
            return bullets;

        float baseAngle = direction.angleRad();
        float startAngle = baseAngle - (float) Math.toRadians(spreadAngle / 2f);
        float angleStep = (float) Math.toRadians(spreadAngle / (pelletCount - 1));

        float speed = 700f;
        float size = 5f;
        float lifetime = 2f;

        for (int i = 0; i < pelletCount; i++) {
            float angle = startAngle + i * angleStep;
            Vector2 dir = new Vector2((float) Math.cos(angle), (float) Math.sin(angle)).nor();
            bullets.add(new Bullet(position, dir, speed, damage, size, lifetime));
        }

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
    public int getCurrentAmmo() {
        return GameConfig.isUnlimitedAmmo() ? -1 : currentAmmo;
    }

    @Override
    public int getReserveAmmo() {
        return GameConfig.isUnlimitedAmmo() ? -1 : reserveAmmo;
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
    public float getCooldown() {
        return cooldown;
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
