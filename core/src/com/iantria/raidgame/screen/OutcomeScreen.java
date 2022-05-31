package com.iantria.raidgame.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Network;
import com.iantria.raidgame.util.ScoreManager;
import com.iantria.raidgame.util.Statistics;
import com.rafaskoberg.gdx.typinglabel.TypingConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class OutcomeScreen implements Screen {

    private enum Outcome{
        PERFECT, WIN_CARRIER_LOST, YOU_LOSE, MARGINAL
    }
    private Outcome outcome;

    private Stage newspaperStage, topTableStage, exitButtonStage, scoresButtonStage;
    private Image newsPaperImage, exitButton, scoresButton;
    private Viewport viewport;
    private Table topTable;

    private String outcomeTitle;
    private ShaderProgram shader;
    private Texture shaderTexture;
    private SpriteBatch batch;
    private FrameBuffer fbo;
    private GlyphLayout layout, rankLayout;
    private Network networkSaveUsage;
    private ScoreManager scoreManager;
    private InputMultiplexer inputMultiplexer;
    private float shaderTime, aspectRatio;
    private int fboScale;

    @Override
    public void show() {
        Constants.stopAllSounds();

        // Do network stuff
        if (Constants.isNetworkAvailable) {
            networkSaveUsage = new Network(Constants.NETWORK_SERVICES_USAGE_API, "service=2");
            scoreManager = new ScoreManager();
            scoreManager.setGameValues();
            scoreManager.saveScore();
        }

        Constants.drumsOutcomeSound.play();
        TypingConfig.DEFAULT_SPEED_PER_CHAR = 0.1f;
        TypingConfig.DEFAULT_WAIT_VALUE = 1.0f;
        TypingConfig.CHAR_LIMIT_PER_FRAME = 1;

        if (Statistics.numberOfLivesLost == Constants.NUMBER_OF_LIVES){
            // You lose
            newsPaperImage = new Image(Constants.newspaperLost);
            outcome = Outcome.YOU_LOSE;
            outcomeTitle = "{COLOR=RED}{WAIT}{SICK}YOU HAVE BEEN DEFEATED!";
        } else if (!Statistics.carrierSurvived) {
            // Win but carrier lost
            newsPaperImage = new Image(Constants.newspaperCarrier);
            outcome = Outcome.WIN_CARRIER_LOST;
            outcomeTitle = "{COLOR=YELLOW}{WAIT}{WAVE}YOU WON, CARRIER LOST!";
        } else if (Statistics.numberOfLivesLost == 0){
            // Perfection
            newsPaperImage = new Image(Constants.newspaperPerfect);
            outcome = Outcome.PERFECT;
            outcomeTitle = "{COLOR=GREEN}{WAIT}{WAVE}PERFECTION!";
        } else {
            // Marginal victory
            newsPaperImage = new Image(Constants.newspaperMarginal);
            outcome = Outcome.MARGINAL;
            outcomeTitle = "{COLOR=YELLOW}{WAIT}{WAVE}MARGINAL VICTORY";
        }

        if (outcome != Outcome.YOU_LOSE){
            Constants.fireworksSound.setLooping(true);
            Constants.fireworksSound.setVolume(0.5f);
            Constants.fireworksSound.play();
        }

        //Viewport
        aspectRatio = (float) Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
        Constants.WINDOW_HEIGHT = (int) (Constants.WINDOW_WIDTH/aspectRatio);
        viewport = new FitViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        viewport.apply();

        //Newspaper
        newsPaperImage.setScale(0.25f);
        newspaperStage = new Stage(viewport);
        newspaperStage.addActor(newsPaperImage);
        newspaperStage.addAction(sequence(scaleTo(0,0),moveTo(0,Constants.WINDOW_HEIGHT),
                parallel(rotateBy(1440,2f),
                        moveTo(Constants.WINDOW_WIDTH - newsPaperImage.getWidth()*newsPaperImage.getScaleX(), 0, 2f),
                        scaleTo(1f,1f, 2f))));

        //topTable
        Label.LabelStyle labelLargeStyle = new Label.LabelStyle();
        BitmapFont myFont = new BitmapFont(Constants.HUDLargeFont.getData().fontFile);
        labelLargeStyle.font = myFont;
        labelLargeStyle.font.getData().setScale(0.3f);
        labelLargeStyle.font.setUseIntegerPositions(false);

        Label.LabelStyle labelSmallStyle = new Label.LabelStyle();
        BitmapFont myFont2 = new BitmapFont(Constants.HUDFont.getData().fontFile);
        labelSmallStyle.font = myFont2;
        labelSmallStyle.font.setUseIntegerPositions(false);
        labelSmallStyle.font.getData().setScale(0.125f);
        labelSmallStyle.fontColor = Color.WHITE;

        TypingLabel titleLabel = new TypingLabel (outcomeTitle, labelLargeStyle);

        topTable = new Table();
        topTable.setFillParent(true);
        //topTable.setDebug(true); // turn off on release

        //defaults
        topTable.top().left().setWidth(Constants.WINDOW_WIDTH - newsPaperImage.getWidth()*newsPaperImage.getScaleX() - 5);
        topTable.columnDefaults(1).width(50).padLeft(2f);
        topTable.columnDefaults(3).padLeft(2f);
        topTable.columnDefaults(0).right();
        topTable.columnDefaults(1).left();
        topTable.columnDefaults(2).right();
        topTable.columnDefaults(3).left();

        topTable.add(titleLabel).colspan(4).left().padBottom(3);
        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Score:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}{RAINBOW}" + Statistics.score, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Game Time:", labelSmallStyle));

        int seconds = (int) (Statistics.gameTime) % 60;
        int minutes = (int) (Statistics.gameTime / (60f) % 60);
        int hours   = (int) (Statistics.gameTime / (60f * 60f) % 24);
        if (hours == 0)
            topTable.add(new TypingLabel ( "{WAIT}{RAINBOW}" + minutes + " min " + seconds + " sec", labelSmallStyle));
        else
            topTable.add(new TypingLabel ("{WAIT}{RAINBOW}" + hours + " hr " + minutes + " min " + seconds + " sec", labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Carrier Status:", labelSmallStyle));
        if (Statistics.carrierSurvived)
            topTable.add(new TypingLabel ("{WAIT}{COLOR=GREEN}Survived", labelSmallStyle));
        else
            topTable.add(new TypingLabel ("{WAIT}{COLOR=RED}Destroyed", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Bombs Dropped:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfBombsDropped, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Enemy Ship was:", labelSmallStyle));
        if (Statistics.enemyShipWasCompleted)
            topTable.add(new TypingLabel ("{WAIT}{COLOR=RED}Completed", labelSmallStyle));
        else
            topTable.add(new TypingLabel ("{WAIT}{COLOR=GREEN}Not Completed", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Bombs Hit:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfBombsLanded, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Helicopters Lost:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfLivesLost, labelSmallStyle));

        float f;
        if (Statistics.numberOfBombsDropped !=0)
            f = ((float)Statistics.numberOfBombsLanded/(float)Statistics.numberOfBombsDropped)*100f;
        else
            f = 0;

        topTable.add(new TypingLabel ("{WAIT}Bomb Accuracy:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + (float) ((int) (f*100f)/100f) + "%", labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Bombers Destroyed:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfBombersDestroyed, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}My Cannon Fired:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfCannonRoundsFired, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Fighters Destroyed:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfFightersDestroyed, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}My Cannon Hits:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfCannonRoundsLanded, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Fighter Cannon Fired:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfTimesFighterFired, labelSmallStyle));
        if (Statistics.numberOfCannonRoundsFired != 0) f = ((float)Statistics.numberOfCannonRoundsLanded/(float)Statistics.numberOfCannonRoundsFired)*100;
        else f = 0;

        topTable.add(new TypingLabel ("{WAIT}My Cannon Accuracy:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + (float) ((int) (f*100f)/100f) + "%", labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Fighters Hit You:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfTimesHitByFighter, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Total Helicopter Damage:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.amountOfDamageTaken, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}AAGuns Destroyed:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfAAGunsDestroyed, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Total Carrier Damage:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.amountOfCarrierDamageTaken, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}AAGuns Fired:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfTimesAAGunFired, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Total Damage Taken:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + (int) (Statistics.amountOfDamageTaken + Statistics.amountOfCarrierDamageTaken), labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}AAGuns Hit You:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfTimesHitByAAGun, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Total Damage Dealt:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.amountOfDamageDealt, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Cruise Missiles Fired:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfTimesCruiseMissileFired, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Damage Out/In Ratio:", labelSmallStyle));
        f =  (float)Statistics.amountOfDamageDealt / (float)(Statistics.amountOfDamageTaken + Statistics.amountOfCarrierDamageTaken + 1);
        topTable.add(new TypingLabel ("{WAIT}" + (float) ((int) (f*100f)/100f) + "" , labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Cruise Missile Hit You:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfTimesHitByCruiseMissile, labelSmallStyle));
        f = (float)Statistics.amountOfDamageDealt / (float)(Statistics.gameTime + 1f);
        topTable.add(new TypingLabel ("{WAIT}Damage/Second:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + (float) ((int) (f*100f)/100f) + "", labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Cruise Missiles Destroyed:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfCruiseMissilesDestroyed, labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}Carrier Landings:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" +  "" + Statistics.numberOfCarrierLandings, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Factories Destroyed:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfFactoriesDestroyed, labelSmallStyle));

        topTable.add(new TypingLabel ("{WAIT}Fuel Used:", labelSmallStyle));
        topTable.add(new TypingLabel ( "{WAIT}" + Statistics.amountOfFuelUsed, labelSmallStyle));

        topTable.row();
        topTable.add(new TypingLabel ("{WAIT}Radar Sites Destroyed:", labelSmallStyle));
        topTable.add(new TypingLabel ("{WAIT}" + Statistics.numberOfRadarsDestroyed, labelSmallStyle));

        topTable.add(new TypingLabel ("{WAIT}", labelSmallStyle));
        topTable.add(new TypingLabel ( "{WAIT}", labelSmallStyle));

        topTableStage = new Stage(viewport);
        topTableStage.addActor(topTable);
        topTableStage.addAction(sequence(moveTo(5, Constants.WINDOW_HEIGHT - 5 - topTableStage.getHeight(),0f)));

        inputMultiplexer = new InputMultiplexer();
        exitButtonStage = new Stage(viewport);
        exitButton = new Image(Constants.exitButton);
        exitButton.setScale(0.2f);
        exitButton.setColor(1,1,1,1);
        exitButton.setPosition(Constants.WINDOW_WIDTH - exitButton.getWidth()*exitButton.getScaleX(),
                Constants.WINDOW_HEIGHT - exitButton.getHeight()*exitButton.getScaleY());
        exitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Constants.fireworksSound.stop();
                Constants.fireworksSound.setLooping(false);
                scoresButton.removeListener(scoresButton.getListeners().first());
                exitButton.removeListener(exitButton.getListeners().first());
                Constants.game.setScreen(new IntroScreen(true));
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                super.touchUp(event, x, y, pointer, button);
            }
        });
        exitButton.setBounds(exitButton.getX(), exitButton.getY(), exitButton.getWidth(), exitButton.getHeight());
        exitButton.setTouchable(Touchable.enabled);
        exitButtonStage.addActor(exitButton);

        scoresButtonStage = new Stage(viewport);
        scoresButton = new Image(Constants.scoresButton);
        scoresButton.setScale(0.15f);
        scoresButton.setColor(1,1,1,1);
        scoresButton.setPosition(Constants.WINDOW_WIDTH - scoresButton.getWidth()*scoresButton.getScaleX() - 5,
                Constants.WINDOW_HEIGHT*0.65f - scoresButton.getHeight()*scoresButton.getScaleY()/2f);
        scoresButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Constants.fireworksSound.stop();
                Constants.fireworksSound.setLooping(false);
                exitButton.removeListener(exitButton.getListeners().first());
                scoresButton.removeListener(scoresButton.getListeners().first());
                Constants.game.setScreen(new HighScoresScreen());
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button){
                super.touchUp(event, x, y, pointer, button);
            }
        });
        scoresButton.setBounds(scoresButton.getX(), scoresButton.getY(), scoresButton.getWidth(), scoresButton.getHeight());
        scoresButton.setTouchable(Touchable.enabled);
        scoresButtonStage.addActor(scoresButton);

        // Set button listeners
        inputMultiplexer.addProcessor(scoresButtonStage);
        inputMultiplexer.addProcessor(exitButtonStage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        // FBO, and Shader
        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS){
            fboScale = 8;
        } else if (Gdx.app.getType() == Application.ApplicationType.Applet || Gdx.app.getType() == Application.ApplicationType.WebGL){
            fboScale = 6 ;
        } else {
            fboScale = 4 ;
        }

        shaderTime = 0;
        batch = new SpriteBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth() / fboScale, Gdx.graphics.getHeight() / fboScale, false);
        Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() / fboScale, Gdx.graphics.getHeight() / fboScale, Pixmap.Format.RGBA8888);
        shaderTexture = new Texture(pixmap);
        pixmap.dispose();
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(batch.getShader().getVertexShaderSource(), Gdx.files.internal("shaders/fireworks.frag").readString());

        if (!shader.isCompiled()) {
            System.out.println("Error compiling shader: " + shader.getLog());
        }
        Constants.HUDFont.getData().setScale(0.15f);
        Constants.HUDFont.setUseIntegerPositions(false);
        layout = new GlyphLayout(Constants.HUDFont, "FPS: 120");

        Constants.HUDLargeFont.getData().setScale(0.15f);
        Constants.HUDLargeFont.setUseIntegerPositions(false);
        Constants.HUDLargeFont.setColor(Color.YELLOW);
        rankLayout = new GlyphLayout(Constants.HUDLargeFont, "RANK: 100");
        //System.out.println("tex:" + shaderTexture.getWidth() + "x" + shaderTexture.getHeight() + "       screen:" + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight() + "    aspRatio:" + aspectRatio);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!(outcome == Outcome.YOU_LOSE)) {
            viewport.apply();
            batch.setProjectionMatrix(viewport.getCamera().combined);
            fbo.begin();
            shaderTime += Gdx.graphics.getDeltaTime();
            batch.setShader(shader);
            shader.bind();
            shader.setUniformf("u_time", shaderTime);
            shader.setUniformf("u_aspect_ratio", aspectRatio);
            batch.begin();
            batch.draw(shaderTexture,0,0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            batch.end();
            fbo.end();
            batch.setShader(null);
            batch.begin();
            batch.draw(fbo.getColorBufferTexture(),0,0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            Constants.HUDFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
                    Constants.WINDOW_WIDTH - exitButton.getWidth()*exitButton.getScaleX() - layout.width - 2,
                    Constants.WINDOW_HEIGHT - layout.height);

            if (Constants.isNetworkAvailable && scoreManager.networkSaveScore != null && scoreManager.networkSaveScore.statusCode == 200) {
                rankLayout.setText(Constants.HUDLargeFont, "RANK: " + scoreManager.networkSaveScore.result);
                Constants.HUDLargeFont.draw(batch, "RANK: " + scoreManager.networkSaveScore.result,
                        Constants.WINDOW_WIDTH - scoresButton.getWidth()*scoresButton.getScaleX() + (scoresButton.getWidth()*scoresButton.getScaleX() - rankLayout.width)/2f - 5,
                        Constants.WINDOW_HEIGHT*0.65f + scoresButton.getHeight()*scoresButton.getScaleY() + 4 + rankLayout.height);
            }
            batch.end();
        }

        newspaperStage.act();
        newspaperStage.draw();

        topTableStage.act();
        topTableStage.draw();

        exitButtonStage.act();
        exitButtonStage.draw();

        if (Constants.isNetworkAvailable && scoreManager.networkSaveScore != null && scoreManager.networkSaveScore.statusCode == 200){
            scoresButtonStage.act();
            scoresButtonStage.draw();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Constants.fireworksSound.stop();
            Constants.fireworksSound.setLooping(false);
            exitButton.removeListener(exitButton.getListeners().first());
            scoresButton.removeListener(scoresButton.getListeners().first());
            Constants.game.setScreen(new IntroScreen(true));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
        exitButtonStage.getViewport().update(width, height);
        topTableStage.getViewport().update(width, height);
        newspaperStage.getViewport().update(width, height);
        scoresButtonStage.getViewport().update(width, height);
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
    public void dispose() {
        exitButtonStage.dispose();;
        newspaperStage.dispose();
        topTableStage.dispose();
        scoresButtonStage.dispose();
        fbo.dispose();
        shader.dispose();
        shaderTexture.dispose();
    }
}
