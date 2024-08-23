package com.raycaster.game.tools;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Animation {

  private final Array<TextureRegion> frames;
  private final float maxFrameTime; // Time duration of each frame
  private float currentFrameTime; // Time passed since frame change
  private final int frameCount; // No. frames in animation
  private int frame; // Current frame no.

  public Animation(TextureRegion region, int frameCount, float cycleTime) {
    frames = new Array<>();
    int frameWidth = region.getRegionWidth() / frameCount;
    for (int i = 0; i < frameCount; i++) {
      frames.add(
          new TextureRegion(region, i * frameWidth, 0, frameWidth, region.getRegionHeight()));
    }
    this.frameCount = frameCount;
    maxFrameTime = cycleTime / frameCount;
    frame = 0;
  }

  public void update(float dt) {
    currentFrameTime += dt;
    if (currentFrameTime > maxFrameTime) {
      frame++;
      currentFrameTime = 0;
    }

    if (frame >= frameCount) {
      frame = 0;
    }
  }

  public TextureRegion getFrame() {
    return frames.get(frame);
  }
}
