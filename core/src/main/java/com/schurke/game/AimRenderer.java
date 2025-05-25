package com.schurke.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class AimRenderer {
    private final OrthographicCamera camera;
    private final Player player;

    public AimRenderer(OrthographicCamera camera, Player player) {
        this.camera = camera;
        this.player = player;
    }

    public void render(ShapeRenderer shape) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        Vector2 playerPos = player.getPosition();
        Vector2 toMouse = new Vector2(mousePos.x, mousePos.y).sub(playerPos).nor();

        float length = 40f;
        float thickness = 4f;

        Vector2 perpendicular = new Vector2(-toMouse.y, toMouse.x).nor().scl(thickness / 2f);
        Vector2 p1 = new Vector2(playerPos).add(perpendicular);
        Vector2 p2 = new Vector2(playerPos).sub(perpendicular);
        Vector2 p3 = new Vector2(playerPos).add(toMouse.scl(length)).sub(perpendicular);
        Vector2 p4 = new Vector2(playerPos).add(toMouse).add(perpendicular);

        shape.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        shape.triangle(p1.x, p1.y, p3.x, p3.y, p4.x, p4.y);
    }
}
