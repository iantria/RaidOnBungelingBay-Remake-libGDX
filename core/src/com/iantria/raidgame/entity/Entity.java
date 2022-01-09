package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Entity  {

    public String id;
    public boolean isMovingObject;

    public enum EntityType {
        FACTORY, AA_GUN, ENEMY_BOMBER, ENEMY_FIGHTER, HELICOPTER, CARRIER, CRUISE_MISSILE, FIGHTER_BULLET, MY_BULLET, MY_BOMB
    }
    public EntityType type;

    // Positions on the maps
    public Vector2 vector1;
    public Vector2 vector2;
    public Vector2 vector3;
    public Vector2 vector4;
    public float temp2_x, temp2_y, temp3_x,temp3_y;
    public float random;

    public float elapsedTime;
    public float refireInterval;
    public float refireElapsedTime;
    public float respawnElapsedTime;
    public float respawnTime;
    public boolean wasHitByBomb;
    public boolean wasHitByCannon;
    public Animation<TextureRegion> wasHitByCannonAnimation;
    public Animation<TextureRegion> wasHitByBombAnimation;
    public ShapeDrawer healthRenderer;

    public Rectangle boundingBox;
    public TextureRegion image;
    public Vector2 position;
    public Vector2 startingPosition;
    public Vector2 relativePositionToMap;

    // Player and Enemy traits
    public boolean isPlayer;
    public boolean isDestroyed;
    public boolean isReadyToFire;
    public float scale;
    public float rotation;
    public float speed;
    public int health;
    public boolean wasHit;
    public float direction;
    public float range;
    Entity primaryTarget;


    public Entity(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        this.id = id;
        this.image = image;
        this.isMovingObject = isMovingObj;
        this.scale = scale;
        this.position = position;
        this.relativePositionToMap = new Vector2(position);
        this.vector1 = new Vector2(position);
        this.vector2 = new Vector2(position);
        this.vector3 = new Vector2(position);
        this.vector4 = new Vector2(position);
        this.rotation = rotation;
        this.startingPosition = new Vector2(position);
        this.boundingBox = new Rectangle(position.x, position.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        this.wasHitByCannonAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[3].getFrameDuration(),Constants.explosionAnimations[3].getKeyFrames());
        this.wasHitByBombAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[1].getFrameDuration(),Constants.explosionAnimations[3].getKeyFrames());

    }

    public boolean intersects(Entity e) {
        this.boundingBox = new Rectangle(position.x, position.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        e.boundingBox = new Rectangle(e.position.x, e.position.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        if (e.boundingBox.overlaps(boundingBox)) return true;

        this.boundingBox = new Rectangle(vector1.x, vector1.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        e.boundingBox = new Rectangle(e.vector1.x, e.vector1.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        if (e.boundingBox.overlaps(boundingBox)) return true;

        this.boundingBox = new Rectangle(vector2.x, vector2.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        e.boundingBox = new Rectangle(e.vector2.x, e.vector2.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        if (e.boundingBox.overlaps(boundingBox)) return true;

        this.boundingBox = new Rectangle(vector3.x, vector3.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        e.boundingBox = new Rectangle(e.vector3.x, e.vector3.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        if (e.boundingBox.overlaps(boundingBox)) return true;

        this.boundingBox = new Rectangle(vector4.x, vector4.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        e.boundingBox = new Rectangle(e.vector4.x, e.vector4.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        return e.boundingBox.overlaps(boundingBox);
    }

    public boolean carrierLandingIntersect() {
        Rectangle carrier = new Rectangle(Constants.carrier.position.x + 15, Constants.carrier.position.y + 15,
                Constants.carrier.image.getRegionWidth()*Constants.carrier.scale - 30,
                Constants.carrier.image.getRegionHeight()*Constants.carrier.scale - 30);
        this.boundingBox = new Rectangle(position.x, position.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        if (carrier.overlaps(boundingBox)) return true;

        this.boundingBox = new Rectangle(vector1.x, vector1.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        carrier = new Rectangle(Constants.carrier.vector1.x + 15, Constants.carrier.vector1.y + 15,
                Constants.carrier.image.getRegionWidth()*Constants.carrier.scale - 30,
                Constants.carrier.image.getRegionHeight()*Constants.carrier.scale - 30);
        if (carrier.overlaps(boundingBox)) return true;

        this.boundingBox = new Rectangle(vector2.x, vector2.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        carrier = new Rectangle(Constants.carrier.vector2.x + 15, Constants.carrier.vector2.y + 15,
                Constants.carrier.image.getRegionWidth()*Constants.carrier.scale - 30,
                Constants.carrier.image.getRegionHeight()*Constants.carrier.scale - 30);
        if (carrier.overlaps(boundingBox)) return true;

        this.boundingBox = new Rectangle(vector3.x, vector3.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        carrier = new Rectangle(Constants.carrier.vector3.x + 15, Constants.carrier.vector3.y + 15,
                Constants.carrier.image.getRegionWidth()*Constants.carrier.scale - 30,
                Constants.carrier.image.getRegionHeight()*Constants.carrier.scale - 30);

        if (carrier.overlaps(boundingBox)) return true;
        carrier = new Rectangle(Constants.carrier.vector4.x + 15, Constants.carrier.vector4.y + 15,
                Constants.carrier.image.getRegionWidth()*Constants.carrier.scale - 30,
                Constants.carrier.image.getRegionHeight()*Constants.carrier.scale - 30);
        this.boundingBox = new Rectangle(vector4.x, vector4.y, image.getRegionWidth()*scale, image.getRegionHeight()*scale);
        return carrier.overlaps(boundingBox);
    }



    public void updateVectorsForMovingObjects() {

        if (position.y < -(Constants.MAP_HEIGHT/2)) position.y = (Constants.MAP_HEIGHT/2) + (position.y + Constants.MAP_HEIGHT/2);
        if (position.y > (Constants.MAP_HEIGHT/2)) position.y = -(Constants.MAP_HEIGHT/2) + (position.y - Constants.MAP_HEIGHT/2);

        if (position.x < -(Constants.MAP_WIDTH/2)) position.x = (Constants.MAP_WIDTH/2) + (position.x + Constants.MAP_WIDTH/2);
        if (position.x > (Constants.MAP_WIDTH/2)) position.x = -(Constants.MAP_WIDTH/2) + (position.x - Constants.MAP_WIDTH/2);

        if (position.x < (Constants.WINDOW_WIDTH/2) && position.x > 0) {
            temp2_x = -(Constants.MAP_WIDTH - position.x);
        } else if (position.x < -(Constants.MAP_WIDTH - Constants.WINDOW_WIDTH) && position.x > - Constants.MAP_WIDTH){
            temp2_x = Constants.MAP_WIDTH + position.x;
        } else {
            temp2_x = Constants.MAP_WIDTH + position.x;
        }
        temp2_y = position.y;

        if (position.y < (Constants.WINDOW_HEIGHT/2) && position.y > 0) {
            temp3_y = -(Constants.MAP_HEIGHT - position.y);
        } else if (position.y < -(Constants.MAP_HEIGHT - Constants.WINDOW_HEIGHT) && position.y > -Constants.MAP_HEIGHT){
            temp3_y = Constants.MAP_HEIGHT + position.y;
        } else {
            temp3_y = Constants.MAP_HEIGHT + position.y;
        }
        temp3_x = position.x;

        vector1.x = position.x;
        vector1.y = position.y;
        vector2.x = temp2_x;
        vector2.y = temp2_y;
        vector3.x = temp3_x;
        vector3.y = temp3_y;
        vector4.x = temp2_x;
        vector4.y = temp3_y;
    }

    public void updateVectorsForStationaryObjects(){

        position.x = Constants.gameMap.vector1.x + relativePositionToMap.x;
        position.y = Constants.gameMap.vector1.y + relativePositionToMap.y;

        vector1.x = Constants.gameMap.vector1.x + relativePositionToMap.x;
        vector1.y = Constants.gameMap.vector1.y + relativePositionToMap.y;
        vector2.x = Constants.gameMap.vector2.x + relativePositionToMap.x;
        vector2.y = Constants.gameMap.vector2.y + relativePositionToMap.y;
        vector3.x = Constants.gameMap.vector3.x + relativePositionToMap.x;
        vector3.y = Constants.gameMap.vector3.y + relativePositionToMap.y;
        vector4.x = Constants.gameMap.vector4.x + relativePositionToMap.x;
        vector4.y = Constants.gameMap.vector4.y + relativePositionToMap.y;


        // Added Jan2nd
        if (position.y < -(Constants.MAP_HEIGHT/2)) position.y = (Constants.MAP_HEIGHT/2) + (position.y + Constants.MAP_HEIGHT/2);
        if (position.y > (Constants.MAP_HEIGHT/2)) position.y = -(Constants.MAP_HEIGHT/2) + (position.y - Constants.MAP_HEIGHT/2);

        if (position.x < -(Constants.MAP_WIDTH/2)) position.x = (Constants.MAP_WIDTH/2) + (position.x + Constants.MAP_WIDTH/2);
        if (position.x > (Constants.MAP_WIDTH/2)) position.x = -(Constants.MAP_WIDTH/2) + (position.x - Constants.MAP_WIDTH/2);

    }


    public boolean isTargetVisibleOnScreen(Entity e) {
        this.boundingBox = new Rectangle(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        e.boundingBox = new Rectangle(e.position.x, e.position.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        if (e.boundingBox.overlaps(boundingBox)) return true;

        e.boundingBox = new Rectangle(e.vector1.x, e.vector1.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        if (e.boundingBox.overlaps(boundingBox)) return true;

        e.boundingBox = new Rectangle(e.vector2.x, e.vector2.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        if (e.boundingBox.overlaps(boundingBox)) return true;

        e.boundingBox = new Rectangle(e.vector3.x, e.vector3.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        if (e.boundingBox.overlaps(boundingBox)) return true;

        e.boundingBox = new Rectangle(e.vector4.x, e.vector4.y, e.image.getRegionWidth()*e.scale, e.image.getRegionHeight()*e.scale);
        return e.boundingBox.overlaps(boundingBox);

//
//        return ((e.position.x - e.image.getRegionWidth()*e.scale/2f <= Constants.WINDOW_WIDTH && e.position.x - e.image.getRegionWidth()*e.scale/2f >= 0) &&
//                (e.position.y + e.image.getRegionHeight()*e.scale/2f <= Constants.WINDOW_HEIGHT && e.position.y + e.image.getRegionHeight()*e.scale/2f >= 0));
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    public boolean isWasHitByBomb() {
        return wasHitByBomb;
    }

    public void setWasHitByBomb(boolean wasHitByBomb) {
        this.wasHitByBomb = wasHitByBomb;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public void setPlayer(boolean player) {
        isPlayer = player;
    }



    public TextureRegion getImage() {
        return image;
    }

    public void setImage(TextureRegion image) {
        this.image = image;
    }


    public boolean isMovingObject() {
        return isMovingObject;
    }

    public void setMovingObject(boolean movingObject) {
        isMovingObject = movingObject;
    }

    public Vector2 getVector1() {
        return vector1;
    }

    public void setVector1(Vector2 vector1) {
        this.vector1 = vector1;
    }

    public Vector2 getVector2() {
        return vector2;
    }

    public void setVector2(Vector2 vector2) {
        this.vector2 = vector2;
    }

    public Vector2 getVector3() {
        return vector3;
    }

    public void setVector3(Vector2 vector3) {
        this.vector3 = vector3;
    }

    public Vector2 getVector4() {
        return vector4;
    }

    public void setVector4(Vector2 vector4) {
        this.vector4 = vector4;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(Vector2 startingPosition) {
        this.startingPosition = startingPosition;
    }

    public Vector2 getRelativePositionToMap() {
        return relativePositionToMap;
    }

    public void setRelativePositionToMap(Vector2 relativePositionToMap) {
        this.relativePositionToMap = relativePositionToMap;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isWasHit() {
        return wasHit;
    }

    public void setWasHit(boolean wasHit) {
        this.wasHit = wasHit;
    }

    public boolean isWasHitByCannon() {
        return wasHitByCannon;
    }

    public void setWasHitByCannon(boolean wasHitByCannon) {
        this.wasHitByCannon = wasHitByCannon;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public Entity getPrimaryTarget() {
        return primaryTarget;
    }

    public void setPrimaryTarget(Entity primaryTarget) {
        this.primaryTarget = primaryTarget;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public float getRefireInterval() {
        return refireInterval;
    }

    public void setRefireInterval(float refireInterval) {
        this.refireInterval = refireInterval;
    }

    public float getRefireElapsedTime() {
        return refireElapsedTime;
    }

    public void setRefireElapsedTime(float refireElapsedTime) {
        this.refireElapsedTime = refireElapsedTime;
    }

    public float getRespawnElapsedTime() {
        return respawnElapsedTime;
    }

    public void setRespawnElapsedTime(float respawnElapsedTime) {
        this.respawnElapsedTime = respawnElapsedTime;
    }

    public float getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    public boolean isAttacking;

    public boolean isReadyToFire() {
        return isReadyToFire;
    }

    public void setReadyToFire(boolean readyToFire) {
        isReadyToFire = readyToFire;
    }


}
