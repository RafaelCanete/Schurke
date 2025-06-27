package com.schurke.game.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.ArrayList;
import java.util.Iterator;

public class DashEffectManager {
    private ArrayList<DashParticle> particles;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    public DashEffectManager(OrthographicCamera camera) {
        particles = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        this.camera = camera;
    }

    public void createDashEffect(float x, float y) {
        // Erstelle mehrere Partikel an der Position
        for (int i = 1; i < 10; i*=2) {
            float size = MathUtils.random(1f, 2f);  // Etwas größere Partikel
            float lifetime = MathUtils.random(0.5f, 0.8f);
            // Füge zufällige Verschiebung zur Position hinzu
            float offsetX = MathUtils.random(-10f, 10f);
            float offsetY = MathUtils.random(-10f, 10f);
            particles.add(new DashParticle(x + offsetX, y + offsetY, size, lifetime));
        }
    }

    public void update(float delta) {
        Iterator<DashParticle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            DashParticle particle = iterator.next();
            particle.update(delta);
            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }

    public void render() {
        if (particles.isEmpty()) return;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (DashParticle particle : particles) {
            particle.render(shapeRenderer);
        }
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
} 