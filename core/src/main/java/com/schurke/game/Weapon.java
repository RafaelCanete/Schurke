package com.schurke.game;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

public interface Weapon {
    List<Bullet> shoot(Vector2 position, Vector2 direction);

    float getCooldown();

    boolean hasAmmo();

    int getAmmo();

    int getCurrentAmmo();

    int getReserveAmmo();

    void reload();

    void dispose();

    void update(float delta);

    boolean isReloading();

}
