package com.iantria.raidgame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Entity  {

    public String id;

    public enum EntityType {
        FACTORY, AA_GUN, ENEMY_BOMBER, ENEMY_FIGHTER, HELICOPTER, CARRIER, CRUISE_MISSILE, FIGHTER_BULLET, MY_BULLET, MY_BOMB
    }
    public EntityType type;

    // Positions on the maps
    public Vector2 position;
    public Vector2 startingPosition;
    public Vector2 relativePositionToMap;
    public Vector2 vector1;
    public Vector2 vector2;
    public Vector2 vector3;
    public Vector2 vector4;
    public float temp2_x, temp2_y, temp3_x,temp3_y;

    // Timers
    public float elapsedTime;
    public float refireInterval;
    public float refireElapsedTime;
    public float respawnElapsedTime;
    public float respawnTime;

    // Boolean
    public boolean isMovingObject;
    public boolean wasHitByBomb;
    public boolean wasHitByCannon;
    public boolean isAttacking;
    public boolean isPlayer;
    public boolean isDestroyed;
    public boolean isReadyToFire;
    public boolean wasHit;

    // Other
    public float random;
    public long soundID = -1;

    // Animations
    public Animation<TextureRegion> wasHitByCannonAnimation;
    public Animation<TextureRegion> wasHitByBombAnimation;
    public ShapeDrawer healthRenderer;

    // Image characteristics
    public Rectangle boundingBox;
    public TextureRegion image;

    // Player and Enemy traits
    public float scale;
    public float rotation;
    public float speed;
    public int health;
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
        this.soundID = -1;
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

    }

}
