package com.schurke.game.PowerUps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.entities.Player;

public class PowerUps {
    private Vector2 position;
    private Texture texture;
    private static final float SIZE = 40f;

    public PowerUps(Vector2 position) {
        this.position = position;
        this.texture = new Texture(Gdx.files.internal("powerups/infHealth.png")); // ✅ make sure this path matches your PNG
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, SIZE, SIZE);
    }

    public boolean isPickedUp(Player player) {
        float distance = player.getPosition().dst(position);
        return distance < (SIZE + player.getSize()) / 2f;
    }

    public void applyEffect(Player player) {
        player.setInvincible(true);
    }

    public void dispose() {
        texture.dispose();
    }
}
