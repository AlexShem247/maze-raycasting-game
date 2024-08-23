package com.raycaster.game.sprite;

import static com.raycaster.game.tools.RayBeam.calcDistance;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.screens.PlayScreen;

public class Player extends Sprite {

  private final World world;
  public Body b2body;
  public final int SIZE = 10;
  private int speed, sideSpeed, currentAngle = 0;

  public Player(PlayScreen screen) {
    this.world = screen.getWorld();
    definePlayer();
    setBounds(0, 0, SIZE, SIZE);
  }

  @Override
  public void rotate(float degrees) {
    super.rotate(degrees);
    b2body.setTransform(b2body.getPosition(), currentAngle * MathUtils.degreesToRadians);
    currentAngle += degrees;
  }

  public void definePlayer() {
    BodyDef bdef = new BodyDef();
    bdef.position.set(RaycasterGame.width / 2f, RaycasterGame.height / 2f);
    bdef.type = BodyDef.BodyType.DynamicBody;
    b2body = world.createBody(bdef);
    rotate(-45);

    FixtureDef fdef = new FixtureDef();
    CircleShape shape = new CircleShape();
    shape.setRadius(SIZE);

    fdef.shape = shape;
    fdef.filter.categoryBits = RaycasterGame.PLAYER_BIT;
    b2body.createFixture(fdef).setUserData(this);
  }

  public float getRadians() {
    return currentAngle * MathUtils.degreesToRadians;
  }

  public float getDegrees() {
    return currentAngle;
  }
  public Vector2 getDirectionVector() {
    return new Vector2((float) Math.cos(getRadians()), (float) Math.sin(getRadians()));
  }

  public void increaseSpeed(int increment) {
    speed += increment;
  }

  public void resetSpeed() {
    speed = 0;
  }

  public void increaseSideSpeed(int increment) {
    sideSpeed += increment;
  }

  public void resetSideSpeed() {
    sideSpeed = 0;
  }

  public void update(float dt) {
    double x = speed * dt * Math.cos(getRadians());
    double y = speed * dt * Math.sin(getRadians());
    double xP = sideSpeed * dt * Math.sin(Math.PI - getRadians());
    double yP = sideSpeed * dt * Math.cos(Math.PI - getRadians());
    b2body.setLinearVelocity(new Vector2((float) (x + xP), (float) (y + yP)));
  }
}
