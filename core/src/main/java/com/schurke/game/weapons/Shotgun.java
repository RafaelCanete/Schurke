package com.schurke.game.weapons;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.combat.Bullet;
import com.schurke.game.core.GameConfig;

public class Shotgun implements Weapon {
    private final Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shotgun/shoot.wav"));

    private final float cooldown = 0.6f;
    private final float damage = 20f;
    private final int pelletCount = 3;
    private final float spreadAngle = 20f;

    @Override
    public List<Bullet> shoot(Vector2 position, Vector2 direction) {
        List<Bullet> bullets = new ArrayList<>();

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
    }

    @Override
    public void update(float delta) {
    }

}
