package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class EnemyShip extends Entity {

	public boolean isDestroyed;
	public boolean isSinking;
	public Animation<TextureRegion> explosion1;
	public Animation<TextureRegion> explosion2;
	public Animation<TextureRegion> explosion3;
	public float explosionElapsedTime;
	public float healthElapsedTime;
	private int firedCount = 0;
	private boolean isCannonReadyToFire;
	private float cannonElapsedTime;
	public int explosionIndex;
	public float soundId = -1;

	public EnemyShip(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
		super(id, scale, isMovingObj, position, rotation, image);
		//this.direction=270f;
		init();
	}


	public void init() {
		this.explosion1 = new Animation<TextureRegion>(Constants.explosionAnimations[1].getFrameDuration(),Constants.explosionAnimations[1].getKeyFrames());
//		this.explosion2 = new Animation<TextureRegion>(Constants.explosionAnimations[4].getFrameDuration(),Constants.explosionAnimations[4].getKeyFrames());
//		this.explosion3 = new Animation<TextureRegion>(Constants.explosionAnimations[7].getFrameDuration(),Constants.explosionAnimations[7].getKeyFrames());
		this.health = 1;
		this.isDestroyed = false;
		this.isSinking = false;
		this.isAttacking = false;
		this.speed = 0;
		this.type = EntityType.ENEMY_SHIP;
		vector1 = new Vector2(position);
		vector2 = new Vector2(position);
		vector3 = new Vector2(position);
		vector4 = new Vector2(position);
		explosionIndex = 4;
    	//refireInterval = Constants.ENEMY_CRUISE_MISSILE_FIRING_INTERVAL;
    	updateVectorsForMovingObjects();
    	soundId = -1;
	}

	public void reset() {
		this.isDestroyed = false;
		this.isSinking = false;
		this.isAttacking = false;
		this.wasHit = false;
		this.health = 1;
		this.speed = 0;
		this.position.x = Constants.gameMap.position.x + startingPosition.x - Constants.WINDOW_WIDTH/2 + 200;
		this.position.y = Constants.gameMap.position.y + startingPosition.y;
		updateVectorsForMovingObjects();
		soundId = -1;
	}


	public void update(float delta) {

		if (speed != 0) Constants.enemyShipWakeEffect.update(delta);

    	position.x = position.x - Constants.map_dx;
    	position.y = position.y - Constants.map_dy;
		
		updateVectorsForMovingObjects();
		
        healthElapsedTime += delta;
        if (!isAttacking && !isDestroyed && !isSinking && healthElapsedTime > 3) {
        	healthElapsedTime = 0;
        	health += 1 + (6 - Constants.getRemainingFactories());
        	if (health >= Constants.ENEMY_SHIP_HEALTH) {
        		health = Constants.ENEMY_SHIP_HEALTH;
        		speed = Constants.ENEMY_SHIP_SPEED;
        		Statistics.enemyShipWasCompleted = true;
        		isAttacking = true;
        		Constants.combatTextList.add(new ScrollingCombatText("EnemyShipCompleted", 1f, new Vector2(Constants.helicopter.position), ("ENEMY SHIP COMPLETED!"), Color.YELLOW, Constants.scrollingCombatFont, true));
        	}
        }

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

		if(wasHitByBomb){
			explosionElapsedTime += delta;
		}

    	elapsedTime += delta;
        if (isAttacking && !isSinking && !isDestroyed && !isReadyToFire
        		&& !(Constants.helicopter.mode == Helicopter.FlyingMode.CRASHED) && (elapsedTime >= Constants.ENEMY_SHIP_CRUISE_MISSILE_FIRING_INTERVAL)) {
        	elapsedTime = 0;
        	isReadyToFire = true;  // Cruise Missile
        }

        //if (!isDestroyed && wasHitByMissile) main.missleImpact.update(delta);
        
//        if (isDestroyed && elapsedTime > Constants.ENEMY_SHIP_RESPAWN_TIMER){
//        	elapsedTime = 0;
//        	reset();
//        }
        
    	cannonElapsedTime = cannonElapsedTime + delta; 
        if (isAttacking && !isSinking && !isDestroyed && !isReadyToFire
        		&& !(Constants.helicopter.mode == Helicopter.FlyingMode.CRASHED) && cannonElapsedTime >= Constants.ENEMY_SHIP_FIRING_INTERVAL) {
        	cannonElapsedTime = 0;
        	isCannonReadyToFire = true;
        }

		if ((vector1.x <= Constants.WINDOW_WIDTH && vector1.x >= 0) &&
				(vector1.y <= Constants.WINDOW_HEIGHT && vector1.y >= 0) ||
				(vector2.x <= Constants.WINDOW_WIDTH && vector2.x >= 0) &&
						(vector2.y <= Constants.WINDOW_HEIGHT && vector2.y >= 0) ||
				(vector3.x <= Constants.WINDOW_WIDTH && vector3.x >= 0) &&
						(vector3.y <= Constants.WINDOW_HEIGHT && vector3.y >= 0) ||
				(vector4.x <= Constants.WINDOW_WIDTH && vector4.x >= 0) &&
						(vector4.y <= Constants.WINDOW_HEIGHT && vector4.y >= 0) && isAttacking) {
			rotation =((float)Constants.getSignedDegreesToHelicopter(new Vector2(position.x + image.getRegionWidth()*scale/2, position.y + image.getRegionHeight()*scale/2)));

			// Refire
			if (isReadyToFire) fireCruiseMissile(CruiseMissile.MainTarget.PLAYER_IS_TARGET);
	        if (isCannonReadyToFire) fireCannon();
	    } else if (isAttacking && speed == 0 && !Constants.carrier.isSinking && !Constants.carrier.isDestroyed) {
	    	if (isReadyToFire) {
				rotation =((float)Constants.getSignedDegreesToCarrier(new Vector2(position.x + image.getRegionWidth()*scale/2, position.y + image.getRegionHeight()*scale/2)));
	    		fireCruiseMissile(CruiseMissile.MainTarget.CARRIER_IS_TARGET);
	    		Constants.combatTextList.add(new ScrollingCombatText("DestroyerAttacking", 1f, new Vector2( Constants.helicopter.position), ("SHIP ATTACKING CARRIER!"), Color.YELLOW, Constants.scrollingCombatFont, true));
	    	}
	    }
        
		if (Math.abs((vector1.x - 140) - Constants.carrier.position.x) < 30 ){
			speed = 0;
		}
		
	    float hip = delta * speed;

		position.x -= hip;

//	    position.x += hip * Math.sin(Math.toRadians(rotation+270));
//	    position.y -= hip * Math.cos(Math.toRadians(rotation+270));

//	    if (rotation >= 360) rotation = rotation - 360;
//	    if (rotation <= 0) rotation = rotation + 360;


	    //image.rotate(getRotation() - image.getRotation());
        
	}


	public void draw(Batch batch) {
        if (isDestroyed){
        	// dont draw it
        } else if (isSinking) {
        	wasHitByBomb = false;
			if(explosionIndex == 1) {
				//System.out.println("Alpha: " + (batch.getColor().a - explosionElapsedTime )  + "     time:" + explosionElapsedTime);
				batch.setColor(1, 1, 1, (batch.getColor().a - explosionElapsedTime ));
			}
			batch.draw(image, vector1.x, vector1.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector2.x, vector2.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector3.x, vector3.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector4.x, vector4.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.setColor(1,1,1,1);


			batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
					position.x + image.getRegionWidth()*(0.2f)*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
					position.y + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
			batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
					position.x + image.getRegionWidth()*0.5f * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
					position.y  + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
			batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
					position.x + image.getRegionWidth()*0.8f * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
					position.y + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);

//			batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
//					position.x - random + image.getRegionWidth()*0.2f * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//					position.y + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//			batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
//					position.x  + image.getRegionWidth()*(0.5f) * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//					position.y  + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//			batch.draw(explosion2.getKeyFrame(explosionElapsedTime),
//					position.x - random + image.getRegionWidth()*0.8f * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//					position.y + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//
//			batch.draw(explosion3.getKeyFrame(explosionElapsedTime),
//					position.x + image.getRegionWidth()*0.3f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//					position.y + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//			batch.draw(explosion3.getKeyFrame(explosionElapsedTime),
//					position.x + image.getRegionWidth()*0.6f * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//					position.y  + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//			batch.draw(explosion3.getKeyFrame(explosionElapsedTime),
//					position.x + image.getRegionWidth()*(0.9f) * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//					position.y + image.getRegionHeight()/2*scale* - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);

			if (explosion1.isAnimationFinished(explosionElapsedTime) && explosionIndex > 0) {
				//System.out.println("exp index: " +explosionIndex);
				explosionIndex--;
				random = Constants.random.nextInt(24) - 12;
				explosionElapsedTime = 0;
			}
       } else {

        	if (speed != 0 ) {
				Constants.enemyShipWakeEffect.setPosition(vector1.x + image.getRegionWidth() * scale, vector1.y + image.getRegionHeight() * scale / 2);
				Constants.enemyShipWakeEffect.draw(batch);
			}

			batch.draw(image, vector1.x, vector1.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector2.x, vector2.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector3.x, vector3.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
			batch.draw(image, vector4.x, vector4.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

			healthRenderer = new ShapeDrawer(batch, Constants.singlePixelTextureRegion);
			healthRenderer.filledRectangle(vector1.x + image.getRegionWidth()*scale/2-25,
					vector1.y - 6, (float)health / Constants.ENEMY_SHIP_HEALTH * 50, 3, Color.GREEN);
			healthRenderer.rectangle(vector1.x + image.getRegionWidth()*scale/2-26,
					vector1.y - 7, 52, 5, Color.WHITE);

			healthRenderer.filledRectangle(vector2.x + image.getRegionWidth()*scale/2-25,
					vector2.y - 6, (float)health / Constants.ENEMY_SHIP_HEALTH * 50, 3, Color.GREEN);
			healthRenderer.rectangle(vector2.x + image.getRegionWidth()*scale/2-26,
					vector2.y - 7, 52, 5, Color.WHITE);

			healthRenderer.filledRectangle(vector3.x + image.getRegionWidth()*scale/2-25,
					vector3.y - 6, (float)health / Constants.ENEMY_SHIP_HEALTH * 50, 3, Color.GREEN);
			healthRenderer.rectangle(vector3.x + image.getRegionWidth()*scale/2-26,
					vector3.y - 7, 52, 5, Color.WHITE);

			healthRenderer.filledRectangle(vector4.x + image.getRegionWidth()*scale/2-25,
					vector4.y - 6, (float)health / Constants.ENEMY_SHIP_HEALTH * 50, 3, Color.GREEN);
			healthRenderer.rectangle(vector4.x + image.getRegionWidth()*scale/2-26,
					vector4.y - 7, 52, 5, Color.WHITE);


			if (wasHitByBomb){
				//renderMissleImpact();
				if (!wasHitByBombAnimation.isAnimationFinished(explosionElapsedTime)){
//					batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
//						position.x + image.getRegionWidth()*0.8f*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//						position.y + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
					batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
						position.x + image.getRegionWidth()*0.55f * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
						position.y + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
//					batch.draw(explosion1.getKeyFrame(explosionElapsedTime),
//						position.x + image.getRegionWidth()*0.3f * scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionWidth()/2,
//						position.y + image.getRegionHeight()/2*scale - explosion1.getKeyFrame(explosionElapsedTime).getRegionHeight()/2);
				} else {
					wasHitByBomb = false;
					wasHit = false;
					explosionElapsedTime = 0f;
				}
			}
        }
        

 	}
	
    private void fireCruiseMissile(Projectile.MainTarget target) {
    	if (target == CruiseMissile.MainTarget.CARRIER_IS_TARGET){
			soundId = Constants.carrierAlarm.play(Constants.volume);
    	}
		Statistics.numberOfTimesCruiseMissileFired++;
		CruiseMissile projectile = new CruiseMissile(id + "_CruiseMissile" + firedCount, 0.05f, true,
				new Vector2(position.x + image.getRegionWidth()*scale/2f - Constants.cruiseMissileTexture.getRegionWidth()*0.05f/2f,
						position.y + image.getRegionHeight()*scale/2f - Constants.cruiseMissileTexture.getRegionHeight()*0.05f/2f),
				rotation, Constants.MISSILE_SPEED, Constants.cruiseMissileTexture ,
				Projectile.Type.ENEMY_CRUISE_MISSILE,  target);
		Constants.fireMissileEffect.play(Constants.volume);
		Constants.projectileList.add(projectile);
		isReadyToFire = false;
		elapsedTime = 0;
    }
    
    private void fireCannon() {
		Projectile p= new Projectile(id + "_Bullet" + firedCount, 0.25f, true,
				new Vector2(position.x + image.getRegionWidth()*scale/2f - Constants.enemyBulletTextureRegion.getRegionWidth()*0.25f/2f ,
						position.y + image.getRegionHeight()*scale/2f - Constants.enemyBulletTextureRegion.getRegionHeight()*0.25f/2f),
				rotation, Constants.BULLET_SPEED, Constants.enemyBulletTextureRegion, Projectile.Type.AA_GUN_BULLET);
		Constants.AAGunFireSound.play(Constants.volume);
		Constants.projectileList.add(p);
		isCannonReadyToFire = false;
    }
}
