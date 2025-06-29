package com.schurke.game.weapons;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.combat.Bullet;

public class AssaultRifle implements Weapon {
    private final Sound shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shotgun/shoot.wav"));
    private final Texture bulletTexture = new Texture(Gdx.files.internal("projectile/projectile_laser.png"));
    private final float cooldown = 0.08f; // Fast fire rate
    private final float damage = 30f;
    private final int pellets = 1; // Single bullet per shot

    @Override
    public List<Bullet> shoot(Vector2 position, Vector2 direction) {
        List<Bullet> bullets = new ArrayList<>();
        float speed = 900f;
        float size = 7f; // Much smaller
        float lifetime = 1.2f;
        float damage = 80f; // Increased damage
        for (int i = 0; i < pellets; i++) {
            bullets.add(new Bullet(position, direction, speed, damage, size, lifetime, null, false, 0, false));
        }
        shootSound.play(0.5f);
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
        bulletTexture.dispose();
    }

    @Override
    public void update(float delta) {}
}
