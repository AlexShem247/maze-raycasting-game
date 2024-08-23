package com.raycaster.game.tools;

import static com.raycaster.game.RaycasterGame.WALL_TEXTURE_SIZE;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.borders.RectBorder.BorderType;
import com.raycaster.game.screens.PlayScreen;

public class RayCollision implements Drawable {

  public Vector2 collision;

  public final float height, x, distance;
  private final ShapeRenderer shapeRenderer;
  private final short groupIndex;
  private final PlayScreen screen;

  RayCollision(Vector2 collision, ShapeRenderer shapeRenderer, float height, float x,
      float distance,
      short groupIndex, PlayScreen screen) {
    this.collision = collision;
    this.shapeRenderer = shapeRenderer;
    this.height = height;
    this.x = x;
    this.distance = distance;
    this.groupIndex = groupIndex;
    this.screen = screen;
  }

  @Override
  public void draw() {
    shapeRenderer.begin(ShapeType.Filled);
    float brightness = Math.min(height * (PlayScreen.WALL_SEPARATION
        / 100f) / PlayScreen.WALL_SEPARATION, 1);
    int color = (int) (WALL_TEXTURE_SIZE * (((collision.x + collision.y) / PlayScreen.WALL_HEIGHT) % 1));
    float pixelLength = height*(1+PlayScreen.WALL_HEIGHT_SCALAR) / WALL_TEXTURE_SIZE;
    float startingHeight = RaycasterGame.height / 2f - height * PlayScreen.WALL_HEIGHT_SCALAR - screen.getMouseY();
    Color[][] texture = groupIndex == 0 ? RaycasterGame.WALL_TEXTURE_1 : RaycasterGame.WALL_TEXTURE_2;
    for (int i=0; i<WALL_TEXTURE_SIZE; i++) {
      shapeRenderer.setColor(getWallColor(texture[color][i], brightness));
      Vector2 top = new Vector2(x,
          startingHeight + i*pixelLength);
      Vector2 bottom = new Vector2(x, startingHeight + (i+1)*pixelLength);
      shapeRenderer.rectLine(top, bottom, RayBeam.RAY_THICKNESS);
    }

    shapeRenderer.end();
  }

  private Color getWallColor(Color color, float brightness) {
    return new Color(color.r * brightness, color.g * brightness, color.b * brightness, 1);
  }

  public static Color mapToColor(int n) {
    n *= 16;
    n %= 1536;
    int phase = n / 256;
    int s = n % 256;

    int red = 0, green = 0, blue = 0;

    switch (phase) {
      case 0:
        red = 255;
        green = s;
        break;
      case 1:
        red = 255 - s;
        green = 255;
        break;
      case 2:
        green = 255;
        blue = s;
        break;
      case 3:
        green = 255 - s;
        blue = 255;
        break;
      case 4:
        red = s;
        blue = 255;
        break;
      case 5:
        red = 255;
        blue = 255 - s;
        break;
    }

    return new Color(red / 255f, green / 255f, blue / 255f, 1f);
  }

  @Override
  public float getDistance() {
    return distance;
  }

  @Override
  public int compare(Drawable d1, Drawable d2) {
    return Float.compare(d1.getDistance(), d2.getDistance());
  }

  @Override
  public int compareTo(Drawable other) {
    return Float.compare(this.getDistance(), other.getDistance());
  }
}
