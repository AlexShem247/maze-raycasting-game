package com.raycaster.game.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.screens.PlayScreen;
import com.raycaster.game.tools.InteractiveEntity;

abstract public class Item extends InteractiveEntity {

  public float height;
  protected final Texture img;
  public boolean destroy = false;
  public int spawnX;
  public int spawnY;

  public Item(PlayScreen screen, int x, int y, int size, float height, String texture) {
    super(screen, x, y, size, height);
    spawnX = x;
    spawnY = y;
    img = new Texture(texture);
  }

  protected Body getBody() {
    return b2body;
  }

  @Override
  public void selfDefine() {
    BodyDef bdef = new BodyDef();
    bdef.position.set(x, y);
    bdef.type = BodyType.StaticBody;
    b2body = world.createBody(bdef);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(size);
    fdef.filter.categoryBits = RaycasterGame.ITEM_BIT;
    fdef.filter.maskBits = RaycasterGame.PLAYER_BIT | RaycasterGame.ENEMY_BIT;

    fdef.shape = shape;
    b2body.createFixture(fdef).setUserData(this);
  }

  abstract public void collected();

  @Override
  public void dispose() {
    img.dispose();
  }

  abstract TextureRegion getFrame();

  @Override
  public void drawSprite() {
    float[] vals = getDrawBox();
    batch.begin();
    batch.setColor(vals[0], vals[0], vals[0], 1);
    batch.draw(getFrame(), vals[1], vals[2], vals[3], vals[4]);
    batch.end();
  }
}
