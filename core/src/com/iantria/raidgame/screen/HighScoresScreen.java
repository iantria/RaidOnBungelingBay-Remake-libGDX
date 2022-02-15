package com.iantria.raidgame.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.iantria.raidgame.util.Score;
import com.iantria.raidgame.util.ScoreManager;
import com.iantria.raidgame.util.Statistics;


import java.util.LinkedList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class HighScoresScreen implements Screen {

    private Stage  topTableStage, exitButtonStage;
    private Image  exitButton;
    private Viewport viewport;
    private Table topTable;

    private ShaderProgram shader;
    private Texture shaderTexture;
    private SpriteBatch batch;
    private FrameBuffer fbo;
    private GlyphLayout layout;
    private ScoreManager scoreManager;
    private float shaderTime, aspectRatio;
    private int fboScale;
    private int updateTable = 0;
    private LinkedList<Score> scores;
    private Label.LabelStyle labelSmallStyle;

    @Override
    public void show() {
        Constants.stopAllSounds();

        // Do network stuff
        if (Constants.isNetworkAvailable) {
            scoreManager = new ScoreManager();
            scoreManager.retrieveHighScores();
            updateTable = 0;
        }

        Constants.drumsOutcomeSound.play();

        //Viewport
        aspectRatio = (float) Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
        Constants.WINDOW_HEIGHT = (int) ((int) Constants.WINDOW_WIDTH/aspectRatio);
        viewport = new FitViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        viewport.apply();

        //topTable
        Label.LabelStyle labelLargeStyle = new Label.LabelStyle();
        BitmapFont myFont = new BitmapFont(Constants.HUDLargeFont.getData().fontFile);
        labelLargeStyle.font = myFont;
        labelLargeStyle.font.getData().setScale(0.3f);
        labelLargeStyle.font.setUseIntegerPositions(false);
        labelLargeStyle.fontColor = Color.YELLOW;

        labelSmallStyle = new Label.LabelStyle();
        BitmapFont myFont2 = new BitmapFont(Constants.HUDFont.getData().fontFile);
        labelSmallStyle.font = myFont2;
        labelSmallStyle.font.setUseIntegerPositions(false);
        labelSmallStyle.font.getData().setScale(0.10f);
        labelSmallStyle.fontColor = Color.WHITE;

        Label titleLabel = new Label("HIGH SCORES", labelLargeStyle);

        topTable = new Table();
        topTable.setFillParent(true);
        //topTable.setDebug(true); // turn off on release

        //defaults
        topTable.top().left().setWidth(Constants.WINDOW_WIDTH - 5);
        topTable.defaults().center().pad(1,5,1,5);
        topTable.add(titleLabel).colspan(9).left();
        topTable.row().pad(1,5,6,5);
        topTable.add(new Label("Name", labelSmallStyle));
        topTable.add(new Label("Device", labelSmallStyle));
        topTable.add(new Label("Score", labelSmallStyle));
        topTable.add(new Label("Won?", labelSmallStyle));
        topTable.add(new Label("Game Time", labelSmallStyle));
        topTable.add(new Label("Carrier Lost", labelSmallStyle));
        topTable.add(new Label("Heli Lost", labelSmallStyle));
        topTable.add(new Label("Ship Completed", labelSmallStyle));
        topTable.add(new Label("Date", labelSmallStyle));
        topTableStage = new Stage(viewport);
        topTableStage.addActor(topTable);
        topTableStage.addAction(sequence(moveTo(5, 0 - Constants.WINDOW_HEIGHT),
                moveTo(5, Constants.WINDOW_HEIGHT - 5 - topTableStage.getHeight(), 1f)));

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
        Gdx.input.setInputProcessor(exitButtonStage);

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
        shader = new ShaderProgram(batch.getShader().getVertexShaderSource(), Gdx.files.internal("shaders/ocean_water.frag").readString());

        if (!shader.isCompiled()) {
            System.out.println("Error compiling shader: " + shader.getLog());
        }
        Constants.HUDFont.getData().setScale(0.15f);
        Constants.HUDFont.setUseIntegerPositions(false);
        layout = new GlyphLayout(Constants.HUDFont, "FPS: 120");
        //System.out.println("tex:" + shaderTexture.getWidth() + "x" + shaderTexture.getHeight() + "       screen:" + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight() + "    aspRatio:" + aspectRatio);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        fbo.begin();
        shaderTime += Gdx.graphics.getDeltaTime();
        batch.setShader(shader);
        shader.bind();
        shader.setUniformf("u_time", shaderTime);
        //shader.setUniformf("u_aspect_ratio", aspectRatio);
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
        batch.end();

        if (Constants.isNetworkAvailable
                && scoreManager.networkGetHighScores != null
                && scoreManager.networkGetHighScores.statusCode == 200
                && updateTable == 0){
            scores = scoreManager.getHighScoresList();
            for (Score s : scores){
                topTable.row();
                //topTable.add(new Label("" + convertToBoolean(s.isPlayer), labelSmallStyle));
                topTable.add(new Label(s.name, labelSmallStyle));
                topTable.add(new Label(s.deviceType, labelSmallStyle));
                topTable.add(new Label("" + s.score, labelSmallStyle));
                topTable.add(new Label(convertToBoolean(s.isWin), labelSmallStyle));

                int seconds = (int) (s.timeToWin) % 60;
                int minutes = (int) (s.timeToWin / (60f) % 60);
                int hours   = (int) (s.timeToWin / (60f * 60f) % 24);
                if (hours == 0)
                    topTable.add(new Label( minutes + " min " + seconds + " sec", labelSmallStyle));
                else
                    topTable.add(new Label(hours + " hr " + minutes + " min " + seconds + " sec", labelSmallStyle));

                topTable.add(new Label(convertToBoolean(s.isCarrierLost), labelSmallStyle));
                topTable.add(new Label("" + s.helicoptersLost, labelSmallStyle));
                topTable.add(new Label(convertToBoolean(s.isEnemyShipCompleted), labelSmallStyle));
                topTable.add(new Label(s.date, labelSmallStyle));
            }
            updateTable = 1;
        }

        topTableStage.act();
        topTableStage.draw();

        exitButtonStage.act();
        exitButtonStage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Constants.fireworksSound.stop();
            Constants.fireworksSound.setLooping(false);
            exitButton.removeListener(exitButton.getListeners().first());
            Constants.game.setScreen(new IntroScreen(true));
        }
    }

    @Override
    public void resize(int width, int height) {
        exitButtonStage.getViewport().update(width, height, true);
        topTableStage.getViewport().update(width, height, true);
        viewport.apply();
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
        topTableStage.dispose();
    }

    public String convertToBoolean(int i) {
        if (i == 1)
            return "YES";
        else
            return "NO";
    }
}
