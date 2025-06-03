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
import com.schurke.game.combat.RoundManager;
import com.schurke.game.core.GameConfig;
import com.schurke.game.entities.EnemyManager;
import com.schurke.game.entities.Player;
import com.schurke.game.map.TileMap;
import com.schurke.game.ui.HealthBar;
import com.schurke.game.weapons.Shotgun;
import com.schurke.game.weapons.Weapon;
import com.schurke.game.PowerUps.PowerUpsManager;

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
    private static final float DEATH_DELAY = 0.5f;
    private BitmapFont font;
    private SpriteBatch hudBatch;
    private ArrayList<Bullet> bullets;
    private Weapon currentWeapon;
    private CombatController combatController;
    private BulletManager bulletManager;
    private RoundManager roundManager;

    // PowerUps
    private PowerUpsManager powerUpsManager;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
        this.shape = game.getShapeRenderer();
        this.gameOver = false;
        this.deathTimer = 0;

        this.map = new TileMap();
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(map.getTileSize() * map.getMapWidth(), map.getTileSize() * map.getMapHeight(), camera);
        this.viewport.apply();
        this.camera.position.set(map.getCenter(), 0);
        this.player = new Player(map.getCenter(), 100f, 100f, camera);

        this.uiCamera = new OrthographicCamera();
        this.uiViewport = new ScreenViewport(uiCamera);
        this.uiViewport.apply();

        this.enemyManager = new EnemyManager(map);
        this.playerHealthBar = new HealthBar(player, 20f);
        this.image = new Texture("libgdx.png");
        this.font = new BitmapFont();
        this.font.getData().setScale(2.0f);
        this.hudBatch = new SpriteBatch();

        this.bullets = new ArrayList<>();
        this.currentWeapon = new Shotgun();
        this.combatController = new CombatController(player, currentWeapon, camera, bullets);
        this.bulletManager = new BulletManager(bullets, enemyManager);
        this.roundManager = new RoundManager(enemyManager);

        this.powerUpsManager = new PowerUpsManager(map);
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

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        // Draw world
        batch.begin();
        batch.draw(image, 140, 210);
        map.render(batch);
        player.render(batch);
        enemyManager.render(batch);
        powerUpsManager.render(batch);
        batch.end();

        // Update game logic and draw health bars
        shape.begin(ShapeRenderer.ShapeType.Filled);
        if (!gameOver) {
            roundManager.update();
            enemyManager.update(player);
            player.update(map);
            combatController.update(delta);
            bulletManager.updateAndRender(delta, shape);

            // ✅ Power-up logic: spawn + apply (if round >= 3)
            powerUpsManager.update(delta, player, roundManager.getCurrentRound());
        }
        enemyManager.renderHealthBars(shape);
        shape.end();

        // HUD
        renderHUD();

        // UI overlays (health bars, etc.)
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
        player.render(batch);
        enemyManager.render(batch);
        powerUpsManager.render(batch);
        batch.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        enemyManager.renderHealthBars(shape);
        shape.end();

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
            font.draw(hudBatch, "Ammo: " + currentWeapon.getCurrentAmmo() + "/" + currentWeapon.getReserveAmmo(), 20, 40);
        }

        font.draw(hudBatch, "Round: " + roundManager.getCurrentRound(), 20, 80);

        if (currentWeapon.isReloading()) {
            font.draw(hudBatch, "Reloading...", 20, 70);
        }

        if (roundManager.isRoundStarting()) {
            String msg = "Round " + (roundManager.getCurrentRound() + 1) + " in " + roundManager.getCountdownNumber();
            font.draw(hudBatch, msg, Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
        }

        if (player.isInvincible()) {
            font.draw(hudBatch, "Invincible!", Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 40);
        }

        hudBatch.end();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        try {
            if (image != null) image.dispose();
            if (map != null) map.dispose();
            if (font != null) font.dispose();
            if (hudBatch != null) hudBatch.dispose();
            if (player != null) player.dispose();
            if (powerUpsManager != null) powerUpsManager.dispose();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error disposing resources", e);
        }
    }
}
