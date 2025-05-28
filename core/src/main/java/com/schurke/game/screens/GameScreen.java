package com.schurke.game.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.schurke.game.Main;
import com.schurke.game.combat.Bullet;
import com.schurke.game.combat.BulletManager;
import com.schurke.game.combat.CombatController;
import com.schurke.game.core.GameConfig;
import com.schurke.game.entities.EnemyManager;
import com.schurke.game.entities.Player;
import com.schurke.game.map.TileMap;
import com.schurke.game.ui.HealthBar;
import com.schurke.game.weapons.Shotgun;
import com.schurke.game.weapons.Weapon;

public class GameScreen implements Screen {
    private Main game;
    private SpriteBatch batch;
    private Texture image;
    private TileMap map;
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
    private static final float DEATH_DELAY = 0.5f; // Half second delay before transition
    private BitmapFont font;
    private SpriteBatch hudBatch;
    private ArrayList<Bullet> bullets;
    private Weapon currentWeapon;
    private CombatController combatController;
    private BulletManager bulletManager;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
        this.shape = game.getShapeRenderer();

        this.gameOver = false;
        this.deathTimer = 0;

        // Initialize map, camera and player
        this.map = new TileMap();
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(map.getTileSize() * map.getMapWidth(), map.getTileSize() * map.getMapHeight(), camera);
        this.viewport.apply();
        this.camera.position.set(map.getCenter(), 0);
        this.player = new Player(map.getCenter(), 100f, 100f, camera);
        this.viewport.apply();
        this.camera.position.set(map.getCenter(), 0);

        // UI camera and viewport
        this.uiCamera = new OrthographicCamera();
        this.uiViewport = new ScreenViewport(uiCamera);
        this.uiViewport.apply();

        // Initialize game elements
        this.enemyManager = new EnemyManager(map);
        this.playerHealthBar = new HealthBar(player, 20f);
        this.image = new Texture("libgdx.png");
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        hudBatch = new SpriteBatch();

        // Spawn Enemies
        enemyManager = new EnemyManager(map);
        enemyManager.spawnEnemy(10);

        //
        this.bullets = new ArrayList<>();
        this.currentWeapon = new Shotgun();
        this.combatController = new CombatController(player, currentWeapon, camera, bullets);
        this.bulletManager = new BulletManager(bullets, enemyManager);
    }

    @Override
    public void render(float delta) {
        if (gameOver) {
            deathTimer += delta;
            if (deathTimer >= DEATH_DELAY) {
                game.setScreen(new GameOverScreen(game));
                return;
            }
        }

        // Check for ESC key
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        // Kamera & Projektionen
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        // Welt zeichnen
        batch.begin();
        batch.draw(image, 140, 210);
        map.render(batch);
        player.render(batch); // Now using SpriteBatch for player
        batch.end();

        // Shape Rendering
        shape.begin(ShapeRenderer.ShapeType.Filled);
        enemyManager.render(shape);
        if (!gameOver) {
            enemyManager.update(player);
            player.update(map);
            combatController.update(delta);
            bulletManager.updateAndRender(delta, shape);
        }
        shape.end();

        // HUD
        renderHUD();

        // UI rendering with separate camera
        uiViewport.apply();
        shape.setProjectionMatrix(uiCamera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        playerHealthBar.render(shape);
        shape.end();

        if (player.isDead() && !gameOver) {
            gameOver = true;
            deathTimer = 0;
        }
    }

    public void renderWithoutUpdate() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(image, 140, 210);
        map.render(batch);
        player.render(batch); // Now using SpriteBatch for player
        batch.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        enemyManager.render(shape);
        shape.end();

        // UI rendering with separate camera
        uiViewport.apply();
        shape.setProjectionMatrix(uiCamera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        playerHealthBar.render(shape);
        shape.end();
    }

    private void renderHUD() {
        shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        shape.begin(ShapeRenderer.ShapeType.Filled);
        playerHealthBar.render(shape);
        shape.end();

        hudBatch.begin();
        if (GameConfig.isUnlimitedAmmo()) {
            font.draw(hudBatch, "Ammo: ∞", 20, 40);
        } else {
            font.draw(hudBatch, "Ammo: " + currentWeapon.getCurrentAmmo() + "/" + currentWeapon.getReserveAmmo(), 20,
                    40);
        }
        if (currentWeapon.isReloading()) {
            font.draw(hudBatch, "Reloading...", 20, 70);
        }
        hudBatch.end();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null); // Game screen doesn't need input processor
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        try {
            if (image != null)
                image.dispose();
            if (map != null)
                map.dispose();
            if (font != null)
                font.dispose();
            if (hudBatch != null)
                hudBatch.dispose();
            if (player != null) {
                player.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error disposing resources", e);
        }
    }
}
