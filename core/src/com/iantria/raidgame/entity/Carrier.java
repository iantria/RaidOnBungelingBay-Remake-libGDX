package com.iantria.raidgame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class Carrier extends Entity {

    public boolean isDestroyed;
    public boolean isSinking;
    public Animation<TextureRegion> explosion1;
    public Animation<TextureRegion> explosion2;
    public Animation<TextureRegion> explosion3;
    public Animation<TextureRegion> wasHitByMissileAnimation;
    public float explosionElapsedTime;
    public float healthElapsedTime;
    public int explosionIndex;
    public boolean isUnderAttack;

    public Carrier(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        super(id, scale, isMovingObj, position, rotation, image);
        this.health = Constants.MAX_HIT_POINTS_CARRIER;
        this.speed = Constants.CARRIER_SPEED;
        this.isDestroyed = false;
        this.isSinking = false;
        this.image = image;
        this.position = new Vector2(position);
        this.isMovingObject = isMovingObj;
        this.scale = scale;
        this.type = EntityType.CARRIER;
        this.rotation = rotation;

        this.explosion1 = new Animation<TextureRegion>(Constants.explosionAnimations[1].getFrameDuration(),Constants.explosionAnimations[1].getKeyFrames());
        this.explosion2 = new Animation<TextureRegion>(Constants.explosionAnimations[4].getFrameDuration(),Constants.explosionAnimations[4].getKeyFrames());
        //this.explosion3 = new Animation<TextureRegion>(Constants.explosionAnimations[7].getFrameDuration(),Constants.explosionAnimations[7].getKeyFrames());
        this.wasHitByMissileAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[1].getFrameDuration(),Constants.explosionAnimations[1].getKeyFrames());

        setVector1(new Vector2(position));
        setVector2(new Vector2(position));
        setVector3(new Vector2(position));
        setVector4(new Vector2(position));
        updateVectorsForMovingObjects();
        explosionIndex = 4;
        random = Constants.random.nextInt(24) - 12;
        isUnderAttack = false;
        soundID = -1;
    }

    public void update(float delta) {

        if (health < 1 && !isSinking)
            isSinking = true;
        if (isDestroyed && Constants.helicopter.livesCount > 1) {
            Statistics.numberOfLivesLost= Statistics.numberOfLivesLost + (Constants.helicopter.livesCount - 1) ;
            Constants.helicopter.livesCount = 1;
        }

        if (isSinking && Constants.helicopter.mode == Helicopter.FlyingMode.LANDED) {
            Constants.helicopter.mode = Helicopter.FlyingMode.TAKING_OFF;
            Constants.takeOffSound.play();
        }

        float hip = delta * speed;

        position.x = position.x - Constants.map_dx;
        position.y = position.y - Constants.map_dy;

        updateVectorsForMovingObjects();

        position.y += hip;

        if (position.y < -(Constants.MAP_HEIGHT/2f)) position.y = (Constants.MAP_HEIGHT/2f);
        if (position.y > (Constants.MAP_HEIGHT/2f)) position.y = -(Constants.MAP_HEIGHT/2f);

        if (position.x < -(Constants.MAP_WIDTH/2f)) position.x = (Constants.MAP_WIDTH/2f);
        if (position.x > (Constants.MAP_WIDTH/2f)) position.x = -(Constants.MAP_WIDTH/2f);

        if(isSinking){
            explosionElapsedTime += delta;
            if(explosionElapsedTime > 6) {
                explosionElapsedTime = 0;
            }
            if (explosionIndex == 0){
                isDestroyed = true;
                isSinking = false;
            }
        }
        if (wasHit){
            explosionElapsedTime += delta;
        }

        healthElapsedTime += delta;
        if (!isDestroyed && !isSinking && healthElapsedTime > 1) {
            healthElapsedTime = 0;
            health++;
            if (health > Constants.MAX_HIT_POINTS_CARRIER) health = Constants.MAX_HIT_POINTS_CARRIER;
        }


        // Check if being bombed by bombers and play sound, scrolling text
        isUnderAttack = false;
        elapsedTime += delta;
        for (EnemyBomber a: Constants.enemyBombers){
            if (!a.isDestroyed && a.isAttacking && !a.isLanded && !isDestroyed && a.intersects(this)){
                isUnderAttack = true;
            }
        }
        if (soundID == -1 && isUnderAttack) {
            elapsedTime = 0;
            soundID = Constants.carrierAlarm.play(0.5f);
            Constants.combatTextList.add(new ScrollingCombatText("BombersAttacking_" + Constants.carrier.health, 1f, new Vector2(Constants.helicopter.position), ("CARRIER UNDER ATTACK!"), Color.YELLOW, Constants.scrollingCombatFont, true));
        } else {
            if (elapsedTime > 2.5f) {
                soundID = -1;
                elapsedTime = 0;
            }
        }
    }


    public void draw(SpriteBatch batch) {
        if (isDestroyed){
            // dont draw it
        } else if (isSinking) {
            wasHit = false;
            if(explosionIndex == 1)
                batch.setColor(1,1,1, (batch.getColor().a - explosionElapsedTime));

            batch.draw(image, getVector1().x, getVector1().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector2().x, getVector2().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector3().x, getVector3().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector4().x, getVector4().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.setColor(1,1,1,1);

            batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
                    position.x + random + image.getRegionWidth()/2 * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
                    position.y + image.getRegionHeight()*0.2f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
            batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
                    position.x - random + image.getRegionWidth()/2 * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
                    position.y  + image.getRegionHeight()*0.5f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
            batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
                    position.x + random + image.getRegionWidth()/2 * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
                    position.y + image.getRegionHeight()*0.8f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);

//            batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
//                    position.x - random + image.getRegionWidth()/2 * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//                    position.y + image.getRegionHeight()*0.3f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//            batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
//                    position.x + random + image.getRegionWidth()/2 * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//                    position.y  + image.getRegionHeight()*0.6f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//            batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
//                    position.x - random + image.getRegionWidth()/2 * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//                    position.y + image.getRegionHeight()*0.9f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//
//            batch.draw(explosion3.getKeyFrame(explosionElapsedTime),
//                    position.x + image.getRegionWidth()/2  * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//                    position.y + image.getRegionHeight()*0.1f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//            batch.draw(explosion3.getKeyFrame(explosionElapsedTime),
//                    position.x + image.getRegionWidth()/2 * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//                    position.y  + image.getRegionHeight()*0.4f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//            batch.draw(explosion3.getKeyFrame(explosionElapsedTime),
//                    position.x + image.getRegionWidth()/2 * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//                    position.y + image.getRegionHeight()*0.7f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);

            if (explosion1.isAnimationFinished(explosionElapsedTime) && explosionIndex > 0) {
                explosionIndex--;
                random = Constants.random.nextInt(24) - 12;
                explosionElapsedTime = 0;
            }
        } else {
            batch.draw(image, getVector1().x, getVector1().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector2().x, getVector2().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector3().x, getVector3().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, getVector4().x, getVector4().y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

//            healthRenderer = new ShapeDrawer(batch, Constants.singlePixelTextureRegion);
//            healthRenderer.filledRectangle(vector1.x ,
//                vector1.y - 6, (float)health / Constants.MAX_HIT_POINTS_CARRIER * image.getRegionWidth()*scale, 3, Color.GREEN);
//            healthRenderer.rectangle(vector1.x -1,
//                    vector1.y - 7, image.getRegionWidth()*scale + 2, 5, Color.WHITE);
//            healthRenderer.filledRectangle(vector2.x ,
//                    vector2.y - 6, (float)health / Constants.MAX_HIT_POINTS_CARRIER * image.getRegionWidth()*scale, 3, Color.GREEN);
//            healthRenderer.rectangle(vector2.x -1,
//                    vector2.y - 7, image.getRegionWidth()*scale + 2, 5, Color.WHITE);
//
//            healthRenderer.filledRectangle(vector3.x ,
//                    vector3.y - 6, (float)health / Constants.MAX_HIT_POINTS_CARRIER * image.getRegionWidth()*scale, 3, Color.GREEN);
//            healthRenderer.rectangle(vector3.x -1,
//                    vector3.y - 7, image.getRegionWidth()*scale + 2, 5, Color.WHITE);
//
//            healthRenderer.filledRectangle(vector4.x ,
//                    vector4.y - 6, (float)health / Constants.MAX_HIT_POINTS_CARRIER * image.getRegionWidth()*scale, 3, Color.GREEN);
//            healthRenderer.rectangle(vector4.x -1,
//                    vector4.y - 7, image.getRegionWidth()*scale + 2, 5, Color.WHITE);

            if (wasHit){
                batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
                        position.x - random + image.getRegionWidth()/2 * scale - explosion2.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
                        position.y + image.getRegionHeight()*0.25f*scale - explosion2.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
                batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
                        position.x + random + image.getRegionWidth()/2 * scale - explosion2.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
                        position.y  + image.getRegionHeight()*0.5f*scale - explosion2.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
                batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
                        position.x - random + image.getRegionWidth()/2 * scale - explosion2.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
                        position.y + image.getRegionHeight()*0.75f*scale - explosion2.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);

                if (wasHitByMissileAnimation.isAnimationFinished(explosionElapsedTime)){
                    wasHit = false;
                    explosionElapsedTime = 0f;
                }
            }
        }
    }
}