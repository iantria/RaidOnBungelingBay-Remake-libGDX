package com.iantria.raidgame.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.iantria.raidgame.entity.LoadingBar;
import com.iantria.raidgame.RaidGame;
import com.iantria.raidgame.util.Constants;

/**
 * @author Mats Svensson
 */
public class LoadingScreen implements Screen {

    private Stage stage;

    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;

    private float startX, endX;
    private float percent;

    public AssetManager assetManager = new AssetManager();
    private Actor loadingBar;
    private RaidGame game;
    private TextureAtlas textureAtlas;

    public LoadingScreen(RaidGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
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
        assetManager.load("graphics/images.atlas", TextureAtlas.class);
        assetManager.load("graphics/ddgIcon.gif", Texture.class);

        //Sounds
        assetManager.load("sounds/jetcrashfuel.ogg", Music.class);
        assetManager.load("sounds/landhit.ogg", Music.class);
        assetManager.load("sounds/AAGun.ogg", Music.class);
        assetManager.load("sounds/explosion_medium.ogg", Music.class);
        assetManager.load("sounds/explosion_big.ogg", Music.class);
        assetManager.load("sounds/projectileImpact.ogg", Music.class);
        assetManager.load("sounds/FighterGun.ogg", Music.class);
        assetManager.load("sounds/enemyCruise.ogg", Music.class);
        assetManager.load("sounds/cruiseOutFuel.ogg", Music.class);
        assetManager.load("sounds/youwin.ogg", Music.class);
        assetManager.load("sounds/carrierAlarm.ogg", Music.class);
        assetManager.load("sounds/B2Bombing.ogg", Music.class);
        assetManager.load("sounds/machinegun1.ogg", Music.class);
        assetManager.load("sounds/bombdrop2.mp3", Music.class);
        assetManager.load("sounds/MissleLaunch.ogg", Music.class);
        assetManager.load("sounds/Drums2.ogg", Music.class);
        assetManager.load("sounds/helicopter_take_off.mp3", Music.class);
        assetManager.load("sounds/helicopter_cruise.mp3", Music.class);
        assetManager.load("sounds/powerdown_engine.mp3", Music.class);
        assetManager.load("sounds/m61.ogg", Music.class);
        assetManager.load("sounds/ocean.ogg", Music.class);

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
        TextureRegion[] textureLine;

        for (int i = 0; i < 8; i++) {
            textureLine = new TextureRegion[16];
            for (int j = 0; j < 16; j++) {
                textureLine[j] = textureLines[i][j];
            }
            Constants.explosionAnimations[i] = new Animation<>(1 / 16f, textureLine);  // todo
        }
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

        if (assetManager.update()) { // Load some, will return true if done loading
            //Textures
            textureAtlas = assetManager.get("graphics/images.atlas", TextureAtlas.class);
            Constants.enemyShipIcon = new TextureRegion(assetManager.get("graphics/ddgIcon.gif", Texture.class));
            Constants.mapTextureRegion = new TextureRegion(textureAtlas.findRegion("modernmap"));
            Constants.retroMapTextureRegion = new TextureRegion(textureAtlas.findRegion("retro_map"));
            Constants.retroGreenMapTextureRegion = new TextureRegion(textureAtlas.findRegion("retro_map_green"));
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

            //Sounds
            Constants.outOfFuelCrashSound = assetManager.get("sounds/jetcrashfuel.ogg", Music.class);
            Constants.bulletHitLand = assetManager.get("sounds/landhit.ogg", Music.class);
            Constants.AAGunFireSound = assetManager.get("sounds/AAGun.ogg", Music.class);
            Constants.mediumExplosion = assetManager.get("sounds/explosion_medium.ogg", Music.class);
            Constants.bigExplosion = assetManager.get("sounds/explosion_big.ogg", Music.class);
            Constants.projectileImpact = assetManager.get("sounds/projectileImpact.ogg", Music.class);
            Constants.fighterFire = assetManager.get("sounds/FighterGun.ogg", Music.class);
            Constants.enemyCruise = assetManager.get("sounds/enemyCruise.ogg", Music.class);
            Constants.cruiseOutOfFuel = assetManager.get("sounds/cruiseOutFuel.ogg", Music.class);
            Constants.youWin = assetManager.get("sounds/youwin.ogg", Music.class);
            Constants.carrierAlarm = assetManager.get("sounds/carrierAlarm.ogg", Music.class);
            Constants.bombsDroppingSound = assetManager.get("sounds/B2Bombing.ogg", Music.class);
            Constants.fireCannonEffect = assetManager.get("sounds/machinegun1.ogg", Music.class);
            Constants.singleBombDrop = assetManager.get("sounds/bombdrop2.mp3", Music.class);
            Constants.fireMissileEffect = assetManager.get("sounds/MissleLaunch.ogg", Music.class);
            Constants.drumsSound = assetManager.get("sounds/Drums2.ogg", Music.class);
            Constants.takeOffSound = assetManager.get("sounds/helicopter_take_off.mp3", Music.class);
            Constants.chopperSound = assetManager.get("sounds/helicopter_cruise.mp3", Music.class);
            Constants.stopEngine = assetManager.get("sounds/powerdown_engine.mp3", Music.class);
            Constants.m61Sound = assetManager.get("sounds/m61.ogg", Music.class);
            Constants.oceanSound = assetManager.get("sounds/ocean.ogg", Music.class);

            // Fonts
            Constants.scrollingCombatFont = assetManager.get("graphics/font1.fnt", BitmapFont.class);
            Constants.HUDFont = assetManager.get("graphics/font2.fnt", BitmapFont.class);
            Constants.HUDLargeFont = assetManager.get("graphics/font3.fnt", BitmapFont.class);

            loadExplosionAnimations();

            if (Gdx.app.getType().equals(Application.ApplicationType.WebGL)) {
                if (Gdx.input.isTouched()) {
                    // So that audio can work
                    game.setScreen(new IntroScreen(game, false));
                }
            } else {
                game.setScreen(new IntroScreen(game, false));
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

    }
}
