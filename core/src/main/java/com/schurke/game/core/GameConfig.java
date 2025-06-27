package com.schurke.game.core;

public class GameConfig {
    private static boolean unlimitedAmmo = false;
    public static final float DASH_SPEED = 1000f; // Geschwindigkeit des Dashes
    public static final float DASH_DURATION = 0.3f; // Dauer des Dashes in Sekunden
    public static final float DASH_COOLDOWN = 5f; // Abklingzeit des Dashes in Sekunden

    public static boolean isUnlimitedAmmo() {
        return unlimitedAmmo;
    }

    public static void setUnlimitedAmmo(boolean enabled) {
        unlimitedAmmo = enabled;
    }
}
