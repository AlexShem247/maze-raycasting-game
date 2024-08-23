package com.raycaster.game.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.raycaster.game.RaycasterGame;
import com.raycaster.game.items.Item;
import com.raycaster.game.screens.PlayScreen;

public class WorldContactListener implements ContactListener {

  public WorldContactListener() {
    super();
  }

  @Override
  public void beginContact(Contact contact) {
    Fixture fixA = contact.getFixtureA();
    Fixture fixB = contact.getFixtureB();

    int cDef = (fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits);
    switch (cDef) {
      case (RaycasterGame.ENEMY_BIT | RaycasterGame.PLAYER_BIT):
        RaycasterGame.manager.get("oof.wav", Music.class).play();
        PlayScreen.playerHealth -= PlayScreen.enemyStrength;
        break;
      case (RaycasterGame.PLAYER_BIT | RaycasterGame.ITEM_BIT):
        if (fixA.getFilterData().categoryBits == RaycasterGame.ITEM_BIT) {
          ((Item) fixA.getUserData()).collected();
        } else {
          ((Item) fixB.getUserData()).collected();
        }
        break;
    }
  }

  @Override
  public void endContact(Contact contact) {

  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {

  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {

  }
}
