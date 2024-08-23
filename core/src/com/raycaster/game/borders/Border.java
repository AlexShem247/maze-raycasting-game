package com.raycaster.game.borders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public interface Border {

  int getX();

  int getY();

  void generateFixture(World world, BodyDef bdef, FixtureDef fdef);

  void render(ShapeRenderer shapeRenderer);

  Body getBody();

  String toString();
}
