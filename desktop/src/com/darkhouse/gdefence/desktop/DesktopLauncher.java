package com.darkhouse.gdefence.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.darkhouse.gdefence.GDefence;

public class DesktopLauncher {
	public static void main (String[] arg) {
		System.setProperty("user.name", "\\xD0\\x90\\xD0\\xBD\\xD0\\xB4\\xD1\\x80\\xD0\\xB5\\xD0\\xB9");//Comment if user.name on english
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		config.title = "GDefence";
		config.width = 1280;
		config.height = 720;
//		config.resizable = false;
        config.fullscreen = false;
		config.samples = 64;

//		Preferences pref = Gdx.app.getPreferences("config");
//		if (pref.getBoolean("fullscreen", false)) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        config.addIcon("Logo/logo16.png", Files.FileType.Internal);
        config.addIcon("Logo/logo32.png", Files.FileType.Internal);
		new LwjglApplication(new GDefence(), config);
	}
}
