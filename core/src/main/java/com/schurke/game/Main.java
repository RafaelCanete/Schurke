package com.schurke.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    
    @Override
    public void create() {
        try {
            batch = new SpriteBatch();
            if (batch == null) {
                throw new RuntimeException("Failed to create SpriteBatch");
            }
            
            shapeRenderer = new ShapeRenderer();
            if (shapeRenderer == null) {
                throw new RuntimeException("Failed to create ShapeRenderer");
            }
            
            StartScreen startScreen = new StartScreen(this);
            setScreen(startScreen);
            
        } catch (Exception e) {
            Gdx.app.error("Main", "Error creating game", e);
            // Clean up resources if initialization failed
            if (batch != null) batch.dispose();
            if (shapeRenderer != null) shapeRenderer.dispose();
            throw e; // Re-throw to notify the platform of fatal error
        }
    }

    public void startGame() {
        try {
            GameScreen gameScreen = new GameScreen(this);
            setScreen(gameScreen);
        } catch (Exception e) {
            Gdx.app.error("Main", "Error starting game", e);
            // Fall back to start screen
            try {
                setScreen(new StartScreen(this));
            } catch (Exception e2) {
                Gdx.app.error("Main", "Error returning to start screen", e2);
            }
        }
    }
    
    public SpriteBatch getBatch() {
        return batch;
    }
    
    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }
}

