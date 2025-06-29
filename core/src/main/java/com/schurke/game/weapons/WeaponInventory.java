package com.schurke.game.weapons;

import java.util.ArrayList;
import java.util.List;

public class WeaponInventory {
    private final List<Weapon> weapons;
    private int equippedIndex;

    public WeaponInventory() {
        this.weapons = new ArrayList<>();
        this.equippedIndex = 0;
    }

    public void addWeapon(Weapon weapon) {
        if (!weapons.contains(weapon) && weapons.size() < 9) {
            weapons.add(weapon);
        }
    }

    public void unlockWeapon(int slot, Weapon weapon) {
        while (weapons.size() < slot) {
            weapons.add(null);
        }
        weapons.set(slot - 1, weapon);
    }

    public void equip(int index) {
        if (index >= 0 && index < weapons.size() && weapons.get(index) != null) {
            equippedIndex = index;
        }
    }

    public Weapon getEquippedWeapon() {
        if (equippedIndex < weapons.size()) {
            return weapons.get(equippedIndex);
        }
        return null;
    }

    public int getEquippedIndex() {
        return equippedIndex;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public int size() {
        return weapons.size();
    }
}
