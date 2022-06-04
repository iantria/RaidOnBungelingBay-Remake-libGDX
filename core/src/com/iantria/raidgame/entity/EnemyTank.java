package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class EnemyTank extends Entity {

    public Animation<TextureRegion> tankExploded;
    public Animation<TextureRegion> machineGunFiring;
    public float explosionTimer;
    private float startingRotation;
    private float pathTimer;
    private float changePathTimer;


    public EnemyTank(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image, float timer){
        super(id, scale, isMovingObj, position, rotation, image);
        this.image = image;
        this.rotation = rotation;
        this.direction = rotation;
        this.startingRotation = rotation;
        this.scale = scale;
        type = EntityType.ENEMY_TANK;
        this.changePathTimer = timer;
        init();
    }

    public void init() {
        this.tankExploded = new Animation<TextureRegion>(Constants.explosionAnimations[5].getFrameDuration(),Constants.explosionAnimations[5].getKeyFrames());
        this.machineGunFiring = new Animation<TextureRegion>(Constants.explosionAnimations[4].getFrameDuration(),Constants.explosionAnimations[3].getKeyFrames());
        vector1 = new Vector2(position);
        vector2 = new Vector2(position);
        vector3 = new Vector2(position);
        vector4 = new Vector2(position);
        relativePositionToMap = new Vector2(position);
        speed = 0;
        health = Constants.ENEMY_TANK_HEALTH;
        isDestroyed = false;
        updateVectorsForMovingObjects();
        startingPosition = new Vector2(position);
        explosionTimer = 0;
        soundID = -1;
        animationTime = 0;
        isAttacking = false;
        speed = Constants.ENEMY_TANK_SPEED;
        pathTimer = 0;
    }

    public void reset() {
        wasHit = false;
        isDestroyed = false;
        respawnElapsedTime = 0;
        elapsedTime = 0;
        health = Constants.ENEMY_TANK_HEALTH;
        speed = Constants.ENEMY_TANK_SPEED;;
        rotation = startingRotation;
        direction = startingRotation;
        this.position.x = Constants.gameMap.position.x + startingPosition.x;
        this.position.y = Constants.gameMap.position.y + startingPosition.y;
        updateVectorsForMovingObjects();
        explosionTimer = 0;
        soundID = -1;
        animationTime = 0;
        isAttacking = false;
        pathTimer = 0;
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
            if (respawnElapsedTime > Constants.ENEMY_TANK_RESPAWN_TIMER){
                reset();
            }
        } else {
            if (pathTimer > this.changePathTimer) {
                if (rotation != startingRotation) rotation = startingRotation;
                else rotation = startingRotation + 180f;
                pathTimer = 0;
            }

            refireElapsedTime += delta;

            if (!isDestroyed && !isReadyToFire && (refireElapsedTime > (Constants.ENEMY_TANK_FIRING_INTERVAL))) {
                isReadyToFire = true;
                refireElapsedTime = 0;
            }

            if ((position.x <= Constants.WINDOW_WIDTH && position.x >= 0) &&
                    (position.y <= Constants.WINDOW_HEIGHT && position.y >= 0)) {

                if (isReadyToFire && (refireElapsedTime > Constants.ENEMY_TANK_FIRING_INTERVAL) ) {
                    if (Constants.helicopter.mode != Helicopter.FlyingMode.CRASHED)
                        fireGun();
                }
            }
        }

        float hip = delta * speed;

        position.x += hip * Math.sin(Math.toRadians(rotation));
        position.y -= hip * Math.cos(Math.toRadians(rotation));

        direction = rotation - 180;
    }

    public void draw(Batch batch) {
        if (isDestroyed){
            batch.draw(tankExploded.getKeyFrame(explosionTimer),
                    position.x + image.getRegionWidth()/2*scale  - tankExploded.getKeyFrame(explosionTimer).getRegionWidth()/2,
                    position.y + image.getRegionHeight()/2*scale - tankExploded.getKeyFrame(explosionTimer).getRegionHeight()/2);
        } else {
            batch.draw(image, vector1.x, vector1.y, image.getRegionWidth() * scale / 2f, image.getRegionHeight() * scale / 2f, image.getRegionWidth() * scale, image.getRegionHeight() * scale, 1f, 1f, direction);
            batch.draw(image, vector2.x, vector2.y, image.getRegionWidth() * scale / 2f, image.getRegionHeight() * scale / 2f, image.getRegionWidth() * scale, image.getRegionHeight() * scale, 1f, 1f, direction);
            batch.draw(image, vector3.x, vector3.y, image.getRegionWidth() * scale / 2f, image.getRegionHeight() * scale / 2f, image.getRegionWidth() * scale, image.getRegionHeight() * scale, 1f, 1f, direction);
            batch.draw(image, vector4.x, vector4.y, image.getRegionWidth() * scale / 2f, image.getRegionHeight() * scale / 2f, image.getRegionWidth() * scale, image.getRegionHeight() * scale, 1f, 1f, direction);

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
        Statistics.numberOfTimesTankFired++;
        wasHit = Constants.random.nextBoolean(); // 50% chance to hit
        if (wasHit) {
            Statistics.numberOfTimesHitByTanks++;
            Constants.helicopter.health = Constants.helicopter.health - Constants.ENEMY_TANK_DAMAGE;
        }
        Constants.machineGunFire.play(Constants.volume);
    }
}


