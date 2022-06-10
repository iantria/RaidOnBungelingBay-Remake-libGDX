package com.iantria.raidgame.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.iantria.raidgame.entity.LoadingBar;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Network;
import com.badlogic.gdx.graphics.g2d.Animation;

public class LoadingScreen implements Screen {

    private Stage stage;
    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;
    private Actor loadingBar;

    private float startX, endX;
    private float percent;
    public AssetManager assetManager = new AssetManager();
    private TextureAtlas textureAtlas;
    private Network network;

    public LoadingScreen() {
    }

    @Override
    public void show() {
        Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);

        network = new Network(Constants.NETWORK_SERVICES_USAGE_API, "service=0");

        // Tell the manager to load assets for the loading screen
        assetManager.load("graphics/loading.pack", TextureAtlas.class);
        // Wait until they are finished loading
        assetManager.finishLoading();

        // Initialize the stage where we will place everything
        stage = new Stage();

        // Get our textureatlas from the manager
        TextureAtlas atlas = assetManager.get("graphics/loading.pack", TextureAtlas.class);

        // Grab the regions from the atlas and create some images
        logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        // Add the loading bar animation
        Animation<TextureRegion> anim = new Animation<TextureRegion>(
	    0.05f, atlas.findRegions("loading-bar-anim"));
        anim.setPlayMode(PlayMode.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        // Or if you only need a static bar, you can do
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));

        // Add all the actors to the stage
        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);

        // Add everything to be loaded, for instance:

        //set up the texture atlas for Game
        assetManager.load("graphics/explosions.png", Texture.class);
        assetManager.load("graphics/radar_anim.png", Texture.class);
        assetManager.load("graphics/images.atlas", TextureAtlas.class);
        assetManager.load("graphics/ddgIcon.gif", Texture.class);

        assetManager.load("graphics/modernmap.png", Texture.class);
        assetManager.load("graphics/retro_map.png", Texture.class);
        assetManager.load("graphics/retro_map_green.png", Texture.class);

        assetManager.load("graphics/help_info_v1.png", Texture.class);
        assetManager.load("graphics/help_info_mobile.png", Texture.class);

        //Sounds
        assetManager.load("sounds/jetcrashfuel.ogg", Sound.class);
        assetManager.load("sounds/landhit.ogg", Sound.class);
        assetManager.load("sounds/AAGun.ogg", Sound.class);
        assetManager.load("sounds/explosion_medium.ogg", Sound.class);
        assetManager.load("sounds/explosion_big.ogg", Sound.class);
        assetManager.load("sounds/projectileImpact.ogg", Sound.class);
        assetManager.load("sounds/FighterGun.ogg", Sound.class);
        assetManager.load("sounds/enemyCruise.ogg", Sound.class);
        assetManager.load("sounds/cruiseOutFuel.ogg", Sound.class);
        assetManager.load("sounds/carrierAlarm.ogg", Sound.class);
        assetManager.load("sounds/B2Bombing.ogg", Sound.class);
        assetManager.load("sounds/machinegun1.ogg", Sound.class);
        assetManager.load("sounds/bombdrop2.mp3", Sound.class);
        assetManager.load("sounds/MissleLaunch.ogg", Sound.class);
        assetManager.load("sounds/m61.ogg", Sound.class);
        assetManager.load("sounds/radar_beep.mp3", Sound.class);
        assetManager.load("sounds/M60.ogg", Sound.class);

        //Large Sounds (>100KB)
        assetManager.load("sounds/youwin.ogg", Music.class);
        assetManager.load("sounds/Drums2.ogg", Music.class);
        assetManager.load("sounds/Drums.ogg", Music.class);
        assetManager.load("sounds/helicopter_take_off.mp3", Music.class);
        assetManager.load("sounds/helicopter_cruise.mp3", Music.class);
        assetManager.load("sounds/powerdown_engine.mp3", Music.class);
        assetManager.load("sounds/ocean.ogg", Music.class);
        assetManager.load("sounds/fireworks.mp3", Music.class);

        // Fonts
        assetManager.load("graphics/font1.fnt", BitmapFont.class);
        assetManager.load("graphics/font2.fnt", BitmapFont.class);
        assetManager.load("graphics/font3.fnt", BitmapFont.class);
    }

    @Override
    public void resize(int width, int height) {
        // Make the background fill the screen
	    screenBg.setSize(stage.getWidth(), stage.getHeight());

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

	    // Place the logo in the middle of the screen
        logo.setX((stage.getWidth() - logo.getWidth()) / 2);
        logo.setY(loadingFrame.getY() + loadingFrame.getHeight() + 15);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    private void loadExplosionAnimations() {
        Texture x = assetManager.get("graphics/explosions.png", Texture.class);
        Constants.explosionAnimations = new Animation[8];

        //split texture
        TextureRegion[][] textureLines = TextureRegion.split(x, 64, 64);

        for (int i = 0; i < 8; i++) {
            TextureRegion[] textureLine = new TextureRegion[16];
            for (int j = 0; j < 16; j++) {
                textureLine[j] = textureLines[i][j];
            }
            Constants.explosionAnimations[i] = new Animation<>(1 / 16f, textureLine);  // todo
        }
    }

    private void loadRadarAnimation() {
        Texture x = assetManager.get("graphics/radar_anim.png", Texture.class);

        //split texture
        TextureRegion[][] textureLines = TextureRegion.split(x, 250, 255);

        TextureRegion[] textureLine = new TextureRegion[4];
        for (int j = 0; j < 4; j++) {
            textureLine[j] = textureLines[0][j];
        }
        Constants.radarAnimation = new Animation<>(1 / 4f, textureLine);  // todo
        Constants.radarAnimation.setPlayMode(PlayMode.LOOP_PINGPONG);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Constants.isNetworkAvailable == false && network.statusCode == 200) {
            Constants.isNetworkAvailable = true;
        }

        if (assetManager.update()) { // Load some, will return true if done loading

            //Textures
            textureAtlas = assetManager.get("graphics/images.atlas", TextureAtlas.class);
            Constants.enemyShipIcon = new TextureRegion(assetManager.get("graphics/ddgIcon.gif", Texture.class));
            Constants.helpInfoDesktop = new TextureRegion(assetManager.get("graphics/help_info_v1.png", Texture.class));
            Constants.helpInfoMobile = new TextureRegion(assetManager.get("graphics/help_info_mobile.png", Texture.class));

            Constants.mapTextureRegion = new TextureRegion(assetManager.get("graphics/modernmap.png", Texture.class));
            Constants.retroMapTextureRegion = new TextureRegion(assetManager.get("graphics/retro_map.png", Texture.class));
            Constants.retroGreenMapTextureRegion = new TextureRegion(assetManager.get("graphics/retro_map_green.png", Texture.class));

            Constants.carrierTextureRegion = new TextureRegion(textureAtlas.findRegion("Carrier"));
            Constants.helicopterTextureRegion = new TextureRegion(textureAtlas.findRegion("Helicopter"));
            Constants.rotatingBladesTextureRegion = new TextureRegion(textureAtlas.findRegion("Heli_Blade"));
            Constants.playerBulletTextureRegion = new TextureRegion(textureAtlas.findRegion("bullets"));
            Constants.factoryTextureRegion = new TextureRegion(textureAtlas.findRegion("factoryNew"));
            Constants.aaGunTextureRegion = new TextureRegion(textureAtlas.findRegion("AAGun"));
            Constants.enemyBulletTextureRegion = new TextureRegion(textureAtlas.findRegion("projectile"));
            Constants.enemyFighterTextureRegion = new TextureRegion(textureAtlas.findRegion("enemyFighter"));
            Constants.enemyBomberTextureRegion = new TextureRegion(textureAtlas.findRegion("bomber"));
            Constants.fighterBulletsTextureRegion = new TextureRegion(textureAtlas.findRegion("bullets2"));
            Constants.bombTextureRegion = new TextureRegion(textureAtlas.findRegion("emptyBomb"));
            Constants.cruiseMissileTexture = new TextureRegion(textureAtlas.findRegion("CruiseMissle"));
            Constants.enemyShipTextureRegion = new TextureRegion(textureAtlas.findRegion("ddg"));
            Constants.singlePixelTextureRegion = new TextureRegion(textureAtlas.findRegion("singlePoint"));
            Constants.helicopterIcon = new TextureRegion(textureAtlas.findRegion("heliIcon"));
            Constants.factoryIcon = new TextureRegion(textureAtlas.findRegion("factoryIcon"));
            Constants.carrierDirectionArrow = new TextureRegion(textureAtlas.findRegion("arrow"));
            Constants.introScreenSideApache = new TextureRegion(textureAtlas.findRegion("Sideviewapache"));
            Constants.introScreenSideApacheBlade = new TextureRegion(textureAtlas.findRegion("Sideviewapache_blade"));
            Constants.introScreenBackProps = new TextureRegion(textureAtlas.findRegion("sideBackProps"));
            Constants.introScreenFrontApache = new TextureRegion(textureAtlas.findRegion("Frontsideapache"));
            Constants.introScreenFrontBlade = new TextureRegion(textureAtlas.findRegion("Frontsideapache_blade"));
            Constants.introScreenTitle = new TextureRegion(textureAtlas.findRegion("title"));
            Constants.introScreenName = new TextureRegion(textureAtlas.findRegion("name"));
            Constants.playButton = new TextureRegion(textureAtlas.findRegion("play_button"));
            Constants.demoButton = new TextureRegion(textureAtlas.findRegion("demo_button"));
            Constants.scoresButton = new TextureRegion(textureAtlas.findRegion("scores_button"));
            Constants.settingsButton = new TextureRegion(textureAtlas.findRegion("settings_button"));
            Constants.bombButton = new TextureRegion(textureAtlas.findRegion("bomb_button"));
            Constants.fireButton = new TextureRegion(textureAtlas.findRegion("fire_button"));
            Constants.exitButton = new TextureRegion(textureAtlas.findRegion("exit_button"));
            Constants.pauseButton = new TextureRegion(textureAtlas.findRegion("pause_button"));
            Constants.mapButton = new TextureRegion(textureAtlas.findRegion("map_button"));
            Constants.newspaperLost = new TextureRegion(textureAtlas.findRegion("lost_newspaper"));
            Constants.newspaperCarrier = new TextureRegion(textureAtlas.findRegion("victory_carrier_lost"));
            Constants.newspaperMarginal = new TextureRegion(textureAtlas.findRegion("victory_not_perfect"));
            Constants.newspaperPerfect = new TextureRegion(textureAtlas.findRegion("victory_perfect"));
            Constants.radarIcon = new TextureRegion(textureAtlas.findRegion("radar_icon"));
            Constants.enemyBoat = new TextureRegion(textureAtlas.findRegion("enemy_boat"));
            Constants.enemyTank = new TextureRegion(textureAtlas.findRegion("enemy_tank"));

            //Sounds
            Constants.outOfFuelCrashSound = assetManager.get("sounds/jetcrashfuel.ogg", Sound.class);
            Constants.bulletHitLand = assetManager.get("sounds/landhit.ogg", Sound.class);
            Constants.AAGunFireSound = assetManager.get("sounds/AAGun.ogg", Sound.class);
            Constants.mediumExplosion = assetManager.get("sounds/explosion_medium.ogg", Sound.class);
            Constants.bigExplosion = assetManager.get("sounds/explosion_big.ogg", Sound.class);
            Constants.projectileImpact = assetManager.get("sounds/projectileImpact.ogg", Sound.class);
            Constants.fighterFire = assetManager.get("sounds/FighterGun.ogg", Sound.class);
            Constants.enemyCruise = assetManager.get("sounds/enemyCruise.ogg", Sound.class);
            Constants.cruiseOutOfFuel = assetManager.get("sounds/cruiseOutFuel.ogg", Sound.class);
            Constants.carrierAlarm = assetManager.get("sounds/carrierAlarm.ogg", Sound.class);
            Constants.bombsDroppingSound = assetManager.get("sounds/B2Bombing.ogg", Sound.class);
            Constants.fireCannonEffect = assetManager.get("sounds/machinegun1.ogg", Sound.class);
            Constants.singleBombDrop = assetManager.get("sounds/bombdrop2.mp3", Sound.class);
            Constants.fireMissileEffect = assetManager.get("sounds/MissleLaunch.ogg", Sound.class);
            Constants.m61Sound = assetManager.get("sounds/m61.ogg", Sound.class);
            Constants.radarBeep = assetManager.get("sounds/radar_beep.mp3", Sound.class);
            Constants.machineGunFire = assetManager.get("sounds/M60.ogg", Sound.class);

            // Long sounds (>100kB)
            Constants.youWinSound = assetManager.get("sounds/youwin.ogg", Music.class);
            Constants.oceanSound = assetManager.get("sounds/ocean.ogg", Music.class);
            Constants.drumsSound = assetManager.get("sounds/Drums2.ogg", Music.class);
            Constants.drumsOutcomeSound = assetManager.get("sounds/Drums.ogg", Music.class);
            Constants.takeOffSound = assetManager.get("sounds/helicopter_take_off.mp3", Music.class);
            Constants.chopperSound = assetManager.get("sounds/helicopter_cruise.mp3", Music.class);
            Constants.stopEngineSound = assetManager.get("sounds/powerdown_engine.mp3", Music.class);
            Constants.fireworksSound = assetManager.get("sounds/fireworks.mp3", Music.class);

            // Fonts
            Constants.scrollingCombatFont = assetManager.get("graphics/font1.fnt", BitmapFont.class);
            Constants.HUDFont = assetManager.get("graphics/font2.fnt", BitmapFont.class);
            Constants.HUDLargeFont = assetManager.get("graphics/font3.fnt", BitmapFont.class);
            Constants.HUDScoreFont = assetManager.get("graphics/font3.fnt", BitmapFont.class);

            // Particle Effects
            Constants.carrierWakeEffect = new ParticleEffect();
            Constants.carrierWakeEffect.load(Gdx.files.internal("particles/carrier_wake.p"),textureAtlas);
            Constants.enemyShipWakeEffect = new ParticleEffect();
            Constants.enemyShipWakeEffect.load(Gdx.files.internal("particles/enemy_ship_wake.p"),textureAtlas);
            Constants.enemyBoatWakeEffect = new ParticleEffect();
            Constants.enemyBoatWakeEffect.load(Gdx.files.internal("particles/enemy_boat_wake.p"),textureAtlas);
            Constants.bombFlashEffect = new ParticleEffect();
            Constants.bombFlashEffect.load(Gdx.files.internal("particles/bomb_flash.p"),textureAtlas);

            // Animations
            loadExplosionAnimations();
            loadRadarAnimation();

            // Skins
            Constants.skin = new Skin(Gdx.files.internal("skins/neutralizer-ui.json"));

            // Load preferences
            Constants.preferences = Gdx.app.getPreferences("RaidGame");
            Constants.userName =  Constants.preferences.getString("userName", "NotSet");
            Constants.quickMainMenu = Constants.preferences.getBoolean("quickMainMenu", false);
            Constants.showHelpScreen = Constants.preferences.getBoolean("showHelpScreen", true);
            Constants.volume = Constants.preferences.getFloat("volume", 0.75f);
            Constants.defaultMapID = Constants.preferences.getInteger("defaultMapID",3);

            // Next Screen
            if (Gdx.app.getType() != Application.ApplicationType.WebGL && Gdx.app.getType() != Application.ApplicationType.Applet) {
                if (Constants.showHelpScreen) {
                    Constants.game.setScreen(new HowToScreen());
                    //Constants.game.setScreen(new OutcomeScreen());
                    //Constants.game.setScreen(new HighScoresScreen());
                    //Constants.game.setScreen(new MainMenuScreen(false));
                    //Constants.game.setScreen(new GameScreen());
                } else {
                    if (Constants.quickMainMenu)
                        Constants.game.setScreen(new MainMenuScreen(true));
                    else
                        Constants.game.setScreen(new MainMenuScreen(false));
                }
            } else {
                Constants.game.setScreen(new HowToScreen());
            }
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, assetManager.getProgress(), 0.1f);

        // Update positions (and size) to match the percentage
        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);
        loadingBg.invalidate();

        // Show the loading screen
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        // Dispose the loading assets as we no longer need them
        assetManager.unload("graphics/loading.pack");
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
