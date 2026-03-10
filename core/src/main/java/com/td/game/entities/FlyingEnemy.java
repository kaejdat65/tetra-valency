package com.td.game.entities;

public class FlyingEnemy extends Enemy {

    public FlyingEnemy(float maxHealth, float speed, int reward) {
        super(maxHealth, speed, reward);
        this.isFlying = true;
    }

}
