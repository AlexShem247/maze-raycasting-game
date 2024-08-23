package com.raycaster.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.raycaster.game.RaycasterGame;

public class PauseScreen implements Screen {

  private final RaycasterGame game;
  private final Stage stage;
  private final Skin skin;

  private final ShapeRenderer shapeRenderer;

  public PauseScreen(final RaycasterGame game) {
    this.game = game;
    this.stage = new Stage(new ScreenViewport());
    this.skin = new Skin();
    this.shapeRenderer = new ShapeRenderer();

    // Create a simple font
    BitmapFont font = new BitmapFont();
    skin.add("default", font);

    // Create a simple TextButton style
    TextButtonStyle textButtonStyle = new TextButtonStyle();
    textButtonStyle.font = font;
    textButtonStyle.fontColor = Color.WHITE;
    textButtonStyle.overFontColor = Color.GRAY; // Change color when hovered
    textButtonStyle.downFontColor = Color.DARK_GRAY; // Change color when clicked
    skin.add("default", textButtonStyle);

    // Create the pause menu UI
    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    Label pauseLabel = new Label("Game Paused", new Label.LabelStyle(font, Color.WHITE));
    pauseLabel.setFontScale(2);
    TextButton resumeButton = new TextButton("Resume", skin);
    TextButton quitButton = new TextButton("Quit", skin);

    // Set font scale
    pauseLabel.setFontScale(3);
    resumeButton.getLabel().setFontScale(1.5f);
    quitButton.getLabel().setFontScale(1.5f);

    table.add(pauseLabel).colspan(2).padBottom(20);
    table.row();
    table.add(resumeButton).padRight(10);
    table.add(quitButton).padLeft(10);

    resumeButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        game.setScreen(game.playScreen);
      }
    });

    quitButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        Gdx.app.exit();
      }
    });
  }

  @Override
  public void show() {
  }

  @Override
  public void render(float delta)  {
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCursorCatched(false);

    // Render the game scene here first
    game.playScreen.render(RaycasterGame.PAUSE_MENU_DELAY);

    // Darken the background with a semi-transparent black overlay
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(0, 0, 0, 0.5f); // Semi-transparent black
    shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    shapeRenderer.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);

    // Draw the pause menu
    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void dispose() {
    stage.dispose();
    skin.dispose();
  }
}
