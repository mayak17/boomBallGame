package com.boomballgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.boomballgame.Core.Sizei;
import com.boomballgame.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Boom Balls";
		config.width = 480;
		config.height = 720;
		config.resizable = false;
		MainGame game = new MainGame();
		game.SetSizes(new Sizei(config.width, config.height));
		new LwjglApplication(game, config);
	}
}
