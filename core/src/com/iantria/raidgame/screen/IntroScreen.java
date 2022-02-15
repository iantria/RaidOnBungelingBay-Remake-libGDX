package com.iantria.raidgame.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.iantria.raidgame.util.AnimatedImage;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class IntroScreen implements Screen {

    private Image apache;
    private Image blade;
    private Image blade2;
    private Image blade3;
    private Image front;
    private Image frontBlade;
    private Image frontBlade2;
    private Image playButton;
    private Image demoButton;
    private AnimatedImage fireAnim;

    private int x = 1;
    private int y = 1;
    private float z;
    private float shaderTime;
    private int introStep;
    private float scale;
    private float scale2;
    private float rot;
    private float pan;
    private float vol;
    private float aspectRatio;
    private long soundID = -1;

    private Stage stage;
    private Group group;
    private Stage stage2;
    private Group group2;
    private Stage stage3;
    private Group group3;
    private Stage playButtonStage;
    private Stage demoButtonStage;
    private Sprite s;
    private Viewport viewport;
    private ShaderProgram shader;
    private Texture shaderTexture;
    private SpriteBatch batch;
    private FrameBuffer fbo;
    private int fboScale;
    private GlyphLayout layout;
    private InputMultiplexer inputMultiplexer;

    public IntroScreen( boolean isQuick) {
        //Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        Gdx.input.setCursorCatched(false);
        x = 0;
        if (isQuick) introStep = 4;
        else introStep = 1;

        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS){
            fboScale = 4;
        } else if (Gdx.app.getType() == Application.ApplicationType.Applet || Gdx.app.getType() == Application.ApplicationType.WebGL){
            fboScale = 2 ;
        } else {
            fboScale = 2 ;
        }
     }

    @Override
    public void show() {
        Constants.stopAllSounds();
        soundID = -1;
        Statistics.resetScores();
        float aspectRatio = (float)Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
        Constants.WINDOW_HEIGHT = (int) (Constants.WINDOW_WIDTH/aspectRatio);
        viewport = new FitViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        viewport.apply();

        front = new Image(Constants.introScreenFrontApache);
        frontBlade = new Image(Constants.introScreenFrontBlade);
        frontBlade2 = new Image(Constants.introScreenFrontBlade);
        fireAnim = new AnimatedImage(new Animation<TextureRegion>(Constants.explosionAnimations[3].getFrameDuration(),
                Constants.explosionAnimations[5].getKeyFrames()), 5f);
        fireAnim.setScale(1.5f);

        frontBlade.setPosition(0, 375);
        frontBlade.setOrigin(583, 0);
        frontBlade2.setPosition(0, 375);
        frontBlade2.setOrigin(583, 0);

        stage3 = new Stage(viewport);;
        group3 = new Group();
        group3.addActor(front);
        group3.addActor(frontBlade);
        group3.addActor(frontBlade2);

        group3.setScale(0.25f);
        group3.addAction(sequence(moveTo(Constants.WINDOW_WIDTH/2f - frontBlade.getWidth()/2f*0.25f , -Constants.WINDOW_HEIGHT),
                moveTo(Constants.WINDOW_WIDTH/2f - frontBlade.getWidth()/2f*0.25f, Constants.WINDOW_HEIGHT/2f - front.getHeight()*0.25f/2f, 6f),
                delay(2.5f)));
        stage3.addActor(group3);
        inputMultiplexer = new InputMultiplexer();

        // Play Button
        playButton = new Image(Constants.playButton);
        playButton.setScale(0.15f);
        playButton.setBounds(playButton.getX(), playButton.getY(), playButton.getWidth(), playButton.getHeight());
        playButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Constants.chopperSound.setLooping(false);
                Constants.chopperSound.stop();
                Constants.m61Sound.stop();
                Statistics.resetScores();
                Constants.isPlayer = true;
                playButton.removeListener(playButton.getListeners().first());
                demoButton.removeListener(demoButton.getListeners().first());
                Constants.game.setScreen(new GameScreen());
                return true;
            }
        });

        playButtonStage = new Stage(viewport);
        playButtonStage.addAction(sequence(fadeOut(0f),
                moveTo(Constants.WINDOW_WIDTH - playButton.getWidth() * playButton.getScaleX() - 10, Constants.WINDOW_HEIGHT / 2 - 20),
                delay(5f), fadeIn(1.5f)));

        playButtonStage.addActor(playButton);
        playButton.setTouchable(Touchable.enabled);
        inputMultiplexer.addProcessor(playButtonStage);

        // Demo Button
        demoButton = new Image(Constants.demoButton);
        demoButton.setScale(0.15f);
        demoButton.setBounds(demoButton.getX(), demoButton.getY(), demoButton.getWidth(), demoButton.getHeight());
        demoButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Constants.chopperSound.setLooping(false);
                Constants.chopperSound.stop();
                Constants.m61Sound.stop();
                Statistics.resetScores();
                Constants.isPlayer = false;
                playButton.removeListener(playButton.getListeners().first());
                demoButton.removeListener(demoButton.getListeners().first());
                Constants.game.setScreen(new GameScreen());
                return true;
            }
        });

        demoButtonStage = new Stage(viewport);
        demoButtonStage.addAction(sequence(fadeOut(0f),moveTo(10, Constants.WINDOW_HEIGHT/2f - 20),
                delay(5f),fadeIn(1.5f)));
        demoButtonStage.addActor(demoButton);
        demoButton.setTouchable(Touchable.enabled);
        inputMultiplexer.addProcessor(demoButtonStage);

        s = new Sprite(Constants.introScreenSideApache);
        s.flip(true,false);

        apache = new Image(s);
        blade = new Image(Constants.introScreenSideApacheBlade);
        blade2 = new Image(Constants.introScreenSideApacheBlade);
        blade3 = new Image(Constants.introScreenBackProps);

        blade.setPosition(7, 250);
        blade.setOrigin(394, 0);
        blade2.setPosition(7, 250);
        blade2.setOrigin(394, 0);
        blade3.setPosition(945, 195);
        blade3.setOrigin( 105, 90);

        stage = new Stage(viewport);;
        group = new Group();
        group.addActor(apache);
        group.addActor(blade);
        group.addActor(blade2);
        group.addActor(blade3);
        group.setScale(0.1f);
        group.addAction(sequence(moveTo(Constants.WINDOW_WIDTH+Constants.WINDOW_WIDTH/4, Constants.WINDOW_HEIGHT*0.65f),
                moveTo(-Constants.WINDOW_WIDTH/2f,Constants.WINDOW_HEIGHT*0.65f,16f)));
        stage.addActor(group);
        z=0;

        // FBO, and Shader
        shaderTime = 0;
        batch = new SpriteBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth()/fboScale, Gdx.graphics.getHeight()/fboScale, false);
        Pixmap pixmap = new Pixmap( Gdx.graphics.getWidth()/fboScale, Gdx.graphics.getHeight()/fboScale, Pixmap.Format.RGBA8888 );
        shaderTexture = new Texture(pixmap);
        pixmap.dispose();
        shader = new ShaderProgram(batch.getShader().getVertexShaderSource(), Gdx.files.internal("shaders/slow_clouds.frag").readString());
        ShaderProgram.pedantic = false;
        if (!shader.isCompiled()){
            System.out.println("Error compiling shader: " + shader.getLog());
        }
        Constants.HUDFont.getData().setScale(0.15f);
        Constants.HUDFont.setUseIntegerPositions(false);
        layout = new GlyphLayout(Constants.HUDFont, "FPS: 120");

        //System.out.println("tex:" + shaderTexture.getWidth() + "x" + shaderTexture.getHeight() + "       screen:" +Gdx.graphics.getWidth() +"x"+Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.53f, 0.81f, 0.92f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // fboViewport.apply();
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        fbo.begin();
        shaderTime += Gdx.graphics.getDeltaTime();
        batch.setShader(shader);
        shader.bind();
        shader.setUniformf("u_time", shaderTime);
        batch.begin();
        batch.draw(shaderTexture,0,0,Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        batch.end();
        fbo.end();
        batch.setShader(null);

        batch.begin();
        batch.draw(fbo.getColorBufferTexture(),0,0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        Constants.HUDFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), Constants.WINDOW_WIDTH - layout.width - 2, Constants.WINDOW_HEIGHT - layout.height);
        batch.end();

        this.scale += delta * y * 5f;
        scale2 += delta * x * 15f;
        rot += delta * 725f;
        blade3.setRotation(rot);

        if (this.scale <= 0f ) {
            y = 1;
            this.scale = 0f;
        }
        if (this.scale >= 0.5f) {
            y = -1;
            this.scale = 0.5f;
        }
        if (scale2 <= 0.5f ) {
            x = 1;
            scale2 = 0.5f;
        }
        if (scale2 >= 1.1f) {
            x = -1;
            scale2 = 1.1f;
        }
        blade.setScale(this.scale, 1f);
        blade2.setScale(scale2, 1f);
        frontBlade.setScale(this.scale, 1f);
        frontBlade2.setScale(scale2, 1f);

         // 1st Pass
        if (group.getActions().size != 0 && introStep == 1) {
            stage.getViewport().apply();
            stage.act();
            stage.draw();

            if (!Constants.chopperSound.isPlaying()) {
                Constants.chopperSound.setLooping(true);
                Constants.chopperSound.play();
            }

            pan += delta/8f;

            if ((1f - pan) < 1f &&  (1f - pan) > 0f) vol = pan;
            else vol = (1f + (1f-pan));
            if (vol < 0) vol = 0f;
            Constants.chopperSound.setPan(1 - pan, vol*0.3f);
        }

        // Setup next pass
        if (group.getActions().size == 0 && introStep == 1) {
            introStep = 2;
            s.flip(true, false);
            apache = new Image(s);
            stage2 = new Stage(viewport);;
            group2 = new Group();
            group2.addActor(apache);
            group2.addActor(blade);
            group2.addActor(blade2);
            group2.addActor(blade3);
            group2.setScale(0.2f);

            blade.setPosition(394-7, 250);
            blade.setOrigin(394-7, 0);
            blade2.setPosition(394-7, 250);
            blade2.setOrigin(394-7, 0);
            blade3.setPosition(2, 195);
            blade3.setOrigin( 105, 90);

            group2.addAction(sequence(moveTo(-Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT*0.4f),
                    moveTo(Constants.WINDOW_WIDTH*2,Constants.WINDOW_HEIGHT*.4f,10f)));
            stage2.addActor(group2);
            introStep = 3;
            vol = 0;
            pan = -1;
        }

        // 2nd Pass
        if (introStep == 3) {
            stage2.getViewport().apply();
            stage2.act();
            stage2.draw();

            if (!Constants.chopperSound.isPlaying()) {
                Constants.chopperSound.setLooping(true);
                Constants.chopperSound.play();
            }

            pan += delta/5f;

            if ( pan > -1f && pan < 0f) vol = pan + 1;
            else vol = 1 - pan;
            if (vol < 0) vol = 0f;

            Constants.chopperSound.setPan(pan, vol*0.6f);
        }
        // Setup next pass
        if (introStep == 3 && group2.getActions().size == 0) {
            introStep = 4;
            vol = 0;
            pan = 0;
        }
        if (introStep == 4 && group3.getActions().size != 0){
            Constants.oceanSound.stop();
            stage3.getViewport().apply();
            stage3.act();
            stage3.draw();

            if (!Constants.chopperSound.isPlaying()) {
                Constants.chopperSound.setLooping(true);
                Constants.chopperSound.play();
            }

            vol += delta/3f;
            if (vol > 1) vol = 1f;
            Constants.chopperSound.setPan(0,vol);
        }
        if (group3.getActions().size == 0 && introStep == 4){
            fireAnim.setPosition(548, 0);
            group3.addActor(fireAnim);
            stage3.getViewport().apply();
            stage3.act();
            stage3.draw();
            z += delta;
            if (soundID == -1) {
                soundID = Constants.m61Sound.play(0.25f);
                Constants.m61Sound.setLooping(soundID, true);
            }
            if (z > 3f){
                Constants.m61Sound.stop(soundID);
                group3.removeActor(fireAnim);
                introStep = 5;
                stage = new Stage(viewport);
                stage2 = new Stage(viewport);
                Image title = new Image(Constants.introScreenTitle);
                Image name = new Image(Constants.introScreenName);
                title.setScale(0.4f);
                name.setScale(0.25f);

                stage.addActor(title);
                stage.addAction(sequence(moveTo(1,Constants.WINDOW_HEIGHT + 5),delay(1f), moveTo(1, 5, 2f)));

                stage2.addActor(name);
                stage2.addAction(sequence(moveTo(10,Constants.WINDOW_HEIGHT + 15), delay(4f),
                        moveTo(10,Constants.WINDOW_HEIGHT - name.getHeight()*name.getScaleY() - 5, 1f)));
            }
        }
         if (introStep == 5) {
            stage3.getViewport().apply();
            stage3.draw();
            stage.getViewport().apply();
            stage.act();
            stage.draw();
            stage2.getViewport().apply();
            stage2.act();
            stage2.draw();
            playButtonStage.getViewport().apply();
            playButtonStage.act();
            playButtonStage.draw();
            demoButtonStage.getViewport().apply();
            demoButtonStage.act();
            demoButtonStage.draw();
            if (Gdx.graphics.getFramesPerSecond() < Constants.lowestFPS) Constants.lowestFPS = Gdx.graphics.getFramesPerSecond();

            if (group3.getActions().size == 0){
                Gdx.input.setInputProcessor(inputMultiplexer);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        Constants.isPlayer = false;
//        aspectRatio = (float)width/height;
//        Constants.WINDOW_HEIGHT = (int) (Constants.WINDOW_WIDTH/aspectRatio);
        viewport.setWorldSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        viewport.update(width,height);
        viewport.apply();
        stage.getViewport().update(width, height);
        //stage2.getViewport().update(width, height);
        stage3.getViewport().update(width, height);
        demoButtonStage.getViewport().update(width, height);
        playButtonStage.getViewport().update(width, height);
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
        stage.dispose();
        stage2.dispose();
        stage3.dispose();
        demoButtonStage.dispose();
        playButtonStage.dispose();
        fbo.dispose();
        shader.dispose();
        shaderTexture.dispose();
    }
}
