package com.schurke.game.combat;

import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.schurke.game.entities.Enemy;
import com.schurke.game.entities.EnemyManager;

public class BulletManager {
    private final List<Bullet> bullets;
    private final EnemyManager enemyManager;

    public BulletManager(List<Bullet> bullets, EnemyManager enemyManager) {
        this.bullets = bullets;
        this.enemyManager = enemyManager;
    }

    public void updateAndRender(float delta, ShapeRenderer shape, SpriteBatch batch) {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update(delta);
            bullet.render(shape, batch);

            boolean hitEnemy = false;
            if (bullet.isPiercing()) {
                // For piercing bullets, hit all enemies in range, but do not remove bullet here
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (bullet.collidesWith(enemy)) {
                        enemy.takeDamage(bullet.getDamage());
                        enemyManager.hitEnemy(enemy);
                        hitEnemy = true;
                    }
                }
            } else {
                // For non-piercing bullets, remove after first hit
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (bullet.collidesWith(enemy)) {
                        enemy.takeDamage(bullet.getDamage());
                        enemyManager.hitEnemy(enemy);
                        hitEnemy = true;
                        break;
                    }
                }
                if (hitEnemy) {
                    bulletIterator.remove();
                    continue;
                }
            }
            // Remove any bullet that is expired
            if (bullet.isExpired()) {
                bulletIterator.remove();
            }
        }
    }
}
