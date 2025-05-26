package com.schurke.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.entities.Player;

public class HealthBar {
    private float width;
    private float height;
    private Vector2 position;
    private Player player;

    public HealthBar(Player player, float height) {
        this.player = player;
        this.height = height;
        this.width = player.getMaxHealth();
        updatePosition();
    }

    private void updatePosition() {
        this.position = new Vector2(20f, Gdx.graphics.getHeight() - this.height - 40f);
    }

    public void render(ShapeRenderer shape) {
        updatePosition(); // Update position each frame to handle window resizing
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        // Background of Healthbar
        shape.setColor(0.3f,0.3f,0.3f,1f);
        shape.rect(position.x, position.y, width, height);

        // Green Healthbar
        shape.setColor(0f,1f,0f,1f);
        shape.rect(position.x, position.y, (currentHealth/maxHealth)*width, height);
    }
}
