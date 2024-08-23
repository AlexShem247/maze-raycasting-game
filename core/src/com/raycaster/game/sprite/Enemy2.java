package com.raycaster.game.sprite;

import com.raycaster.game.screens.PlayScreen;

public class Enemy2 extends Enemy {

  public Enemy2(PlayScreen screen, int x, int y) {
    super(screen, x, y, 10, 0.8f, "enemy2.png");
  }

  public int getSpeed() {
    return PlayScreen.enemySpeed;
  }
}
