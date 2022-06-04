package com.iantria.raidgame.entity;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.iantria.raidgame.util.Constants;

public class CruiseMissile extends Projectile {

	public Animation<TextureRegion> ranOutOfFuel;
	public float remainingFuel;

	public CruiseMissile(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, float speed, TextureRegion image, Type type, MainTarget target) {
		super(id, scale, isMovingObj, position, rotation, speed, image, type);
		this.mainTarget = target;
		this.speed = speed;
		this.type = type;
		this.rotation = rotation;
		this.scale = scale;
		this.elapsedTime = 0;
		init();

	}


	public void init() {
		remainingFuel = Constants.ENEMY_CRUISE_MISSILE_FUEL;
		ranOutOfFuel = new Animation<TextureRegion>(Constants.explosionAnimations[3].getFrameDuration(),Constants.explosionAnimations[3].getKeyFrames());
		vector1 = new Vector2(position);
		vector2 = new Vector2(position);
		vector3 = new Vector2(position);
		vector4 = new Vector2(position);
    	speed = Constants.MISSILE_SPEED;
    	health = Constants.ENEMY_CRUISE_MISSILE_HEALTH;
		this.elapsedTime = 0;
		updateVectorsForMovingObjects();
		soundID = -1;
	}



	@Override
	public void update(float delta) {
		if (remainingFuel < 1.2 || wasHit) {
			speed = 0;
			elapsedTime += delta;
		}
		
    	position.x = position.x - Constants.map_dx;
    	position.y = position.y - Constants.map_dy;
		
		updateVectorsForMovingObjects();
		
        remainingFuel = remainingFuel - delta;

        if (remainingFuel < 0 || isDestroyed) {
        	Constants.removeProjectileList.add(this);
        	return;
        }
		
		float angleToTarget = 0;
		
		if (mainTarget == MainTarget.PLAYER_IS_TARGET) angleToTarget = (float) Constants.getSignedDegreesToHelicopter(position);
		if (mainTarget == MainTarget.CARRIER_IS_TARGET) angleToTarget = (float) Constants.getSignedDegreesToCarrier(position);
		
		double diff = Constants.calculateDifferenceBetweenAngles(angleToTarget, rotation);
		if (diff < -5) {
			rotation = rotation + 90f * delta;
		} else if (diff > 5){
			rotation = rotation - 90f * delta;
		}
        
	    float hip = delta * speed;

	    position.x += hip * Math.sin(Math.toRadians(rotation));
	    position.y -= hip * Math.cos(Math.toRadians(rotation));

	    if (rotation >= 360) rotation = rotation - 360;
	    if (rotation <= 0) rotation = rotation + 360;

		direction = rotation - 180;

    	checkCollisions(delta);
	}

	@Override
	public void draw(Batch batch) {
        if (remainingFuel > 1.2 && !wasHit) {
			batch.draw(image, vector1.x, vector1.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector2.x, vector2.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector3.x, vector3.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector4.x, vector4.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
        } else {
        	if (soundID == -1)
				soundID = Constants.cruiseOutOfFuel.play(Constants.volume);
			batch.draw(ranOutOfFuel.getKeyFrame(elapsedTime),
					position.x + image.getRegionWidth() / 2 * scale - ranOutOfFuel.getKeyFrame(elapsedTime).getRegionWidth() / 2,
					position.y + image.getRegionHeight() / 2 * scale - ranOutOfFuel.getKeyFrame(elapsedTime).getRegionHeight() / 2);
			if (ranOutOfFuel.isAnimationFinished(elapsedTime)) {
				isDestroyed = true;
			}
		}
	}
}
