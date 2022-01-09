package com.iantria.raidgame.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.iantria.raidgame.RaidGame;
import com.iantria.raidgame.util.AnimatedImage;
import com.iantria.raidgame.util.Constants;
import com.iantria.raidgame.util.Statistics;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class IntroScreen implements Screen {

    private RaidGame game;
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
    private Music fireMusic;

    private int x = 1;
    private int y = 1;
    private float z;
    private float delayTime;
    private int introStep;
    private float scale;
    private float scale2;
    private float rot;
    private float pan;
    private float vol;

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

    public IntroScreen(RaidGame game, boolean isQuick) {
        this.game = game;
        x = 0;
        if (isQuick) introStep = 4;
        else introStep = 1;
     }

    @Override
    public void show() {
        viewport = new StretchViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        front = new Image(Constants.introScreenFrontApache);
        frontBlade = new Image(Constants.introScreenFrontBlade);
        frontBlade2 = new Image(Constants.introScreenFrontBlade);
        fireAnim = new AnimatedImage(new Animation<TextureRegion>(Constants.explosionAnimations[3].getFrameDuration(),
                Constants.explosionAnimations[5].getKeyFrames()), 5f);
        fireAnim.setScale(1.5f);
        fireMusic = Constants.m61Sound;

        frontBlade.setPosition(0, 375);
        frontBlade.setOrigin(583, 0);
        frontBlade2.setPosition(0, 375);
        frontBlade2.setOrigin(583, 0);

        stage3 = new Stage(viewport);;
        group3 = new Group();
        group3.addActor(front);
        group3.addActor(frontBlade);
        group3.addActor(frontBlade2);
        //group3.addActor(fireAnim);

        group3.setScale(0.25f);
        group3.addAction(sequence(moveTo(Constants.WINDOW_WIDTH*.15f, -Constants.WINDOW_HEIGHT),
                moveTo(Constants.WINDOW_WIDTH*.15f, Constants.WINDOW_HEIGHT*0.25f, 6f),
                delay(2.5f)));
        stage3.addActor(group3);


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
        delayTime = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.53f, 0.81f, 0.92f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        delayTime += delta;
        scale += delta * y * 5f;
        scale2 += delta * x * 15f;
        rot += delta * 725f;
        blade3.setRotation(rot);

        if (scale <= 0f ) y = 1;
        if (scale >= 0.5f) y = -1;
        if (scale2 <= 0.5f ) x = 1;
        if (scale2 >= 1.1f) {
            x = -1;
            scale2 = 1.1f;
        }
        blade.setScale(scale, 1f);
        blade2.setScale(scale2, 1f);
        frontBlade.setScale(scale, 1f);
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
            stage3.act();
            stage3.draw();
            z += delta;
            if (!fireMusic.isPlaying()){
                fireMusic.setLooping(true);
                fireMusic.play();
                fireMusic.setVolume(0.25f);
            }
            if (z > 3f){
                fireMusic.stop();
                group3.removeActor(fireAnim);
                introStep = 5;
                stage= new Stage(viewport);
                stage2 = new Stage(viewport);
                Image title = new Image(Constants.introScreenTitle);
                Image name = new Image(Constants.introScreenName);
                title.setScale(0.4f);
                name.setScale(0.25f);

                stage.addAction(sequence(moveTo(1,Constants.WINDOW_HEIGHT + 10),delay(1f), moveTo(1, 10, 2f)));
                stage.addActor(title);

                stage2.addAction(sequence(moveTo(10,Constants.WINDOW_HEIGHT), delay(4f),
                        moveTo(10,Constants.WINDOW_HEIGHT - name.getHeight()*name.getScaleY() - 10, 1f)));
                stage2.addActor(name);

                InputMultiplexer inputMultiplexer = new InputMultiplexer();
                if (Gdx.app.getType() != Application.ApplicationType.Android){
                    // Play Button
                    playButton = new Image(Constants.playButton);
                    playButton.setScale(0.15f);
                    playButton.setBounds(playButton.getX(), playButton.getY(), playButton.getWidth(), playButton.getHeight());
                    playButton.setTouchable(Touchable.enabled);
                    playButton.addListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            Constants.chopperSound.setLooping(false);
                            Constants.chopperSound.stop();
                            Constants.m61Sound.setLooping(false);
                            Constants.m61Sound.stop();
                            Statistics.resetScores();
                            Constants.isPlayer = true;
                            game.setScreen(new GameScreen(game));
                            return true;
                        }

                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        }
                    });

                    playButtonStage = new Stage(viewport);
                    playButtonStage.addAction(sequence(fadeOut(0f),
                            moveTo(Constants.WINDOW_WIDTH - playButton.getWidth() * playButton.getScaleX() - 10, Constants.WINDOW_HEIGHT / 2 - 20),
                            delay(5f), fadeIn(1.5f)));

                    playButtonStage.addActor(playButton);
                    Gdx.input.setInputProcessor(playButtonStage);
                    inputMultiplexer.addProcessor(playButtonStage);
                }
                // Demo Button
                demoButton = new Image(Constants.demoButton);
                demoButton.setScale(0.15f);
                demoButton.setBounds(demoButton.getX(), demoButton.getY(), demoButton.getWidth(), demoButton.getHeight());
                demoButton.setTouchable(Touchable.enabled);
                demoButton.addListener(new ClickListener() {
                    @Override
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                        Constants.chopperSound.setLooping(false);
                        Constants.chopperSound.stop();
                        Constants.m61Sound.setLooping(false);
                        Constants.m61Sound.stop();
                        Statistics.resetScores();
                        Constants.isPlayer = false;
                        game.setScreen(new GameScreen(game));
                        return true;
                    }
                    @Override
                    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    }
                });

                demoButtonStage = new Stage(viewport);
                demoButtonStage.addAction(sequence(fadeOut(0f),moveTo(10, Constants.WINDOW_HEIGHT/2f - 20),
                        delay(5f),fadeIn(1.5f)));
                demoButtonStage.addActor(demoButton);

                inputMultiplexer.addProcessor(demoButtonStage);
                Gdx.input.setInputProcessor(inputMultiplexer);

            }
        }
         if (introStep == 5) {
            stage3.draw();
            stage.getViewport().apply();
            stage.act();
            stage.draw();
            stage2.getViewport().apply();
            stage2.act();
            stage2.draw();
            if (Gdx.app.getType() != Application.ApplicationType.Android) {
                 playButtonStage.act();
                 playButtonStage.draw();
            }
            demoButtonStage.act();
            demoButtonStage.draw();

        }

//        if ((delayTime > 2f) && (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ANY_KEY))) {
//            // If the screen is touched after the game is done loading, go to game screen
//            Constants.chopperSound.setLooping(false);
//            Constants.chopperSound.stop();
//            Constants.m61Sound.setLooping(false);
//            Constants.m61Sound.stop();
//            Statistics.resetScores();
//            game.setScreen(new GameScreen(game));
//        }
    }


    @Override
    public void resize(int width, int height) {
        Constants.isPlayer = false;
        stage.getViewport().update(width, height, true);
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

    }
}
