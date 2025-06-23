package com.schurke.game.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.graphics.Color;
import java.util.Random;

public class BloodEffectManager {
    private ArrayList<BloodParticle> bloodParticles;
    private ArrayList<BloodStain> bloodStains;
    private Random random = new Random();
    public static final Color BLOOD_COLOR = new Color(0.6f, 0.07f, 0.07f, 1f);

    public BloodEffectManager() {
        this.bloodParticles = new ArrayList<>();
        this.bloodStains = new ArrayList<>();
    }

    public void createBloodEffect(Vector2 position) {
        // Create 5-10 blood particles per hit
        int particleCount = MathUtils.random(5, 10);
        for (int i = 0; i < particleCount; i++) {
            bloodParticles.add(new BloodParticle(position));
        }
    }

    public void update(float delta) {
        Iterator<BloodParticle> iterator = bloodParticles.iterator();
        while (iterator.hasNext()) {
            BloodParticle particle = iterator.next();
            particle.update(delta);
            if (particle.isDead()) {
                iterator.remove();
            }
        }
        for (BloodStain stain : bloodStains) {
            stain.update(delta);
        }
    }

    public void addBloodStain(Vector2 position) {
        float size = MathUtils.random(12f, 32f);
        float rotation = MathUtils.random(0f, 360f);
        float alpha = MathUtils.random(0.4f, 0.7f);
        bloodStains.add(new BloodStain(new Vector2(position), size, rotation, alpha));
    }

    public void render(ShapeRenderer shape) {
        // Draw stains first (under everything)
        for (BloodStain stain : bloodStains) {
            stain.render(shape);
        }
        // Then draw particles
        for (BloodParticle particle : bloodParticles) {
            particle.render(shape);
        }
    }

    public void dispose() {
        bloodParticles.clear();
        bloodStains.clear();
    }
} 