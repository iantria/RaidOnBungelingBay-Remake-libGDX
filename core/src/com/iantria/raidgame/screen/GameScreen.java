package com.iantria.raidgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.iantria.raidgame.RaidGame;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.entity.ScrollingCombatText;
import com.iantria.raidgame.util.Statistics;
import com.iantria.raidgame.entity.AAGun;
import com.iantria.raidgame.entity.Carrier;
import com.iantria.raidgame.entity.EnemyBomber;
import com.iantria.raidgame.entity.EnemyFighter;
import com.iantria.raidgame.entity.EnemyShip;
import com.iantria.raidgame.entity.Factory;
import com.iantria.raidgame.entity.GameMap;
import com.iantria.raidgame.entity.HeadsUpDisplay;
import com.iantria.raidgame.entity.Helicopter;
import com.iantria.raidgame.entity.Projectile;

import java.util.LinkedList;
import java.util.ListIterator;


public class GameScreen implements Screen {

    //screen
    private Camera camera;
    private Viewport viewport;
    private  Viewport miniMapViewPort;
    private RaidGame game;
    private boolean paused = false;
    private  ListIterator<Projectile> projectiles;

    //graphics
    private SpriteBatch batch;

    // Timers
    public float winDelayTime = 0;
    public float WIN_DELAY_DURATION = 10;
    public float delayTime;

    public GameScreen(RaidGame game) {
        this.game = game;
        // Camera and Viewport
        camera = new OrthographicCamera(Constants.MAP_WIDTH,  Constants.MAP_HEIGHT);
        viewport = new StretchViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, camera);
        viewport.apply();

        //setting up the map
        Constants.gameMap = new GameMap("Game Map", 1f, true, new Vector2(Constants.WINDOW_WIDTH/2 - 200,0), 0f, Constants.mapTextureRegion);

        //Helicopter
        Constants.helicopter = new Helicopter("Helicopter", 0.125f, true,
                new Vector2(Constants.WINDOW_WIDTH/2f - Constants.helicopterTextureRegion.getRegionWidth()*0.125f/2f,
                        Constants.WINDOW_HEIGHT/2f - Constants.helicopterTextureRegion.getRegionHeight()*0.125f/2f),
                0f,
                Constants.helicopterTextureRegion,
                Constants.rotatingBladesTextureRegion,
                Constants.playerBulletTextureRegion);

        //Carrier
        Constants.carrier = new Carrier("Carrier", 0.2f, true,
                new Vector2(Constants.WINDOW_WIDTH/2 - Constants.carrierTextureRegion.getRegionWidth()*0.2f/2 ,
                        Constants.WINDOW_HEIGHT/2 - Constants.carrierTextureRegion.getRegionHeight()*0.2f/6),  // move heli to bottom of carrier deck
                0f,
                Constants.carrierTextureRegion);
        Constants.helicopter.setSpeed(Constants.carrier.speed);

        //EnemyShip
        Constants.enemyShip = new EnemyShip("EnemyShip", 0.2f, true,
                new Vector2(Constants.ENEMY_SHIP_XY[0] + Constants.WINDOW_WIDTH/2 - 200 - Constants.enemyShipTextureRegion.getRegionWidth()*0.2f/2,
                        Constants.ENEMY_SHIP_XY[1]),
                    0f, Constants.enemyShipTextureRegion);
        Constants.enemyShip.setSpeed(0);

        // Factories
        for (int i =0 ; i < Constants.factories.length; i++){
            Vector2 v = new Vector2(Constants.FACTORY_X[i], Constants.FACTORY_Y[i]);
            Factory f = new Factory("Factory"+i, 0.25f, false, v, 0, Constants.factoryTextureRegion);
            Constants.factories[i] = f;
        }

        // AA Guns
        for (int i =0 ; i < Constants.aaGuns.length; i++){
            Vector2 v = new Vector2(Constants.AA_GUN_X[i]  , Constants.AA_GUN_Y[i] );
            AAGun a = new AAGun("AAGun"+i, 0.5f, false,  v, 0, Constants.aaGunTextureRegion);
            //Constants.random.nextFloat()*360
            Constants.aaGuns[i] = a;
        }

        //Planes
        Constants.enemyFighters[0] = new EnemyFighter("EnemyFighter1", 0.15f, true,
                new Vector2(Constants.FIGHTER_X[0] + Constants.WINDOW_WIDTH/2 - 200, Constants.FIGHTER_Y[0]), 270,
                Constants.enemyFighterTextureRegion, Constants.fighterBulletsTextureRegion );
        Constants.enemyFighters[1] = new EnemyFighter("EnemyFighter2", 0.15f, true,
                new Vector2(Constants.FIGHTER_X[1] + Constants.WINDOW_WIDTH/2 - 200 , Constants.FIGHTER_Y[1]), 270,
                Constants.enemyFighterTextureRegion, Constants.fighterBulletsTextureRegion);

        Constants.enemyBombers[0] = new EnemyBomber("EnemyBomber1", 0.20f, true,
                new Vector2(Constants.BOMBER_X[0] + Constants.WINDOW_WIDTH/2 - 200, Constants.BOMBER_Y[0]), 270,
                Constants.enemyBomberTextureRegion);
        Constants.enemyBombers[1] = new EnemyBomber("EnemyBomber2", 0.20f, true,
                new Vector2(Constants.BOMBER_X[1] + Constants.WINDOW_WIDTH/2 - 200 , Constants.BOMBER_Y[1]), 270,
                Constants.enemyBomberTextureRegion);

        Constants.projectileList = new LinkedList<>();
        Constants.removeProjectileList = new LinkedList<>();
        Constants.combatText.clear();
        Constants.combatText.add(new ScrollingCombatText("Start", 0.02f, new Vector2(Constants.helicopter.position), ("GOOD LUCK!"), Color.GREEN, Constants.scrollingCombatFont, true));
        Constants.isReadyToFireCruiseMissile = false;
        batch = new SpriteBatch();
    }

    @Override
    public void render(float deltaTime) {

        delayTime += deltaTime;

        if (!paused) {
            // Update All
            detectInput(deltaTime);
            checkGameLogic(deltaTime);
            Constants.helicopter.update(deltaTime);
            Constants.gameMap.setSpeed(Constants.helicopter.speed);
            if (!(Constants.helicopter.mode == Helicopter.FlyingMode.FLYING))
                Constants.gameMap.update(deltaTime, Constants.carrier.rotation);
            else
                Constants.gameMap.update(deltaTime, Constants.helicopter.rotation);
            Constants.carrier.update(deltaTime);
            Constants.enemyShip.update(deltaTime);

            for (EnemyFighter a : Constants.enemyFighters) {
                a.update(deltaTime);
            }
            for (EnemyBomber a : Constants.enemyBombers) {
                a.update(deltaTime);
            }
            for (Factory a : Constants.factories) {
                a.update(deltaTime);
            }
            for (AAGun a : Constants.aaGuns) {
                a.update(deltaTime);
            }
            projectiles = Constants.projectileList.listIterator();
            while (projectiles.hasNext()) {
                Projectile p = projectiles.next();
                p.update(deltaTime);
            }
            Constants.projectileList.removeAll(Constants.removeProjectileList);
            Constants.removeProjectileList.clear();
            updateCombatText(deltaTime);
            updateSounds();
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                paused = false;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }

        // ***** Do Draw *****
        camera.update();
        //batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Constants.gameMap.draw(batch);
        Constants.carrier.draw(batch);
        Constants.enemyShip.draw(batch);

        for (Factory a: Constants.factories){
            a.draw(batch);
        }
        for (AAGun a: Constants.aaGuns){
            a.draw(batch);
        }

        for (EnemyFighter a: Constants.enemyFighters){
            a.draw(batch);
        }
        for (EnemyBomber a: Constants.enemyBombers){
            a.draw(batch);
        }
        projectiles = Constants.projectileList.listIterator();
        while (projectiles.hasNext()) {
            Projectile p = projectiles.next();
            p.draw(batch);
        }

        Constants.helicopter.draw(batch);

        for(ScrollingCombatText scr: Constants.combatText){
            scr.render(batch);
        }
        HeadsUpDisplay.draw(batch);

        batch.end();
        // **** End of draw *****

    }

    private void detectInput(float delta) {
        //keyboard input

        // Map Change
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            Constants.gameMap.setImage(Constants.mapTextureRegion);
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            Constants.gameMap.setImage(Constants.retroMapTextureRegion);
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
            Constants.gameMap.setImage(Constants.retroGreenMapTextureRegion);
        }

        // Pause
        if ((Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && delayTime > 2f)) {
            Constants.oceanSound.stop();
            Constants.chopperSound.stop();
            Constants.takeOffSound.stop();
            Constants.enemyCruise.stop();
            Constants.fireCannonEffect.stop();
            Constants.game.setScreen(new IntroScreen(Constants.game, true));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            paused = true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }

        // Player Controlled
        if (Constants.helicopter.isPlayer) {
            // Landing and taking off
            if((Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.Z))
                    && (Constants.helicopter.mode == Helicopter.FlyingMode.FLYING
                    || Constants.helicopter.mode == Helicopter.FlyingMode.LANDED))
                Constants.helicopter.checkIfYouCanLand();
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && Constants.helicopter.mode == Helicopter.FlyingMode.LANDED) {
                Constants.helicopter.mode = Helicopter.FlyingMode.TAKING_OFF;
                Constants.takeOffSound.play();
            }

            // Flying only stuff
            if (Constants.helicopter.mode == Helicopter.FlyingMode.FLYING) {
                if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) Constants.helicopter.tryToFire("fireCannon");
                if (Gdx.input.isKeyPressed(Input.Keys.Z)) Constants.helicopter.tryToFire("fireBomb");
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    Constants.helicopter.rotation += -180f * delta;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    Constants.helicopter.rotation += +180f * delta;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    Constants.helicopter.speed = Constants.helicopter.speed + (75f * delta);
                    if (Constants.helicopter.speed > Constants.MAX_HELICOPTER_SPEED)
                        Constants.helicopter.speed = Constants.MAX_HELICOPTER_SPEED;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    Constants.helicopter.speed -=  (75f * delta);;
                    if (Constants.helicopter.speed < Constants.MIN_HELICOPTER_SPEED)
                        Constants.helicopter.speed = Constants.MIN_HELICOPTER_SPEED;
                } else if (Constants.helicopter.speed < 0 &&
                        Constants.helicopter.speed < Constants.MIN_HELICOPTER_SPEED/5 ) {
                    Constants.helicopter.speed += (75f * delta);
                    if (Constants.helicopter.speed >= Constants.MIN_HELICOPTER_SPEED/5 && Constants.helicopter.speed < 0)
                        Constants.helicopter.speed = 0;
                    if (Constants.helicopter.speed > Constants.MAX_HELICOPTER_SPEED)
                        Constants.helicopter.speed = Constants.MAX_HELICOPTER_SPEED;
                }
            }
        } else {
            //Constants.helicopter.doAIFlying(delta);
        }


        //touch input (also mouse)

    }

    public void updateCombatText(float delta) {
        for(ScrollingCombatText scr: Constants.combatText){
            scr.update(delta);
        }
        Constants.combatText.removeAll(Constants.removeCombatTextList);
        Constants.removeCombatTextList.clear();
    }

    private void updateSounds() {
        if (Constants.helicopter.mode == Helicopter.FlyingMode.LANDED || Constants.helicopter.mode == Helicopter.FlyingMode.CRASHED) {
            if (!Constants.oceanSound.isPlaying()){
                Constants.oceanSound.setLooping(true);
                Constants.oceanSound.setVolume(0.3f);
                Constants.oceanSound.play();
            }
            return;
        } else {
            Constants.oceanSound.stop();
        }
        if (!Constants.chopperSound.isPlaying()) Constants.chopperSound.setLooping(true);
    }

    private void checkGameLogic(float delta) {
        Statistics.gameTime += delta;
        Constants.cruiseMissileDelayTimer += delta;
        if (Constants.getRemainingFactories() < 4 && Constants.cruiseMissileDelayTimer > Constants.ENEMY_CRUISE_MISSILE_FIRING_INTERVAL){
            Constants.isReadyToFireCruiseMissile = true;
            Constants.cruiseMissileDelayTimer = 0;
        }

        if (Constants.getRemainingFactories() == 0){
            winDelayTime += delta;
            if (!Constants.youWin.isPlaying()) {
                Constants.youWin.play();
                Constants.combatText.add(new ScrollingCombatText("BIGWIN", 1f, new Vector2(Constants.WINDOW_WIDTH/2, 50), ("YOU HAVE WON!"), Color.GREEN, Constants.scrollingCombatFont, false));
            }
            if (winDelayTime >= WIN_DELAY_DURATION) {
                winDelayTime = 0;
                Statistics.youWon = true;
                Statistics.carrierSurvived = !Constants.carrier.isDestroyed && !Constants.carrier.isSinking;;
                //Constants.drumsSound.play();
                if (!Constants.carrier.isDestroyed()) Statistics.score = Statistics.score + Constants.SCORE_ENEMY_SHIP_NOT_COMPLETED;
                if (!Statistics.enemyShipWasCompleted) Statistics.score = Statistics.score + Constants.SCORE_CARRIER_ALIVE;
                Statistics.score = Statistics.score + (Constants.SCORE_PER_PLANE_REMAINING * Constants.helicopter.livesCount);
                Constants.oceanSound.stop();
                Constants.chopperSound.stop();
                Constants.takeOffSound.stop();
                Constants.enemyCruise.stop();
                Constants.fireCannonEffect.stop();
                Constants.game.setScreen(new IntroScreen(Constants.game, true));
            }
        }
    }





        @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
