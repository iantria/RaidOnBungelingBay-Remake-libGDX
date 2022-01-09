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
        //setType(EnemyPlane.ENEMY_FIGHTER);
        setVector1(new Vector2(position));
        setVector2(new Vector2(position));
        setVector3(new Vector2(position));
        setVector4(new Vector2(position));
        relativePositionToMap = new Vector2(position);
        speed = 0;
        random = Constants.random.nextFloat()*30;
        refireInterval = Constants.ENEMY_FIGHTER_FIRING_INTERVAL + Constants.random.nextFloat();
        //image.setCenterOfRotation(image.getWidth()/2, image.getHeight()/2);
        health = Constants.ENEMY_FIGHTER_HEALTH;
        isDestroyed = false;
        isLanded = true;
        updateVectorsForMovingObjects();
        setStartingPosition(new Vector2(position));
        explosionTimer = 0;


    }


    public void reset() {
        isLanded = true;
        wasHit = false;
        isDestroyed = false;
        setRespawnElapsedTime(0);
        setElapsedTime(0);
        //getPlaneExploded().restart();
        setHealth(Constants.ENEMY_FIGHTER_HEALTH);
        setSpeed(0);
        rotation = 270f;
        direction = 90f;
        position.x = Constants.gameMap.position.x + getStartingPosition().x - Constants.WINDOW_WIDTH/2 + 200;
        position.y = Constants.gameMap.position.y + getStartingPosition().y;
        updateVectorsForMovingObjects();
        explosionTimer = 0;
    }



    public void update(float delta) {
        position.x = position.x - Constants.map_dx;
        position.y = position.y - Constants.map_dy;

        updateVectorsForMovingObjects();

        if (wasHit || isDestroyed) explosionTimer += delta;

        if (isDestroyed) {
            speed = 0.0f;
            respawnElapsedTime += delta;
            if (respawnElapsedTime > Constants.ENEMY_FIGHTER_RESPAWN_TIMER){
                reset();
            }
        } else if (!isLanded) {
            if (speed >= Constants.ENEMY_FIGHTER_SPEED + random){
                angleToTarget = Constants.getSignedDegreesToHelicopter(new Vector2(position.x + image.getRegionWidth()*scale/2, position.y + image.getRegionHeight()*scale/2));
                diff = Constants.calculateDifferenceBetweenAngles(angleToTarget, rotation);
                refireElapsedTime += delta;

                if (!isDestroyed() && !isReadyToFire() && (refireElapsedTime > Constants.ENEMY_FIGHTER_FIRING_INTERVAL)) {
                    isReadyToFire = true;
                    refireElapsedTime = 0;
                }

                 if ((position.x <= Constants.WINDOW_WIDTH && position.x >= 0) &&
                        (position.y <= Constants.WINDOW_HEIGHT && position.y >= 0)) {
                        if (!Constants.enemyCruise.isPlaying()) {
                            Constants.enemyCruise.play();
                        }

//                    //Fire if you have a good angle, and ready to fire
                    if ((diff > -2.5 && diff < 2.5) && isReadyToFire &&
                            (refireElapsedTime > Constants.ENEMY_FIGHTER_FIRING_INTERVAL) ) {
                        if (Constants.helicopter.mode != Helicopter.FlyingMode.CRASHED) fireGun();
                    }
                } else {
                    //main.enemyCruise.stop();
                }

                if (isReadyToFire) {  // turn towards heli
                    if (this.intersects(Constants.helicopter) && refireElapsedTime > 5) numberOfTurns++;
                    if (numberOfTurns > 3) {
                        isReadyToFire = false;
                        refireElapsedTime = 0;
                        numberOfTurns = 0;
                        //System.out.println("Detected looping...");
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

        direction =  getRotation() - 180;


    }

    public void draw(Batch batch) {
        if (isDestroyed){
            batch.draw(planeExploded.getKeyFrame(explosionTimer),
                    position.x + image.getRegionWidth()/2*scale  - planeExploded.getKeyFrame(explosionTimer).getRegionWidth()/2,
                    position.y + image.getRegionHeight()/2*scale - planeExploded.getKeyFrame(explosionTimer).getRegionHeight()/2);

        } else {



            batch.draw(image, getVector1().x, getVector1().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector2().x, getVector2().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector3().x, getVector3().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector4().x, getVector4().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

            if (wasHit){
//                if (getPlaneWasHitAnimation().getFrame() < 15) {
//                    getPlaneWasHitAnimation().draw(getVector1().x+5, getVector1().y-13);
//                    getPlaneWasHitAnimation().draw(getVector2().x+5, getVector2().y-3);
//                    getPlaneWasHitAnimation().draw(getVector3().x+5, getVector3().y-3);
//                    getPlaneWasHitAnimation().draw(getVector4().x+5, getVector4().y-3);
                } else {
                    setWasHit(false);
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
        projectile.setRotation(getRotation());
        projectile.setDirection(getDirection()+180);// todo
        Constants.projectileList.add(projectile);
        Constants.fireCannonEffect.play();
    }

}


