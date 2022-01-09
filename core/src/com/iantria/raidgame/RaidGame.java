package com.iantria.raidgame;

import com.badlogic.gdx.Game;
import com.iantria.raidgame.screen.GameScreen;
import com.iantria.raidgame.screen.LoadingScreen;
import com.iantria.raidgame.util.Constants;

public class RaidGame extends Game {

	GameScreen gameScreen;

	@Override
	public void create() {
		Constants.game = this;
		setScreen(new LoadingScreen(this));
//		gameScreen = new GameScreen();
//		setScreen(gameScreen);
	}


//	@Override
//	public void dispose() {
//		gameScreen.dispose();
//	}
//
//
//	@Override
//	public void render() {
//		super.render();
//	}
//
//
//	@Override
//	public void resize(int width, int height) {
//		gameScreen.resize(width, height);
//	}
}
