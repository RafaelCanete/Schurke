package com.schurke.game.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import java.util.Random;

public class BloodStain {
    private Vector2 position;
    private float size;
    private float rotation;
    private float alpha;
    private int blobCount = 5 + (int)(Math.random() * 4); // 5-8 Kreise
    private float[] offsetsX = new float[blobCount];
    private float[] offsetsY = new float[blobCount];
    private float[] radii = new float[blobCount];
    private static final Random rand = new Random();
    private float fadeTimer = 0f;
    private float fadeDuration = 0.5f;
    private float targetAlpha;

    public BloodStain(Vector2 position, float size, float rotation, float alpha) {
        this.position = position;
        this.size = size;
        this.rotation = rotation;
        this.targetAlpha = alpha;
        this.alpha = 0f;
        {
            for (int i = 0; i < blobCount; i++) {
                offsetsX[i] = rand.nextFloat() * size * 0.7f - size * 0.35f;
                offsetsY[i] = rand.nextFloat() * size * 0.7f - size * 0.35f;
                radii[i] = size * (0.4f + rand.nextFloat() * 0.4f);
            }
        }
    }

    public void render(ShapeRenderer shape) {
        Color c = new Color(BloodEffectManager.BLOOD_COLOR);
        c.a = alpha;
        shape.setColor(c);
        shape.identity();
        shape.translate(position.x, position.y, 0);
        shape.rotate(0, 0, 1, rotation);
        for (int i = 0; i < blobCount; i++) {
            shape.circle(offsetsX[i], offsetsY[i], radii[i]);
        }
        shape.identity();
    }

    public void update(float delta) {
        if (alpha < targetAlpha) {
            fadeTimer += delta;
            alpha = Math.min(targetAlpha, fadeTimer / fadeDuration * targetAlpha);
        }
    }
} 