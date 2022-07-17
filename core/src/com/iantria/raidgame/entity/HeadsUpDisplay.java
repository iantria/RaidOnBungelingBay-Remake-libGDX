package com.iantria.raidgame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

import space.earlygrey.shapedrawer.ShapeDrawer;


public class HeadsUpDisplay {
    private static String text;
    private static GlyphLayout layout;
    private static Color color = new Color();
    private static ShapeDrawer shapeDrawer;
    private static Float temp;
    private static TextureRegion enemyClippedEnemyShip;
    public static TextureRegion miniMap;

    public static void draw(SpriteBatch batch) {
        shapeDrawer = new ShapeDrawer(batch, Constants.singlePixelTextureRegion);

//        miniMap = new TextureRegion(ScreenUtils.getFrameBufferTexture());
//        batch.draw(miniMap,Constants.WINDOW_WIDTH/2 - 29,Constants.WINDOW_HEIGHT-27, 58, 27);

        // Bombs
        setTextAndLayout("Bombs:", Constants.HUDFont, 0.10f);
        Constants.HUDFont.draw(batch, text, Constants.WINDOW_WIDTH - layout.width - 56, Constants.WINDOW_HEIGHT - 4);
        if (Constants.helicopter.bombCount > 6) {
            color = Color.GREEN;
        } else if (Constants.helicopter.bombCount > 4) {
            color = Color.YELLOW;
        } else if (Constants.helicopter.bombCount > 2) {
            color = Color.ORANGE;
        } else {
            color = Color.RED;
        }
        int spacing = (50 - Constants.BOMBS_PER_PLANE * 2) / Constants.BOMBS_PER_PLANE;
        for (int i = 0; i < Constants.helicopter.bombCount; i++) {
            shapeDrawer.filledRectangle(Constants.WINDOW_WIDTH - 53 + ((spacing + 3) * i), Constants.WINDOW_HEIGHT - 8 - 2, spacing, 6, color);
            //shapeDrawer.filledRectangle(Constants.WINDOW_WIDTH - 115 + ((spacing + 3) * i),3 + 12, spacing, 15, color);
        }
        color = Color.WHITE;
        for (int i = 0; i < Constants.BOMBS_PER_PLANE; i++) {
            shapeDrawer.rectangle(Constants.WINDOW_WIDTH - 53 + ((spacing + 3) * i), Constants.WINDOW_HEIGHT - 8 - 2, spacing, 6, color);
        }

        // Cannon
        setTextAndLayout("Cannon:", Constants.HUDFont, 0.10f);
        Constants.HUDFont.draw(batch, text, Constants.WINDOW_WIDTH - layout.width - 56, Constants.WINDOW_HEIGHT - 14);
        float f = ((float) Constants.helicopter.cannonCount / (float) Constants.CANNON_ROUNDS) * 50f;
        if (f > 35) {
            color = Color.GREEN;
        } else if (f > 25) {
            color = Color.YELLOW;
        } else if (f > 12.5) {
            color = Color.ORANGE;
        } else {
            color = Color.RED;
        }
        shapeDrawer.filledRectangle(Constants.WINDOW_WIDTH - 53, Constants.WINDOW_HEIGHT - 20, f, 6, color);
        shapeDrawer.rectangle(Constants.WINDOW_WIDTH - 53, Constants.WINDOW_HEIGHT - 20, 50, 6, Color.WHITE);

        // Fuel
        f = (float) Constants.helicopter.fuelCount * 50f / (float) Constants.FUEL_CAPACITY;
        setTextAndLayout("Fuel:", Constants.HUDFont, 0.10f);
        Constants.HUDFont.draw(batch, text, Constants.WINDOW_WIDTH - layout.width - 56, Constants.WINDOW_HEIGHT - 24);
        if (f > 35) {
            color = Color.GREEN;
        } else if (f > 25) {
            color = Color.YELLOW;
        } else if (f > 12.5) {
            color = Color.ORANGE;
        } else {
            color = Color.RED;
        }
        shapeDrawer.filledRectangle(Constants.WINDOW_WIDTH - 53, Constants.WINDOW_HEIGHT - 30, f, 6, color);
        shapeDrawer.rectangle(Constants.WINDOW_WIDTH - 53, Constants.WINDOW_HEIGHT - 30, 50, 6, Color.WHITE);

        // Plane Health
        if (Constants.helicopter.health < 0) Constants.helicopter.health = 0;
        f = (float) Constants.helicopter.health * 50f / (float) Constants.MAX_HIT_POINTS_HELICOPTER;
        setTextAndLayout("Health:", Constants.HUDFont, 0.10f);
        Constants.HUDFont.draw(batch, text, Constants.WINDOW_WIDTH - layout.width - 56, Constants.WINDOW_HEIGHT - 34);
        if (f > 35) {
            color = Color.GREEN;
        } else if (f > 25) {
            color = Color.YELLOW;
        } else if (f > 12.5) {
            color = Color.ORANGE;
        } else {
            color = Color.RED;
        }
        shapeDrawer.filledRectangle(Constants.WINDOW_WIDTH - 53, Constants.WINDOW_HEIGHT - 40, f, 6, color);
        shapeDrawer.rectangle(Constants.WINDOW_WIDTH - 53, Constants.WINDOW_HEIGHT - 40, 50, 6, Color.WHITE);

        // Lives
        for (int i = 0; i < Constants.helicopter.livesCount - 1; i++) {
            batch.draw(Constants.helicopterIcon, 2 + i * 20, Constants.WINDOW_HEIGHT - 20, Constants.helicopterIcon.getRegionWidth() / 12, Constants.helicopterIcon.getRegionHeight() / 16);
        }

        // Factories
        for (int i = 0; i < Constants.getRemainingFactories(); i++) {
            batch.draw(Constants.factoryIcon, 2 + i * 12, Constants.WINDOW_HEIGHT - 32, Constants.factoryIcon.getRegionWidth() / 3, Constants.factoryIcon.getRegionHeight() / 3);
        }

        // Radars locked ON
        batch.draw(Constants.radarIcon, 2, Constants.WINDOW_HEIGHT - 51, Constants.radarIcon.getRegionWidth() / 20, Constants.radarIcon.getRegionHeight() / 20);
        setTextAndLayout("" + RadarSite.numberOfLockedOnRadars, Constants.HUDFont, 0.125f);
        Constants.HUDFont.draw(batch, text, 16, Constants.WINDOW_HEIGHT - 41);

        //Enemy Ship
        temp = (float) Constants.enemyShip.health / (float) Constants.ENEMY_SHIP_HEALTH * 100f;
        if (!Constants.enemyShip.isDestroyed && !Constants.enemyShip.isSinking && !Constants.enemyShip.isAttacking) {
            setTextAndLayout("Enemy Ship is " + temp.intValue() + "% Complete", Constants.HUDFont, 0.10f);
            Constants.HUDFont.draw(batch, text, 2, 7);
        } else if (Constants.enemyShip.isDestroyed) {
            setTextAndLayout("Enemy Ship Destroyed", Constants.HUDFont, 0.10f);
            Constants.HUDFont.draw(batch, text, 2, 7);
        } else {
            batch.setColor(Color.RED);
            setTextAndLayout("Enemy Ship Completed", Constants.HUDFont, 0.10f);
            Constants.HUDFont.draw(batch, text, 2, 7);
            batch.setColor(Color.WHITE);
        }

        enemyClippedEnemyShip = new TextureRegion(Constants.enemyShipIcon.getTexture(),0,0,
                (int)(Constants.enemyShipIcon.getTexture().getWidth()*temp/100), // Clip!
                Constants.enemyShipIcon.getTexture().getHeight());
        batch.draw(Constants.enemyShipIcon, 3, 10, Constants.enemyShipIcon.getRegionWidth() / 3, Constants.enemyShipIcon.getRegionHeight() / 3);
        batch.setColor(Color.RED);
        batch.draw(enemyClippedEnemyShip, 3, 10, enemyClippedEnemyShip.getRegionWidth() / 3, enemyClippedEnemyShip.getRegionHeight() / 3);
          // Another way to clip
        //        batch.draw(Constants.enemyShipIcon.getTexture(), 4, 10,
//                Constants.enemyShipIcon.getRegionWidth() / 3f * (float) Constants.enemyShip.health / (float) Constants.ENEMY_SHIP_HEALTH,
//                Constants.enemyShipIcon.getRegionHeight() / 3,
//                Constants.enemyShipIcon.getU(),
//                Constants.enemyShipIcon.getV2(),
//                Constants.enemyShipIcon.getU2() * (float) Constants.enemyShip.health / (float) Constants.ENEMY_SHIP_HEALTH,
//                Constants.enemyShipIcon.getV());
        batch.setColor(Color.WHITE);

        // FPS
//        setTextAndLayout("FPS:" + Gdx.graphics.getFramesPerSecond(), Constants.HUDFont, 0.10f);
//        Constants.HUDFont.draw(batch, text, Constants.WINDOW_WIDTH/2f - layout.width/2, Constants.WINDOW_HEIGHT - layout.height -1);

        //Score
        setTextAndLayout("Score:", Constants.HUDFont, 0.10f);
        Constants.HUDFont.draw(batch, text, Constants.WINDOW_WIDTH - layout.width - 65, 8);
        String s = "000000".substring(Integer.toString(Statistics.score).length(), 6);
        setTextAndLayout(s + Statistics.score, Constants.HUDScoreFont, 0.25f);
        Constants.HUDScoreFont.draw(batch, text, Constants.WINDOW_WIDTH - layout.width - 4, 14);

        // Carrier Position HUD
        if (!Constants.carrier.isDestroyed){
            //Constants.carrierDirectionArrow.setRotation((float) Constants.getUnsignedSignedDegreesToCarrier(Constants.helicopter.getPosition()));
            batch.draw(Constants.carrierDirectionArrow, Constants.WINDOW_WIDTH/2 - Constants.carrierDirectionArrow.getRegionWidth()/12/2 ,
                    20,
                    Constants.carrierDirectionArrow.getRegionWidth()/12/2,Constants.carrierDirectionArrow.getRegionHeight()/12/2,
                    Constants.carrierDirectionArrow.getRegionWidth()/12,Constants.carrierDirectionArrow.getRegionHeight()/12,
                    1f, 1f,
                    (float) Constants.getSignedDegreesToCarrier(new Vector2(Constants.helicopter.position.x + Constants.helicopter.image.getRegionWidth()/2*Constants.helicopter.scale,
                            Constants.helicopter.position.y + Constants.helicopter.image.getRegionHeight()/2*Constants.helicopter.scale ))-180);

            if (Constants.carrier.health < 0) Constants.carrier.health = 0;
            setTextAndLayout("Carrier", Constants.HUDFont, 0.10f);
            Constants.HUDFont.draw(batch, text, Constants.WINDOW_WIDTH/2 - layout.width/2 , 8);
            f = (float) Constants.carrier.health * 50f / (float) Constants.MAX_HIT_POINTS_CARRIER;
            if (f > 35) {
                color = Color.GREEN;
            } else if (f > 25) {
                color = Color.YELLOW;
            } else if (f > 12.5) {
                color = Color.ORANGE;
            } else {
                color = Color.RED;
            }
            shapeDrawer.filledRectangle(Constants.WINDOW_WIDTH/2 - 25,  10, f, 6, color);
            shapeDrawer.rectangle(Constants.WINDOW_WIDTH/2 - 25,  10, 50, 6, Color.WHITE);
        }
    }

        private static void setTextAndLayout(String t, BitmapFont font, float scale) {
        text = t;
        font.getData().setScale(scale, scale);
        font.setUseIntegerPositions(false);
        layout = new GlyphLayout(font, text);
        }
}
