package com.schurke.game.screens;

import java.util.ArrayList;
import java.util.List;

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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.schurke.game.Main;
import com.schurke.game.combat.Bullet;
import com.schurke.game.combat.BulletManager;
import com.schurke.game.combat.CombatController;
import com.schurke.game.combat.RoundManager;
import com.schurke.game.core.GameConfig;
import com.schurke.game.entities.EnemyManager;
import com.schurke.game.entities.OrbManager;
import com.schurke.game.entities.Player;
import com.schurke.game.map.TileMap;
import com.schurke.game.ui.HealthBar;
import com.schurke.game.ui.LevelBar;
import com.schurke.game.ui.DashCooldownUI;
import com.schurke.game.ui.LaserBurstCooldownUI;
import com.schurke.game.weapons.LaserGun;
import com.schurke.game.weapons.Weapon;
import com.schurke.game.PowerUps.PowerUpsManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.audio.Music;
import com.schurke.game.weapons.WeaponInventory;
import com.schurke.game.weapons.Shotgun;
import com.schurke.game.weapons.AssaultRifle;

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
    private OrbManager orbManager;
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

    private Texture cursorTexture;
    private Music gameMusic;

    private DashCooldownUI dashCooldownUI;
    private LaserBurstCooldownUI laserBurstCooldownUI;

    private WeaponInventory weaponInventory;
    private Shotgun shotgun;
    private AssaultRifle assaultRifle;

    private Texture lasergunIcon = new Texture(Gdx.files.internal("weapons/lasergun.png"));
    private Texture shotgunIcon = new Texture(Gdx.files.internal("weapons/shotgun.png"));
    private Texture assaultRifleIcon = new Texture(Gdx.files.internal("weapons/assault_rifle.png"));

    // Add fields for unlock notifications
    private String weaponUnlockMessage = null;
    private float weaponUnlockMessageTimer = 0f;
    private static final float WEAPON_UNLOCK_MSG_DURATION = 3.0f;

    // Add fields for Q ability unlock notification
    private boolean qAbilityUnlocked = false;
    private boolean showQUnlockMsg = false;
    private float qUnlockMsgTimer = 0f;
    private static final float Q_UNLOCK_MSG_DURATION = 3.0f;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
        this.shape = game.getShapeRenderer();
        this.gameOver = false;
        this.deathTimer = 0;

        this.map = new TileMap();
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(camera);
        this.viewport.apply();
        this.camera.position.set(map.getCenter(), 0);
        this.player = new Player(map.getCenter(), 100f, 100f, camera);

        this.uiCamera = new OrthographicCamera();
        this.uiViewport = new ScreenViewport(uiCamera);
        this.uiViewport.apply();

        this.enemyManager = new EnemyManager(map);
        this.orbManager = new OrbManager();
        this.playerHealthBar = new HealthBar(player, 20f);
        this.image = new Texture("libgdx.png");
        this.font = new BitmapFont();
        this.font.getData().setScale(2.0f);
        this.hudBatch = new SpriteBatch();
        this.levelBar = new LevelBar(player, font);

        this.bullets = new ArrayList<>();
        this.weaponInventory = new WeaponInventory();
        this.currentWeapon = new LaserGun();
        this.weaponInventory.unlockWeapon(1, currentWeapon); // Slot 1: LaserGun always available
        this.shotgun = new Shotgun();
        this.assaultRifle = new AssaultRifle();
        this.combatController = new CombatController(player, weaponInventory.getEquippedWeapon(), camera, bullets);
        this.bulletManager = new BulletManager(bullets, enemyManager);
        this.roundManager = new RoundManager(enemyManager);

        this.powerUpsManager = new PowerUpsManager(map);

        this.cursorTexture = new Texture(Gdx.files.internal("cursor/cursor_aim.png"));

        // Initialisiere DashCooldownUI
        this.dashCooldownUI = new DashCooldownUI(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize LaserBurstCooldownUI
        this.laserBurstCooldownUI = new LaserBurstCooldownUI(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Lade und starte die Hintergrundmusik
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/background_music/ingame_music.mp3"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.5f);
        gameMusic.play();
    }

    @Override
    public void render(float delta) {
        // Clamp delta to avoid stutter on lag spikes
        delta = Math.min(delta, 1f / 30f);

        if (gameOver) {
            deathTimer += delta;
            if (deathTimer >= DEATH_DELAY) {
                game.setScreen(new GameOverScreen(game, this));
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        // Camera follows player, but clamp to map bounds
        float camHalfWidth = camera.viewportWidth / 2f;
        float camHalfHeight = camera.viewportHeight / 2f;
        float mapPixelWidth = map.getMapWidth() * map.getTileSize();
        float mapPixelHeight = map.getMapHeight() * map.getTileSize();
        float camX = player.getPosition().x;
        float camY = player.getPosition().y;
        camX = Math.max(camHalfWidth, Math.min(camX, mapPixelWidth - camHalfWidth));
        camY = Math.max(camHalfHeight, Math.min(camY, mapPixelHeight - camHalfHeight));
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
        batch.end();

        // Draw health bars (over everything)
        shape.begin(ShapeRenderer.ShapeType.Filled);
        enemyManager.renderHealthBars(shape);
        orbManager.render(shape);
        shape.end();

        // Update game logic and draw health bars
        shape.begin(ShapeRenderer.ShapeType.Filled);
        if (!gameOver) {
            roundManager.update(player);
            enemyManager.update(player);
            orbManager.update(delta, player.getPosition(), enemyManager.getEnemies());
            orbManager.checkLevelUpgrade(player.getLevel());
            player.update(map);
            combatController.update(delta);
            bulletManager.updateAndRender(delta, shape, batch);

            // Power-up logic now depends on player level
            powerUpsManager.update(delta, player);

            // Weapon unlock logic
            if (player.getLevel() >= 5 && weaponInventory.size() < 2) {
                weaponInventory.unlockWeapon(2, shotgun);
                weaponUnlockMessage = "Shotgun unlocked!";
                weaponUnlockMessageTimer = WEAPON_UNLOCK_MSG_DURATION;
            }
            if (player.getLevel() >= 20 && weaponInventory.size() < 3) {
                weaponInventory.unlockWeapon(3, assaultRifle);
                weaponUnlockMessage = "Assault Rifle unlocked!";
                weaponUnlockMessageTimer = WEAPON_UNLOCK_MSG_DURATION;
            }

            // Decrement timer
            if (weaponUnlockMessageTimer > 0) {
                weaponUnlockMessageTimer -= delta;
                if (weaponUnlockMessageTimer <= 0) weaponUnlockMessage = null;
            }

            // Weapon switching logic
            for (int i = 0; i < 9; i++) {
                if (Gdx.input.isKeyJustPressed(Keys.NUM_1 + i)) {
                    weaponInventory.equip(i);
                    // Update CombatController with new weapon
                    combatController.setWeapon(weaponInventory.getEquippedWeapon());
                }
            }

            // Q ability logic
            if (!qAbilityUnlocked && player.getLevel() >= 10) {
                qAbilityUnlocked = true;
                showQUnlockMsg = true;
                qUnlockMsgTimer = Q_UNLOCK_MSG_DURATION;
            }
            if (showQUnlockMsg) {
                qUnlockMsgTimer -= delta;
                if (qUnlockMsgTimer <= 0) showQUnlockMsg = false;
            }
        }
        shape.end();

        // HUD
        renderHUD();

        // UI overlays (health bars, etc.)
        uiViewport.apply();
        shape.setProjectionMatrix(uiCamera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        playerHealthBar.render(shape);
        shape.end();

        // Render the new level bar
        levelBar.render(shape, hudBatch);

        // Set up UI projection matrix for dash cooldown and cursor
        hudBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        // Render dash cooldown UI
        hudBatch.begin();
        dashCooldownUI.render(hudBatch, player.getDashCooldownTimer());

        // Render laser burst cooldown UI
        if (qAbilityUnlocked) {
            laserBurstCooldownUI.render(hudBatch, combatController.getLaserBurstCooldown(), combatController.isLaserBurstReloading());
        }

        // Render cursor (now in the same batch)
        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();
        float cx = mx - cursorTexture.getWidth() / 2f;
        float cy = my - cursorTexture.getHeight() / 2f;
        hudBatch.draw(cursorTexture, cx, cy);
        hudBatch.end();

        // Reset projection matrix
        batch.setProjectionMatrix(camera.combined);

        // Render weapon inventory UI (bottom left)
        renderWeaponInventoryUI();

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
        batch.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        enemyManager.renderHealthBars(shape);
        shape.end();

        uiViewport.apply();
        shape.setProjectionMatrix(uiCamera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        playerHealthBar.render(shape);
        shape.end();

        // Render the level bar
        levelBar.render(shape, hudBatch);

        // Set up UI projection matrix for dash cooldown and cursor
        hudBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        // Render dash cooldown UI and cursor
        hudBatch.begin();
        dashCooldownUI.render(hudBatch, player.getDashCooldownTimer());

        // Render laser burst cooldown UI
        laserBurstCooldownUI.render(hudBatch, combatController.getLaserBurstCooldown(), combatController.isLaserBurstReloading());

        // Render cursor
        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();
        float cx = mx - cursorTexture.getWidth() / 2f;
        float cy = my - cursorTexture.getHeight() / 2f;
        hudBatch.draw(cursorTexture, cx, cy);
        hudBatch.end();
    }

    private void renderHUD() {
        shape.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        // No health bar here! Only in UI overlay.
        hudBatch.begin();
        if (player.isInvincible()) {
            // Positioniere den Text unter der Level-Bar
            float screenX = Gdx.graphics.getWidth() / 2f;
            float screenY = Gdx.graphics.getHeight() - 60f; // 20px unter der Level-Bar

            // Zentriere den Text
            String text = "Invincible!";
            float textWidth = font.draw(hudBatch, text, 0, 0).width;

            // Zeichne den Text mit einem leichten Pulsieren
            float alpha = (float) (0.6f + 0.4f * Math.sin(Gdx.graphics.getFrameId() * 0.1f));
            font.setColor(1, 1, 1, alpha);
            font.draw(hudBatch, text, screenX - textWidth / 2, screenY);
            font.setColor(1, 1, 1, 1); // Setze die Farbe zurück
        }

        // Render orb unlock message
        if (orbManager.shouldShowUnlockMessage()) {
            float screenX = Gdx.graphics.getWidth() / 2f;
            float screenY = Gdx.graphics.getHeight() / 2f + 100f; // Center of screen, slightly above

            // Zentriere den Text
            String text = "You unlocked the protecting orb!";
            float textWidth = font.draw(hudBatch, text, 0, 0).width;

            // Zeichne den Text mit einem leichten Pulsieren
            float alpha = (float) (0.7f + 0.3f * Math.sin(Gdx.graphics.getFrameId() * 0.15f));
            font.setColor(0.4f, 0.8f, 1.0f, alpha); // Blue color for orb message
            font.draw(hudBatch, text, screenX - textWidth / 2, screenY);
            font.setColor(1, 1, 1, 1); // Setze die Farbe zurück
        }

        // In renderHUD, after orb unlock message, show weapon unlock message if present
        if (weaponUnlockMessage != null) {
            float screenX = Gdx.graphics.getWidth() / 2f;
            float screenY = Gdx.graphics.getHeight() / 2f + 60f;
            String text = weaponUnlockMessage;
            float textWidth = font.draw(hudBatch, text, 0, 0).width;
            float alpha = (float) (0.7f + 0.3f * Math.sin(Gdx.graphics.getFrameId() * 0.15f));
            font.setColor(1f, 0.8f, 0.2f, alpha); // Orange for weapon unlock
            font.draw(hudBatch, text, screenX - textWidth / 2, screenY);
            font.setColor(1, 1, 1, 1);
        }

        // In renderHUD, after orb/weapon unlock messages, show Q unlock message if present
        if (showQUnlockMsg) {
            float screenX = Gdx.graphics.getWidth() / 2f;
            float screenY = Gdx.graphics.getHeight() / 2f + 30f;
            String text = "Unlocked Burst Shot!";
            float textWidth = font.draw(hudBatch, text, 0, 0).width;
            float alpha = (float) (0.7f + 0.3f * Math.sin(Gdx.graphics.getFrameId() * 0.15f));
            font.setColor(0.4f, 1f, 0.7f, alpha); // Greenish for Q unlock
            font.draw(hudBatch, text, screenX - textWidth / 2, screenY);
            font.setColor(1, 1, 1, 1);
        }
        hudBatch.end();
    }

    private void renderWeaponInventoryUI() {
        float startX = 20f;
        float startY = 80f;
        float slotSize = 72f;
        float padding = 16f;
        List<Weapon> weapons = weaponInventory.getWeapons();
        int equipped = weaponInventory.getEquippedIndex();

        hudBatch.begin();
        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i) == null) continue;
            float x = startX + i * (slotSize + padding);
            float y = startY;
            // Draw slot background highlight only
            if (i == equipped) {
                hudBatch.setColor(0.2f, 0.8f, 1f, 0.25f); // Subtle highlight for equipped
                hudBatch.draw(assaultRifleIcon, x, y, slotSize, slotSize); // Use a transparent overlay or just skip if you want no highlight
            }
            hudBatch.setColor(1, 1, 1, 1);
            // Draw weapon icon
            Texture icon = null;
            if (weapons.get(i) instanceof LaserGun) icon = lasergunIcon;
            else if (weapons.get(i) instanceof Shotgun) icon = shotgunIcon;
            else if (weapons.get(i) instanceof AssaultRifle) icon = assaultRifleIcon;
            if (icon != null) {
                hudBatch.draw(icon, x + 4, y + 4, slotSize - 8, slotSize - 8);
            }
            // Draw slot number overlay
            font.setColor(1, 1, 1, 0.7f);
            font.draw(hudBatch, String.valueOf(i + 1), x + 4, y + 16);
            font.setColor(1, 1, 1, 1);
        }
        hudBatch.end();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
        // Systemcursor verstecken
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        // Setze die Musik fort wenn der Screen wieder angezeigt wird
        if (gameMusic != null) {
            gameMusic.play();
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        // Cursor zurücksetzen
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        // Pausiere die Musik wenn der Screen versteckt wird (z.B. beim Pause-Menü)
        if (gameMusic != null) {
            gameMusic.pause();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
        levelBar.resize(width, height);
        // Update dash cooldown UI position
        this.dashCooldownUI = new DashCooldownUI(width, height);
        // Update laser burst cooldown UI position
        this.laserBurstCooldownUI = new LaserBurstCooldownUI(width, height);
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
            if (cursorTexture != null) cursorTexture.dispose();
            if (combatController != null) combatController.dispose();
            // Cursor zurücksetzen
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            if (gameMusic != null) {
                gameMusic.dispose();
            }
            if (dashCooldownUI != null) {
                dashCooldownUI.dispose();
            }
            if (laserBurstCooldownUI != null) {
                laserBurstCooldownUI.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error disposing resources", e);
        }
    }
}
