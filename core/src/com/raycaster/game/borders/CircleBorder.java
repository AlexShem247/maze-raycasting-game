package com.raycaster.game.borders;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.raycaster.game.RaycasterGame;

public class CircleBorder implements Border {

  private final int x, y, radius;
  private Body body;

  public CircleBorder(int x, int y, int radius) {
    // Add shape properties
    this.x = x;
    this.y = y;
    this.radius = radius;
  }

  @Override
  public String toString() {
    return "Circle(x=" + x + ", y=" + y + ", radius=" + radius + ")";
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
  public int getRadius() {return radius;}

  @Override
  public void generateFixture(World world, BodyDef bdef, FixtureDef fdef) {
    CircleShape circleShape = new CircleShape();
    bdef.type = BodyType.StaticBody;
    bdef.position.set(x, y);

    body = world.createBody(bdef);

    circleShape.setRadius(radius);
    fdef.shape = circleShape;
    fdef.filter.categoryBits = RaycasterGame.HORIZONTAL_BORDER_BIT;
    fdef.filter.groupIndex = 1;
    body.createFixture(fdef);
    circleShape.dispose();
  }

  @Override
  public void render(ShapeRenderer shapeRenderer) {
    shapeRenderer.circle(x, y, radius);
  }

  @Override
  public Body getBody() {
    return body;
  }
}
