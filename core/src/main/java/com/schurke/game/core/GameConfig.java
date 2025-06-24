package com.schurke.game.core;

public class GameConfig {
    // Game Settings
    private static boolean unlimitedAmmo = false;
    
    // Portal Settings
    public static final int PORTAL_SPAWN_LEVEL = 10;
    public static final float PORTAL_SPAWN_DELAY = 3f;
    
    // Player Settings
    public static final float PLAYER_SAFE_SPAWN_X = 200f;
    public static final float PLAYER_SAFE_SPAWN_Y = 200f;
    public static final float PLAYER_SAFE_SPAWN_X_MAP2 = 100f;
    public static final float PLAYER_SAFE_SPAWN_Y_MAP2 = 100f;
    
    // UI Settings
    public static final float FONT_SCALE = 2.0f;
    public static final float HUD_FONT_SCALE = 2.2f;
    
    // Game Constants
    public static final float DEATH_DELAY = 0.5f;
    public static final float HIT_DURATION = 0.2f;
    public static final float MESSAGE_DURATION = 20f;

    public static boolean isUnlimitedAmmo() {
        return unlimitedAmmo;
    }

    public static void setUnlimitedAmmo(boolean enabled) {
        unlimitedAmmo = enabled;
    }
}
