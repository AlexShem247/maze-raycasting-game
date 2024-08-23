package com.raycaster.game.tools;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.borders.Border;
import com.raycaster.game.borders.BorderVals;
import com.raycaster.game.items.Item;
import com.raycaster.game.screens.PlayScreen;
import com.raycaster.game.sprite.Enemy;

public class MapCreator {

  private final PlayScreen screen;
  private final Border[] shapes;
  private final Enemy[] enemies;
  private final Item[] items;

  public final int playerSpawnX;
  public final int playerSpawnY;


  public MapCreator(PlayScreen screen, int noCoins, int noEnemies) {
    this.screen = screen;
    BorderVals generator = new BorderVals(screen, 8, 6,
        RaycasterGame.width, RaycasterGame.height, noCoins, noEnemies);
    playerSpawnX = generator.playerSpawnX;
    playerSpawnY = generator.playerSpawnY;
    shapes = generator.getShapes();
    enemies = generator.getEnemies();
    items = generator.getItems();
    generator.saveToPNG(RaycasterGame.SAVE_IMG_PATH);
    generateStaticBodies();
  }

  private void generateStaticBodies() {
    World world = screen.getWorld();
    BodyDef bdef = new BodyDef();
    FixtureDef fdef = new FixtureDef();
    for (Border shape : shapes) {
      shape.generateFixture(world, bdef, fdef);
    }
  }

  public Border[] getShapes() {
    return shapes;
  }

  public Enemy[] getEnemies() {
    return enemies;
  }

  public Item[] getItems() {
    return items;
  }
}
