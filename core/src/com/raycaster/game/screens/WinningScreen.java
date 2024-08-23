package com.raycaster.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

public class WinningScreen implements Screen {

  private final Stage stage;
  private final Skin skin;

  public WinningScreen(final RaycasterGame game) {
    this.stage = new Stage(new ScreenViewport());
    this.skin = new Skin();

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

    // Create the UI elements
    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    Label titleLabel = new Label("You Win", new Label.LabelStyle(font, Color.WHITE));
    titleLabel.setFontScale(3);

    Label messageLabel = new Label("You collected all the coins.", new Label.LabelStyle(font, Color.WHITE));
    messageLabel.setFontScale(1.5f);

    TextButton nextRoundButton = new TextButton("Next Round", skin);
    nextRoundButton.getLabel().setFontScale(1.5f);
    nextRoundButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        game.roundNo++;
        game.create();
      }
    });

    TextButton quitButton = new TextButton("Quit", skin);
    quitButton.getLabel().setFontScale(1.5f);
    quitButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        Gdx.app.exit();
      }
    });

    // Arrange the UI elements in the table
    table.add(titleLabel).colspan(2).padBottom(20);
    table.row();
    table.add(messageLabel).colspan(2).padBottom(20);
    table.row();
    table.add(nextRoundButton).padRight(10);
    table.add(quitButton).padLeft(10);
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void render(float delta) {
    Gdx.input.setCursorCatched(false);
    // Clear the screen with a black background
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // Draw the UI
    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
