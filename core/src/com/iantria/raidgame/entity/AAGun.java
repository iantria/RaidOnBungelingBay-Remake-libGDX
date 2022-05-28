package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class AAGun extends Entity {

    public Animation<TextureRegion> explodeAnimation;
    public float explosionTimer;
    private int firedCount = 0;

    public AAGun(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        super(id, scale, isMovingObj, position, rotation, image);
        startingPosition = new Vector2(position);
        this.scale = scale;
        init();
    }


    public void init() {
        health = Constants.ENEMY_AA_GUN_HEALTH;
        isDestroyed = false;
        //refireInterval = Constants.ENEMY_AA_GUN_FIRING_INTERVAL - (RadarSite.numberOfLockedOnRadars*0.25f);
        vector1 = new Vector2(position);
        vector2 = new Vector2(position);
        vector3 = new Vector2(position);
        vector4 = new Vector2(position);
        updateVectorsForStationaryObjects();
        explosionTimer = 0;
        elapsedTime = 0;
        respawnTime = 0;
        isReadyToFire = false;
        type = EntityType.AA_GUN;
        this.explodeAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[7].getFrameDuration(),Constants.explosionAnimations[7].getKeyFrames());

    }

    public void reset() {
        this.health = Constants.ENEMY_AA_GUN_HEALTH;
        this.isDestroyed = false;
        wasHit = false;
        wasHitByCannon = false;
        wasHitByBomb = false;
        this.explosionTimer = 0;
        this.elapsedTime = 0;
        this.respawnTime = 0;
        this.isReadyToFire = false;
        this.position.x = Constants.gameMap.position.x + startingPosition.x;
        this.position.y = Constants.gameMap.position.y + startingPosition.y;
        updateVectorsForStationaryObjects();

    }

    public void update(float delta) {
        updateVectorsForStationaryObjects();

        // Ready to Fire?
        elapsedTime += delta;
        if (wasHit || isDestroyed) explosionTimer += delta;

        if (!isDestroyed && !isReadyToFire &&
                !(Constants.helicopter.mode == Helicopter.FlyingMode.CRASHED) &&
                (elapsedTime >= Constants.ENEMY_AA_GUN_FIRING_INTERVAL - (RadarSite.numberOfLockedOnRadars*0.25f))) {
            elapsedTime = 0;
            isReadyToFire = true;
        }

        // Rotation
        if ((vector1.x <= Constants.WINDOW_WIDTH && vector1.x >= 0) &&
                (vector1.y <= Constants.WINDOW_HEIGHT && vector1.y >= 0) ||
                (vector2.x <= Constants.WINDOW_WIDTH && vector2.x >= 0) &&
                        (vector2.y <= Constants.WINDOW_HEIGHT && vector2.y >= 0) ||
                (vector3.x <= Constants.WINDOW_WIDTH && vector3.x >= 0) &&
                        (vector3.y <= Constants.WINDOW_HEIGHT && vector3.y >= 0) ||
                (vector4.x <= Constants.WINDOW_WIDTH && vector4.x >= 0) &&
                        (vector4.y <= Constants.WINDOW_HEIGHT && vector4.y >= 0)) {
            rotation =((float)Constants.getSignedDegreesToHelicopter(new Vector2(position.x + image.getRegionWidth()*scale/2f, position.y + image.getRegionHeight()*scale/2f)));

            direction =  rotation - 180;
            // Refire
            if (isReadyToFire) {
                fireAAGun();
            }
        } else {
            isReadyToFire = false;
        }

        //Respawn AA
        if (isDestroyed) {
            respawnTime += delta;
            if (respawnTime >= (3 + 3 * Constants.getRemainingFactories())) {
                respawnTime = 0;
                reset();
            }
        }
    }

    public void draw(Batch batch) {
        if (isDestroyed) {
            batch.draw(explodeAnimation.getKeyFrame(explosionTimer),
                    position.x + image.getRegionWidth()/2*scale  - explodeAnimation.getKeyFrame(explosionTimer).getRegionWidth()/2,
                    position.y + image.getRegionHeight()/2*scale - explodeAnimation.getKeyFrame(explosionTimer).getRegionHeight()/2);
        } else {
            batch.draw(image, vector1.x, vector1.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector2.x, vector2.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector3.x, vector3.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector4.x, vector4.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

            if (wasHit){
//                if (getWasHitByCannonAnimation().getFrame() < 15){
//                    getWasHitByCannonAnimation().draw(vector1.getX()-5, vector1.getY()-5);
//                } else {
//                    setWasHit(false);
//                    getWasHitByCannonAnimation().restart();
//                }
            }

        }
    }


    private void fireAAGun() {
        if (Constants.isReadyToFireCruiseMissile) {
            Statistics.numberOfTimesCruiseMissileFired++;
            firedCount++;
            CruiseMissile projectile = new CruiseMissile(id + "_CruiseMissile" + firedCount, 0.05f, true,
                    new Vector2(position.x + image.getRegionWidth()*scale/2f - Constants.cruiseMissileTexture.getRegionWidth()*0.05f/2f,
                            position.y + image.getRegionHeight()*scale/2f - Constants.cruiseMissileTexture.getRegionHeight()*0.05f/2f),
                    rotation, Constants.MISSILE_SPEED, Constants.cruiseMissileTexture ,
                    Projectile.Type.ENEMY_CRUISE_MISSILE,  CruiseMissile.MainTarget.PLAYER_IS_TARGET);
            Constants.fireMissileEffect.play();
            Constants.projectileList.add(projectile);
            Constants.isReadyToFireCruiseMissile = false;
            isReadyToFire = false;
        } else {
            //System.out.println(this.id + " " + isReadyToFire + " elp:" + elapsedTime  + " int:" + refireInterval);
            firedCount++;
            Statistics.numberOfTimesAAGunFired++;
            Projectile p= new Projectile(id + "_Bullet" + firedCount, 0.25f, true,
                    new Vector2(position.x + image.getRegionWidth()*scale/2f - Constants.enemyBulletTextureRegion.getRegionWidth()*0.25f/2f,
                            position.y + image.getRegionHeight()*scale/2f - Constants.enemyBulletTextureRegion.getRegionHeight()*0.25f/2f),
                    rotation, Constants.BULLET_SPEED, Constants.enemyBulletTextureRegion, Projectile.Type.AA_GUN_BULLET);
            Constants.AAGunFireSound.play();
            Constants.projectileList.add(p);
            isReadyToFire = false;
        }
    }
}
