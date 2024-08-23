package com.raycaster.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.raycaster.game.screens.LosingScreen;
import com.raycaster.game.screens.PauseScreen;
import com.raycaster.game.screens.PlayScreen;
import com.raycaster.game.screens.StartScreen;
import com.raycaster.game.screens.WinningScreen;
import com.raycaster.game.tools.TextureLoader;

public class RaycasterGame extends Game {

  public static int width = 960, height = 720;
  public final int FPS = 60;
  public final String title = "Ray Caster Game";
  public SpriteBatch batch;
  public static final short HORIZONTAL_BORDER_BIT = 1;
  public static final short VERTICAL_BORDER_BIT = 2;
  public static final short BORDER_BIT = HORIZONTAL_BORDER_BIT | VERTICAL_BORDER_BIT;
  public static final short PLAYER_BIT = 4;
  public static final short ENEMY_BIT = 8;
  public static final short ITEM_BIT = 16;
  public static AssetManager manager;
  public static Color[][] WALL_TEXTURE_1;
  public static Color[][] WALL_TEXTURE_2;
  public static int WALL_TEXTURE_SIZE;
  public static String SAVE_IMG_PATH = "maze.png";

  public static float PAUSE_MENU_DELAY = 1000;
  public Screen startScreen;

  public Screen playScreen;
  public Screen pauseScreen;
  public Screen winningScreen;
  public Screen losingScreen;
  public int roundNo = 1;

  @Override
  public void create() {
    batch = new SpriteBatch();
    manager = new AssetManager();
    manager.load("coin.wav", Sound.class);
    manager.load("oof.wav", Music.class);
    manager.finishLoading();
    WALL_TEXTURE_1 = TextureLoader.loadTexture("wall1.png");
    WALL_TEXTURE_2 = TextureLoader.loadTexture("wall2.png");
    WALL_TEXTURE_SIZE = WALL_TEXTURE_1.length;

    // Decide level stats
    int enemyStrength = roundNo;
    int enemySpeed = 15 * roundNo;
    int noCoins = 5 + 5 * roundNo;
    int noEnemies = 3 + 3 * roundNo;

    playScreen = new PlayScreen(this, noCoins, noEnemies, enemySpeed, enemyStrength);
    pauseScreen = new PauseScreen(this);
    winningScreen = new WinningScreen(this);
    losingScreen = new LosingScreen(this);
    startScreen = new StartScreen(this);

    setScreen(startScreen);
  }

  @Override
  public void render() {
    super.render();
  }

  @Override
  public void dispose() {
    batch.dispose();
  }
}
