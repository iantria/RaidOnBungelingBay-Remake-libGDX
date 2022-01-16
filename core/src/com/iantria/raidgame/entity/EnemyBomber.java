package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;


public class EnemyBomber extends Entity {

    public boolean isLanded = true;
    public Animation<TextureRegion> planeExploded;
    public Animation<TextureRegion> bombsDroppingOnCarrier;
    public float explosionTimer;
    public float bombingExplosionTimer;

    public EnemyBomber(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        super(id, scale, isMovingObj, position, rotation, image);

        setStartingPosition(new Vector2(position));
        setRelativePositionToMap(new Vector2(position));
        this.rotation = rotation;
        this.direction = 90f;
        init();
    }



    public void init() {
        this.planeExploded = new Animation<TextureRegion>(Constants.explosionAnimations[5].getFrameDuration(),Constants.explosionAnimations[5].getKeyFrames());
        this.bombsDroppingOnCarrier = new Animation<TextureRegion>(Constants.explosionAnimations[1].getFrameDuration(),Constants.explosionAnimations[1].getKeyFrames());
        explosionTimer = 0;
        bombingExplosionTimer = 0;
        setVector1(new Vector2(position));
        setVector2(new Vector2(position));
        setVector3(new Vector2(position));
        setVector4(new Vector2(position));
        speed = 0;
        random = Constants.random.nextFloat()*30;
        type = EntityType.ENEMY_BOMBER;
        health = Constants.ENEMY_BOMBER_HEALTH;
        isDestroyed = false;
        isAttacking = false;
        isLanded = true;
        updateVectorsForMovingObjects();
    }


    public void reset() {
        isLanded = true;
        wasHit = false;
        explosionTimer = 0;
        isDestroyed = false;
        isAttacking = false;
        setRespawnElapsedTime(0);
        setElapsedTime(0);
        //getPlaneExploded().restart();
        health = Constants.ENEMY_BOMBER_HEALTH;
        setSpeed(0);
        rotation = 270f;
        this.direction = 90f;
        position.x = Constants.gameMap.position.x + getStartingPosition().x - Constants.WINDOW_WIDTH/2 + 200;
        position.y = Constants.gameMap.position.y + getStartingPosition().y;
        updateVectorsForMovingObjects();
    }



    public void update(float delta) {

        position.x = position.x - Constants.map_dx;
        position.y = position.y - Constants.map_dy;

        if (wasHit || isDestroyed) explosionTimer += delta;
        bombingExplosionTimer += delta;

        updateVectorsForMovingObjects();

        if (intersects(Constants.carrier) && !isDestroyed() && !isLanded && !Constants.carrier.isDestroyed) {
            setAttacking(true);
        }


        if(isAttacking && !Constants.carrierAlarm.isPlaying() && !Constants.carrier.isDestroyed) {
            Constants.combatTextList.add(new ScrollingCombatText("BombersAttacking" + Constants.carrier.health, 1f, new Vector2(Constants.helicopter.position), ("CARRIER UNDER ATTACK!"), Color.YELLOW, Constants.scrollingCombatFont, true));
            Constants.carrierAlarm.play();
        }

        if (isDestroyed) {
            isAttacking = false;
            speed = 0.0f;
            respawnElapsedTime += delta;
            if (respawnElapsedTime > (Constants.ENEMY_BOMBER_RESPAWN_TIMER + (Constants.getRemainingFactories()*10))){
                reset();
            }
        } else if (Constants.carrier.isDestroyed) {

        } else if (!isLanded) {
            if (speed == Constants.ENEMY_BOMBER_SPEED){
                float angleToTarget =  Constants.getSignedDegreesToCarrier(new Vector2(position.x + image.getRegionWidth()/2*scale, position.y + image.getRegionHeight()/2*scale));
                //angleToTarget = Constants.getSignedDegreesToHelicopter(new Vector2(position.x + image.getRegionWidth()*scale/2, position.y + image.getRegionHeight()*scale/2));
                float diff = Constants.calculateDifferenceBetweenAngles(angleToTarget, rotation);
                if (diff < - 5) {
                    rotation = rotation + 90f * delta;
                } else if (diff > 5){
                    rotation = rotation - 90f * delta;
                }
            } else {
                speed += 50f * delta;
                if (speed > Constants.ENEMY_BOMBER_SPEED) speed = Constants.ENEMY_BOMBER_SPEED;
            }
        } else {
            setElapsedTime(getElapsedTime() + delta);
            if (Constants.getRemainingFactories() < 5 && getElapsedTime() > 5){
                isLanded =  false;
                setElapsedTime(0);
            }
        }

        float hip = delta * speed;

        position.x += hip * java.lang.Math.sin(java.lang.Math.toRadians(rotation));
        position.y -= hip *java.lang.Math.cos(java.lang.Math.toRadians(rotation));

        if (rotation >= 360) rotation = rotation - 360;
        if (rotation <= 0) rotation = rotation + 360;

        direction =  getRotation() - 180;

        //image.rotate(getRotation() - image.getRotation());
    }


    public void draw(Batch batch) {

        if (isDestroyed()) {
            batch.draw(planeExploded.getKeyFrame(explosionTimer),
                    position.x + image.getRegionWidth()/2*scale  - planeExploded.getKeyFrame(explosionTimer).getRegionWidth()/2,
                    position.y + image.getRegionHeight()/2*scale - planeExploded.getKeyFrame(explosionTimer).getRegionHeight()/2);

        } else {
            if (intersects(Constants.carrier)) {
                if (!bombsDroppingOnCarrier.isAnimationFinished(bombingExplosionTimer)) {
                    batch.draw(bombsDroppingOnCarrier.getKeyFrame(bombingExplosionTimer),
                            Constants.carrier.position.x + Constants.carrier.image.getRegionWidth()/2*scale  - bombsDroppingOnCarrier.getKeyFrame(explosionTimer).getRegionWidth()/2,
                            Constants.carrier.position.y + random + Constants.carrier.image.getRegionHeight()/2*scale - bombsDroppingOnCarrier.getKeyFrame(explosionTimer).getRegionHeight()/2);
                    if (!Constants.bombsDroppingSound.isPlaying())
                        Constants.bombsDroppingSound.play();
                } else {
                    Constants.carrier.health = Constants.carrier.health - Constants.ENEMY_BOMBER_BOMB_DMG;
                    //Constants.combatText.add(new ScrollingCombatText("BomberDamage" + Constants.carrier.health, 1f, new Vector2(Constants.helicopter.position), ("-" + Constants.ENEMY_BOMBER_BOMB_DMG + " Carrier Health"), Color.YELLOW, Constants.scrollingCombatFont, true));
                    Statistics.amountOfCarrierDamageTaken += Constants.ENEMY_BOMBER_BOMB_DMG;
                    bombingExplosionTimer = 0;
                    random = Constants.random.nextInt(30) - 15;;
                }
            }

            batch.draw(image, getVector1().x, getVector1().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector2().x, getVector2().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector3().x, getVector3().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector4().x, getVector4().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);


            if (wasHit) {
//                if (getPlaneWasHitAnimation().getFrame() < 15) {
//                    getPlaneWasHitAnimation().draw(getVector1().x + 33,
//                            getVector1().y + 5);
//                    getPlaneWasHitAnimation().draw(getVector2().x + 33,
//                            getVector2().y + 5);
//                    getPlaneWasHitAnimation().draw(getVector3().x + 33,
//                            getVector3().y + 5);
//                    getPlaneWasHitAnimation().draw(getVector4().x + 33,
//                            getVector4().y + 5);

                } else {
                    setWasHit(false);
//                    getPlaneWasHitAnimation().restart();
                }
            }
        }
    }
