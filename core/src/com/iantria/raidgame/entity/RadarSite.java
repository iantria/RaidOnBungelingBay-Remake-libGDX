package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class RadarSite extends Entity {

    public Animation<TextureRegion> explodeAnimation;
    public Animation<TextureRegion> radarSiteAnimation;
    public float explosionTimer;
    public boolean isLockedOn;
    public static int numberOfLockedOnRadars;

    public RadarSite(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        super(id, scale, isMovingObj, position, rotation, image);
        startingPosition = new Vector2(position);
        this.scale = scale;
        init();
    }


    public void init() {
        health = Constants.ENEMY_RADAR_HEALTH;
        isDestroyed = false;
        isLockedOn = false;
        vector1 = new Vector2(position);
        vector2 = new Vector2(position);
        vector3 = new Vector2(position);
        vector4 = new Vector2(position);
        updateVectorsForStationaryObjects();
        explosionTimer = 0;
        elapsedTime = 0;
        respawnTime = 0;
        isReadyToFire = false;
        type = EntityType.RADAR_SITE;
        this.radarSiteAnimation = new Animation<TextureRegion>(Constants.radarAnimation.getFrameDuration(), Constants.radarAnimation.getKeyFrames());
        radarSiteAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        this.explodeAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[7].getFrameDuration(),Constants.explosionAnimations[7].getKeyFrames());
    }

    public void reset() {
        this.health = Constants.ENEMY_RADAR_HEALTH;
        this.isDestroyed = false;
        this.isLockedOn = false;
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

        elapsedTime += delta;
        if (wasHit || isDestroyed) explosionTimer += delta;

        //Respawn RADAR
        if (isDestroyed) {
            respawnTime += delta;
            if (respawnTime >= (9 + 3 * Constants.getRemainingFactories())) {
                respawnTime = 0;
                reset();
            }
        } else if ((vector1.x <= Constants.WINDOW_WIDTH && vector1.x >= 0) &&   // Lock on Helicopter if in range
                    (vector1.y <= Constants.WINDOW_HEIGHT && vector1.y >= 0) ||
                    (vector2.x <= Constants.WINDOW_WIDTH && vector2.x >= 0) &&
                            (vector2.y <= Constants.WINDOW_HEIGHT && vector2.y >= 0) ||
                    (vector3.x <= Constants.WINDOW_WIDTH && vector3.x >= 0) &&
                            (vector3.y <= Constants.WINDOW_HEIGHT && vector3.y >= 0) ||
                    (vector4.x <= Constants.WINDOW_WIDTH && vector4.x >= 0) &&
                            (vector4.y <= Constants.WINDOW_HEIGHT && vector4.y >= 0)) {
                // lock on
                if (!isReadyToFire) {
                    isReadyToFire = true;

                    // increment number of radars locked on if not already locked on
                    if (!isLockedOn) {
                        isLockedOn = true;
                        Constants.combatTextList.add(new ScrollingCombatText("RadarLock_" + id, 1f, new Vector2(Constants.helicopter.position), ("RADAR SITE LOCKED ON!"), Color.YELLOW, Constants.scrollingCombatFont, true));
                        RadarSite.numberOfLockedOnRadars++;
                        Constants.radarBeep.play(Constants.volume);
                    }
                }
            }
    }

    public void draw(Batch batch) {
        if (isDestroyed) {
            batch.draw(explodeAnimation.getKeyFrame(explosionTimer),
                    position.x + image.getRegionWidth()/2*scale  - explodeAnimation.getKeyFrame(explosionTimer).getRegionWidth()/2,
                    position.y + image.getRegionHeight()/2*scale - explodeAnimation.getKeyFrame(explosionTimer).getRegionHeight()/2);
        } else {
            batch.draw(radarSiteAnimation.getKeyFrame(elapsedTime,true), vector1.x, vector1.y, radarSiteAnimation.getKeyFrame(elapsedTime).getRegionWidth()*scale/2, radarSiteAnimation.getKeyFrame(elapsedTime).getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(radarSiteAnimation.getKeyFrame(elapsedTime,true), vector2.x, vector2.y, radarSiteAnimation.getKeyFrame(elapsedTime).getRegionWidth()*scale/2, radarSiteAnimation.getKeyFrame(elapsedTime).getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(radarSiteAnimation.getKeyFrame(elapsedTime,true), vector3.x, vector3.y, radarSiteAnimation.getKeyFrame(elapsedTime).getRegionWidth()*scale/2, radarSiteAnimation.getKeyFrame(elapsedTime).getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(radarSiteAnimation.getKeyFrame(elapsedTime,true), vector4.x, vector4.y, radarSiteAnimation.getKeyFrame(elapsedTime).getRegionWidth()*scale/2, radarSiteAnimation.getKeyFrame(elapsedTime).getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

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

}
