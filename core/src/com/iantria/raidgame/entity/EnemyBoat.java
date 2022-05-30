package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class EnemyBoat extends Entity {

    float angleToTarget = 0f;
    float diff = 0;
    public Animation<TextureRegion> boatExploded;
    public Animation<TextureRegion> machineGunFiring;
    public float explosionTimer;

    private float startingRotation;
    private float nextRotation;

    private ParticleEffect wakeEffect;
    private float pathTimer;
    private float changePathTimer;

    public EnemyBoat(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image, float timer){
        super(id, scale, isMovingObj, position, rotation, image);
        this.image = image;
        this.rotation = rotation;
        this.direction = rotation;
        this.startingRotation = rotation;
        this.scale = scale;
        this.startingRotation = rotation;
        this.nextRotation = startingRotation;
        this.wakeEffect = new ParticleEffect(Constants.enemyBoatWakeEffect);
        type = EntityType.ENEMY_BOAT;
        this.changePathTimer = timer;
        init();
    }

    public void init() {
        this.boatExploded = new Animation<TextureRegion>(Constants.explosionAnimations[5].getFrameDuration(),Constants.explosionAnimations[5].getKeyFrames());
        this.machineGunFiring = new Animation<TextureRegion>(Constants.explosionAnimations[4].getFrameDuration(),Constants.explosionAnimations[3].getKeyFrames());
        vector1 = new Vector2(position);
        vector2 = new Vector2(position);
        vector3 = new Vector2(position);
        vector4 = new Vector2(position);
        relativePositionToMap = new Vector2(position);
        speed = 0;
        health = Constants.ENEMY_BOAT_HEALTH;
        isDestroyed = false;
        updateVectorsForMovingObjects();
        startingPosition = new Vector2(position);
        explosionTimer = 0;
        pathTimer = 0;
        soundID = -1;
        animationTime = 0;
        isAttacking = false;
    }

    public void reset() {
        wasHit = false;
        isDestroyed = false;
        respawnElapsedTime = 0;
        elapsedTime = 0;
        health = Constants.ENEMY_BOAT_HEALTH;
        speed = 0;
        rotation = startingRotation;
        direction = startingRotation;
        this.nextRotation = startingRotation;

        wakeEffect.reset();
        wakeEffect.getEmitters().get(0).getAngle().setLowMin(rotation+90f);
        wakeEffect.getEmitters().get(0).getAngle().setLowMax(rotation+90f);
        wakeEffect.getEmitters().get(0).getAngle().setHighMin(rotation-30f+90f);
        wakeEffect.getEmitters().get(0).getAngle().setHighMax(rotation+30f+90f);

        this.position.x = Constants.gameMap.position.x + startingPosition.x;
        this.position.y = Constants.gameMap.position.y + startingPosition.y;
        updateVectorsForMovingObjects();
        explosionTimer = 0;
        pathTimer = 0;
        soundID = -1;
        animationTime = 0;
        isAttacking = false;
    }

    public void update(float delta) {
        // Update positions
        position.x = position.x - Constants.map_dx;
        position.y = position.y - Constants.map_dy;
        updateVectorsForMovingObjects();

        // Timers
        elapsedTime += delta;
        if (isDestroyed) explosionTimer += delta;
        if (isAttacking && !isDestroyed) animationTime += delta;
        pathTimer += delta;

        if (isDestroyed) {
            speed = 0.0f;
            respawnElapsedTime += delta;
            if (respawnElapsedTime > Constants.ENEMY_BOAT_RESPAWN_TIMER){
                reset();
            }
        } else {
            wakeEffect.getEmitters().get(0).getAngle().setLowMin(rotation+90f);
            wakeEffect.getEmitters().get(0).getAngle().setLowMax(rotation+90f);
            wakeEffect.getEmitters().get(0).getAngle().setHighMin(rotation-30f+90f);
            wakeEffect.getEmitters().get(0).getAngle().setHighMax(rotation+30f+90f);
            wakeEffect.update(delta);

            if (speed >= Constants.ENEMY_BOAT_SPEED){
                if (pathTimer > this.changePathTimer) {
                    nextRotation = rotation + 90f;
                    pathTimer = 0;
                }

                diff = Constants.calculateDifferenceBetweenAngles(rotation, nextRotation);

                if (diff < -1.5)
                    rotation = rotation + 15f * delta;
                else if (diff > 1.5)
                    rotation = rotation - 15f * delta;

                refireElapsedTime += delta;

                if (!isDestroyed && !isReadyToFire && (refireElapsedTime > (Constants.ENEMY_BOAT_FIRING_INTERVAL))) {
                    isReadyToFire = true;
                    refireElapsedTime = 0;
                }

                if ((position.x <= Constants.WINDOW_WIDTH && position.x >= 0) &&
                        (position.y <= Constants.WINDOW_HEIGHT && position.y >= 0)) {

                    if (isReadyToFire && (refireElapsedTime > Constants.ENEMY_BOAT_FIRING_INTERVAL) ) {
                        if (Constants.helicopter.mode != Helicopter.FlyingMode.CRASHED)
                            fireGun();
                    }
                }
            } else {
                speed = speed + (2f * delta);
                if (speed > Constants.ENEMY_BOAT_SPEED) speed = Constants.ENEMY_BOAT_SPEED;
            }
        }

        float hip = delta * speed;

        position.x += hip * Math.sin(Math.toRadians(rotation));
        position.y -= hip * Math.cos(Math.toRadians(rotation));

        if (rotation >= 360) rotation = rotation - 360;
        if (rotation < 0) rotation = rotation + 360;

        direction = rotation - 180;
    }

    public void draw(Batch batch) {
        if (isDestroyed){
            batch.draw(boatExploded.getKeyFrame(explosionTimer),
                    position.x + image.getRegionWidth()/2*scale  - boatExploded.getKeyFrame(explosionTimer).getRegionWidth()/2,
                    position.y + image.getRegionHeight()/2*scale - boatExploded.getKeyFrame(explosionTimer).getRegionHeight()/2);
        } else {
            if (speed != 0) {
                wakeEffect.setPosition(vector1.x + image.getRegionWidth() / 2 * scale, vector1.y + image.getRegionHeight() / 2 * scale);
                wakeEffect.draw(batch);
            }

            batch.draw(image, vector1.x, vector1.y, image.getRegionWidth() * scale / 2, image.getRegionHeight() * scale / 2, image.getRegionWidth() * scale, image.getRegionHeight() * scale, 1f, 1f, direction);
            batch.draw(image, vector2.x, vector2.y, image.getRegionWidth() * scale / 2, image.getRegionHeight() * scale / 2, image.getRegionWidth() * scale, image.getRegionHeight() * scale, 1f, 1f, direction);
            batch.draw(image, vector3.x, vector3.y, image.getRegionWidth() * scale / 2, image.getRegionHeight() * scale / 2, image.getRegionWidth() * scale, image.getRegionHeight() * scale, 1f, 1f, direction);
            batch.draw(image, vector4.x, vector4.y, image.getRegionWidth() * scale / 2, image.getRegionHeight() * scale / 2, image.getRegionWidth() * scale, image.getRegionHeight() * scale, 1f, 1f, direction);

            // Draw machine gun fire and hits
            if (isAttacking) {
                if (animationTime < 2) {
                    batch.draw(machineGunFiring.getKeyFrame(animationTime),
                            position.x + image.getRegionWidth() / 2f * scale - 8f,
                            position.y + image.getRegionHeight() / 2f * scale - 8f,
                            16,16);

                    if (wasHit && (Constants.helicopter.mode != Helicopter.FlyingMode.CRASHED)) { // draw hit on helicopter
                        batch.draw(machineGunFiring.getKeyFrame(animationTime),
                                Constants.WINDOW_WIDTH/2f - 8f,
                                Constants.WINDOW_HEIGHT/2f - 8f,
                                16,16);
                    }
                } else {
                    animationTime = 0;
                    isAttacking = false;
                    wasHit = false;
                }
            }
        }
    }

    private void fireGun() {
        isReadyToFire = false;
        refireElapsedTime = 0;
        isAttacking = true;
        Statistics.numberOfTimesBoatFired++;
        wasHit = Constants.random.nextBoolean(); // 50% chance to hit
        if (wasHit) {
            Statistics.numberOfTimesHitByBoats++;
            Constants.helicopter.health = Constants.helicopter.health - Constants.ENEMY_BOAT_DAMAGE;
        }
        Constants.machineGunFire.play();
    }
}


