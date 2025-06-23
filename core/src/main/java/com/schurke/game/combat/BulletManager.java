package com.schurke.game.combat;

import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.schurke.game.entities.Enemy;
import com.schurke.game.entities.EnemyManager;

public class BulletManager {
    private final List<Bullet> bullets;
    private final EnemyManager enemyManager;

    public BulletManager(List<Bullet> bullets, EnemyManager enemyManager) {
        this.bullets = bullets;
        this.enemyManager = enemyManager;
    }

    public void updateAndRender(float delta, ShapeRenderer shape) {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update(delta);
            bullet.render(shape);

            for (Enemy enemy : enemyManager.getEnemies()) {
                if (bullet.collidesWith(enemy)) {
                    enemy.takeDamage(bullet.getDamage());
                    enemyManager.hitEnemy(enemy);
                    bulletIterator.remove();
                    break;
                }
            }
        }
    }
}
