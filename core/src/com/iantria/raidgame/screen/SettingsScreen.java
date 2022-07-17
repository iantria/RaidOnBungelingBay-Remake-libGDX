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
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.iantria.raidgame.util.Constants;
import com.rafaskoberg.gdx.typinglabel.TypingConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class SettingsScreen implements Screen {

    private Stage tableStage, exitButtonStage;
    private Image exitButton;
    private Viewport viewport;
    private Table table;
    private InputMultiplexer inputMultiplexer;

    private ShaderProgram shader;
    private Texture shaderTexture;
    private SpriteBatch batch;
    private FrameBuffer fbo;
    private GlyphLayout layout;
    private float shaderTime, aspectRatio;
    private int fboScale;
    private TypingLabel.LabelStyle labelSmallStyle;

    private TextField usernameTextField = new TextField(Constants.userName, Constants.skin);
    private CheckBox mainMenuCheckBox = new CheckBox(null, Constants.skin);
    private CheckBox helpScreenCheckBox = new CheckBox(null, Constants.skin);
    private Slider volumeSlider = new Slider(0.0f, 1f, 0.05f, false, Constants.skin);
    private CheckBox map1CheckBox = new CheckBox("Modern", Constants.skin);
    private CheckBox map2CheckBox = new CheckBox("C64", Constants.skin);
    private CheckBox map3CheckBox = new CheckBox("NES", Constants.skin);
    private ButtonGroup buttonGroup = new ButtonGroup(map1CheckBox, map2CheckBox, map3CheckBox);

    @Override
    public void show() {
        Constants.stopAllSounds();

        //Viewport
        aspectRatio = (float) Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
        Constants.WINDOW_HEIGHT = (int) (Constants.WINDOW_WIDTH/aspectRatio);
        viewport = new FitViewport(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        viewport.apply();
        tableStage = new Stage(viewport);

        // Type Label Config
        TypingConfig.DEFAULT_SPEED_PER_CHAR = 0.1f;
        TypingConfig.DEFAULT_WAIT_VALUE = 1.0f;
        TypingConfig.CHAR_LIMIT_PER_FRAME = 1;

        //topTable
        TypingLabel.LabelStyle labelLargeStyle = new TypingLabel.LabelStyle();
        BitmapFont myFont = new BitmapFont(Constants.HUDLargeFont.getData().fontFile);
        labelLargeStyle.font = myFont;
        labelLargeStyle.font.getData().setScale(0.3f);
        labelLargeStyle.font.setUseIntegerPositions(false);
        labelLargeStyle.fontColor = Color.YELLOW;

        labelSmallStyle = new TypingLabel.LabelStyle();
        BitmapFont myFont2 = new BitmapFont(Constants.HUDFont.getData().fontFile);
        labelSmallStyle.font = myFont2;
        labelSmallStyle.font.setUseIntegerPositions(false);
        labelSmallStyle.font.getData().setScale(0.2f);
        labelSmallStyle.fontColor = Color.WHITE;

        // UI Elements
        usernameTextField.setMaxLength(7);
        usernameTextField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                if((int) key == 10  || (int) key == 13){
                    Constants.userName = usernameTextField.getText();
                    tableStage.unfocus(usernameTextField);
                }
            }
        });

        if (Constants.defaultMapID == 1) map1CheckBox.setChecked(true);
        if (Constants.defaultMapID == 2) map2CheckBox.setChecked(true);
        if (Constants.defaultMapID == 3) map3CheckBox.setChecked(true);

        mainMenuCheckBox.setChecked(Constants.quickMainMenu);
        mainMenuCheckBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                tableStage.unfocus(usernameTextField);
                Constants.quickMainMenu = mainMenuCheckBox.isChecked();
                return false;
            }
        });
        helpScreenCheckBox.setChecked(Constants.showHelpScreen);
        helpScreenCheckBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                tableStage.unfocus(usernameTextField);
                Constants.showHelpScreen = helpScreenCheckBox.isChecked();
                return false;
            }
        });
        volumeSlider.setValue(Constants.volume);
        volumeSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                tableStage.unfocus(usernameTextField);
                Constants.volume = volumeSlider.getValue();
                return false;
            }
        });
        map1CheckBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                tableStage.unfocus(usernameTextField);
                boolean enabled = map1CheckBox.isChecked();
                if (enabled) Constants.defaultMapID = 1;
                return false;
            }
        });
        map2CheckBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                tableStage.unfocus(usernameTextField);
                boolean enabled = map2CheckBox.isChecked();
                if (enabled) Constants.defaultMapID = 2;
                return false;
            }
        });
        map3CheckBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                tableStage.unfocus(usernameTextField);
                boolean enabled = map3CheckBox.isChecked();
                if (enabled) Constants.defaultMapID = 3;
                return false;
            }
        });
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setUncheckLast(true);

        // Table stuff
        TypingLabel titleTypingLabel = new TypingLabel("{COLOR=YELLOW}{WAIT}{WAVE}SETTINGS", labelLargeStyle);

        table = new Table();
        table.setFillParent(true);
        //table.setDebug(true); // turn off on release
        table.top().center().align(Align.center);
        table.defaults().center().pad(3,3,3,3);

        // Title
        table.row();
        table.add(titleTypingLabel).colspan(2).center();

        table.row().pad(8,3,3,3);
        table.add(new TypingLabel("User Name:", labelSmallStyle)).right().fillY();
        table.add(usernameTextField).width(80).left();

        if (Gdx.app.getType() != Application.ApplicationType.WebGL && Gdx.app.getType() != Application.ApplicationType.Applet) {
            table.row();
            table.add(new TypingLabel("Show Help Screen:", labelSmallStyle)).right().fillY();
            table.add(helpScreenCheckBox).left();
        }

        table.row();
        table.add(new TypingLabel("Fast Main Menu:", labelSmallStyle)).right().fillY();
        table.add(mainMenuCheckBox).left();

        table.row();
        table.add(new TypingLabel("Volume:", labelSmallStyle)).right().fillY();
        table.add(volumeSlider).left();

        table.row().pad(3,3,0,3);;
        table.add(new TypingLabel("Default Map:", labelSmallStyle)).right().fillY();
        table.add(map1CheckBox).left();

        table.row().pad(0,3,0,3);
        table.add(new TypingLabel("", labelSmallStyle)).right().fillY();
        table.add(map2CheckBox).left();

        table.row().pad(0,3,0,3);
        table.add(new TypingLabel("", labelSmallStyle)).right().fillY();
        table.add(map3CheckBox).left();

        tableStage.addActor(table);
        tableStage.addAction(sequence(fadeOut(0f), moveTo(0, Constants.WINDOW_HEIGHT - tableStage.getHeight(), 0f), fadeIn(1.0f)));

        exitButtonStage = new Stage(viewport);
        exitButton = new Image(Constants.exitButton);
        exitButton.setScale(0.2f);
        exitButton.setColor(1,1,1,1);
        exitButton.setPosition(Constants.WINDOW_WIDTH - exitButton.getWidth()*exitButton.getScaleX(),
                Constants.WINDOW_HEIGHT - exitButton.getHeight()*exitButton.getScaleY());
        exitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                exitButton.removeListener(exitButton.getListeners().first());
                mainMenuCheckBox.removeListener(mainMenuCheckBox.getListeners().first());
                helpScreenCheckBox.removeListener(helpScreenCheckBox.getListeners().first());
                volumeSlider.removeListener(volumeSlider.getListeners().first());
                map1CheckBox.removeListener(map1CheckBox.getListeners().first());
                map2CheckBox.removeListener(map2CheckBox.getListeners().first());
                map3CheckBox.removeListener(map3CheckBox.getListeners().first());
                savePreferences();
                Constants.game.setScreen(new MainMenuScreen(true));
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

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(exitButtonStage);
        inputMultiplexer.addProcessor(tableStage);
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
        shader = new ShaderProgram(batch.getShader().getVertexShaderSource(), Gdx.files.internal("shaders/ocean_water.frag").readString());

        if (!shader.isCompiled()) {
            System.out.println("Error compiling shader: " + shader.getLog());
        }
        Constants.HUDFont.getData().setScale(0.12f);
        Constants.HUDFont.setUseIntegerPositions(false);
        layout = new GlyphLayout(Constants.HUDFont, "FPS: 120");
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
//        Constants.HUDFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
//                Constants.WINDOW_WIDTH - exitButton.getWidth()*exitButton.getScaleX() - layout.width - 2,
//                Constants.WINDOW_HEIGHT - layout.height);
        batch.end();

        tableStage.act();
        tableStage.draw();

        exitButtonStage.act();
        exitButtonStage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            exitButton.removeListener(exitButton.getListeners().first());
            mainMenuCheckBox.removeListener(mainMenuCheckBox.getListeners().first());
            helpScreenCheckBox.removeListener(helpScreenCheckBox.getListeners().first());
            volumeSlider.removeListener(volumeSlider.getListeners().first());
            map1CheckBox.removeListener(map1CheckBox.getListeners().first());
            map2CheckBox.removeListener(map2CheckBox.getListeners().first());
            map3CheckBox.removeListener(map3CheckBox.getListeners().first());
            savePreferences();
            Constants.game.setScreen(new MainMenuScreen(true));
        }
    }

    private void savePreferences() {
        Constants.userName = usernameTextField.getText();
        if (Constants.userName == null || Constants.userName == "" || Constants.userName.length() == 0) Constants.userName = "NotSet";

        Constants.preferences.putString("userName",Constants.userName);
        Constants.preferences.putBoolean("quickMainMenu", Constants.quickMainMenu);
        Constants.preferences.putBoolean("showHelpScreen",Constants.showHelpScreen);
        Constants.preferences.putInteger("defaultMapID",Constants.defaultMapID);
        Constants.preferences.putFloat("volume", Constants.volume);
        Constants.preferences.flush();
    }

    @Override
    public void resize(int width, int height) {
        exitButtonStage.getViewport().update(width, height, true);
        tableStage.getViewport().update(width, height, true);
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
        tableStage.dispose();
        batch.dispose();
        fbo.dispose();
        shader.dispose();
        shaderTexture.dispose();
    }
}
