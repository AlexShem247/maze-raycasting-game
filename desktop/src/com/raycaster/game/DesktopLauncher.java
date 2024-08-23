package com.raycaster.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		RaycasterGame main = new RaycasterGame();
		config.setWindowedMode(RaycasterGame.width, RaycasterGame.height);
		config.setForegroundFPS(main.FPS);
		config.setTitle(main.title);
		new Lwjgl3Application(main, config);
	}
}
