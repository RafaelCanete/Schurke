package com.schurke.game.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.graphics.Color;

public class BloodEffectManager {
    private ArrayList<BloodParticle> bloodParticles;

    public BloodEffectManager() {
        this.bloodParticles = new ArrayList<>();
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
    }

    public void render(ShapeRenderer shape) {
        for (BloodParticle particle : bloodParticles) {
            particle.render(shape);
        }
    }

    public void dispose() {
        bloodParticles.clear();
    }
} 