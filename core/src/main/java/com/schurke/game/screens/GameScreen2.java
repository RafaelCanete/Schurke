package com.schurke.game.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Cursor;
import com.schurke.game.Main;
import com.schurke.game.combat.Bullet;
import com.schurke.game.combat.BulletManager;
import com.schurke.game.combat.CombatController;
import com.schurke.game.combat.RoundManager;
import com.schurke.game.core.GameConfig;
import com.schurke.game.entities.EnemyManager;
import com.schurke.game.entities.Player;
import com.schurke.game.entities.PortalManager;
import com.schurke.game.map.TileMap2;
import com.schurke.game.ui.HealthBar;
import com.schurke.game.ui.LevelBar;
import com.schurke.game.weapons.LaserGun;
import com.schurke.game.weapons.Weapon;
import com.schurke.game.PowerUps.PowerUpsManager;

public class GameScreen2 extends BaseGameScreen {
    private Main game;
    private SpriteBatch batch;
    private Texture image;
    private TileMap2 map;
    private ShapeRenderer shape;
    private Player player;
    private HealthBar playerHealthBar;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private Viewport uiViewport;
    private EnemyManager enemyManager;
    private boolean gameOver;
    private float deathTimer;
    private static final float DEATH_DELAY = 0.5f;
    private BitmapFont font;
    private SpriteBatch hudBatch;
    private ArrayList<Bullet> bullets;
    private Weapon currentWeapon;
    private CombatController combatController;
    private BulletManager bulletManager;
    private RoundManager roundManager;
    private LevelBar levelBar;

    // PowerUps
    private PowerUpsManager powerUpsManager;
    private PortalManager portalManager;

    private Texture cursorTexture;
    private float portalSpawnTimer = 0f;
    private static final float PORTAL_SPAWN_DELAY = 3f; // 3 Sekunden Verzögerung

    public GameScreen2(Main game, Player player) {
        this.game = game;
        this.batch = game.getBatch();
        this.shape = game.getShapeRenderer();
        this.gameOver = false;
        this.deathTimer = 0;

        this.map = new TileMap2();
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(camera);
        this.viewport.apply();
        this.camera.position.set(map.getCenter(), 0);
        
        // Verwende den übergebenen Player (mit Level und XP)
        this.player = player;
        // Setze Position auf sichere Position (nicht in der Mitte, wo das Portal ist)
        Vector2 safePosition = new Vector2(100f, 100f); // Sichere Position in der Ecke
        this.player.setPosition(safePosition);
        // Aktualisiere die Kamera im Player für GameScreen2
        this.player.updateCamera(camera);

        this.uiCamera = new OrthographicCamera();
        this.uiViewport = new ScreenViewport(uiCamera);
        this.uiViewport.apply();

        this.enemyManager = new EnemyManager(map);
        // Entferne alle Gegner beim Betreten der zweiten Map
        this.enemyManager.getEnemies().clear();
        this.playerHealthBar = new HealthBar(player, 20f);
        this.image = new Texture("libgdx.png");
        this.font = new BitmapFont();
        this.font.getData().setScale(2.0f);
        this.hudBatch = new SpriteBatch();
        this.levelBar = new LevelBar(player, font);

        this.bullets = new ArrayList<>();
        this.currentWeapon = new LaserGun();
        this.combatController = new CombatController(player, currentWeapon, camera, bullets);
        this.bulletManager = new BulletManager(bullets, enemyManager);
        this.roundManager = new RoundManager(enemyManager);

        this.powerUpsManager = new PowerUpsManager(map, false);
        this.portalManager = new PortalManager(map, false); // Portal nicht sofort spawnen

        this.cursorTexture = new Texture(Gdx.files.internal("cursor/cursor_aim.png"));
    }

    @Override
    public void render(float delta) {
        // Clamp delta to avoid stutter on lag spikes
        delta = Math.min(delta, 1f / 30f);

        if (gameOver) {
            deathTimer += delta;
            if (deathTimer >= DEATH_DELAY) {
                game.setScreen(new GameOverScreen(game));
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        // Kamera zentriert auf die kleine Map (nicht dem Spieler folgen)
        float mapPixelWidth = map.getMapWidth() * map.getTileSize();
        float mapPixelHeight = map.getMapHeight() * map.getTileSize();
        float camX = mapPixelWidth / 2f; // Zentrum der Map
        float camY = mapPixelHeight / 2f; // Zentrum der Map
        camera.position.set(camX, camY, 0);
        camera.update();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        // Draw world
        batch.begin();
        batch.draw(image, 140, 210);
        map.render(batch);
        batch.end();

        // Draw blood stains and particles (under player/enemies)
        shape.begin(ShapeRenderer.ShapeType.Filled);
        enemyManager.renderBloodEffects(shape);
        shape.end();

        batch.begin();
        player.render(batch);
        enemyManager.render(batch, player);
        powerUpsManager.render(batch);
        enemyManager.renderPopups(batch);
        // Bullet rendering (innerhalb des SpriteBatch-Blocks)
        bulletManager.updateAndRender(delta, shape, batch);
        batch.end();

        // Portal rendering (separat)
        shape.begin(ShapeRenderer.ShapeType.Filled);
        portalManager.render(batch, shape);
        shape.end();

        // KEIN Gegner-Spawning und KEIN Gegner-Update auf Map 2!
        // if (!gameOver) {
        //     roundManager.update(player);
        //     enemyManager.update(player);
        //     player.update(map);
        //     combatController.update(delta);
        //     powerUpsManager.update(delta, player);
        // }
        if (!gameOver) {
            player.update(map);
            combatController.update(delta);
            powerUpsManager.update(delta, player);
            
            // Portal-Spawn-Logik mit Verzögerung
            portalSpawnTimer += delta;
            if (portalSpawnTimer >= PORTAL_SPAWN_DELAY && !portalManager.isPortalActive()) {
                // Spawn Portal in der Mitte nach der Verzögerung
                portalManager.spawnPortalInCenter();
            }
            
            portalManager.update(delta, player);
            
            // Prüfe Portal-Kollision
            if (portalManager.isPlayerInPortal(player)) {
                // Zurück zur ersten Map - erstelle neuen GameScreen mit bestehendem Player
                GameScreen newGameScreen = new GameScreen(game);
                newGameScreen.setPlayer(player); // Player übertragen
                game.setScreen(newGameScreen);
                return;
            }
        }

        // Draw health bars (over everything)
        shape.begin(ShapeRenderer.ShapeType.Filled);
        enemyManager.renderHealthBars(shape);
        shape.end();

        // UI overlays (health bars, etc.)
        uiViewport.apply();
        shape.setProjectionMatrix(uiCamera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        playerHealthBar.render(shape);
        shape.end();

        // Render the new level bar
        levelBar.render(shape, hudBatch);

        // HUD
        renderHUD();

        // Cursor-Bild im UI-Layer (Screen-Koordinaten) zeichnen
        batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();
        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();
        float cx = mx - cursorTexture.getWidth() / 2f;
        float cy = my - cursorTexture.getHeight() / 2f;
        batch.draw(cursorTexture, cx, cy);
        batch.end();
        // Batch-Projektion wieder auf Kamera zurücksetzen
        batch.setProjectionMatrix(camera.combined);

        if (player.isDead() && !gameOver) {
            gameOver = true;
            deathTimer = 0;
        }
    }

    @Override
    public void renderWithoutUpdate() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(image, 140, 210);
        map.render(batch);
        player.render(batch);
        enemyManager.render(batch, player);
        powerUpsManager.render(batch);
        enemyManager.renderPopups(batch);
        // Bullet rendering (innerhalb des SpriteBatch-Blocks)
        bulletManager.updateAndRender(0, shape, batch); // delta = 0 für renderWithoutUpdate
        batch.end();

        // Blood effects
        shape.begin(ShapeRenderer.ShapeType.Filled);
        enemyManager.renderBloodEffects(shape);
        enemyManager.renderHealthBars(shape);
        shape.end();

        // Portal rendering (separat)
        shape.begin(ShapeRenderer.ShapeType.Filled);
        portalManager.render(batch, shape);
        shape.end();

        uiViewport.apply();
        shape.setProjectionMatrix(uiCamera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        playerHealthBar.render(shape);
        shape.end();
    }

    private void renderHUD() {
        shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        // No health bar here! Only in UI overlay.

        hudBatch.begin();
        if (GameConfig.isUnlimitedAmmo()) {
            font.draw(hudBatch, "Ammo: ∞", 20, 40);
        } else {
            font.draw(hudBatch, "Ammo: -", 20, 40);
        }

        if (player.isInvincible()) {
            font.draw(hudBatch, "Invincible!", Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 40);
        }

        // Zeige an, dass wir auf Map 2 sind
        font.draw(hudBatch, "MAP 2", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() - 20);

        hudBatch.end();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
        // Systemcursor verstecken
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        // Cursor zurücksetzen
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
        levelBar.resize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        map.dispose();
        image.dispose();
        font.dispose();
        hudBatch.dispose();
        cursorTexture.dispose();
        powerUpsManager.dispose();
        portalManager.dispose();
    }
} 