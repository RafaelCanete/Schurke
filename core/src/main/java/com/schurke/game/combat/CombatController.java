package com.schurke.game.combat;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.schurke.game.entities.Player;
import com.schurke.game.weapons.Weapon;
import com.schurke.game.weapons.LaserBurst;

public class CombatController {
    private Weapon weapon;
    private final LaserBurst laserBurst;
    private final Player player;
    private final OrthographicCamera camera;
    private final List<Bullet> bullets;
    private float shootCooldown = 0f;

    public CombatController(Player player, Weapon weapon, OrthographicCamera camera, List<Bullet> bullets) {
        this.weapon = weapon;
        this.laserBurst = new LaserBurst();
        this.player = player;
        this.camera = camera;
        this.bullets = bullets;
    }

    public void update(float delta) {
        weapon.update(delta);
        laserBurst.update(delta);
        shootCooldown -= delta;

        // Normal shooting with left mouse button
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shootCooldown <= 0f && weapon.hasAmmo()) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);

            Vector2 shootDir = new Vector2(mousePos.x, mousePos.y).sub(player.getPosition()).nor();
            List<Bullet> newBullets = weapon.shoot(player.getPosition(), shootDir);
            bullets.addAll(newBullets);

            shootCooldown = weapon.getCooldown();
        }

        // Laser burst ability with Q key
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) && laserBurst.hasAmmo()) {
            Vector2 centerDir = new Vector2(1, 0); // Direction doesn't matter for burst
            List<Bullet> burstBullets = laserBurst.shoot(player.getPosition(), centerDir);
            bullets.addAll(burstBullets);
        }

        // Cancel reload with R key
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && laserBurst.isReloading()) {
            laserBurst.cancelReload();
        }
    }

    public void dispose() {
        laserBurst.dispose();
    }

    public float getLaserBurstCooldown() {
        return laserBurst.getCooldown();
    }

    public boolean isLaserBurstReloading() {
        return laserBurst.isReloading();
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
}
