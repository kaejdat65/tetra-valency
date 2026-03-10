package com.td.game.entities;

import com.badlogic.gdx.math.Vector3;

public class DemonEnemy extends Enemy {
    public DemonEnemy(float maxHealth, float speed, int reward) {
        super(maxHealth, speed, reward);
    }

    @Override
    protected void updateModelPosition() {
        super.updateModelPosition();
        if (modelInstance != null && alive && freezeTimer <= 0 && animationController == null) {
            float waddle = (float) Math.sin(walkTimer * 10f) * 15f; 
            modelInstance.transform.rotate(Vector3.Z, waddle);
        }
    }
}
