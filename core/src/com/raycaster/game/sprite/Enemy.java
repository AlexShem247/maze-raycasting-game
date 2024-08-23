package com.raycaster.game.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.screens.PlayScreen;
import com.raycaster.game.tools.InteractiveEntity;

abstract public class Enemy extends InteractiveEntity {

  public Body b2body;
  private final Texture img;
  private boolean seePlayer = false;
  public int spawnX;
  public int spawnY;
  public boolean shouldDraw = false;
  private static final boolean HIDE_BEHIND_WALL = false;

  public Enemy(PlayScreen screen, int x, int y, int size, float height, String texture) {
    super(screen, x, y, size, height);
    img = new Texture(texture);
    spawnX = x;
    spawnY = y;
  }

  @Override
  public void selfDefine() {
    BodyDef bdef = new BodyDef();
    bdef.position.set(x, y);
    bdef.type = BodyType.DynamicBody;
    bdef.bullet = true;
    b2body = world.createBody(bdef);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(size);
    fdef.filter.categoryBits = RaycasterGame.ENEMY_BIT;
    fdef.filter.maskBits =
        RaycasterGame.PLAYER_BIT | RaycasterGame.ENEMY_BIT | RaycasterGame.BORDER_BIT;

    fdef.shape = shape;
    b2body.createFixture(fdef).setUserData(this);
  }

  protected int getSpriteWidth() {
    return img.getWidth();
  }

  protected int getSpriteHeight() {
    return img.getHeight();
  }

  protected Body getBody() {
    return b2body;
  }

  abstract int getSpeed();

  @Override
  public void update(float dt) {
    super.update(dt);
    float x = getBody().getPosition().x - player.b2body.getPosition().x;
    float y = getBody().getPosition().y - player.b2body.getPosition().y;
    float angle = (float) Math.atan2(y, x);
    Vector2 velocity = new Vector2(0, 0);
    if (seePlayer) {
      velocity = new Vector2((float) (getSpeed() * -Math.cos(angle)),
          (float) (getSpeed() * -Math.sin(angle)));
    }

    b2body.setLinearVelocity(velocity);
  }

  public void canSeePlayer(boolean bool) {
    seePlayer = bool;
  }

  @Override
  public void dispose() {
    img.dispose();
  }

  @Override
  public void drawSprite() {
    float[] vals = getDrawBox();
    batch.begin();
    batch.setColor(vals[0], vals[0], vals[0], 1);
    if (HIDE_BEHIND_WALL)
      batch.draw(img, vals[1], vals[2], shouldDraw ? vals[3] : 0, shouldDraw ? vals[4] : 0);
    else
      batch.draw(img, vals[1], vals[2], vals[3], vals[4]);
    batch.end();

  }

  public int getCentreX() {
    return (int) (drawX + (img.getWidth() * height * this.height / 1000) / 2f);
  }
}
