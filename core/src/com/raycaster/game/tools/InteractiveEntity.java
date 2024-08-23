package com.raycaster.game.tools;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.screens.PlayScreen;
import com.raycaster.game.sprite.Player;

abstract public class InteractiveEntity extends Sprite implements Drawable {

  protected final World world;
  protected final Player player;
  protected final Batch batch;
  protected final PlayScreen screen;
  public Body b2body;
  public final int size;
  public float height;
  protected final int x, y;
  protected float vX, vY;
  protected int drawX;

  public InteractiveEntity(PlayScreen screen, int x, int y, int size, float height) {
    this.world = screen.getWorld();
    this.player = screen.getPlayer();
    this.batch = screen.getBatch();
    this.screen = screen;
    this.x = x;
    this.y = y;
    this.size = size;
    this.height = height;
    selfDefine();
    setBounds(0, 0, size, size);
  }

  abstract public void selfDefine();

  abstract protected Body getBody();

  public void update(float dt) {
    float x = getBody().getPosition().x - player.b2body.getPosition().x;
    float y = getBody().getPosition().y - player.b2body.getPosition().y;
    float dir = -player.getRadians() - ((float) Math.PI / 2);
    vX = (float) (x * Math.cos(dir) - y * Math.sin(dir));
    vY = (float) -(x * Math.sin(dir) + y * Math.cos(dir));
  }

  abstract public void dispose();

  abstract protected int getSpriteWidth();

  abstract protected int getSpriteHeight();

  protected float[] getDrawBox() {
    float distance = vY;
    float height = (float) (PlayScreen.WALL_SEPARATION * RayBeam.DV / Math.pow(distance,
        this.height) * 1.8f);
    float imgWidth = getSpriteWidth() * height * this.height / 1000;
    float imgHeight = getSpriteHeight() * height * this.height / 1000;
    float brightness = Math.min(height * (PlayScreen.WALL_SEPARATION
        / 500f) / PlayScreen.WALL_SEPARATION, 1);
    drawX = (int) ((RaycasterGame.width / 2f + vX * RayBeam.DV / distance) - imgWidth / 2);
    return new float[]{brightness, drawX, RaycasterGame.height / 2f - imgHeight / 1.6f + screen.getMouseY(), imgWidth,
        imgHeight};
  }

  public abstract void drawSprite();

  @Override
  public void draw() {
    drawSprite();
  }

  @Override
  public float getDistance() {
    return vY;
  }

  @Override
  public int compareTo(Drawable other) {
    return Float.compare(this.getDistance(), other.getDistance());
  }

  @Override
  public int compare(Drawable d1, Drawable d2) {
    return Float.compare(d1.getDistance(), d2.getDistance());
  }
}
