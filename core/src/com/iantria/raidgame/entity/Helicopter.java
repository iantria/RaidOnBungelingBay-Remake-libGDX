package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

public class Helicopter extends Entity {

    public enum FlyingMode {
        LANDED, LANDING, TAKING_OFF, FLYING, CRASHED
    }
    public FlyingMode mode;

    public int bombCount;
    public int cannonCount;
    public int fuelCount;
    public int livesCount;
    public long lastFireBomb;
    public long lastFireCannon;
    public float fuelElapsedTime; // The time that has passed, reset to 0 after +-1 sec.
    public float generalDelayTime = 0;
    public float bladesSpinUpDownTime;
    public float FUEL_DURATION = Constants.FUEL_DURATION;
    public float YOU_CRASHED_DELAY_DURATION = 2.5f;
    public float YOU_LOST_DELAY_DURATION = 5.5f;
    public float dmg;
    public float percent;
    public Vector2 temp;
    public TextureRegion blades;
    public  TextureRegion bullets;
    public float bladeRotation;
    public Animation<TextureRegion> planeExploded;
    public Animation<TextureRegion> planeWasHitAnimation;
    public float explosionTimer;

    public Helicopter(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image, TextureRegion blades, TextureRegion bullets) {
        super(id, scale, isMovingObj, position, rotation, image);

        this.mode = FlyingMode.LANDED;
        this.bladesSpinUpDownTime = 0f;
        this.isPlayer = Constants.isPlayer;
        this.rotation = rotation;
        this.direction = rotation;
        this.image = image;
        this.blades = blades;
        this.bullets = bullets;
        this.bladeRotation = 0f;
        this.position = new Vector2(position);
        this.isMovingObject = isMovingObj;
        this.scale = scale;
        this.livesCount = Constants.NUMBER_OF_LIVES;
        this.health = Constants.MAX_HIT_POINTS_HELICOPTER;
        this.fuelCount = Constants.FUEL_CAPACITY;
        this.bombCount = Constants.BOMBS_PER_PLANE;
        this.cannonCount = Constants.CANNON_ROUNDS;
        this.speed = Constants.CARRIER_SPEED;
        this.relativePositionToMap = new Vector2(Constants.WINDOW_WIDTH / 2f + 400, Constants.WINDOW_HEIGHT / 2f);
        init();
    }

    public void init() {
        this.planeExploded = new Animation<TextureRegion>(Constants.explosionAnimations[7].getFrameDuration(),Constants.explosionAnimations[7].getKeyFrames());
        this.planeWasHitAnimation = new Animation<TextureRegion>(Constants.explosionAnimations[2].getFrameDuration(),Constants.explosionAnimations[2].getKeyFrames());
        this.livesCount = Constants.NUMBER_OF_LIVES;
        this.health = Constants.MAX_HIT_POINTS_HELICOPTER;
        this.fuelCount = Constants.FUEL_CAPACITY;
        this.bombCount = Constants.BOMBS_PER_PLANE;
        this.cannonCount = Constants.CANNON_ROUNDS;
        this.mode = FlyingMode.LANDED;
        this.speed = Constants.CARRIER_SPEED;
        this.bladeRotation = 0f;
        this.generalDelayTime = 0;
        lastFireCannon = System.currentTimeMillis();
        lastFireBomb = System.currentTimeMillis();
        type = EntityType.HELICOPTER;
    }

    public void reset() {
        this.health = Constants.MAX_HIT_POINTS_HELICOPTER;
        this.fuelCount = Constants.FUEL_CAPACITY;
        this.bombCount = Constants.BOMBS_PER_PLANE;
        this.cannonCount = Constants.CANNON_ROUNDS;
        this.mode = FlyingMode.LANDED;
        this.speed = Constants.CARRIER_SPEED;
        this.bladeRotation = 0f;
        Constants.chopperSound.stop();
        this.generalDelayTime = 0;
        bladesSpinUpDownTime = 0;
        lastFireCannon = System.currentTimeMillis();
        lastFireBomb = System.currentTimeMillis();

    }

    public void update(float delta) {
        if (wasHit || mode == FlyingMode.CRASHED ) explosionTimer += delta;

        if (!isPlayer) doAIFlying(delta);

        if (mode == FlyingMode.CRASHED){
            speed = 0;
            Constants.map_dx = 0;
            Constants.map_dy = 0;
            return;
        }

        percent = ((float)health/(float)Constants.MAX_HIT_POINTS_HELICOPTER)*100;
        dmg = 0.0f;
//        if (percent <= 70 && percent  >= 50) dmg = 0.1f;
//        else if (percent < 50 && percent  >= 30) dmg = 0.2f;
//        else if (percent < 30 && percent  >= 0) dmg = 0.3f;

        //if (health <= 70) main.playerDamage.update(delta);

        fuelElapsedTime += delta;
        if (fuelElapsedTime >= FUEL_DURATION && (mode == FlyingMode.FLYING)) {
            fuelElapsedTime = 0;
            fuelCount--;
            if (fuelCount == 30){
                Constants.combatTextList.add(new ScrollingCombatText("LowFuel", 1f, new Vector2(Constants.helicopter.position), ("LOW FUEL"), Color.YELLOW, Constants.scrollingCombatFont, true));
            }
            Statistics.amountOfFuelUsed++;
        }

        //blades
        if (mode == FlyingMode.CRASHED) {

        } else if (mode == FlyingMode.LANDING){
            if (bladesSpinUpDownTime > 0) {
                Constants.chopperSound.setVolume(bladesSpinUpDownTime/720);
                bladeRotation += -bladesSpinUpDownTime * delta;
                bladesSpinUpDownTime -= (240f * delta);
            } else {
                mode = FlyingMode.LANDED;
            }
        } else if (mode == FlyingMode.TAKING_OFF) {
            if (bladesSpinUpDownTime < 720f) {
                bladeRotation += -bladesSpinUpDownTime * delta;
                bladesSpinUpDownTime += (240f * delta);
            } else {
                Constants.chopperSound.setLooping(true);
                Constants.chopperSound.setVolume(1f);
                Constants.chopperSound.play();
                mode = FlyingMode.FLYING;
                speed = 0f;
            }
        }

        if (mode == FlyingMode.FLYING) {
            bladesSpinUpDownTime = 720f;
            bladeRotation += -720f * delta;
            rotation = rotation % 360;
            while (rotation <0 ) rotation += 360;
        }

        while (bladeRotation < 0) bladeRotation += 360;
   }

    public void draw(SpriteBatch batch){

        if (this.mode == FlyingMode.CRASHED){
            if (!planeExploded.isAnimationFinished(explosionTimer)) {
                batch.draw(planeExploded.getKeyFrame(explosionTimer),
                        Constants.WINDOW_WIDTH / 2f - planeExploded.getKeyFrame(explosionTimer).getRegionWidth() / 2f,
                        Constants.WINDOW_HEIGHT / 2f - planeExploded.getKeyFrame(explosionTimer).getRegionHeight() / 2f);
            }else {

            }
        } else {
            // Draw helicopter and blades
            batch.draw(image, position.x, position.y , image.getRegionWidth()*scale/2f, image.getRegionHeight()*scale/2f + 6, image.getRegionWidth()*scale , image.getRegionHeight()*scale, 1f, 1f, rotation);
            batch.draw(blades, position.x - 6 , position.y + 12, 210/8/2f, 214/8/2f, 210/8f, 214/8f, 1f, 1f, bladeRotation);

            if (wasHit){
                if (!planeWasHitAnimation.isAnimationFinished(explosionTimer)) {
                    batch.draw(planeWasHitAnimation.getKeyFrame(explosionTimer),
                            Constants.WINDOW_WIDTH/2f  - planeWasHitAnimation.getKeyFrame(explosionTimer).getRegionWidth()/2f,
                            Constants.WINDOW_HEIGHT/2f - planeWasHitAnimation.getKeyFrame(explosionTimer).getRegionHeight()/2f);
                } else {
                    wasHit = false;
                    explosionTimer = 0;
                }
            }

//            if (health <= 70) {
//                main.playerDamage.position(position.x+ getImageCenterX(), position.y+getImageCenterY());
//                ((ConfigurableEmitter)main.playerDamage.getEmitter(0)).angularOffset.setValue(getRotation()-180);
//                ((ConfigurableEmitter)main.playerDamage.getEmitter(0)).speed.setMax(120f*(main.gameMap.speed)/Constants.MIN_PLANE_SPEED);
//                ((ConfigurableEmitter)main.playerDamage.getEmitter(0)).speed.setMin(120f*(main.gameMap.speed)/Constants.MIN_PLANE_SPEED);
//                ((ConfigurableEmitter)main.playerDamage.getEmitter(0)).initialSize.setMax(10f + (70f-(float)health)/3f);
//                ((ConfigurableEmitter)main.playerDamage.getEmitter(0)).initialSize.setMin(10f + (70f-(float)health)/3f);
//                main.playerDamage.render();
//            }

        }
    }




    public void tryToFire(String weapon) {
        if (mode == FlyingMode.LANDED || mode == FlyingMode.LANDING || mode == FlyingMode.TAKING_OFF || mode == FlyingMode.CRASHED) return;
        if (weapon.equals("fireBomb")){
            if (System.currentTimeMillis() - lastFireBomb < Constants.MY_BOMB_FIRING_INTERVAL) {
                return;
            }
            if (bombCount > 0) {
                lastFireBomb = System.currentTimeMillis();
                Constants.singleBombDrop.play();
                bombCount--;
                if (bombCount == 0)
                    Constants.combatTextList.add(new ScrollingCombatText("OutOfBombs", 1f, new Vector2(Constants.helicopter.position), ("OUT OF BOMBS"), Color.YELLOW, Constants.scrollingCombatFont, true));
                Statistics.numberOfBombsDropped++;
                Projectile bomb = new Projectile("playerBomb" + bombCount, 0.3f, false,
                        //new Vector2(position.x - 9, position.y + 8), rotation, 0f, bullets, Projectile.Type.MY_BOMB);
                new Vector2(Constants.WINDOW_WIDTH/2f - bullets.getRegionWidth()*0.3f/2f ,
                        Constants.WINDOW_HEIGHT/2f - bullets.getRegionHeight()*0.3f/2f ), rotation, 0, Constants.bombTextureRegion, Projectile.Type.MY_BOMB);
                Constants.projectileList.add(bomb);
            }
        }

        if (weapon.equals("fireCannon")){
            if (System.currentTimeMillis() - lastFireCannon < Constants.MY_CANNON_FIRING_INTERVAL) {
                return;
            }
            if (cannonCount > 0 ) {
                lastFireCannon = System.currentTimeMillis();
                Constants.m61Sound.play(0.25f);;
                cannonCount--;
                Statistics.numberOfCannonRoundsFired++;
                Projectile cannon = new Projectile("playerCannon", 0.2f, true,
                        new Vector2(Constants.WINDOW_WIDTH/2 - bullets.getRegionWidth()*0.2f/2 ,
                                Constants.WINDOW_HEIGHT/2 - bullets.getRegionHeight()*0.2f/2 + 5),
                        rotation + 180, Constants.BULLET_SPEED, bullets, Projectile.Type.MY_BULLET);
                Constants.projectileList.add(cannon);
            }
        }
    }





    public void doAIFlying(float delta) {
        // If landed... take off
        if (mode == FlyingMode.LANDED) {
            generalDelayTime += delta;
            if (generalDelayTime >= 1.0) {
                generalDelayTime = 0;
                mode = FlyingMode.TAKING_OFF;
                Constants.takeOffSound.play();
            }
            return;
        }

        if (mode == FlyingMode.TAKING_OFF || mode == FlyingMode.LANDING) return;

        // Priority = Bombers, low health/bombs/fuel, factories
        if (Constants.enemyBombers[0].isAttacking && !Constants.carrier.isDestroyed){
            primaryTarget = Constants.enemyBombers[0];
        } else if (Constants.enemyBombers[1].isAttacking && !Constants.carrier.isDestroyed) {
            primaryTarget = Constants.enemyBombers[1];
        } else if ((fuelCount < 30 || bombCount == 0 || cannonCount == 0 || health < 40) && !Constants.carrier.isDestroyed) {
            primaryTarget = Constants.carrier;
        } else if (Constants.getRemainingFactories() > 0 ){
             //Attack factory
            for (Factory f: Constants.factories){
                if (!f.isDestroyed) {
                    primaryTarget = f;
                    break;
                }
            }
        } else {

        }

        temp = new Vector2(primaryTarget.position.x + primaryTarget.image.getRegionWidth()/2*primaryTarget.scale,
                primaryTarget.position.y + primaryTarget.image.getRegionHeight()/2*primaryTarget.scale);
        float angleToTarget = (float) Constants.getSignedDegrees(new Vector2(position.x + image.getRegionWidth()/2*scale, position.y + image.getRegionHeight()/2*scale), temp);
        float diff = Constants.calculateDifferenceBetweenAngles(angleToTarget, rotation-180);

        if (diff < -2.5)  rotation += +180f * delta;
        else if (diff > 2.5) rotation += -180f * delta;

        if (primaryTarget.type == EntityType.CARRIER) {
            if (isTargetVisibleOnScreen(primaryTarget))
                if (this.intersects(primaryTarget)){
                    slowSpeedDown(delta, Constants.MAX_HELICOPTER_SPEED/16);
                    if (this.carrierLandingIntersect()) slowSpeedDown(delta, 0);
                    checkIfYouCanLand();
                } else {
                    slowSpeedDown(delta, Constants.MAX_HELICOPTER_SPEED / 6);
                }
            else
                speedUp(delta, Constants.MAX_HELICOPTER_SPEED);

        } else if (primaryTarget.type == EntityType.ENEMY_BOMBER){
                if (isTargetVisibleOnScreen(primaryTarget)) {
                    slowSpeedDown(delta, Constants.MAX_HELICOPTER_SPEED / 4);
                    tryToFire("fireCannon");
                } else {
                    speedUp(delta, Constants.MAX_HELICOPTER_SPEED);
                }
        } else if (primaryTarget.type == EntityType.FACTORY) {
            if (isTargetVisibleOnScreen(primaryTarget))
                if (this.intersects(primaryTarget)){
                    slowSpeedDown(delta, Constants.MAX_HELICOPTER_SPEED / 18);
                    //slowSpeedDown(delta, 0);
                    tryToFire("fireBomb");
                    tryToFire("fireCannon");
                } else
                slowSpeedDown(delta, Constants.MAX_HELICOPTER_SPEED/4);
            else
                speedUp(delta, Constants.MAX_HELICOPTER_SPEED);
        } else {
            System.out.println("Do I ever get here?");
            if (isTargetVisibleOnScreen(primaryTarget)) tryToFire("fireCannon");
        }
    }

    public void slowSpeedDown(float delta, float minSpeed){
        Constants.helicopter.speed =  Constants.helicopter.speed - (75f * delta);
        if (Constants.helicopter.speed < minSpeed && minSpeed != 0)
            Constants.helicopter.speed =  Constants.helicopter.speed + (75f * delta); // speed up if you are below minSpeed
        if (Constants.helicopter.speed < minSpeed && minSpeed == 0) speed = 0;
    }

    public void speedUp(float delta, float maxSpeed){
        Constants.helicopter.speed =  Constants.helicopter.speed + (75f * delta);
        if (Constants.helicopter.speed > maxSpeed) Constants.helicopter.speed = maxSpeed;
    }



    public void checkIfYouCanLand() {
        //Can you land on Carrier?
        if (!(Constants.helicopter.mode == Helicopter.FlyingMode.CRASHED)
                && !Constants.carrier.isDestroyed
                && Constants.helicopter.mode == Helicopter.FlyingMode.FLYING
                && Constants.helicopter.carrierLandingIntersect()){
            if ((Constants.helicopter.speed <= 20 && Constants.helicopter.speed >= -20)){
                Constants.helicopter.mode = Helicopter.FlyingMode.LANDING;
                Constants.stopEngineSound.play();
                Statistics.numberOfLandings++;
                Constants.helicopter.fuelCount = Constants.FUEL_CAPACITY;
                Constants.helicopter.bombCount = Constants.BOMBS_PER_PLANE;
                Constants.helicopter.cannonCount = Constants.CANNON_ROUNDS;
                Constants.helicopter.health = Constants.MAX_HIT_POINTS_HELICOPTER;
                Constants.helicopter.speed = Constants.CARRIER_SPEED;
                Constants.gameMap.direction = Constants.carrier.direction;
                Constants.combatTextList.add(new ScrollingCombatText("LandedOnCarrier" + Statistics.numberOfLandings, 1f, new Vector2(Constants.helicopter.position), ("Repaired, Refueled, Reloaded!"), Color.GREEN, Constants.scrollingCombatFont, true));
            }
        }

//        //Can you land on SecretBase?
//        if (!isCrashed && !isLanded && main.secretBase.collidesWith(this)){
//            float angle = getRotation();
//            angle = Math.abs(angle % 360);
//
//            if (main.gameMap.speed == Constants.MIN_PLANE_SPEED){
//                setLanded(true);
//                setAfterburner(false);
//                main.landEffect1.play();
//                main.cruiseSpeedEffect.stop();
//                missileCount = Constants.MISSILES_PER_PLANE;
//                cannonCount = Constants.CANNON_ROUNDS;
//                main.combatText.add(new ScrollingCombatText("LandedOnSecretBase" + main.statistics.numberOfLandings, 1f, main.plane.position.copy(), ("Reloaded!"), Color.green, main.ttfTiny, true));
//            }
//        }
    }



    public void updatePositionsAfterCrash() {
        Vector2 old = new Vector2(Constants.gameMap.position);

        float carrierY = Constants.carrier.position.y - Constants.gameMap.position.y;
        Constants.helicopter.rotation = 0;
        Constants.helicopter.position = new Vector2(Constants.WINDOW_WIDTH/2f - Constants.helicopterTextureRegion.getRegionWidth()*0.125f/2f,
                                                    Constants.WINDOW_HEIGHT/2f - Constants.helicopterTextureRegion.getRegionHeight()*0.125f/2f);
        Constants.gameMap.position = new Vector2(Constants.WINDOW_WIDTH/2 - 200, -carrierY + 65); //todo
        Constants.carrier.position = new Vector2(Constants.WINDOW_WIDTH/2 - Constants.carrierTextureRegion.getRegionWidth()*0.2f/2 ,
                                                 Constants.WINDOW_HEIGHT/2 - Constants.carrierTextureRegion.getRegionHeight()*0.2f/6);
        Constants.helicopter.reset();

        if (old.x < (-Constants.MAP_WIDTH +  Constants.WINDOW_WIDTH/2)) old.x = old.x + Constants.MAP_WIDTH;

        Constants.enemyShip.position = new Vector2((Constants.gameMap.position.x -old.x) + Constants.enemyShip.position.x,
                (Constants.gameMap.position.y -old.y)  + Constants.enemyShip.position.y);
        for (EnemyFighter p : Constants.enemyFighters) {
            p.position = new Vector2((Constants.gameMap.position.x -old.x) + p.position.x,
                            (Constants.gameMap.position.y -old.y)  + p.position.y);
        }

        for (EnemyBomber p : Constants.enemyBombers) {
            p.position = new Vector2((Constants.gameMap.position.x -old.x) + p.position.x,
                            (Constants.gameMap.position.y -old.y)  + p.position.y);
        }

        for (Projectile p : Constants.projectileList) {
            p.position =new Vector2((Constants.gameMap.position.x -old.x) + p.position.x,
                            (Constants.gameMap.position.y -old.y)  + p.position.y);
        }
        for (EnemyBoat p : Constants.enemyBoats) {
            p.position =new Vector2((Constants.gameMap.position.x -old.x) + p.position.x,
                    (Constants.gameMap.position.y -old.y)  + p.position.y);
        }
        for (EnemyTank p : Constants.enemyTanks) {
            p.position =new Vector2((Constants.gameMap.position.x -old.x) + p.position.x,
                    (Constants.gameMap.position.y -old.y)  + p.position.y);
        }

        Constants.combatTextList.add(new ScrollingCombatText("Ready", 1f, new Vector2(Constants.helicopter.position), "READY FOR TAKEOFF!", Color.GREEN, Constants.scrollingCombatFont, true));
    }


}
