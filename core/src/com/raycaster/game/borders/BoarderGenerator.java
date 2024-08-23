package com.raycaster.game.borders;

import com.raycaster.game.items.Item;
import com.raycaster.game.sprite.Enemy;

public interface BoarderGenerator {

  Border[] getShapes();

  Enemy[] getEnemies();

  Item[] getItems();
}
