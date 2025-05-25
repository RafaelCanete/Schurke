package com.schurke.game;

public class GameConfig {
    private static boolean unlimitedAmmo = false;

    public static boolean isUnlimitedAmmo() {
        return unlimitedAmmo;
    }

    public static void setUnlimitedAmmo(boolean enabled) {
        unlimitedAmmo = enabled;
    }
}
