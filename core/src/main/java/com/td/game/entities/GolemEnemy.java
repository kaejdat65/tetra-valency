package com.td.game.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;

public class GolemEnemy extends Enemy {
    private float animationTime;

    public GolemEnemy(float maxHealth, float speed, int reward) {
        super(maxHealth, speed, reward);
        this.animationTime = 0f;
        this.armor = 10f; // Armored default
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!alive || modelInstance == null || freezeTimer > 0)
            return;

        if (currentWaypointIndex < waypoints.size) {
            float moveSpeed = baseSpeed * slowMultiplier;
            animationTime += deltaTime * moveSpeed * 4f;

            Node leftArm = modelInstance.getNode("leftArm");
            Node rightArm = modelInstance.getNode("rightArm");
            Node leftLeg = modelInstance.getNode("leftLeg");
            Node rightLeg = modelInstance.getNode("rightLeg");

            float swingAngle = MathUtils.sin(animationTime) * 35f;

            if (leftArm != null) {
                leftArm.localTransform.setToTranslation(0.6f, 1.8f, 0)
                        .rotate(1, 0, 0, -swingAngle);
            }
            if (rightArm != null) {
                rightArm.localTransform.setToTranslation(-0.6f, 1.8f, 0)
                        .rotate(1, 0, 0, swingAngle);
            }
            if (leftLeg != null) {
                leftLeg.localTransform.setToTranslation(0.2f, 0.9f, 0)
                        .rotate(1, 0, 0, swingAngle);
            }
            if (rightLeg != null) {
                rightLeg.localTransform.setToTranslation(-0.2f, 0.9f, 0)
                        .rotate(1, 0, 0, -swingAngle);
            }

            modelInstance.calculateTransforms();
        }
    }

    @Override
    protected void updateModelPosition() {
        super.updateModelPosition();
        if (modelInstance != null && alive && freezeTimer <= 0 && animationController == null) {
            Node leftArm = modelInstance.getNode("leftArm");
            if (leftArm == null) {
                float waddle = (float) Math.sin(walkTimer * 6f) * 10f; // degrees
                modelInstance.transform.rotate(Vector3.Z, waddle);
            }
        }
    }
}
