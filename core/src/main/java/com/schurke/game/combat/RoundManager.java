package com.schurke.game.combat;

import com.schurke.game.entities.EnemyManager;

public class RoundManager {
    private int currentRound = 1;
    private EnemyManager enemyManager;
    private boolean waitingForNextRound = false;
    private float countdown = 0;
    private boolean roundStarting = false;

    public RoundManager(EnemyManager enemyManager) {
        this.enemyManager = enemyManager;
        enemyManager.spawnEnemy(currentRound * 2);
    }

    public void update() {
        if (!roundStarting && enemyManager.hasNoEnemies()) {
            startCountdown();
        }

        if (roundStarting) {
            countdown -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            if (countdown <= 0) {
                currentRound++;
                enemyManager.spawnEnemy(currentRound * 2);
                roundStarting = false;
            }
        }
    }

    public void startCountdown() {
        countdown = 3f;
        roundStarting = true;
    }

    public boolean isRoundStarting() {
        return roundStarting;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getCountdownNumber() {
        return Math.max(1, (int) Math.ceil(countdown));
    }
}
