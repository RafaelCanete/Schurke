package com.schurke.game.PowerUps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.entities.Player;

public class HealthPowerUp {
    private Vector2 position;
    private Texture texture;
    private static final float SIZE = 40f;
    private static final float HEAL_AMOUNT = 50f;

    public HealthPowerUp(Vector2 position) {
        this.position = position;
        this.texture = new Texture(Gdx.files.internal("powerups/infHealth.png"));
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, SIZE, SIZE);
    }

    public boolean isPickedUp(Player player) {
        return player.getBounds().contains(position.x + SIZE / 2, position.y + SIZE / 2);
    }

    public void applyEffect(Player player) {
        player.addHealth(HEAL_AMOUNT);
    }

    public void dispose() {
        texture.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }
}
