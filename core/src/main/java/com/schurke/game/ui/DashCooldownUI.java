package com.schurke.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.schurke.game.core.GameConfig;

public class DashCooldownUI {
    private BitmapFont spaceFont;
    private float x, y;
    private static final float SIZE = 64f;
    private Texture dashIcon;
    private ShapeRenderer shapeRenderer;
    private static final float COOLDOWN_TIME = 5f; // Die maximale Cooldown-Zeit
    
    public DashCooldownUI(float screenWidth, float screenHeight) {
        this.spaceFont = new BitmapFont();
        spaceFont.setColor(Color.WHITE);
        spaceFont.getData().setScale(1.0f);
        
        // Position oben rechts mit etwas Abstand vom Rand
        this.x = screenWidth - SIZE - 20;
        this.y = screenHeight - SIZE - 20;
        
        this.dashIcon = new Texture(Gdx.files.internal("skills_images/dash.png"));
        this.shapeRenderer = new ShapeRenderer();
    }
    
    public void render(SpriteBatch batch, float cooldownTimer) {
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
            
            // Zeichne den grünen Füllkreis
            shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 0.4f);
            float startAngle = 90; // Start bei 12 Uhr Position
            float endAngle = progress * 360;
            shapeRenderer.arc(x + SIZE/2, y + SIZE/2, SIZE/2, startAngle, endAngle);
        } else {
            // Wenn kein Cooldown, zeige vollen grünen Kreis
            shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 0.4f);
            shapeRenderer.circle(x + SIZE/2, y + SIZE/2, SIZE/2);
        }
        
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        
        // Starte den SpriteBatch wieder für das Icon und den Text
        batch.begin();
        
        // Zeichne das Dash-Icon
        Color originalColor = batch.getColor().cpy();
        
        if (cooldownTimer > 0) {
            batch.setColor(1, 1, 1, 0.5f);
        } else {
            batch.setColor(1, 1, 1, 1f);
        }
        batch.draw(dashIcon, x, y, SIZE, SIZE);
        
        // Setze die originale Farbe zurück
        batch.setColor(originalColor);

        // Zeichne "SPACE" unter dem Icon mit schwarzem Umriss für bessere Lesbarkeit
        spaceFont.setColor(0, 0, 0, 1);
        for(int offsetX = -1; offsetX <= 1; offsetX++) {
            for(int offsetY = -1; offsetY <= 1; offsetY++) {
                if(offsetX != 0 || offsetY != 0) {
                    spaceFont.draw(batch, "SPACE", x + SIZE/2 + offsetX, y - 5 + offsetY, 0, Align.center, false);
                }
            }
        }
        spaceFont.setColor(Color.WHITE);
        spaceFont.draw(batch, "SPACE", x + SIZE/2, y - 5, 0, Align.center, false);
    }
    
    public void dispose() {
        spaceFont.dispose();
        dashIcon.dispose();
        shapeRenderer.dispose();
    }
} 