package com.iantria.raidgame.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.entity.ScrollingCombatText;
import com.iantria.raidgame.util.Network;
import com.iantria.raidgame.util.TouchDirectionPieMenu;
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
    private TouchDirectionPieMenu touchDirectionPieMenu;
    private Stage fireButtonStage;
    private Stage bombButtonStage;
    private Stage mapButtonStage;
    private Stage pauseButtonStage;
    private Stage exitButtonStage;
    private Image bombButton;
    private Image fireButton;
    private Image exitButton;
    private Image mapButton;
    private Image pauseButton;
    private boolean paused = false;

    //graphics
    private SpriteBatch batch;
    private ListIterator<Projectile> projectiles;

    // Timers
    public float winDelayTime = 0;
    public float WIN_DELAY_DURATION = 10;
    public float delayTime;

    Network network;


    public GameScreen() {
        // Do admin stuff
        if (Constants.isNetworkAvailable)
            network = new Network(Constants.NETWORK_SERVICES_USAGE_API, "service=1");

        // Camera and Viewport
        camera = new OrthographicCamera(Constants.MAP_WIDTH,  Constants.MAP_HEIGHT);
        float aspectRatio = (float)Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
        Constants.WINDOW_HEIGHT = (int) (Constants.WINDOW_WIDTH/aspectRatio);
        viewport = new ExtendViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, camera);
        viewport.apply();
        batch = new SpriteBatch();

        //Map
        Constants.mapID = 1;
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
        Constants.helicopter.speed = Constants.carrier.speed;

        //EnemyShip
        Constants.enemyShip = new EnemyShip("EnemyShip", 0.2f, true,
                new Vector2(Constants.ENEMY_SHIP_XY[0] + Constants.WINDOW_WIDTH/2 - 200 - Constants.enemyShipTextureRegion.getRegionWidth()*0.2f/2,
                        Constants.ENEMY_SHIP_XY[1]),
                    0f, Constants.enemyShipTextureRegion);
        Constants.enemyShip.speed = 0;

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
        Constants.combatTextList.clear();
        Constants.removeCombatTextList.clear();
        Constants.combatTextList.add(new ScrollingCombatText("Start", 0.02f, new Vector2(Constants.helicopter.position), ("GOOD LUCK!"), Color.GREEN, Constants.scrollingCombatFont, true));
        Constants.isReadyToFireCruiseMissile = false;
        Statistics.resetScores();

        //Mobile Buttons
        doMobileButtons();
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0.53f, 0.81f, 0.92f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        delayTime += deltaTime;

        if (!paused) {
            // Update All
            checkHealthAndFuel(deltaTime);
            checkGameLogic(deltaTime);
            detectInput(deltaTime);
            Constants.helicopter.update(deltaTime);
            Constants.gameMap.speed = Constants.helicopter.speed;
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
            Constants.projectileList.removeAll(Constants.removeProjectileList);
            Constants.removeProjectileList.clear();
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
        if (Gdx.graphics.getFramesPerSecond() < Constants.lowestFPS) Constants.lowestFPS = Gdx.graphics.getFramesPerSecond();

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

        Constants.helicopter.draw(batch);

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

        for(ScrollingCombatText scr: Constants.combatTextList){
            scr.render(batch);
        }
        HeadsUpDisplay.draw(batch);

        batch.end();
        // **** End of draw *****

        if (Gdx.app.getType().equals(Application.ApplicationType.Android) || Gdx.app.getType().equals(Application.ApplicationType.iOS)) {
            drawAllMobileButtons();
        }
    }

    private void detectInput(float delta) {
        // Map Change
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            Constants.mapID = 1;
            Constants.gameMap.image = Constants.mapTextureRegion;
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            Constants.mapID = 2;
            Constants.gameMap.image = Constants.retroMapTextureRegion;
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
            Constants.mapID = 3;
            Constants.gameMap.image = Constants.retroGreenMapTextureRegion;
        }

        // Pause and Exit
        if ((Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && delayTime > 2f)) {
            Constants.oceanSound.stop();
            Constants.chopperSound.stop();
            Constants.takeOffSound.stop();
            Constants.enemyCruise.stop();
            Constants.fireCannonEffect.stop();
            exitButton.removeListener(exitButton.getListeners().first());
            mapButton.removeListener(mapButton.getListeners().first());
            //pauseButton.removeListener(pauseButton.getListeners().first());
            Constants.game.setScreen(new IntroScreen(true));
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
            if((Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.Z)
                    || touchDirectionPieMenu.menu.getHighlightedIndex() == 5)
                    && (Constants.helicopter.mode == Helicopter.FlyingMode.FLYING))
                Constants.helicopter.checkIfYouCanLand();
            if((Gdx.input.isKeyPressed(Input.Keys.SPACE)
                    || touchDirectionPieMenu.menu.getHighlightedIndex() == 0
                    || touchDirectionPieMenu.menu.getHighlightedIndex() == 1
                    || touchDirectionPieMenu.menu.getHighlightedIndex() == 2)
                    && Constants.helicopter.mode == Helicopter.FlyingMode.LANDED) {
                Constants.helicopter.mode = Helicopter.FlyingMode.TAKING_OFF;
                Constants.takeOffSound.play();
            }

            // Flying only stuff
            if (Constants.helicopter.mode == Helicopter.FlyingMode.FLYING) {
                if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || fireButton.getColor().a == 1)
                    Constants.helicopter.tryToFire("fireCannon");
                if (Gdx.input.isKeyPressed(Input.Keys.Z)  || bombButton.getColor().a == 1)
                    Constants.helicopter.tryToFire("fireBomb");
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 0
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 6
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 7) {
                    Constants.helicopter.rotation += -180f * delta;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 2
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 3
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 4) {
                    Constants.helicopter.rotation += +180f * delta;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.UP)
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 1
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 2
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 0) {
                    Constants.helicopter.speed = Constants.helicopter.speed + (75f * delta);
                    if (Constants.helicopter.speed > Constants.MAX_HELICOPTER_SPEED)
                        Constants.helicopter.speed = Constants.MAX_HELICOPTER_SPEED;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 4
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 5
                        || touchDirectionPieMenu.menu.getHighlightedIndex() == 6) {
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
           //  Handled by AI code in helicopter
        }

    }

    public void updateCombatText(float delta) {
        for(ScrollingCombatText scr: Constants.combatTextList){
            scr.update(delta);
        }
        Constants.combatTextList.removeAll(Constants.removeCombatTextList);
        Constants.removeCombatTextList.clear();
    }

    private void updateSounds() {
        if (Constants.helicopter.mode == Helicopter.FlyingMode.LANDED) {
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
            if (!Constants.youWinSound.isPlaying()) {
                Constants.youWinSound.play();
                Constants.combatTextList.add(new ScrollingCombatText("BIGWIN", 1f, new Vector2(Constants.WINDOW_WIDTH/2, 50), ("YOU HAVE WON!"), Color.GREEN, Constants.scrollingCombatFont, false));
            }
            if (winDelayTime >= WIN_DELAY_DURATION) {
                winDelayTime = 0;
                Statistics.youWon = true;
                Statistics.carrierSurvived = !Constants.carrier.isDestroyed && !Constants.carrier.isSinking;;
                //Constants.drumsSound.play();
                if (!Constants.carrier.isDestroyed) Statistics.score = Statistics.score + Constants.SCORE_ENEMY_SHIP_NOT_COMPLETED;
                if (!Statistics.enemyShipWasCompleted) Statistics.score = Statistics.score + Constants.SCORE_CARRIER_ALIVE;
                Statistics.score = Statistics.score + (Constants.SCORE_PER_PLANE_REMAINING * Constants.helicopter.livesCount);
                Constants.stopAllSounds();
                exitButton.removeListener(exitButton.getListeners().first());
                mapButton.removeListener(mapButton.getListeners().first());
                //pauseButton.removeListener(pauseButton.getListeners().first());
                Constants.game.setScreen(new OutcomeScreen());
            }
        }
    }

    public void checkHealthAndFuel(float delta) {
        if (Constants.helicopter.fuelCount < 1 || Constants.helicopter.health < 1) {
            if (!(Constants.helicopter.mode == Helicopter.FlyingMode.CRASHED)) {
                Constants.chopperSound.stop();
                Constants.outOfFuelCrashSound.play();
                Constants.projectileImpact.stop();
                Constants.helicopter.mode = Helicopter.FlyingMode.CRASHED;
                Constants.helicopter.livesCount--;
                Statistics.numberOfLivesLost++;

                if (Constants.helicopter.fuelCount < 1) Statistics.numberOfRanOutFuel++;
                if (Constants.helicopter.livesCount <= 0 && !Constants.drumsSound.isPlaying()) {
                    Statistics.carrierSurvived = !Constants.carrier.isDestroyed && !Constants.carrier.isSinking;
                    Constants.combatTextList.add(new ScrollingCombatText("YOULOST", 1f, new Vector2(Constants.WINDOW_WIDTH / 2, 50), ("YOU HAVE BEEN DEFEATED!"), Color.RED, Constants.scrollingCombatFont, false));
                    Constants.drumsSound.play();
                }
            }
            Constants.helicopter.generalDelayTime += delta;
            if (Constants.helicopter.generalDelayTime >= Constants.helicopter.YOU_CRASHED_DELAY_DURATION) {
                if (Constants.helicopter.livesCount <= 0) {
                    if (!Constants.drumsSound.isPlaying()) {
                        Constants.stopAllSounds();
                        exitButton.removeListener(exitButton.getListeners().first());
                        mapButton.removeListener(mapButton.getListeners().first());
                        //pauseButton.removeListener(pauseButton.getListeners().first());
                        Constants.game.setScreen(new OutcomeScreen());
                    }
                } else {
                    Constants.helicopter.generalDelayTime = 0;
                    Constants.helicopter.updatePositionsAfterCrash();
                    Constants.helicopter.mode = Helicopter.FlyingMode.LANDED;
                }
            }
        }
    }

    private void doMobileButtons() {
        // Mobile Stuff
        touchDirectionPieMenu = new TouchDirectionPieMenu(viewport);
        touchDirectionPieMenu.menu.setPosition(Constants.WINDOW_WIDTH - touchDirectionPieMenu.menu.getWidth() - 8,
                Constants.WINDOW_HEIGHT/2f - touchDirectionPieMenu.menu.getHeight()/2f);

        fireButtonStage = new Stage(viewport);
        fireButton = new Image(Constants.fireButton);
        fireButton.setScale(0.10f);
        fireButton.setColor(1,1,1,0.33f);
        fireButton.setPosition(8, Constants.WINDOW_HEIGHT*.37f - fireButton.getHeight()/2f*fireButton.getScaleY());
        fireButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                Constants.helicopter.tryToFire("fireCannon");
                fireButton.setColor(1,1,1,1);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                super.touchUp(event, x, y, pointer, button);
                fireButton.setColor(1,1,1,0.33f);
            }
        });
        fireButton.setBounds(fireButton.getX(), fireButton.getY(), fireButton.getWidth(), fireButton.getHeight());
        fireButtonStage.addActor(fireButton);

        bombButtonStage = new Stage(viewport);
        bombButton = new Image(Constants.bombButton);
        bombButton.setScale(0.10f);
        bombButton.setColor(1,1,1,0.33f);
        bombButton.setPosition(8, Constants.WINDOW_HEIGHT*0.63f - bombButton.getHeight()/2*bombButton.getScaleY());
        bombButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Constants.helicopter.tryToFire("fireBomb");
                bombButton.setColor(1,1,1,1);
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                super.touchUp(event, x, y, pointer, button);
                bombButton.setColor(1,1,1,0.33f);
            }
        });
        bombButton.setBounds(bombButton.getX(), bombButton.getY(), bombButton.getWidth(), bombButton.getHeight());
        bombButtonStage.addActor(bombButton);

        mapButtonStage = new Stage(viewport);
        mapButton = new Image(Constants.mapButton);
        mapButton.setScale(0.2f);
        mapButton.setColor(1,1,1,1);
        mapButton.setPosition(Constants.WINDOW_WIDTH*0.4f - mapButton.getWidth()/2f*mapButton.getScaleX(),
                Constants.WINDOW_HEIGHT - mapButton.getHeight()*mapButton.getScaleY());
        mapButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mapButton.setColor(1,1,1,0.33f);
                if (Constants.mapID == 3){
                    Constants.gameMap.image = Constants.mapTextureRegion;
                    Constants.mapID = 1;
                } else if (Constants.mapID == 1){
                    Constants.gameMap.image = Constants.retroMapTextureRegion;
                    Constants.mapID = 2;
                } else {
                    Constants.gameMap.image = Constants.retroGreenMapTextureRegion;
                    Constants.mapID = 3;
                }
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                mapButton.setColor(1,1,1,1.0f);
                super.touchUp(event, x, y, pointer, button);
            }
        });
        mapButton.setBounds(mapButton.getX(), mapButton.getY(), mapButton.getWidth(), mapButton.getHeight());
        mapButtonStage.addActor(mapButton);

        exitButtonStage = new Stage(viewport);
        exitButton = new Image(Constants.exitButton);
        exitButton.setScale(0.2f);
        exitButton.setColor(1,1,1,1);
        exitButton.setPosition(Constants.WINDOW_WIDTH*0.6f - exitButton.getWidth()/2f*exitButton.getScaleX(),
                Constants.WINDOW_HEIGHT - exitButton.getHeight()*exitButton.getScaleY());
        exitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Constants.stopAllSounds();
                exitButton.removeListener(exitButton.getListeners().first());
                mapButton.removeListener(mapButton.getListeners().first());
                fireButton.removeListener(fireButton.getListeners().first());
                bombButton.removeListener(bombButton.getListeners().first());
                //pauseButton.removeListener(pauseButton.getListeners().first());
                Constants.game.setScreen(new IntroScreen(true));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                super.touchUp(event, x, y, pointer, button);
            }
        });
        exitButton.setBounds(exitButton.getX(), exitButton.getY(), exitButton.getWidth(), exitButton.getHeight());
        exitButtonStage.addActor(exitButton);

        // Only enable touch events if mobile
        if (Gdx.app.getType().equals(Application.ApplicationType.Android) || Gdx.app.getType().equals(Application.ApplicationType.iOS)) {
            InputMultiplexer inputMultiplexer = new InputMultiplexer();
            bombButton.setTouchable(Touchable.enabled);
            inputMultiplexer.addProcessor(bombButtonStage);
            fireButton.setTouchable(Touchable.enabled);
            inputMultiplexer.addProcessor(fireButtonStage);
            mapButton.setTouchable(Touchable.enabled);
            inputMultiplexer.addProcessor(mapButtonStage);
            exitButton.setTouchable(Touchable.enabled);
            inputMultiplexer.addProcessor(exitButtonStage);
            inputMultiplexer.addProcessor(touchDirectionPieMenu.stage);
            Gdx.input.setInputProcessor(inputMultiplexer);
        }
    }

    private void drawAllMobileButtons() {
        touchDirectionPieMenu.stage.act();
        touchDirectionPieMenu.stage.draw();
        fireButtonStage.act();
        fireButtonStage.draw();
        bombButtonStage.act();
        bombButtonStage.draw();
        mapButtonStage.act();
        mapButtonStage.draw();
        exitButtonStage.act();
        exitButtonStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        Constants.gameMap.updateMapSegments();
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
        exitButtonStage.dispose();
        mapButtonStage.dispose();
        bombButtonStage.dispose();
        fireButtonStage.dispose();
        pauseButtonStage.dispose();
    }
}
