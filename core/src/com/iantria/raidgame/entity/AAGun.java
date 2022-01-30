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
        setStartingPosition(new Vector2(position));
        this.scale = scale;
        init();
    }


    public void init() {
        health = Constants.ENEMY_AA_GUN_HEALTH;
        isDestroyed = false;
        refireInterval = Constants.ENEMY_AA_GUN_FIRING_INTERVAL + Constants.random.nextFloat()*2;
        setVector1(new Vector2(position));
        setVector2(new Vector2(position));
        setVector3(new Vector2(position));
        setVector4(new Vector2(position));
        updateVectorsForStationaryObjects();
        explosionTimer = 0;
        elapsedTime = 0;
        respawnTime = 0;
        isReadyToFire = false;
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
        this.position.x = Constants.gameMap.position.x + getStartingPosition().x;
        this.position.y = Constants.gameMap.position.y + getStartingPosition().y;
        updateVectorsForStationaryObjects();

    }

    public void update(float delta) {
        updateVectorsForStationaryObjects();

        // Ready to Fire?
        elapsedTime += delta;
        if (wasHit || isDestroyed) explosionTimer += delta;

        if (!isDestroyed && !isReadyToFire &&
                !(Constants.helicopter.mode == Helicopter.FlyingMode.CRASHED) &&
                (elapsedTime >= refireInterval)) {
            elapsedTime = 0;
            isReadyToFire = true;
        }

        // Rotation
        if ((getVector1().x <= Constants.WINDOW_WIDTH && getVector1().x >= 0) &&
                (getVector1().y <= Constants.WINDOW_HEIGHT && getVector1().y >= 0) ||
                (getVector2().x <= Constants.WINDOW_WIDTH && getVector2().x >= 0) &&
                        (getVector2().y <= Constants.WINDOW_HEIGHT && getVector2().y >= 0) ||
                (getVector3().x <= Constants.WINDOW_WIDTH && getVector3().x >= 0) &&
                        (getVector3().y <= Constants.WINDOW_HEIGHT && getVector3().y >= 0) ||
                (getVector4().x <= Constants.WINDOW_WIDTH && getVector4().x >= 0) &&
                        (getVector4().y <= Constants.WINDOW_HEIGHT && getVector4().y >= 0)) {
            rotation =((float)Constants.getSignedDegreesToHelicopter(new Vector2(position.x + image.getRegionWidth()*scale/2f, position.y + image.getRegionHeight()*scale/2f)));

            direction =  getRotation() - 180;
            // Refire
            if (isReadyToFire()) {
                fireAAGun();
            }
        } else {
            setReadyToFire(false);
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
            batch.draw(image, getVector1().x, getVector1().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector2().x, getVector2().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector3().x, getVector3().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector4().x, getVector4().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

            if (wasHit){
//                if (getWasHitByCannonAnimation().getFrame() < 15){
//                    getWasHitByCannonAnimation().draw(getVector1().getX()-5, getVector1().getY()-5);
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
