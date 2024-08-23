package com.raycaster.game.tools;

public interface Drawable extends Comparable<Drawable> {

  void draw();

  float getDistance();

  int compare(Drawable d1, Drawable d2);

}
