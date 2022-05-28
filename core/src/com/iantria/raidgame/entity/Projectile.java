package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class Projectile extends Entity {

    private final Animation<TextureRegion> bombHitNothingAnimation;

    public enum Type {
        MY_BULLET,  MY_BOMB,
        AA_GUN_BULLET, ENEMY_FIGHTER_BULLET,
        ENEMY_CRUISE_MISSILE;
    }
    public enum MainTarget {
        PLAYER_IS_TARGET, CARRIER_IS_TARGET;
    }

    public Type type;
    public MainTarget mainTarget;
    public boolean bombHitNothing;

     public Projectile(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, float speed, TextureRegion image, Type type) {
        super(id, scale, isMovingObj, position, rotation, image);
        this.speed = speed;
        this.type = type;
        this.rotation = rotation;
        this.scale = scale;
        this.elapsedTime = 0;
        this.bombHitNothingAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[3].getFrameDuration(),Constants.explosionAnimations[3].getKeyFrames());
         vector1 = new Vector2(position);
         vector2 = new Vector2(position);
         vector3 = new Vector2(position);
         vector4 = new Vector2(position);
         updateVectorsForMovingObjects();
         soundID = -1;
     }

    public void update(float delta) {

        float hip = delta * speed;
        elapsedTime += delta;

        position.x = position.x - Constants.map_dx;
        position.y = position.y - Constants.map_dy;
        updateVectorsForMovingObjects();

        position.x += hip * java.lang.Math.sin(java.lang.Math.toRadians(rotation));
        position.y -= hip *java.lang.Math.cos(java.lang.Math.toRadians(rotation));

        checkCollisions(delta);

        if ((position.x < -10 || position.x > Constants.WINDOW_WIDTH + 10) ||
                (position.y < -10 || position.y > Constants.WINDOW_HEIGHT + 10)){
            Constants.removeProjectileList.add(this);
        } else if (elapsedTime > 2.0f && type != Type.ENEMY_CRUISE_MISSILE) {
            Constants.removeProjectileList.add(this);
        }

        if (rotation >= 360) rotation = rotation - 360;
        if (rotation <= 0) rotation = rotation + 360;

        direction =  rotation - 180;
    }

    public void draw(Batch batch) {
         if (bombHitNothing && type == Type.MY_BOMB) {
             if (soundID == -1)
                soundID = Constants.cruiseOutOfFuel.play();
             batch.draw(bombHitNothingAnimation.getKeyFrame(elapsedTime),
                     position.x + image.getRegionWidth() / 2 * scale - bombHitNothingAnimation.getKeyFrame(elapsedTime).getRegionWidth() / 2,
                     position.y + image.getRegionHeight() / 2 * scale - bombHitNothingAnimation.getKeyFrame(elapsedTime).getRegionHeight() / 2);
             if (bombHitNothingAnimation.isAnimationFinished(elapsedTime)) {
//                 isDestroyed = true;
//                 bombHitNothing = false;
//                 Constants.removeProjectileList.add(this);
             }
         } else {
             batch.draw(image, vector1.x, vector1.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
             batch.draw(image, vector2.x, vector2.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
             batch.draw(image, vector3.x, vector3.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);
             batch.draw(image, vector4.x, vector4.y, image.getRegionWidth()*scale/2, image.getRegionHeight()*scale/2 , image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, direction);

         }
    }

    public void reset() {

    }

    // Helper methods
    public void updateEntityForHit(Entity f){
        int x = f.health;
        if (type == Type.MY_BOMB) {
            x = x - Constants.BOMB_DAMAGE;
            Statistics.amountOfDamageDealt += Constants.BOMB_DAMAGE;
            Statistics.numberOfBombsLanded++;
            Constants.mediumExplosion.play();
            f.wasHitByBomb = true;
            f.wasHit = true;
        }
        if (type == Type.MY_BULLET) {
            x = x - Constants.CANNON_DAMAGE;
            Constants.bulletHitLand.play();
            Statistics.amountOfDamageDealt += Constants.CANNON_DAMAGE;
            Statistics.numberOfCannonRoundsLanded++;
            f.wasHitByCannon = true;
            f.wasHit = true;
        }
        f.health = x;
    }

    public void checkCollisions(float delta) {
        // Enemy Projectiles, do they hit me?
        if (type == Type.AA_GUN_BULLET){
            if(intersects(Constants.helicopter)) {
                Constants.helicopter.wasHit = true;
                Constants.projectileImpact.play();
                Statistics.amountOfDamageTaken += Constants.ENEMY_AA_GUN_DAMAGE;
                Constants.helicopter.health -= Constants.ENEMY_AA_GUN_DAMAGE;
                Constants.combatTextList.add(new ScrollingCombatText("AAGun" + Constants.helicopter.health, 1f, new Vector2(Constants.helicopter.position), ("-" + Constants.ENEMY_AA_GUN_DAMAGE + " Health"), Color.RED, Constants.scrollingCombatFont, true));
                Statistics.numberOfTimesHitByAAGun++;
                Constants.AAGunFireSound.stop();
                Constants.removeProjectileList.add(this);
                return;
            }
        } else if (type == Type.ENEMY_FIGHTER_BULLET){
            if(intersects(Constants.helicopter)) {
                Constants.helicopter.wasHit = true;
                Constants.projectileImpact.play();
                Statistics.amountOfDamageTaken += Constants.ENEMY_FIGHTER_GUN_DAMAGE;
                Constants.helicopter.health -= Constants.ENEMY_FIGHTER_GUN_DAMAGE;
                Constants.combatTextList.add(new ScrollingCombatText("EnemyFighter" + Constants.helicopter.health, 1f, new Vector2(Constants.helicopter.position), ("-" + Constants.ENEMY_FIGHTER_GUN_DAMAGE + " Health"), Color.RED, Constants.scrollingCombatFont, true));
                Statistics.numberOfTimesHitByFighter++;
                Constants.fighterFire.stop();
                Constants.removeProjectileList.add(this);
                return;
            }
        }
        else if (type == Type.ENEMY_CRUISE_MISSILE){
            if(!wasHit && !isDestroyed && intersects(Constants.helicopter) && mainTarget == MainTarget.PLAYER_IS_TARGET) {
                Constants.helicopter.wasHit = true;
                Constants.projectileImpact.play();
                Constants.removeProjectileList.add(this);
                wasHit = true; // added Jan 19
                Statistics.amountOfDamageTaken += Constants.ENEMY_CRUISE_MISSILE_DAMAGE;
                Constants.helicopter.health -= Constants.ENEMY_CRUISE_MISSILE_DAMAGE;
                Constants.combatTextList.add(new ScrollingCombatText("CruiseMissile" + Constants.helicopter.health, 1f, new Vector2(Constants.helicopter.position), ("-" + Constants.ENEMY_CRUISE_MISSILE_DAMAGE + " Health"), Color.RED, Constants.scrollingCombatFont, true));
                Statistics.numberOfTimesHitByCruiseMissile++;
                return;
            }
            if(!wasHit && !isDestroyed && intersects(Constants.carrier) && mainTarget == MainTarget.CARRIER_IS_TARGET) {
                Constants.carrier.wasHit = true;
                Constants.projectileImpact.play();
                Constants.removeProjectileList.add(this);
                wasHit = true;  // added Jan 19
                Statistics.amountOfCarrierDamageTaken += Constants.ENEMY_CRUISE_MISSILE_DAMAGE;
                Constants.carrier.health -= Constants.ENEMY_CRUISE_MISSILE_DAMAGE;
                Constants.combatTextList.add(new ScrollingCombatText("shipCruiseMissile" + Constants.carrier.health, 1f, new Vector2(Constants.helicopter.position), ("-" + Constants.ENEMY_CRUISE_MISSILE_DAMAGE + " Carrier Health"), Color.YELLOW, Constants.scrollingCombatFont, true));
                Statistics.numberOfTimesHitByCruiseMissile++;
                return;
            }

          // My Projectiles - check if I hit stuff
        } else if (type == Type.MY_BULLET || type == Type.MY_BOMB) {
            if (!Constants.enemyShip.isDestroyed && !Constants.enemyShip.isSinking && Constants.enemyShip.intersects(this)){
                if (type == Type.MY_BOMB) {
                    updateEntityForHit(Constants.enemyShip);
                    if (Constants.enemyShip.health < 1) {
                        if (Constants.enemyShip.isAttacking) {
                            Constants.enemyShip.isSinking = true;
                            Constants.enemyShip.isAttacking = false;
                            Constants.bigExplosion.play();
                            Statistics.score = Statistics.score + Constants.SCORE_ENEMY_SHIP;
                            Constants.combatTextList.add(new ScrollingCombatText("BomberScore" , 1f, new Vector2(Constants.helicopter.position), ("+" + Constants.SCORE_ENEMY_SHIP + " Score"), Color.WHITE, Constants.scrollingCombatFont, true));
                        } else Constants.enemyShip.health = 1;
                    }
                    Constants.removeProjectileList.add(this);
                    return;
                }
            }

            for (Factory f : Constants.factories){
                if (f.isDestroyed) continue;
                if (type == Type.MY_BULLET) continue;
                if (f.intersects(this)){
                    updateEntityForHit(f);
                    if (f.health < 1) {
                        f.isDestroyed = true;
                        f.wasHitByBomb = true;
                        Constants.bigExplosion.play();
                        Statistics.numberOfFactoriesDestroyed++;
                        if (Statistics.numberOfFactoriesDestroyed == 6) {
                            Constants.combatTextList.add(new ScrollingCombatText("YouWon", 1f, new Vector2(Constants.helicopter.position), ("YOU HAVE WON!"), Color.GREEN, Constants.scrollingCombatFont, true));
                        }
                        Statistics.score = Statistics.score + Constants.SCORE_FACTORY;
                        Constants.combatTextList.add(new ScrollingCombatText("FactoryScore" , 1f, new Vector2(Constants.helicopter.position), ("+" + Constants.SCORE_FACTORY + " Score"), Color.WHITE, Constants.scrollingCombatFont, true));
                    }
                    Constants.removeProjectileList.add(this);
                    return;
                }
            }

            // You can shoot down cruise missiles
            for (Projectile f : Constants.projectileList){
                if (f.isDestroyed || f.wasHit) continue;
                if (type == Type.MY_BOMB) continue;
                if (f.type == Type.ENEMY_CRUISE_MISSILE && f.intersects(this)){
                    updateEntityForHit(f);
                    if (f.health < 1) {
                        f.wasHit = true;
                        if (f.soundID == -1)
                            f.soundID = Constants.cruiseOutOfFuel.play();
                        Statistics.numberOfCruiseMissilesDestroyed++;
                        Statistics.score += Constants.SCORE_CRUISE_MISSILE;
                        Constants.combatTextList.add(new ScrollingCombatText("CruiseScore" , 1f, new Vector2(Constants.helicopter.position), ("+" + Constants.SCORE_CRUISE_MISSILE + " Score"), Color.WHITE, Constants.scrollingCombatFont, true));
                    }
                    Constants.removeProjectileList.add(this);
                    return;
                }
            }
            for (AAGun f : Constants.aaGuns){
                if (f.isDestroyed) continue;
                if (f.intersects(this)){
                    updateEntityForHit(f);
                    if (f.health < 1) {
                        f.isDestroyed = true;
                        Constants.bigExplosion.play();
                        Statistics.numberOfAAGunsDestroyed++;
                        Statistics.score += Constants.SCORE_AA_GUN;
                        Constants.combatTextList.add(new ScrollingCombatText("AAGunScore" , 1f, new Vector2(Constants.helicopter.position), ("+" + Constants.SCORE_AA_GUN + " Score"), Color.WHITE, Constants.scrollingCombatFont, true));
                    }
                    Constants.removeProjectileList.add(this);
                    return;
                }
            }


            for (RadarSite f : Constants.radarSites){
                if (f.isDestroyed) continue;
                if (f.intersects(this)){
                    updateEntityForHit(f);
                    if (f.health < 1) {
                        f.isDestroyed = true;
                        Constants.bigExplosion.play();
                        Statistics.numberOfRadarsDestroyed++;
                        Statistics.score += Constants.SCORE_RADAR_SITE;
                        Constants.combatTextList.add(new ScrollingCombatText("RadarSiteScore" , 1f, new Vector2(Constants.helicopter.position), ("+" + Constants.SCORE_RADAR_SITE + " Score"), Color.WHITE, Constants.scrollingCombatFont, true));
                        RadarSite.numberOfLockedOnRadars--;
                    }
                    Constants.removeProjectileList.add(this);
                    return;
                }
            }



            for (EnemyFighter f : Constants.enemyFighters) {
                if (f.isDestroyed) continue;
                if (!f.isLanded && type == Projectile.Type.MY_BOMB) continue;
                if (f.intersects(this)) {
                    updateEntityForHit(f);
                    if (f.health < 1) {
                        f.isDestroyed = true;
                        Constants.bigExplosion.play();
                        Statistics.numberOfFightersDestroyed++;
                        Statistics.score = Statistics.score + Constants.SCORE_FIGHTER;
                        Constants.combatTextList.add(new ScrollingCombatText("FighterScore" , 1f, new Vector2(Constants.helicopter.position), ("+" + Constants.SCORE_FIGHTER + " Score"), Color.WHITE, Constants.scrollingCombatFont, true));
                    }
                    Constants.removeProjectileList.add(this);
                    return;
                }
            }
            for (EnemyBomber f : Constants.enemyBombers){
                if (f.isDestroyed) continue;
                if (!f.isLanded && type == Projectile.Type.MY_BOMB) continue;
                if (f.intersects(this)){
                    updateEntityForHit(f);
                    if (f.health < 1) {
                        f.isDestroyed = true;
                        Constants.bigExplosion.play();
                        Statistics.numberOfBombersDestroyed++;
                        Statistics.score = Statistics.score + Constants.SCORE_BOMBER;
                        Constants.combatTextList.add(new ScrollingCombatText("BomberScore" , 1f, new Vector2(Constants.helicopter.position), ("+" + Constants.SCORE_BOMBER + " Score"), Color.WHITE, Constants.scrollingCombatFont, true));
                    }
                    Constants.removeProjectileList.add(this);
                    return;
                }
            }
            if (type == Type.MY_BOMB){
                bombHitNothing = true;
            }
        }
    }
}
