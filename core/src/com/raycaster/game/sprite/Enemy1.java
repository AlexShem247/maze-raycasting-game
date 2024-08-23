package com.raycaster.game.sprite;

import com.raycaster.game.screens.PlayScreen;

public class Enemy1 extends Enemy {

  public Enemy1(PlayScreen screen, int x, int y) {
    super(screen, x, y, 10, 0.8f, "enemy1.png");
  }

  public int getSpeed() {
    return PlayScreen.enemySpeed;
  }
}
