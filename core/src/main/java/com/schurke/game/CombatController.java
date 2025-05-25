package com.schurke.game;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CombatController {
    private final Weapon weapon;
    private final Player player;
    private final OrthographicCamera camera;
    private final List<Bullet> bullets;
    private float shootCooldown = 0f;

    public CombatController(Player player, Weapon weapon, OrthographicCamera camera, List<Bullet> bullets) {
        this.weapon = weapon;
        this.player = player;
        this.camera = camera;
        this.bullets = bullets;
    }

    public void update(float delta) {
        weapon.update(delta);
        shootCooldown -= delta;

        if (weapon.isReloading())
            return;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shootCooldown <= 0f && weapon.hasAmmo()) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);

            Vector2 shootDir = new Vector2(mousePos.x, mousePos.y).sub(player.getPosition()).nor();
            List<Bullet> newBullets = weapon.shoot(player.getPosition(), shootDir);
            bullets.addAll(newBullets);

            shootCooldown = weapon.getCooldown();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) || !weapon.hasAmmo()) {
            weapon.reload();
        }
    }
}
