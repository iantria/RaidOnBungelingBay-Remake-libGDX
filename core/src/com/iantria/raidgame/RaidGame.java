package com.iantria.raidgame;

import com.badlogic.gdx.Game;
import com.iantria.raidgame.screen.GameScreen;
import com.iantria.raidgame.screen.LoadingScreen;
import com.iantria.raidgame.util.Constants;

public class RaidGame extends Game {

	@Override
	public void create() {
		Constants.game = this;
		setScreen(new LoadingScreen(this));
	}

	@Override
	public void dispose() {
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
}
