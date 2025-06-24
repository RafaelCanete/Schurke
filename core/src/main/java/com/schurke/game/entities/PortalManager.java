package com.schurke.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.schurke.game.map.BaseTileMap;
import java.util.Random;

public class PortalManager {
    private Portal portal;
    private BaseTileMap map;
    private Random random;
    private boolean portalSpawned = false;
    private static final int PORTAL_SPAWN_LEVEL = 10;
    private String portalLocation = "";
    private float portalSpawnTime = 0f;
    private boolean showPortalMessage = false;
    private static final float MESSAGE_DURATION = 20f; // 20 Sekunden
    private boolean portalPermanentlyDisabled = false; // Portal komplett deaktivieren
    
    public PortalManager(BaseTileMap map) {
        this.map = map;
        this.random = new Random();
    }
    
    public PortalManager(BaseTileMap map, boolean spawnImmediately) {
        this.map = map;
        this.random = new Random();
        if (spawnImmediately) {
            spawnPortalInCenter();
        }
    }
    
    public void update(float delta, Player player) {
        // Spawn Portal nach Level 10, aber nur wenn nicht permanent deaktiviert
        if (!portalPermanentlyDisabled && player.getLevel() >= PORTAL_SPAWN_LEVEL && !portalSpawned) {
            spawnPortal();
        }
        
        // Update Portal Animation
        if (portal != null && portal.isActive()) {
            portal.update(delta);
        }
        
        // Update Portal Message Timer
        if (showPortalMessage) {
            portalSpawnTime += delta;
            if (portalSpawnTime >= MESSAGE_DURATION) {
                showPortalMessage = false; // Nur die Nachricht verschwindet, das Portal bleibt!
            }
        }
    }
    
    private void spawnPortal() {
        // Spawn Portal an einer der 4 Himmelsrichtungen
        float margin = 100f;
        float mapWidth = map.getMapWidth() * map.getTileSize();
        float mapHeight = map.getMapHeight() * map.getTileSize();
        
        int direction = random.nextInt(4); // 0=Norden, 1=Osten, 2=Süden, 3=Westen
        float x, y;
        
        switch (direction) {
            case 0: // Norden
                x = margin + random.nextFloat() * (mapWidth - 2 * margin);
                y = mapHeight - margin - 80f; // 80f ist Portal-Größe
                portalLocation = "NORDEN";
                break;
            case 1: // Osten
                x = mapWidth - margin - 80f;
                y = margin + random.nextFloat() * (mapHeight - 2 * margin);
                portalLocation = "OSTEN";
                break;
            case 2: // Süden
                x = margin + random.nextFloat() * (mapWidth - 2 * margin);
                y = margin;
                portalLocation = "SÜDEN";
                break;
            default: // Westen
                x = margin;
                y = margin + random.nextFloat() * (mapHeight - 2 * margin);
                portalLocation = "WESTEN";
                break;
        }
        
        portal = new Portal(new Vector2(x, y));
        portalSpawned = true;
        showPortalMessage = true;
        portalSpawnTime = 0f;
    }
    
    public void spawnPortalInCenter() {
        // Spawn Portal in der Mitte der Map
        float mapWidth = map.getMapWidth() * map.getTileSize();
        float mapHeight = map.getMapHeight() * map.getTileSize();
        
        float x = (mapWidth - 80f) / 2f; // 80f ist Portal-Größe
        float y = (mapHeight - 80f) / 2f;
        
        portal = new Portal(new Vector2(x, y));
        portalSpawned = true;
        portalLocation = "MITTE";
    }
    
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        if (portal != null && portal.isActive()) {
            portal.renderShape(shape);
        }
    }
    
    public boolean isPlayerInPortal(Player player) {
        if (portal != null && portal.isActive()) {
            return portal.isPlayerInPortal(player);
        }
        return false;
    }
    
    public boolean isPortalActive() {
        return portal != null && portal.isActive();
    }
    
    public boolean shouldShowPortalMessage() {
        return showPortalMessage;
    }
    
    public String getPortalLocation() {
        return portalLocation;
    }
    
    public void deactivatePortal() {
        if (portal != null) {
            portal.setActive(false);
            portal = null; // Portal komplett entfernen
        }
        portalSpawned = false; // Portal-Spawn-Status zurücksetzen
        showPortalMessage = false; // Nachricht auch ausblenden
        portalPermanentlyDisabled = true; // Portal permanent deaktivieren
    }
    
    public void dispose() {
        if (portal != null) {
            portal.dispose();
        }
    }
} 