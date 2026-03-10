package com.td.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.td.game.elements.Element;


public class Player implements Disposable {
    private Vector3 position;
    private float rotation;

    
    private Vector3 startPos;
    private Vector3 targetPos;
    private float animTime;
    private float moveDuration;
    private boolean isMoving;
    private float modelScale = 1f;

    private ModelInstance modelInstance;
    private AnimationController animationController;
    private ModelInstance staffOrbInstance; 
    
    private Node armL, armR, legL, legR;
    private Quaternion armLNeutral, armRNeutral, legLNeutral, legRNeutral;

    public Player(Vector3 startPosition) {
        this.position = startPosition.cpy();
        this.startPos = new Vector3();
        this.targetPos = new Vector3();
        this.rotation = 0f;
        this.isMoving = false;
    }

    public void setModel(ModelInstance modelInstance, float scale) {
        this.modelInstance = modelInstance;
        this.modelScale = scale;

        if (modelInstance.animations.size > 0) {
            this.animationController = new AnimationController(modelInstance);

            
            boolean played = false;
            for (com.badlogic.gdx.graphics.g3d.model.Animation anim : modelInstance.animations) {
                Gdx.app.log("Player", "Found Animation: " + anim.id);
                if (anim.id.toLowerCase(java.util.Locale.ROOT).contains("idle")) {
                    animationController.setAnimation(anim.id, -1);
                    played = true;
                }
            }
            if (!played) {
                Gdx.app.log("Player",
                        "No 'idle' found, playing first animation: " + modelInstance.animations.get(0).id);
                animationController.setAnimation(modelInstance.animations.get(0).id, -1);
            }
        }

        
        armL = modelInstance.getNode("DEF-ARM.L", true);
        armR = modelInstance.getNode("DEF-ARM.R", true);
        legL = modelInstance.getNode("DEF-LEG.L", true);
        legR = modelInstance.getNode("DEF-LEG.R", true);

        
        if (armL != null)
            armLNeutral = new Quaternion(armL.localTransform.getRotation(new Quaternion()));
        if (armR != null)
            armRNeutral = new Quaternion(armR.localTransform.getRotation(new Quaternion()));
        if (legL != null)
            legLNeutral = new Quaternion(legL.localTransform.getRotation(new Quaternion()));
        if (legR != null)
            legRNeutral = new Quaternion(legR.localTransform.getRotation(new Quaternion()));

        
        
        Gdx.app.log("Player", "--- Model Nodes ---");
        for (com.badlogic.gdx.graphics.g3d.model.Node node : modelInstance.nodes) {
            printNodes(node, "");
        }
        Gdx.app.log("Player", "-------------------");

        updateTransform();
    }

    private void printNodes(com.badlogic.gdx.graphics.g3d.model.Node node, String prefix) {
        Gdx.app.log("Player", prefix + node.id);
        if (node.hasChildren()) {
            for (com.badlogic.gdx.graphics.g3d.model.Node child : node.getChildren()) {
                printNodes(child, prefix + "  ");
            }
        }
    }

    public void setStaffOrbModel(ModelInstance orbInstance, Element element) {
        this.staffOrbInstance = orbInstance;
        updateTransform();
    }

    public void clearStaffOrb() {
        this.staffOrbInstance = null;
    }

    
    public void moveTo(Vector3 target, float duration) {
        this.startPos.set(this.position);
        this.targetPos.set(target);
        this.moveDuration = duration;
        this.animTime = 0;
        this.isMoving = true;

        
        Vector3 dir = target.cpy().sub(startPos);
        if (dir.len2() > 0.01f) {
            this.rotation = (float) Math.toDegrees(Math.atan2(-dir.x, -dir.z));
        }
    }

    public void setPosition(Vector3 pos) {
        this.position.set(pos);
        this.isMoving = false;
        updateTransform();
    }

    public void update(float delta) {
        
        if (animationController != null) {
            animationController.update(delta);
        }

        
        if (isMoving) {
            animTime += delta;
            float t = animTime / moveDuration;

            if (t >= 1f) {
                position.set(targetPos);
                isMoving = false;
            } else {
                
                position.set(startPos).lerp(targetPos, t);

                
                float jumpHeight = 0.6f;
                float hopAngle = t * (float) Math.PI * 2f;
                position.y = Math.abs((float) Math.sin(hopAngle)) * jumpHeight;

                applyProceduralAnimation(true);
            }
            updateTransform();
        } else {
            
            if (modelInstance != null) {
                applyProceduralAnimation(false);
            }
        }

        
        if (staffOrbInstance != null) {
            staffOrbInstance.transform.rotate(Vector3.Y, delta * 90f);
        }
    }

    private void applyProceduralAnimation(boolean moving) {
        if (modelInstance == null)
            return;

        float swingAngle = 0f;

        if (moving) {
            float speedMultiplier = 12f;
            swingAngle = (float) Math.sin(animTime * speedMultiplier) * 35f;
        } else {
            
            float speedMultiplier = 2f;
            swingAngle = (float) Math.sin(System.currentTimeMillis() / 1000.0 * speedMultiplier) * 10f; 
                                                                                                        
                                                                                                        
                                                                                                        
                                                                                                        
        }

        
        
        
        if (armL != null && armLNeutral != null) {
            armL.localTransform.idt().rotate(armLNeutral).rotate(Vector3.X, swingAngle).rotate(Vector3.Z, swingAngle);
        }
        if (armR != null && armRNeutral != null) {
            armR.localTransform.idt().rotate(armRNeutral).rotate(Vector3.X, -swingAngle).rotate(Vector3.Z, -swingAngle);
        }
        if (legL != null && legLNeutral != null) {
            legL.localTransform.idt().rotate(legLNeutral).rotate(Vector3.X, -swingAngle).rotate(Vector3.Z, -swingAngle);
        }
        if (legR != null && legRNeutral != null) {
            legR.localTransform.idt().rotate(legRNeutral).rotate(Vector3.X, swingAngle).rotate(Vector3.Z, swingAngle);
        }

        modelInstance.calculateTransforms();
    }

    private void updateTransform() {
        if (modelInstance != null) {
            modelInstance.transform.setToTranslation(position.x, position.y + 0.5f, position.z); 

            
            float stretch = 1f;
            float squish = 1f;
            if (isMoving) {
                float t = animTime / moveDuration;
                float bounce = Math.abs((float) Math.sin(t * Math.PI * 2f));
                stretch = 1.0f + bounce * 0.4f;
                squish = 1.0f - bounce * 0.2f;
            } else {
                float idleBounce = (float) Math.sin(System.currentTimeMillis() / 1000.0 * 4f);
                stretch = 1.0f + idleBounce * 0.05f;
                squish = 1.0f - idleBounce * 0.05f;
            }

            modelInstance.transform.scl(modelScale * squish, modelScale * stretch, modelScale * squish);

            
            modelInstance.transform.rotate(Vector3.Y, rotation + 180f);
        }

        if (staffOrbInstance != null) {
            
            
            float rad = (float) Math.toRadians(rotation);
            float baseToCurrentRatio = (com.td.game.utils.Constants.TILE_SIZE / 2.0f) * 2.5f;
            float offsetX = 0.4f * baseToCurrentRatio; 
            float offsetZ = 0.4f * baseToCurrentRatio; 
            float offsetY = 1.8f * baseToCurrentRatio; 

            float x = position.x + (float) (Math.cos(rad) * offsetX - Math.sin(rad) * offsetZ);
            float z = position.z + (float) (Math.sin(rad) * offsetX + Math.cos(rad) * offsetZ);

            staffOrbInstance.transform.setToTranslation(x, position.y + offsetY, z);
            staffOrbInstance.transform.scale(0.5f * baseToCurrentRatio, 0.5f * baseToCurrentRatio,
                    0.5f * baseToCurrentRatio); 
        }
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        if (modelInstance != null) {
            modelBatch.render(modelInstance, environment);
        }

        if (staffOrbInstance != null) {
            
            
        }
    }

    public Vector3 getPosition() {
        return position;
    }

    public void save(com.td.game.systems.SaveData data) {
        data.playerX = position.x;
        data.playerY = position.y;
        data.playerZ = position.z;
    }

    public void load(com.td.game.systems.SaveData data) {
        position.set(data.playerX, data.playerY, data.playerZ);
        this.startPos.set(position);
        this.targetPos.set(position);
        this.isMoving = false;
        updateTransform();
    }

    @Override
    public void dispose() {
        
    }
}

