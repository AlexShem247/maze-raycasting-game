package com.raycaster.game.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.screens.PlayScreen;
import com.raycaster.game.tools.Animation;

public class Coin extends Item {
  private Animation coinAnimation;
  private final int NO_FRAMES = 8;
  private final int SIZE = 2;
  private final float CYCLE_TIME = 0.5f;

  public Coin(PlayScreen screen, int x, int y) {
    super(screen, x, y, 3, 0.8f, "coin.png");
    coinAnimation = new Animation(new TextureRegion(img), NO_FRAMES, CYCLE_TIME);
  }

  protected int getSpriteWidth() {
    return SIZE*img.getWidth()/NO_FRAMES;
  }

  protected int getSpriteHeight() {
    return SIZE*img.getHeight();
  }

  @Override
  public void update(float dt) {
    super.update(dt);
    coinAnimation.update(dt);
  }

  @Override
  public void collected() {
    RaycasterGame.manager.get("coin.wav", Sound.class).play(0.2f);
    destroy = true;
  }

  @Override
  TextureRegion getFrame() {
    return coinAnimation.getFrame();
  }
}
