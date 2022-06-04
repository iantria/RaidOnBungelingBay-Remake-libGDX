package com.iantria.raidgame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Factory extends Entity {

    private float factoryHealthElapsedTime;
    private float bombExplosionElapsedTime;
    private float destroyedExplosionElapsedTime;
    private Animation<TextureRegion> explodeAnimation;
    private int explosionIndex;
    private ParticleEffect bombEffect;

    public Factory(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        super(id, scale, isMovingObj, position, rotation, image);
        this.scale = scale;
        type = EntityType.FACTORY;
        this.bombEffect = new ParticleEffect(Constants.bombFlashEffect);
        init();
    }

    public void init() {
        vector1 = new Vector2(position);
        vector2 = new Vector2(position);
        vector3 = new Vector2(position);
        vector4 = new Vector2(position);
        this.explodeAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[7].getFrameDuration(),Constants.explosionAnimations[7].getKeyFrames());
        this.wasHitByBombAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[2].getFrameDuration(),Constants.explosionAnimations[2].getKeyFrames());
        //this.wasHitByCannonAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[3].getFrameDuration(),Constants.explosionAnimations[3].getKeyFrames());
        health = Constants.ENEMY_FACTORY_HEALTH;
        isDestroyed = false;
        explosionIndex = 4;
        updateVectorsForStationaryObjects();
    }

    // Fatcories do nor respawn - can remove
    public void reset() {
        health = Constants.ENEMY_FACTORY_HEALTH;
        isDestroyed = false;
        explosionIndex = 4;
        wasHit = false;
        wasHitByCannon = false;
        wasHitByBomb = false;
        updateVectorsForStationaryObjects();
    }


    public void update(float delta) {
        updateVectorsForStationaryObjects();

        // Update Health
        factoryHealthElapsedTime += delta;

        if (factoryHealthElapsedTime >= 3) {
            factoryHealthElapsedTime = 0;
            if (!isDestroyed && health < Constants.ENEMY_FACTORY_HEALTH) {
                int x = health;
                x = x + 1 + (6 - Constants.getRemainingFactories()); // increase health if less factories remain
                if (x > Constants.ENEMY_FACTORY_HEALTH) x = Constants.ENEMY_FACTORY_HEALTH;
                health = x;
            }
        }

        if (!isDestroyed && (vector1.x <= Constants.WINDOW_WIDTH && vector1.x >= 0) &&
                (vector1.y <= Constants.WINDOW_HEIGHT && vector1.y >= 0)) {
            //main.smokeStack.update(delta);
            //if (wasHitByBomb) Constants.bombEffect.start();
        }

        if (isDestroyed) destroyedExplosionElapsedTime += delta;

        if (isDestroyed && explosionIndex > 0) {
            bombEffect.update(delta);
        }

        if (wasHitByBomb) {
            bombEffect.update(delta);
            bombExplosionElapsedTime += delta;
        }
    }

    public void draw(Batch batch) {
        if (isDestroyed) {
            wasHitByBomb = false;
            batch.draw(explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime),
                    vector1.x + random + image.getRegionWidth() / 2 * scale - explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime).getRegionWidth() / 2,
                    vector1.y + random + image.getRegionHeight() / 2 * scale - explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime).getRegionHeight() / 2);
            batch.draw(explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime),
                    vector2.x + random + image.getRegionWidth() / 2 * scale - explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime).getRegionWidth() / 2,
                    vector2.y + random + image.getRegionHeight() / 2 * scale - explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime).getRegionHeight() / 2);
            batch.draw(explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime),
                    vector3.x + random + image.getRegionWidth() / 2 * scale - explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime).getRegionWidth() / 2,
                    vector3.y + random + image.getRegionHeight() / 2 * scale - explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime).getRegionHeight() / 2);
            batch.draw(explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime),
                    vector4.x + random + image.getRegionWidth() / 2 * scale - explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime).getRegionWidth() / 2,
                    vector4.y + random + image.getRegionHeight() / 2 * scale - explodeAnimation.getKeyFrame(destroyedExplosionElapsedTime).getRegionHeight() / 2);

            if (explosionIndex > 0) {
                bombEffect.setPosition(vector1.x + image.getRegionWidth()/2f * scale, vector1.y + image.getRegionHeight()/2f * scale);
                bombEffect.draw(batch);
            }
            if (explodeAnimation.isAnimationFinished(destroyedExplosionElapsedTime) && explosionIndex > 0) {
                explosionIndex--;
                random = Constants.random.nextInt(12) - 6;
                destroyedExplosionElapsedTime = 0;
            }
        } else {
//            if ((vector1.x <= Constants.WINDOW_WIDTH && vector1.x >= 0) &&
//                    (vector1.y <= Constants.WINDOW_HEIGHT && vector1.y >= 0)) {
//                main.smokeStack.setPosition(vector1.x+18, vector1.y+8);
//                main.smokeStack.render();
//                main.smokeStack.setPosition(vector1.x+30, vector1.y+15);
//                main.smokeStack.render();
//            }
            batch.draw(image, vector1.x, vector1.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector2.x, vector2.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector3.x, vector3.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
            batch.draw(image, vector4.x, vector4.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

            if (wasHitByBomb){
                bombEffect.setPosition(vector1.x + image.getRegionWidth() / 2 * scale, vector1.y + image.getRegionHeight() / 2 * scale);
                bombEffect.draw(batch);
                if (!wasHitByBombAnimation.isAnimationFinished(bombExplosionElapsedTime)){
                    batch.draw(wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime),
                            vector1.x + image.getRegionWidth()/2*scale  - wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime).getRegionWidth()/2,
                            vector1.y + image.getRegionHeight()/2*scale - wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime).getRegionHeight()/2);
                    batch.draw(explodeAnimation.getKeyFrame(bombExplosionElapsedTime),
                            vector2.x + image.getRegionWidth()/2*scale  - wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime).getRegionWidth()/2,
                            vector2.y + image.getRegionHeight()/2*scale - wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime).getRegionHeight()/2);
                    batch.draw(explodeAnimation.getKeyFrame(bombExplosionElapsedTime),
                            vector3.x + image.getRegionWidth()/2*scale  - wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime).getRegionWidth()/2,
                            vector3.y + image.getRegionHeight()/2*scale - wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime).getRegionHeight()/2);
                    batch.draw(explodeAnimation.getKeyFrame(bombExplosionElapsedTime),
                            vector4.x + image.getRegionWidth()/2*scale  - wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime).getRegionWidth()/2,
                            vector4.y + image.getRegionHeight()/2*scale - wasHitByBombAnimation.getKeyFrame(bombExplosionElapsedTime).getRegionHeight()/2);
                } else {
                    wasHitByBomb = false;
                    wasHit = false;
                    bombExplosionElapsedTime = 0f;
                }
            }

            healthRenderer = new ShapeDrawer(batch, Constants.singlePixelTextureRegion);
            healthRenderer.filledRectangle(vector1.x ,
                    vector1.y - 6, (float)health / Constants.ENEMY_FACTORY_HEALTH * image.getRegionWidth()*scale, 3, Color.GREEN);
            healthRenderer.rectangle(vector1.x -1,
                    vector1.y - 7, image.getRegionWidth()*scale + 2, 5, Color.WHITE);

            healthRenderer.filledRectangle(vector2.x ,
                    vector2.y - 6, (float)health / Constants.ENEMY_FACTORY_HEALTH * image.getRegionWidth()*scale, 3, Color.GREEN);
            healthRenderer.rectangle(vector2.x -1,
                    vector2.y - 7, image.getRegionWidth()*scale + 2, 5, Color.WHITE);

            healthRenderer.filledRectangle(vector3.x ,
                    vector3.y - 6, (float)health / Constants.ENEMY_FACTORY_HEALTH * image.getRegionWidth()*scale, 3, Color.GREEN);
            healthRenderer.rectangle(vector3.x -1,
                    vector3.y - 7, image.getRegionWidth()*scale + 2, 5, Color.WHITE);

            healthRenderer.filledRectangle(vector4.x ,
                    vector4.y - 6, (float)health / Constants.ENEMY_FACTORY_HEALTH * image.getRegionWidth()*scale, 3, Color.GREEN);
            healthRenderer.rectangle(vector4.x -1,
                    vector4.y - 7, image.getRegionWidth()*scale + 2, 5, Color.WHITE);

        }
    }
}

