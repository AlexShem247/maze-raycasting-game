package com.raycaster.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.items.Item;
import com.raycaster.game.sprite.Enemy;
import com.raycaster.game.sprite.Player;
import com.raycaster.game.tools.Drawable;
import com.raycaster.game.tools.InteractiveEntity;
import com.raycaster.game.tools.MapCreator;
import com.raycaster.game.tools.RayBeam;
import com.raycaster.game.tools.WorldContactListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayScreen implements Screen {

  private static final int ROTATION_SPEED = 9;
  private static final int MOVEMENT_SPEED = 10000;
  public static final int WALL_SEPARATION = 20;
  public static final float WALL_HEIGHT_SCALAR = 1.5f;
  public static final int WALL_HEIGHT = (int) (WALL_SEPARATION * (WALL_HEIGHT_SCALAR + 1));
  private int mouseX = RaycasterGame.width / 2;
  private int mouseY = RaycasterGame.height / 2;
  RaycasterGame game;
  private final World world;
  private final Box2DDebugRenderer b2dr;
  private final MapCreator creator;
  private Texture wallTexture;
  private final ShapeRenderer shapeRenderer;
  private final OrthographicCamera gamecam;
  private final FitViewport gamePort;
  private final Player player;
  private final RayBeam rayBeam;
  private final Texture bg, crossHair;
  private final Enemy[] enemies;
  private final Array<Item> items;
  public static double playerHealth;
  public static int coinsRemaining;
  public static int initialCoins;
  public static int enemySpeed;
  public static float enemyStrength;
  BitmapFont font = new BitmapFont();


  public PlayScreen(RaycasterGame game, int noCoins, int noEnemies, int enemySpeed, int enemyStrength) {
    playerHealth = 100;
    PlayScreen.enemySpeed = enemySpeed;
    PlayScreen.enemyStrength = enemyStrength;

    this.game = game;
    shapeRenderer = new ShapeRenderer();

    gamecam = new OrthographicCamera();
    gamePort = new FitViewport(RaycasterGame.width, RaycasterGame.height, gamecam);
    gamecam.position.set(gamePort.getWorldWidth() / 2f, gamePort.getWorldHeight() / 2f, 0);
    gamecam.setToOrtho(true, RaycasterGame.width, RaycasterGame.height);

    // Gravity vector
    world = new World(new Vector2(0, 0), true);
    b2dr = new Box2DDebugRenderer();
    world.setContactListener(new WorldContactListener());

    // Create player
    player = new Player(this);
    player.rotate(0);


    // Create enemies and items
    creator = new MapCreator(this, noCoins, noEnemies);
    enemies = creator.getEnemies();
    items = new Array<>(creator.getItems());

    player.b2body.setTransform(new Vector2(creator.playerSpawnX, creator.playerSpawnY), player.b2body.getAngle());

    // Create beam generator
    rayBeam = new RayBeam(this);

    // Get textures
    bg = new Texture("background.png");
    crossHair = new Texture("crosshair.png");

    font.getData().setScale(2);

    game.setScreen(game.pauseScreen);
  }

  public World getWorld() {
    return world;
  }

  @Override
  public void show() {

  }

  public void update(float dt) {
    // Performing logic
    handleInput(dt);
    player.update(dt);
    rayBeam.performRayCasting();
    rayBeam.activateEnemies();
    for (Enemy enemy : enemies) {
      enemy.update(dt);
    }
    for (Item item : items) {
      if (item.destroy) {
        items.removeValue(item, true);
        world.destroyBody(item.b2body);
        coinsRemaining--;
      } else {
        item.update(dt);
      }
    }
    world.step(1f / game.FPS, 6, 2);
    for (Enemy enemy : enemies) {
      enemy.canSeePlayer(false);
    }
  }

  @Override
  public void render(float dt) {
    // Drawing objects
    if (dt != RaycasterGame.PAUSE_MENU_DELAY) {
      Gdx.input.setCursorCatched(true);
      update(dt);
    }

    // Clear the game screen with black
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // Draw Background
    game.batch.begin();
    game.batch.setColor(1, 1, 1, 1);
    game.batch.draw(getBackgroundRegion(), 0, 0);
    game.batch.end();

    shapeRenderer.setProjectionMatrix(gamecam.combined);

    // Draw walls, enemies and coins
    Drawable[] walls = rayBeam.getCollisions();
    List<Drawable> drawables = new ArrayList<>(walls.length + enemies.length + items.size);
    Collections.addAll(drawables, walls);
    Collections.addAll(drawables, enemies);
    for (Item item : items) {
      drawables.add(item);
    }
    drawables.removeAll(Collections.singleton(null));
    Collections.sort(drawables);

    // Draw in reverse order
    for (int i = drawables.size() - 1; i >= 0; i--) {
      drawables.get(i).draw();
    }

    // Render our Box2DDebugLines
    // b2dr.render(world, gamecam.combined);

    game.batch.begin();
    game.batch.setColor(1, 1, 1, 1);
    game.batch.draw(crossHair, RaycasterGame.width/2f-crossHair.getWidth()/2f,
        RaycasterGame.height/2f+crossHair.getHeight()/2f);

    // Draw "Coins Left" in the top left corner
    font.draw(game.batch, "Coins Left: " + coinsRemaining, 10, Gdx.graphics.getHeight() - 10);

    // Draw "Health" in the top right corner
    String healthText = "Health: " + (int) playerHealth;
    float healthTextWidth = font.draw(game.batch, healthText, 0, 0).width; // Get width of the drawn text
    font.draw(game.batch, healthText, Gdx.graphics.getWidth() - 10 - healthTextWidth, Gdx.graphics.getHeight() - 10);

    game.batch.end();

    if (coinsRemaining <= 0) {
      game.setScreen(game.winningScreen);
    } else if (playerHealth <= 0) {
      game.setScreen(game.losingScreen);
    }
  }

  private TextureRegion getBackgroundRegion() {
    return new TextureRegion(bg, 0, RaycasterGame.height + getMouseY(),
        RaycasterGame.width, RaycasterGame.height);
  }

  public int getMouseY() {
    return mouseY - RaycasterGame.height / 2;
  }

  private void handleInput(float dt) {
    if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
      game.setScreen(game.pauseScreen);
    }

    // Get mouse movements
    // Calculate the mouse displacement from the center
    int deltaX = Gdx.input.getX() - Gdx.graphics.getWidth() / 2;
    int deltaY = Gdx.input.getY() - Gdx.graphics.getHeight() / 2;

    // If the mouse has moved from the center, reposition it to the center
    if ((deltaX != 0 || deltaY != 0) && deltaX != RaycasterGame.width / -2f
        && deltaY != RaycasterGame.height / -2f) {
      Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
      mouseX += deltaX;
      mouseY = Math.max(Math.min(mouseY + deltaY, RaycasterGame.height), 0);
    }

    // Handle Turning
    if (deltaX < 0 && deltaX != RaycasterGame.width / -2f) {
      player.rotate(-ROTATION_SPEED * dt * Math.abs(deltaX));
    }
    if (deltaX > 0) {
      player.rotate(ROTATION_SPEED * dt * Math.abs(deltaX));
    }

    // Handle Strafing
    player.resetSideSpeed();
    if (Gdx.input.isKeyPressed(Keys.A)) {
      player.increaseSideSpeed(MOVEMENT_SPEED);
    }
    if (Gdx.input.isKeyPressed(Keys.D)) {
      player.increaseSideSpeed(-MOVEMENT_SPEED);
    }

    // Handing Forward/Backward Movement
    player.resetSpeed();
    if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
      player.increaseSpeed(MOVEMENT_SPEED);
    }
    if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
      player.increaseSpeed(-MOVEMENT_SPEED);
    }
  }

  @Override
  public void resize(int width, int height) {
    gamePort.update(width, height);
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void hide() {

  }

  @Override
  public void dispose() {
    bg.dispose();
    crossHair.dispose();
    for (Enemy enemy : enemies) {
      enemy.dispose();
    }
  }

  public Player getPlayer() {
    return player;
  }

  public ShapeRenderer getShapeRender() {
    return shapeRenderer;
  }

  public Batch getBatch() {
    return game.batch;
  }

  public List<InteractiveEntity> getEntities() {
    List<InteractiveEntity> entities = new ArrayList<>(enemies.length + items.size);
    Collections.addAll(entities, enemies);
    for (Item item : items) {
      entities.add(item);
    }
    return entities;
  }
}
