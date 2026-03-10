package com.td.game.entities;

public class BatEnemy extends FlyingEnemy {
    
    public BatEnemy(float maxHealth, float speed, int reward) {
        super(maxHealth, speed, reward);
        // isFlying is already set to true in FlyingEnemy constructor
    }

    @Override
    protected void updateModelPosition() {
        super.updateModelPosition();
        if (modelInstance != null && alive && freezeTimer <= 0 && animationController == null) {
            float flap = (float) Math.abs(Math.sin(walkTimer * 15f)) * 0.2f;
            modelInstance.transform.scale(1f + flap, 1f + flap, 1f + flap);
        }
    }
}
