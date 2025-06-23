package com.schurke.game.weapons;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.schurke.game.combat.Bullet;

public interface Weapon {
    List<Bullet> shoot(Vector2 position, Vector2 direction);

    float getCooldown();

    boolean hasAmmo();

    int getAmmo();

    void dispose();

    void update(float delta);

}
