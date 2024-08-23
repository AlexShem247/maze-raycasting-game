package com.raycaster.game.borders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.raycaster.game.RaycasterGame;

public class RectBorder implements Border {

  private final int x, y, width, height;
  private Body body;

  public enum BorderType {HORIZONTAL, VERTICAL}

  public RectBorder(int x, int y, int width, int height) {
    // Add shape properties
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;
  }

  @Override
  public String toString() {
    return "Rectangle(x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ")";
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  @Override
  public void generateFixture(World world, BodyDef bdef, FixtureDef fdef) {
    // Create body and set its type and position
    Vector2 centre = new Vector2(x + width / 2f, y + height / 2f);
    bdef.type = BodyType.StaticBody;
    bdef.position.set(0, 0); // Set reference point

    body = world.createBody(bdef);

    // Create polygon shapes
    PolygonShape top = new PolygonShape();
    PolygonShape bottom = new PolygonShape();
    PolygonShape left = new PolygonShape();
    PolygonShape right = new PolygonShape();

    // Set the vertices for each shape
    top.set(new Vector2[]{new Vector2(x, y), centre, new Vector2(x + width, y)});
    bottom.set(new Vector2[]{new Vector2(x, y + height),
        centre, new Vector2(x + width, y + height)});
    left.set(new Vector2[]{new Vector2(x, y), centre, new Vector2(x, y + height)});
    right.set(new Vector2[]{new Vector2(x + width, y), centre, new Vector2(x + width, y + height)});

    // Create and attach fixtures
    FixtureDef topFDef = new FixtureDef();
    FixtureDef bottomFDef = new FixtureDef();
    FixtureDef leftFDef = new FixtureDef();
    FixtureDef rightFDef = new FixtureDef();

    topFDef.shape = top;
    bottomFDef.shape = bottom;
    leftFDef.shape = left;
    rightFDef.shape = right;

    // Set other fixture properties (e.g., categoryBits, maskBits)
    topFDef.filter.categoryBits = RaycasterGame.HORIZONTAL_BORDER_BIT;
    bottomFDef.filter.categoryBits = RaycasterGame.HORIZONTAL_BORDER_BIT;
    leftFDef.filter.categoryBits = RaycasterGame.VERTICAL_BORDER_BIT;
    rightFDef.filter.categoryBits = RaycasterGame.VERTICAL_BORDER_BIT;
    topFDef.filter.groupIndex = 0;
    bottomFDef.filter.groupIndex = 0;
    leftFDef.filter.groupIndex = 0;
    rightFDef.filter.groupIndex = 0;

    body.createFixture(topFDef);
    body.createFixture(bottomFDef);
    body.createFixture(leftFDef);
    body.createFixture(rightFDef);

    // Dispose of the shapes
    top.dispose();
    bottom.dispose();
    left.dispose();
    right.dispose();
  }

  @Override
  public void render(ShapeRenderer shapeRenderer) {
    shapeRenderer.rect(x, y, width, height);
  }

  @Override
  public Body getBody() {
    return body;
  }
}
