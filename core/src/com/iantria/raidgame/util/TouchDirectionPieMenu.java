package com.iantria.raidgame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.payne.games.piemenu.PieMenu;

public class TouchDirectionPieMenu {

    private Skin skin;
    private Texture tmpTex;
    private Batch batch;

    public PieMenu menu;
    public Stage stage;

    public TouchDirectionPieMenu(Viewport viewport) {
        /* Setting up the Stage. */
        skin = new Skin(Gdx.files.internal("skins/skin.json"));
        batch = new PolygonSpriteBatch();
        stage = new Stage(viewport, batch);

        /* Ideally, you would extract such a pixel from your Atlas instead. */
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1,1,1,1);
        pixmap.fill();
        tmpTex = new Texture(pixmap);
        pixmap.dispose();
        TextureRegion whitePixel = new TextureRegion(tmpTex);

        /* ====================================================================\
        |                  HERE BEGINS THE MORE SPECIFIC CODE                  |
        \==================================================================== */

        /* Setting up and creating the widget. */
        PieMenu.PieMenuStyle style = new PieMenu.PieMenuStyle();
        style.circumferenceWidth = 1;
        style.circumferenceColor = new Color(.0f,.0f,.0f,0.2f);
        style.sliceColor = new Color(.1f,.1f,.1f,0.2f);
        style.downColor = new Color(.5f,.75f,.5f,0.5f);

        menu = new PieMenu(whitePixel, style, 40, 0.15f, 22.5f ,360);

        /* Adding a selection-listener. */
        menu.addListener(new PieMenu.PieMenuListener(menu) {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x,y,pointer, button);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x,y,pointer, button);
                menu.setSelectedIndex(PieMenu.NO_SELECTION);
                menu.setHighlightedIndex(PieMenu.NO_SELECTION);
            }
        });

        for (int i = 0; i < 8; i++) {
            Label label = new Label("", skin);
            menu.addActor(label);
        }

        /* Customizing the behavior. */
        menu.setDefaultIndex(PieMenu.NO_SELECTION);
        stage.addActor(menu);
    }
}
