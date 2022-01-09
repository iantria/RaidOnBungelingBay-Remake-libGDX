package com.iantria.raidgame.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.Random;
import com.badlogic.gdx.audio.Music;
import com.iantria.raidgame.RaidGame;
import com.iantria.raidgame.entity.AAGun;
import com.iantria.raidgame.entity.Carrier;
import com.iantria.raidgame.entity.EnemyBomber;
import com.iantria.raidgame.entity.EnemyFighter;
import com.iantria.raidgame.entity.EnemyShip;
import com.iantria.raidgame.entity.Factory;
import com.iantria.raidgame.entity.GameMap;
import com.iantria.raidgame.entity.Helicopter;
import com.iantria.raidgame.entity.Projectile;
import com.iantria.raidgame.entity.ScrollingCombatText;

public class Constants {

    public static final String VERSION = "V0.5 libGDX";
    public static RaidGame game;
    public static boolean isPlayer;

    // Map
    public static final int MAP_WIDTH = 3200;
    public static final int MAP_HEIGHT = 2000;
    public static float map_dx;
    public static float map_dy;

    //Window attributes
    public static int WINDOW_RATIO = 8;  //8
    public static int WINDOW_WIDTH = MAP_WIDTH/ WINDOW_RATIO;
    public static int WINDOW_HEIGHT = MAP_HEIGHT/ WINDOW_RATIO;

    // Plane
    public static final int BOMBS_PER_PLANE = 9;
    public static final int CANNON_ROUNDS = 100;
    public static final int FUEL_CAPACITY = 100;
    public static final int FUEL_DURATION = 1; // # milliseconds for 1 fuel unit
    public static final int NUMBER_OF_LIVES = 3;

    // Health
    public static final int ENEMY_SHIP_HEALTH = 120;
    public static final int ENEMY_FACTORY_HEALTH = 120;
    public static final int ENEMY_AA_GUN_HEALTH = 10;
    public static final int ENEMY_CRUISE_MISSILE_HEALTH = 10;
    public static final int ENEMY_BOMBER_HEALTH = 10;
    public static final int ENEMY_FIGHTER_HEALTH = 10;
    public static final int MAX_HIT_POINTS_HELICOPTER = 100;
    public static final int MAX_HIT_POINTS_CARRIER = 200;

    // Damage
    public static final int CANNON_DAMAGE = 10;
    public static final int BOMB_DAMAGE = 20;
    public static final int ENEMY_AA_GUN_DAMAGE = 10;
    public static final int ENEMY_BOMBER_BOMB_DMG = 10;
    public static final int ENEMY_CRUISE_MISSILE_DAMAGE = 20;
    public static final int ENEMY_FIGHTER_GUN_DAMAGE = 10;

    // Timing
    public static final int FIRING_INTERVAL_BOMB = 1100;
    public static final int FIRING_INTERVAL_CANNON = 500;
    public static final int ENEMY_SHIP_FIRING_INTERVAL = 3;
    public static final int ENEMY_SHIP_RESPAWN_TIMER = 20;
    public static final int ENEMY_FIGHTER_RESPAWN_TIMER = 10;
    public static final int ENEMY_FIGHTER_FIRING_INTERVAL = 5;
    public static final int ENEMY_CRUISE_MISSILE_FIRING_INTERVAL = 15;
    public static final int ENEMY_CRUISE_MISSILE_FUEL = 12;
    public static final int ENEMY_BOMBER_RESPAWN_TIMER = 15;
    public static final int ENEMY_AA_GUN_FIRING_INTERVAL = 2;

    // Speeds
    public static final float CARRIER_SPEED = MAP_HEIGHT/200f;
    public static final float ENEMY_SHIP_SPEED = MAP_HEIGHT/200f;;
    public static final float MISSILE_SPEED = MAP_HEIGHT/12f;;
    public static final float BULLET_SPEED = MAP_HEIGHT/4f;;
    public static final float MAX_HELICOPTER_SPEED = MAP_HEIGHT/10f;
    public static final float MIN_HELICOPTER_SPEED = -MAP_HEIGHT/28f;
    public static final float ENEMY_FIGHTER_SPEED = MAP_HEIGHT/12f;
    public static final float ENEMY_BOMBER_SPEED = MAP_HEIGHT/14f;

    //Score
    public static final int SCORE_ENEMY_SHIP = 1000;
    public static final int SCORE_AA_GUN = 10;
    public static final int SCORE_FACTORY = 1000;
    public static final int SCORE_BOMBER = 50;
    public static final int SCORE_CRUISE_MISSILE = 75;
    public static final int SCORE_FIGHTER = 100;
    public static final int SCORE_CARRIER_ALIVE = 1000;
    public static final int SCORE_ENEMY_SHIP_NOT_COMPLETED = 2500;
    public static final int SCORE_PER_PLANE_REMAINING = 500;

    // 5     2      3    6      4     1
    public static final float[] FACTORY_X = {3440/2, 2992/2, 5844/2, 2469, 1776/2, 1952/2};
    public static final float[] FACTORY_Y = {2000 - 2284/2, 2000 -1164/2, 2000 - 780/2, 2000 - 1710, 2000 -3292/2, 2000 -1516/2};

    //  2      2      4     4     5    5     1    3     3     3     6     6     1                   g
    public static final float[] AA_GUN_X = {2676, 2441, 429, 904, 1706, 1787, 1322, 2675, 2618, 2976, 1802, 1582, 964, 917};
    public static final float[] AA_GUN_Y = {2000 - 1464,  2000 - 1704, 2000 - 1664, 2000 - 1699,  2000 - 1357, 2000 - 1096, 2000 - 1047,
                                            2000 - 258,  2000 - 561, 2000 - 366, 2000 - 465, 2000 - 572, 2000 - 974, 2000 - 768};

    public static final float[] BOMBER_X = {1135 ,1180};
    public static final float[] BOMBER_Y = {2000 - 487, 2000 - 487};
    public static final float[] FIGHTER_X = {2640 ,2690};
    public static final float[] FIGHTER_Y = {2000 - 1508, 2000 - 1508};
    public static final float[] ENEMY_SHIP_XY = {2580 ,2000 - 222};
    public static final float[] SECRET_BASE_XY = {2210, 1064};

    public static final String FILE_NAME = "raid.ini";

    // TextureRegions
    public static TextureRegion mapTextureRegion, retroMapTextureRegion, retroGreenMapTextureRegion,
            carrierTextureRegion, helicopterTextureRegion,
            rotatingBladesTextureRegion, playerBulletTextureRegion, enemyFighterTextureRegion,
            fighterBulletsTextureRegion,enemyBomberTextureRegion, factoryTextureRegion, aaGunTextureRegion,
            enemyBulletTextureRegion, bombTextureRegion, cruiseMissileTexture, enemyShipTextureRegion,
            singlePixelTextureRegion, helicopterIcon, factoryIcon, carrierIcon,enemyShipIcon,
            carrierDirectionArrow, introScreenSideApache, introScreenSideApacheBlade, introScreenBackProps,
            introScreenFrontApache, introScreenFrontBlade, introScreenName, introScreenTitle,
            playButton, demoButton;

    //Sounds
    public static Music outOfFuelCrashSound, bulletHitLand, AAGunFireSound, mediumExplosion,
            bigExplosion, youWin, carrierAlarm, projectileImpact, fighterFire, enemyCruise,
            bombsDroppingSound, cruiseOutOfFuel, fireCannonEffect, singleBombDrop, fireMissileEffect,
            drumsSound, takeOffSound, chopperSound,stopEngine, m61Sound, oceanSound;

    public static  Animation<TextureRegion>[] explosionAnimations;


    // Entities
    public static Factory[] factories = new Factory[6];
    public static AAGun[] aaGuns = new AAGun[14]; //14
    public static EnemyFighter[] enemyFighters = new EnemyFighter[2];
    public static EnemyBomber[] enemyBombers = new EnemyBomber[2];
    public static Random random = new Random();
    public static Helicopter helicopter;
    public static Carrier carrier;
    public static EnemyShip enemyShip;
    public static GameMap gameMap;
    public static boolean isReadyToFireCruiseMissile;
    public static float cruiseMissileDelayTimer;
    public static LinkedList<Projectile> projectileList;
    public static LinkedList<Projectile> removeProjectileList;
    public static LinkedList<ScrollingCombatText> removeCombatTextList = new LinkedList<ScrollingCombatText>();
    public static LinkedList<ScrollingCombatText> combatText = new LinkedList<ScrollingCombatText>();
    public static BitmapFont scrollingCombatFont;
    public static BitmapFont HUDFont;
    public static BitmapFont HUDLargeFont;

    public static int getRemainingFactories() {
        int number = 0;
        for (Factory f: factories){
            if (!f.isDestroyed) number++;
        }
        return number;
    }



    public static float getSignedDegreesToHelicopter(Vector2 screenPoint) {
        float dy = screenPoint.y - (helicopter.position.y + helicopter.image.getRegionHeight()/2*helicopter.scale);
        float dx = (helicopter.getPosition().x + helicopter.image.getRegionWidth()/2*helicopter.scale) - screenPoint.x;
        return (float) Math.toDegrees(Math.atan2(dx, dy));
    }

    public static float getSignedDegreesToCarrier(Vector2 screenPoint) {
        double dy = screenPoint.y - (carrier.getPosition().y + carrier.image.getRegionHeight()/2*carrier.scale); //todo
        double dx = carrier.getPosition().x + carrier.image.getRegionWidth()/2*carrier.scale - screenPoint.x;
        return (float) Math.toDegrees(Math.atan2(dx, dy));
        //return (float) (Math.atan2(-dx, -dy) / Math.PI * 180 + 180);
    }

//    public static float getUnsignedSignedDegreesToCarrier(Vector2 screenPoint) {
//        //double dy = screenPoint.y - (carrier.getPosition().y + carrier.image.getHeight()/2* carrier.scale);
//
////        double dy = screenPoint.y - (carrier.getPosition().y + carrier.image.getRegionHeight()/2*carrier.scale); //todo
////        double dx = carrier.getPosition().x + carrier.image.getRegionWidth()/2*carrier.scale - screenPoint.x;
//
//
//        double dy = screenPoint.y - (carrier.getPosition().y);
//        double dx = (carrier.getPosition().x + 16) - screenPoint.x ;
//        return (float) Math.toDegrees(Math.atan2(dx, dy));
//    }


    public static float getSignedDegrees(Vector2 from, Vector2 to) {
        float dy = from.y - to.y;
        float dx = to.x - from.x;
        return (float) Math.toDegrees(Math.atan2(dx, dy));
    }



    //    public static float getUnsignedDegreesToHelicopter(Vector2 screenPoint) {
//        float dy = screenPoint.y - helicopter.position.y;
//        float dx = helicopter.getPosition().x - screenPoint.x;
//        return (float) (Math.atan2(-dx, -dy) / Math.PI * 180 + 180);
//    }


    public static float getAngleToHelicopter(Vector2 screenPoint) {
        double dy = screenPoint.y - Constants.WINDOW_HEIGHT/2 + helicopter.image.getRegionHeight()*helicopter.scale/2f;
        double dx = Constants.WINDOW_WIDTH/2 - helicopter.image.getRegionWidth()*helicopter.scale/2f - screenPoint.x;
        return (float) Math.toDegrees(Math.atan2(dx, dy));
    }



//    public static float getUnsignedDegrees(Vector2 from, Vector2 to) {
//        float dy = from.y - to.y;
//        float dx = to.x - from.x;
//        return (float) (Math.atan2(-dx, -dy) / Math.PI * 180 + 180);
//    }
//




    public static float calculateDifferenceBetweenAngles(float firstAngle, float secondAngle) {
        float difference = secondAngle - firstAngle;
        while (difference < -180)
            difference += 360;
        while (difference > 180)
            difference -= 360;
        return difference;
    }

//    private void checkGameLogic(float delta) {
//        Statistics.gameTime += delta;
//        cruiseMissileDelayTimer = cruiseMissileDelayTimer + delta;
//        if (Constants.getRemainingFactories() < 4 && cruiseMissileDelayTimer > Constants.ENEMY_CRUISE_MISSILE_FIRING_INTERVAL){
//            isReadyToFireCruiseMissile = true;
//            cruiseMissileDelayTimer = 0;
//        }
//    }


}
