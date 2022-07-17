package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class EnemyFighter extends Entity {

    public boolean isLanded = true;
    public TextureRegion projectileImage;

    float angleToTarget = 0f;
    float diff = 0;
    public Animation<TextureRegion> planeExploded;
    public float explosionTimer;
    private int numberOfTurns;

    public EnemyFighter(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image, TextureRegion projectile){
        super(id, scale, isMovingObj, position, rotation, image);
        this.image = image;
        this.projectileImage = projectile;
        this.rotation = rotation;
        this.direction = 90f;
        this.scale = scale;
        type = EntityType.ENEMY_FIGHTER;
        init();
    }

    public void init() {
        this.planeExploded = new Animation<TextureRegion>(Constants.explosionAnimations[5].getFrameDuration(),Constants.explosionAnimations[5].getKeyFrames());
        vector1 = new Vector2(position);
        vector2 = new Vector2(position);
        vector3 = new Vector2(position);
        vector4 = new Vector2(position);
        relativePositionToMap = new Vector2(position);
        speed = 0;
        random = Constants.random.nextFloat()*30;
        health = Constants.ENEMY_FIGHTER_HEALTH;
        isDestroyed = false;
        isLanded = true;
        updateVectorsForMovingObjects();
        startingPosition = new Vector2(position);
        explosionTimer = 0;
        soundID = -1;

    }


    public void reset() {
        isLanded = true;
        wasHit = false;
        isDestroyed = false;
        respawnElapsedTime = 0;
        elapsedTime = 0;
        //getPlaneExploded().restart();
        health = Constants.ENEMY_FIGHTER_HEALTH;
        speed = 0;
        rotation = 270f;
        direction = 90f;
        position.x = Constants.gameMap.position.x + startingPosition.x - Constants.WINDOW_WIDTH/2 + 200;
        position.y = Constants.gameMap.position.y + startingPosition.y;
        updateVectorsForMovingObjects();
        explosionTimer = 0;
        soundID = -1;
    }



    public void update(float delta) {
        position.x = position.x - Constants.map_dx;
        position.y = position.y - Constants.map_dy;

        updateVectorsForMovingObjects();

        if (wasHit || isDestroyed) {
            explosionTimer += delta;
        }

        if (isDestroyed) {
            speed = 0.0f;
            respawnElapsedTime += delta;
            if (soundID != -1) {
                Constants.enemyCruise.setLooping(soundID, false);
                Constants.enemyCruise.stop(soundID);
                soundID = -1;
            }
            if (respawnElapsedTime > Constants.ENEMY_FIGHTER_RESPAWN_TIMER){
                reset();
            }
        } else if (!isLanded) {
            if (speed >= Constants.ENEMY_FIGHTER_SPEED + random){
                angleToTarget = Constants.getSignedDegreesToHelicopter(new Vector2(position.x + image.getRegionWidth()*scale/2, position.y + image.getRegionHeight()*scale/2));
                diff = Constants.calculateDifferenceBetweenAngles(angleToTarget, rotation);
                refireElapsedTime += delta;

                if (!isDestroyed && !isReadyToFire && (refireElapsedTime >
                        (Constants.ENEMY_FIGHTER_FIRING_INTERVAL - (RadarSite.numberOfLockedOnRadars*0.25f)))) {
                    isReadyToFire = true;
                    refireElapsedTime = 0;
                }

                if ((position.x <= Constants.WINDOW_WIDTH && position.x >= 0) &&
                        (position.y <= Constants.WINDOW_HEIGHT && position.y >= 0)) {
                     if (soundID == -1) {
                         soundID = Constants.enemyCruise.play(Constants.volume*0.5f);
                         Constants.enemyCruise.setLooping(soundID, true);
                     }

                    //Fire if you have a good angle, and ready to fire
                    if ((diff > -2.5 && diff < 2.5) && isReadyToFire &&
                            (refireElapsedTime > Constants.ENEMY_FIGHTER_FIRING_INTERVAL) ) {
                        if (Constants.helicopter.mode != Helicopter.FlyingMode.CRASHED)
                            fireGun();
                    }
                } else {
                    Constants.enemyCruise.setLooping(soundID, false);
                    Constants.enemyCruise.stop(soundID);
                    soundID = -1;
                }

                if (isReadyToFire) {  // turn towards heli
                    if (this.intersects(Constants.helicopter) && refireElapsedTime >
                            (Constants.ENEMY_FIGHTER_FIRING_INTERVAL - (RadarSite.numberOfLockedOnRadars*0.25f))) numberOfTurns++;
                    if (numberOfTurns > 3) {
                        isReadyToFire = false;
                        refireElapsedTime = 0;
                        numberOfTurns = 0;
                    }

                    if (diff < -2.5) {
                        rotation = rotation + 180f * delta;
                    } else if (diff > 2.5) {
                        rotation = rotation - 180f * delta;
                    }
                } // else cruise
            } else {
                speed = speed + (50f * delta);
                if (speed > Constants.ENEMY_FIGHTER_SPEED + random) speed = Constants.ENEMY_FIGHTER_SPEED + random ;
            }
        } else {
            elapsedTime += delta;
            if (Constants.getRemainingFactories() < 6 && elapsedTime > 3){
                isLanded = false;
                elapsedTime = 0;
            }
        }

        float hip = delta * speed;

        position.x += hip * java.lang.Math.sin(java.lang.Math.toRadians(rotation));
        position.y -= hip *java.lang.Math.cos(java.lang.Math.toRadians(rotation));

        if (rotation >= 360) rotation = rotation - 360;
        if (rotation <= 0) rotation = rotation + 360;

        direction = rotation - 180;


    }

    public void draw(Batch batch) {
        if (isDestroyed){
            batch.draw(planeExploded.getKeyFrame(explosionTimer),
                    position.x + image.getRegionWidth()/2*scale  - planeExploded.getKeyFrame(explosionTimer).getRegionWidth()/2,
                    position.y + image.getRegionHeight()/2*scale - planeExploded.getKeyFrame(explosionTimer).getRegionHeight()/2);

        } else {



            batch.draw(image, vector1.x, vector1.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector2.x, vector2.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector3.x, vector3.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector4.x, vector4.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

            if (wasHit){
//                if (getPlaneWasHitAnimation().getFrame() < 15) {
//                    getPlaneWasHitAnimation().draw(vector1.x+5, vector1.y-13);
//                    getPlaneWasHitAnimation().draw(vector2.x+5, vector2.y-3);
//                    getPlaneWasHitAnimation().draw(vector3.x+5, vector3.y-3);
//                    getPlaneWasHitAnimation().draw(vector4.x+5, vector4.y-3);
                } else {
                    wasHit = false;
//                    getPlaneWasHitAnimation().restart();
                }
            }
        }

    private void fireGun() {
        isReadyToFire = false;
        refireElapsedTime = 0;
        Statistics.numberOfTimesFighterFired++;
        Projectile projectile = new Projectile(id + "_EnemyMissile" + Statistics.numberOfTimesFighterFired, 0.2f, true,
                new Vector2(position.x + +image.getRegionWidth()*scale/2 - projectileImage.getRegionWidth()*0.2f/2 ,
                        position.y + +image.getRegionHeight()*scale/2 - projectileImage.getRegionHeight()*0.2f/2), rotation , Constants.BULLET_SPEED, projectileImage,
                Projectile.Type.ENEMY_FIGHTER_BULLET);
        projectile.speed = Constants.BULLET_SPEED;
        projectile.rotation = rotation;
        projectile.direction = direction + 180;// todo
        Constants.projectileList.add(projectile);
        Constants.fireCannonEffect.play(Constants.volume);
    }

}


