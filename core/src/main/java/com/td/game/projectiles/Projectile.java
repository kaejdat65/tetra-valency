package com.td.game.projectiles;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.td.game.elements.Element;
import com.td.game.entities.Enemy;
import com.td.game.systems.WaveManager;

public class Projectile {
    private Vector3 position;
    private Enemy target;
    private Element element;
    private float speed;
    private float damage;
    private boolean active;

    private ModelInstance modelInstance;

    // For Fire AoE
    private WaveManager waveManager;
    private static final float FIRE_AOE_RADIUS = 3.0f;

    public Projectile(Vector3 startPos, Enemy target, Element element, float speed, float damage,
            ModelInstance modelInstance, WaveManager waveManager) {
        this.position = new Vector3(startPos);
        this.target = target;
        this.element = element;
        this.speed = speed;
        this.damage = damage;
        this.active = true;
        this.modelInstance = modelInstance;
        this.waveManager = waveManager;

        if (this.modelInstance != null) {
            this.modelInstance.transform.setToTranslation(position);
        }
    }

    public void update(float delta) {
        if (!active)
            return;

        if (target == null || !target.isAlive()) {
            active = false;
            return;
        }

        Vector3 targetPos = target.getPosition().cpy();
        targetPos.y += 1.0f; // Aim at center of body

        Vector3 direction = targetPos.cpy().sub(position).nor();
        position.add(direction.scl(speed * delta));

        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(position);
        }

        // Check collision
        if (position.dst(targetPos) < 1.0f) {
            onHit();
        }
    }

    private void onHit() {
        active = false;

        switch (element) {
            case FIRE:
                // Deals AoE damage & adds ramping stacks
                if (waveManager != null) {
                    for (Enemy e : waveManager.getActiveEnemies()) {
                        if (e.isAlive() && e.getPosition().dst(target.getPosition()) <= FIRE_AOE_RADIUS) {
                            e.takeDamage(damage, element);
                            e.addFireStack();
                        }
                    }
                } else {
                    target.takeDamage(damage, element);
                    target.addFireStack();
                }
                break;

            case WATER:
                // Slow effect implementation
                target.takeDamage(damage, element);
                target.applySlow(2.0f, 0.5f); // 2 second slow, 50% speed
                break;

            case EARTH:
                // Stun effect implementation
                target.takeDamage(damage, element);
                target.applyStun(1.5f); // 1.5 second stun
                break;

            case AIR:
                // Knockback effect implementation
                target.takeDamage(damage, element);
                target.applyKnockback(1.5f); // 1.5 tiles knockback distance
                break;

            default:
                target.takeDamage(damage, element);
                break;
        }
    }

    public void render(ModelBatch batch, Environment environment) {
        if (active && modelInstance != null) {
            batch.render(modelInstance, environment);
        }
    }

    public boolean isActive() {
        return active;
    }
}
