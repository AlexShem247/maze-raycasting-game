package com.raycaster.game.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.borders.Border;
import com.raycaster.game.screens.PlayScreen;
import com.raycaster.game.sprite.Enemy;
import com.raycaster.game.sprite.Player;
import java.util.List;

public class RayBeam {

  public static final float RAY_THICKNESS = 1;
  private final Player player;
  private final World world;
  private final PlayScreen screen;
  private final ShapeRenderer shapeRenderer;
  private final float RAY_LENGTH = (float) Math.sqrt(
      Math.pow(RaycasterGame.width, 2) + Math.pow(RaycasterGame.height, 2));
  public static final int FOV = 60;
  public static final int noLines = (int) (RaycasterGame.width / RAY_THICKNESS);
  public static final float DV = (float) (((float) RaycasterGame.width) / (2 * Math.tan(
      Math.toRadians(FOV / 2f))));
  private final List<InteractiveEntity> entities;
  public RayCollision[] collisions;
  private int collisionCounter;
  private Vector2 direction;

  public RayBeam(PlayScreen screen) {
    player = screen.getPlayer();
    world = screen.getWorld();
    this.screen = screen;
    shapeRenderer = screen.getShapeRender();
    this.entities = screen.getEntities();
  }

  public void performRayCasting() {
    for (InteractiveEntity entity : entities) {
      entity.getBody().setActive(false);
    }
    Vector2 start = player.b2body.getPosition();
    direction = player.getDirectionVector().rotateDeg(RayBeam.FOV / 2f);

    RayCastCallback callback = new RayCastCallback() {
      @Override
      public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
          float fraction) {
        int bits = fixture.getFilterData().categoryBits;
        float distance = calcDistance(point, player.b2body.getPosition());
        float height = (float) ((PlayScreen.WALL_SEPARATION * RayBeam.DV) / (
            calcDistance(player.b2body.getPosition(), point)
                * Math.cos(direction.angleRad() - player.getRadians())));

        if (bits <= RaycasterGame.VERTICAL_BORDER_BIT) {
          collisions[collisionCounter] = new RayCollision(new Vector2(point.x, point.y),
              shapeRenderer, height,
              collisionCounter * RAY_THICKNESS, distance, fixture.getFilterData().groupIndex, screen);
        }
        return fraction;
      }
    };

    collisions = new RayCollision[noLines];
    for (collisionCounter = 0; collisionCounter < noLines; collisionCounter++) {
      Vector2 end = new Vector2(start).add(direction.x * RAY_LENGTH, direction.y * RAY_LENGTH);
      world.rayCast(callback, start, end);
      direction = player.getDirectionVector().rotateRad(
          (float) Math.atan((collisionCounter - (RaycasterGame.width / 2f)) / DV));
    }
    for (InteractiveEntity entity : entities) {
      entity.getBody().setActive(true);
    }
  }

  public static float calcDistance(Vector2 p1, Vector2 p2) {
    return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
  }

  public RayCollision[] getCollisions() {
    return collisions;
  }

  public void activateEnemies() {
    Vector2 start = player.b2body.getPosition();
    direction = player.getDirectionVector();

    RayCastCallback callback = new RayCastCallback() {
      @Override
      public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
          float fraction) {
        int bits = fixture.getFilterData().categoryBits;
        if (bits == RaycasterGame.ENEMY_BIT) {
          Enemy enemy = (Enemy) fixture.getUserData();
          int x = enemy.getCentreX();
          enemy.shouldDraw = 0 <= x && x < noLines && enemy.getDistance() < collisions[x].distance;
          if (0 <= x && x < noLines && enemy.getDistance() < collisions[x].distance) {
            enemy.canSeePlayer(true);
          }
        }
        return fraction;
      }
    };

    for (int i = 0; i < 360; i++) {
      Vector2 end = new Vector2(start).add(direction.x * RAY_LENGTH, direction.y * RAY_LENGTH);
      world.rayCast(callback, start, end);
      direction = direction.rotateDeg(1);
    }
  }
}
