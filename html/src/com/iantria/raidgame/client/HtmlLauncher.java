package com.iantria.raidgame.client;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.iantria.raidgame.RaidGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {

                GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);  // resizable
                //GwtApplicationConfiguration config = new GwtApplicationConfiguration(1600, 900);

                config.padHorizontal = 0;
                config.padVertical = 0;

                //Window.addResizeHandler(new ResizeListener());
                config.fullscreenOrientation = GwtGraphics.OrientationLockType.LANDSCAPE;

                // new GwtApplicationConfiguration(960, 600);
                //config.antialiasing = true;  // "smoother" rendering (through a "blur" of edges)
                // Resizable application, uses available space in browser
                //return new GwtApplicationConfiguration(true);
                // Fixed size application:
                return config;
        }

//        public class ResizeListener implements ResizeHandler {
//                @Override
//                public void onResize(ResizeEvent event) {
//                        if (Gdx.graphics.isFullscreen()) {
//                                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
//                        } else {
//                                int width = event.getWidth() ;
//                                int height = event.getHeight();
//                                getRootPanel().setWidth("" + width + "px");
//                                getRootPanel().setHeight("" + height + "px");
//                                getApplicationListener().resize(width, height);
//                                Gdx.graphics.setWindowedMode(width, height);
//                        }
//                }
//        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new RaidGame();
        }
}