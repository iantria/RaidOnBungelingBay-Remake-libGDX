package com.iantria.raidgame.desktop;


import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.iantria.raidgame.RaidGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1600,900);
		config.setWindowSizeLimits(1600, 900, 4096, 2160);


		config.setTitle("Raid on Ashbridge Bay");
		config.setWindowIcon("graphics/icon.png");

		//config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 2);

		new Lwjgl3Application(new RaidGame(), config);

	}
}


