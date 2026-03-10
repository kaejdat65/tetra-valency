package com.td.game.entities;

public class PinkBlobEnemy extends Enemy {

    public PinkBlobEnemy(float maxHealth, float speed, int reward) {
        super(maxHealth, speed, reward);
    }

    @Override
    protected void updateModelPosition() {
        super.updateModelPosition();
        if (modelInstance != null && alive && freezeTimer <= 0 && animationController == null) {
            float squash = (float) Math.abs(Math.sin(walkTimer * 15f)) * 0.3f;
            modelInstance.transform.scale(1f - squash * 0.5f, 1f + squash, 1f - squash * 0.5f);
        }
    }
}
