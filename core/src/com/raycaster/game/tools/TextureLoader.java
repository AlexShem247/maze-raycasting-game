package com.raycaster.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class TextureLoader {
  public static Color[][] loadTexture(String imagePath) {
    Pixmap pixmap = new Pixmap(Gdx.files.internal(imagePath));

    int width = pixmap.getWidth();
    int height = pixmap.getHeight();

    Color[][] colorArray = new Color[width][height];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int pixel = pixmap.getPixel(x, y);
        float r = ((pixel >> 24) & 0xFF) / 255.0f;
        float g = ((pixel >> 16) & 0xFF) / 255.0f;
        float b = ((pixel >> 8) & 0xFF) / 255.0f;
        colorArray[x][y] = new Color(r, g, b, 1);
      }
    }

    pixmap.dispose();
    return colorArray;
  }
}
