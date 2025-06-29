package com.schurke.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

public class LaserBurstCooldownUI {
    private BitmapFont qFont;
    private float x, y;
    private static final float SIZE = 64f;
    private Texture laserBurstIcon;
    private ShapeRenderer shapeRenderer;
    private static final float COOLDOWN_TIME = 30f; // 30 second cooldown

    public LaserBurstCooldownUI(float screenWidth, float screenHeight) {
        this.qFont = new BitmapFont();
        qFont.setColor(Color.WHITE);
        qFont.getData().setScale(1.0f);

        // Position oben rechts, to the left of the dash cooldown
        this.x = screenWidth - SIZE - 100; // 100px to the left of dash cooldown
        this.y = screenHeight - SIZE - 20; // Same height as dash cooldown

        // Use a fire icon for laser burst (or create a simple colored circle if no icon)
        this.laserBurstIcon = new Texture(Gdx.files.internal("skills_images/fire.png"));
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(SpriteBatch batch, float cooldownTimer, boolean isReloading) {
        // Beende den SpriteBatch, um den ShapeRenderer zu nutzen
        batch.end();

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Zeichne den grauen Hintergrundkreis
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.4f);
        shapeRenderer.circle(x + SIZE/2, y + SIZE/2, SIZE/2);

        if (cooldownTimer > 0) {
            // Berechne den Fortschritt (0 bis 1)
            float progress = 1 - (cooldownTimer / COOLDOWN_TIME);

            // Zeichne den roten Füllkreis für laser burst
            Color fillColor = isReloading ? new Color(1.0f, 0.3f, 0.3f, 0.4f) : new Color(0.8f, 0.2f, 0.2f, 0.4f);
            shapeRenderer.setColor(fillColor);
            float startAngle = 90; // Start bei 12 Uhr Position
            float endAngle = progress * 360;
            shapeRenderer.arc(x + SIZE/2, y + SIZE/2, SIZE/2, startAngle, endAngle);
        } else {
            // Wenn kein Cooldown, zeige vollen roten Kreis
            shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 0.4f);
            shapeRenderer.circle(x + SIZE/2, y + SIZE/2, SIZE/2);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        // Starte den SpriteBatch wieder für das Icon und den Text
        batch.begin();

        // Zeichne das Laser Burst Icon
        Color originalColor = batch.getColor().cpy();

        if (cooldownTimer > 0 || isReloading) {
            batch.setColor(1, 1, 1, 0.5f);
        } else {
            batch.setColor(1, 1, 1, 1f);
        }
        batch.draw(laserBurstIcon, x, y, SIZE, SIZE);

        // Setze die originale Farbe zurück
        batch.setColor(originalColor);

        // Zeichne "Q" unter dem Icon mit schwarzem Umriss für bessere Lesbarkeit
        qFont.setColor(0, 0, 0, 1);
        for(int offsetX = -1; offsetX <= 1; offsetX++) {
            for(int offsetY = -1; offsetY <= 1; offsetY++) {
                if(offsetX != 0 || offsetY != 0) {
                    qFont.draw(batch, "Q", x + SIZE/2 + offsetX, y - 5 + offsetY, 0, Align.center, false);
                }
            }
        }
        qFont.setColor(Color.WHITE);
        qFont.draw(batch, "Q", x + SIZE/2, y - 5, 0, Align.center, false);

        // Show cooldown time if on cooldown
        if (cooldownTimer > 0) {
            String timeText = String.format("%.0f", cooldownTimer);
            qFont.setColor(1, 1, 1, 1);
            qFont.draw(batch, timeText, x + SIZE/2, y + SIZE + 15, 0, Align.center, false);
        }
    }

    public void dispose() {
        qFont.dispose();
        laserBurstIcon.dispose();
        shapeRenderer.dispose();
    }
}
