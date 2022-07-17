package com.iantria.raidgame.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.iantria.raidgame.util.Constants;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class HowToScreen implements Screen {


    private ShaderProgram shader;
    private Texture shaderTexture;
    private SpriteBatch batch;
    private FrameBuffer fbo;
    private GlyphLayout layout;
    private float shaderTime, aspectRatio;
    private int fboScale;
    private FitViewport viewport;
    private Stage info;
    private Image infoImage;

    @Override
    public void show() {
        //Viewport
        aspectRatio = (float) Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
        Constants.WINDOW_HEIGHT = (int) ((int) Constants.WINDOW_WIDTH/aspectRatio);
        viewport = new FitViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        viewport.apply();

        info = new Stage(viewport);
        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS)
            infoImage = new Image(Constants.helpInfoMobile);
        else
            infoImage = new Image(Constants.helpInfoDesktop);

        infoImage.setScale(0.20f);
        infoImage.setPosition(Constants.WINDOW_WIDTH/2f - infoImage.getWidth()/2f*infoImage.getScaleX(), Constants.WINDOW_HEIGHT/2f - infoImage.getHeight()/2f*infoImage.getScaleY());

        info.addActor(infoImage);

        // FBO, and Shader
        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS){
            fboScale = 5;
        } else if (Gdx.app.getType() == Application.ApplicationType.Applet || Gdx.app.getType() == Application.ApplicationType.WebGL){
            fboScale = 2 ;
        } else {
            fboScale = 2 ;
        }

        shaderTime = 0;
        batch = new SpriteBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        fbo = new FrameBuffer(Pixmap.Format.RGB888, Gdx.graphics.getWidth() / fboScale, Gdx.graphics.getHeight() / fboScale, false);
        Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth() / fboScale, Gdx.graphics.getHeight() / fboScale, Pixmap.Format.RGBA8888);
        shaderTexture = new Texture(pixmap);
        pixmap.dispose();
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(batch.getShader().getVertexShaderSource(), Gdx.files.internal("shaders/ocean_moving.frag").readString());

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
        Gdx.gl.glClearColor(0.53f, 0.81f, 0.92f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        fbo.begin();
        shaderTime += Gdx.graphics.getDeltaTime();
        batch.setShader(shader);
        shader.bind();
        shader.setUniformf("u_time", shaderTime);
        shader.setUniformf("u_resolution", new Vector2(fbo.getWidth(),fbo.getHeight()));
        batch.begin();
        batch.draw(shaderTexture,0,0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        batch.end();
        fbo.end();
        batch.setShader(null);
        batch.begin();
        batch.draw(fbo.getColorBufferTexture(),0,0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        //layout.setText(Constants.HUDFont, "FPS: " + Gdx.graphics.getFramesPerSecond());
//        Constants.HUDFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
//                Constants.WINDOW_WIDTH - layout.width - 2,
//                Constants.WINDOW_HEIGHT - layout.height);
        batch.end();

        info.act();
        info.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.isTouched()) {
            if (Constants.quickMainMenu)
                Constants.game.setScreen(new MainMenuScreen(true));
            else
                Constants.game.setScreen(new MainMenuScreen(false));
        }
    }

    @Override
    public void resize(int width, int height) {
        info.getViewport().update(width, height);
        viewport.apply();
//        viewport.update(width, height);
//        viewport.apply();
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
        info.dispose();
        batch.dispose();
        fbo.dispose();
        shader.dispose();
        shaderTexture.dispose();
    }

}
