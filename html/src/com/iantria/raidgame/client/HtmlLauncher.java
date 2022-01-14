package com.iantria.raidgame.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.iantria.raidgame.RaidGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {

                GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);
                // new GwtApplicationConfiguration(960, 600);
                //config.antialiasing = true;  // "smoother" rendering (through a "blur" of edges)
                // Resizable application, uses available space in browser
                //return new GwtApplicationConfiguration(true);
                // Fixed size application:
                return config;
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new RaidGame();
        }
}