package com.raycaster.game.borders;

import com.raycaster.game.items.Coin;
import com.raycaster.game.items.Item;
import com.raycaster.game.screens.PlayScreen;
import com.raycaster.game.sprite.Enemy;
import com.raycaster.game.sprite.Enemy1;
import com.raycaster.game.sprite.Enemy2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

// Maze class to represent and generate a maze
public class BorderVals implements BoarderGenerator {
  private final PlayScreen screen;
  private final int mazeWidth;
  private final int mazeHeight;
  private final int imageWidth;
  private final int imageHeight;
  private final boolean[][] maze;
  private boolean[][] pillars;

  @Override
  public Border[] getShapes() {
    return borders.toArray(new Border[0]);
  }

  @Override
  public Enemy[] getEnemies() {
    return enemies.toArray(new Enemy[0]);
  }

  @Override
  public Item[] getItems() {
    return items.toArray(new Item[0]);
  }

  static class Pos { final int x, y; Pos(int x, int y) { this.x = x; this.y = y; } }
  enum MazeObjects {PLAYER, ENEMY1, ENEMY2, COIN}
  private MazeObjects[][] mazePos;

  private final List<int[]> wallDirections;
  private final List<int[]> directions;
  private static final boolean reverseColors = false;

  private static final String wall = "██";
  private static final String passage = "  ";

  private final List<Border> borders = new ArrayList<>();
  private final List<Item> items = new ArrayList<>();
  private final List<Enemy> enemies = new ArrayList<>();
  public int playerSpawnX;
  public int playerSpawnY;
  private final int noCoins;
  private final int noEnemies;

  public BorderVals(PlayScreen screen, int mazeWidth, int mazeHeight, int imageWidth, int imageHeight,
      int noCoins, int noEnemies) {
    this.screen = screen;
    this.mazeWidth = mazeWidth;
    this.mazeHeight = mazeHeight;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.noCoins = noCoins;
    this.noEnemies = noEnemies;
    maze = new boolean[2 * mazeHeight + 1][2 * mazeWidth + 1];
    wallDirections = Arrays.asList(new int[]{0, 2}, new int[]{2, 0}, new int[]{0, -2}, new int[]{-2, 0});
    directions = Arrays.asList(new int[]{0, 1}, new int[]{1, 0}, new int[]{0, -1}, new int[]{-1, 0});

    // Initialise maze with outer walls
    for (int i = 0; i < 2 * mazeHeight + 1; i++) {
      for (int j = 0; j < 2 * mazeWidth + 1; j++) {
        maze[i][j] = true;  // true represents walls
      }
    }

    // Generate maze starting from a random odd index
    Random random = new Random();
    int start_x = random.nextInt(mazeHeight) * 2 + 1;
    int start_y = random.nextInt(mazeWidth) * 2 + 1;
    carvePassages(start_x, start_y);

    // Remove dead ends from the maze
    removeDeadEnds();

    // Remove single wall blocks
    findPillars();
    removeIsolatedWalls();

    // Generate Maze Object Positions
    generateMazeObjects();

    // Generate borders
    generateBorders();
  }

  void generateMazeObjects() {
    mazePos = new MazeObjects[2 * mazeHeight + 1][2 * mazeWidth + 1];
    List<Pos> empty = new ArrayList<>();
    for (int i = 1; i < 2 * mazeHeight; i += 1) {
      for (int j = 1; j < 2 * mazeWidth; j += 1) {
        if (!maze[i][j] && !pillars[i][j]) {
          // Empty space
          empty.add(new Pos(j, i));
        }
      }
    }

    Collections.shuffle(empty);
    Pos pos;

    // Find spawn point for player
    pos = pop(empty);
    mazePos[pos.y][pos.x] = MazeObjects.PLAYER;

    PlayScreen.initialCoins = noCoins;
    PlayScreen.coinsRemaining = noCoins;
    int totalItems = noCoins + noEnemies;
    int emptySpaces = empty.size();

    // Determine the actual number of items to be placed, limited by available spaces
    int itemsToAdd = Math.min(totalItems, emptySpaces);

    // Calculate the proportions of coins and enemies
    double coinRatio = (double) noCoins / totalItems;

    // Counters for added coins and enemies
    int coinsAdded = 0;
    int enemiesAdded = 0;

    for (int i = 0; i < itemsToAdd; i++) {
      pos = pop(empty);
      if (coinsAdded < noCoins && (double) coinsAdded / itemsToAdd < coinRatio) {
        mazePos[pos.y][pos.x] = MazeObjects.COIN;
        coinsAdded++;
      } else if (enemiesAdded < noEnemies) {
        mazePos[pos.y][pos.x] = enemiesAdded % 2 == 0 ? MazeObjects.ENEMY1 : MazeObjects.ENEMY2;
        enemiesAdded++;
      } else {
        // In case we have filled the proportionate number of coins but still have enemies left
        mazePos[pos.y][pos.x] = enemiesAdded % 2 == 0 ? MazeObjects.ENEMY1 : MazeObjects.ENEMY2;
        enemiesAdded++;
      }
    }
    PlayScreen.initialCoins = coinsAdded;
  }

  private Pos pop(List<Pos> positions) {
    Pos pos = positions.get(0);
    positions.remove(0);
    return pos;
  }

  // Finds pillars and sets them as true
  void findPillars() {
    // Find single walls
    boolean[][] single = new boolean[2 * mazeHeight + 1][2 * mazeWidth + 1];
    for (int i = 1; i < 2 * mazeHeight; i += 1) {
      for (int j = 1; j < 2 * mazeWidth; j += 1) {
        if (isIsolatedWall(i, j)) {
          single[i][j] = true;
        }
      }
    }

    pillars = new boolean[2 * mazeHeight + 1][2 * mazeWidth + 1];

    // Iterate through the single array to find horizontal pillars
    for (int i = 2; i < 2 * mazeHeight - 1; i += 1) {
      for (int j = 2; j < 2 * mazeWidth - 1; j += 1) {
        // Check for the pattern 00000 with X at the center
        //                       01010
        //                       00000
        if (!single[i-1][j-2] && !single[i-1][j-1] && !single[i-1][j] && !single[i-1][j+1] && !single[i-1][j+2] &&
            !single[i][j-2] && single[i][j-1] && !single[i][j] && single[i][j+1] && !single[i][j+2] &&
            !single[i+1][j-2] && !single[i+1][j-1] && !single[i+1][j] && !single[i+1][j+1] && !single[i+1][j+2]) {
          // If pattern matches, adjust neighboring cells in single and mark the center in pillars
          single[i][j - 1] = false;
          single[i][j + 1] = false;
          pillars[i][j] = true;
        }
      }
    }

    // Iterate through the single array to find vertical pillars
    for (int i = 2; i < 2 * mazeHeight - 1; i += 1) {
      for (int j = 2; j < 2 * mazeWidth - 1; j += 1) {
        // Check for the pattern 000 with X at the center
        //                       010
        //                       000
        //                       010
        //                       000
        if (!single[i-2][j-1] && !single[i-2][j] && !single[i-2][j+1] &&
            !single[i-1][j-1] && single[i-1][j] && !single[i-1][j+1] &&
            !single[i][j-1] && !single[i][j] && !single[i][j+1] &&
            !single[i+1][j-1] && single[i+1][j] && !single[i+1][j+1] &&
            !single[i+2][j-1] && !single[i+2][j] && !single[i+2][j+1]) {
          // If pattern matches, adjust neighboring cells in single and mark the center in pillars
          single[i - 1][j] = false;
          single[i + 1][j] = false;
          pillars[i][j] = true;
        }
      }
    }
  }

  // Remove isolated single wall blocks from the maze
  private void removeIsolatedWalls() {
    for (int i = 1; i < 2 * mazeHeight; i += 1) {
      for (int j = 1; j < 2 * mazeWidth; j += 1) {
        if (isIsolatedWall(i, j)) {
          maze[i][j] = false;  // Remove the isolated wall block
        }
      }
    }
  }


  // Check if a wall block is isolated (completely surrounded by passages)
  private boolean isIsolatedWall(int x, int y) {
    if (maze[x][y]) {  // Check if it's a wall block
      for (int[] dir : directions) {
        int nx = x + dir[0];
        int ny = y + dir[1];
        if (!isValidMove(nx, ny) || maze[nx][ny]) {
          return false;  // Not completely surrounded by passages
        }
      }
      return true;  // Completely surrounded by passages
    }
    return false;  // Not a wall block
  }

  // Carve passages using DFS
  private void carvePassages(int x, int y) {
    maze[x][y] = false;

    // Shuffle directions randomly
    Collections.shuffle(wallDirections);

    // Explore each direction
    for (int[] dir : wallDirections) {
      int nx = x + dir[0];
      int ny = y + dir[1];
      if (isValidMove(nx, ny) && maze[nx][ny]) {
        maze[x + dir[0] / 2][y + dir[1] / 2] = false;  // Carve the wall
        carvePassages(nx, ny);  // Recursively carve passages
      }
    }
  }

  // Check if the move is valid
  private boolean isValidMove(int x, int y) {
    return x >= 0 && x < 2 * mazeHeight + 1 && y >= 0 && y < 2 * mazeWidth + 1;
  }

  // Remove dead ends from the maze
  private void removeDeadEnds() {
    for (int i = 1; i < 2 * mazeHeight; i += 1) {
      for (int j = 1; j < 2 * mazeWidth; j += 1) {
        if (isDeadEnd(i, j)) {
          maze[i][j] = false;  // Make the dead end a passage
          // Mark the surrounding walls as passages, except for the current passage
          for (int[] dir : directions) {
            int nx = i + dir[0];
            int ny = j + dir[1];
            if (isWithinBorder(nx, ny) && maze[nx][ny]) {
              maze[nx][ny] = false;  // Carve the wall
            }
          }
        }
      }
    }
  }

  private boolean isWithinBorder(int x, int y) {
    return 1 <= x  && x < 2 * mazeHeight && 1 <= y  && y < 2 * mazeWidth;
  }

  // Check if a cell is a dead end (surrounded by 3 walls)
  private boolean isDeadEnd(int x, int y) {
    if (maze[x][y])
      return false;

    int wallCount = 0;

    for (int[] dir : directions) {
      int nx = x + dir[0];
      int ny = y + dir[1];
      if (maze[nx][ny]) {
        wallCount++;
      }
    }

    return wallCount == 3;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (boolean[] row : maze) {
      for (boolean cell : row) {
        if (reverseColors)
          sb.append(!cell ? wall : passage);
        else
          sb.append(cell ? wall : passage);
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  private void generateBorders() {
    // Calculate scaling factors
    double cellWidth = (double) imageWidth / (2 * mazeWidth + 1);
    double cellHeight = (double) imageHeight / (2 * mazeHeight + 1);

    // Adjust pathway and wall thickness ratios
    double pathwayThicknessRatio = 4.0;
    double wallWidth = cellWidth / pathwayThicknessRatio;
    double wallHeight = cellHeight / pathwayThicknessRatio;

    // Draw border walls
    borders.add(new RectBorder(0, 0, imageWidth, (int) (cellHeight / 2)));
    borders.add(new RectBorder(0, imageHeight - (int) (cellHeight / 2), imageWidth, imageHeight));
    borders.add(new RectBorder(0, 0, (int) (cellWidth / 2), imageHeight));
    borders.add(new RectBorder(imageWidth - (int) (cellWidth / 2), 0, imageWidth, imageHeight));

    // Draw horizontal walls as contiguous segments
    boolean onHorizontalWall = false;
    int startX = 0, startY = 0;
    int width = 0;
    for (int i = 0; i < 2 * mazeHeight + 1; i++) {
      for (int j = 0; j < 2 * mazeWidth + 1; j++) {
        if (maze[i][j]) {
          if (!onHorizontalWall) {
            startX = (int) (j * cellWidth + cellWidth / 2 - wallWidth / 2);
            startY = (int) (i * cellHeight + cellHeight / 2 - wallHeight / 2);
            width = (int) wallWidth;
          } else {
            width += (int) Math.ceil(cellWidth);
          }
          onHorizontalWall = true;
        } else {
          if (onHorizontalWall) {
            // Draw the horizontal wall segment
            if (width > (int) Math.ceil(cellWidth))
              borders.add(new RectBorder(startX, startY, width, (int) Math.ceil(wallHeight)));
            width = 0;
          }
          onHorizontalWall = false;
        }
      }
      // End of row, draw the last segment if still on a wall
      if (onHorizontalWall) {
        if (width > (int) Math.ceil(cellWidth))
          borders.add(new RectBorder(startX, startY, width, (int) Math.ceil(wallHeight)));
        width = 0;
        onHorizontalWall = false;
      }
    }

    // Draw vertical walls as contiguous segments
    boolean onVerticalWall = false;
    startX = 0;
    startY = 0;
    int height = 0;
    for (int j = 0; j < 2 * mazeWidth + 1; j++) {
      for (int i = 0; i < 2 * mazeHeight + 1; i++) {
        if (maze[i][j]) {
          if (!onVerticalWall) {
            startX = (int) (j * cellWidth + cellWidth / 2 - wallWidth / 2);
            startY = (int) (i * cellHeight + cellHeight / 2 - wallHeight / 2);
            height = (int) wallHeight;
          } else {
            height += (int) Math.ceil(cellHeight);
          }
          onVerticalWall = true;
        } else {
          if (onVerticalWall) {
            // Draw the vertical wall segment
            if (height > (int) Math.ceil(cellHeight)) {
              borders.add(new RectBorder(startX, startY, (int) Math.ceil(wallWidth), height));
            }
            height = 0;
          }
          onVerticalWall = false;
        }
      }
      // End of column, draw the last segment if still on a wall
      if (onVerticalWall) {
        if (height > (int) Math.ceil(cellHeight)) {
          borders.add(new RectBorder(startX, startY, (int) Math.ceil(wallWidth), height));
        }
        height = 0;
        onVerticalWall = false;
      }
    }

    // Draw pillars
    for (int j = 0; j < 2 * mazeWidth + 1; j++) {
      for (int i = 0; i < 2 * mazeHeight + 1; i++) {
        if (pillars[i][j]) {
          borders.add(new CircleBorder((int) (j * cellWidth + cellWidth / 2),
              (int) (i * cellHeight + cellHeight / 2),
              (int) Math.ceil(wallHeight) * 2));
        }
      }
    }

    // Draw maze object
    for (int j = 0; j < 2 * mazeWidth + 1; j++) {
      for (int i = 0; i < 2 * mazeHeight + 1; i++) {
        if (mazePos[i][j] != null) {
          int x = (int) (j * cellWidth + cellWidth / 2);
          int y = (int) (i * cellHeight + cellHeight / 2);
          switch (mazePos[i][j]) {
            case ENEMY1:
              enemies.add(new Enemy1(screen, x, y));
              break;
            case ENEMY2:
              enemies.add(new Enemy2(screen, x, y));
              break;
            case COIN:
              items.add(new Coin(screen, x, y));
              break;
            case PLAYER:
              playerSpawnX = x;
              playerSpawnY = y;
              break;
          }
        }
      }
    }
  }

  // Generate and save the maze as a PNG image
  public void saveToPNG(String fileName) {
    BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    g2d.setColor(new Color(195, 195, 195));
    g2d.fillRect(0, 0, imageWidth, imageHeight);

    // Draw shapes
    g2d.setColor(Color.BLACK);
    for (Border shape: borders) {
      if (shape instanceof RectBorder) {
        RectBorder rect = (RectBorder) shape;
        g2d.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
      } else if (shape instanceof CircleBorder) {
        CircleBorder circle = (CircleBorder) shape;
        g2d.fillOval(circle.getX() - circle.getRadius(),
            circle.getY() - circle.getRadius(),
            2 * circle.getRadius(), 2 * circle.getRadius());
      }
    }

    // Draw other objects
    g2d.setColor(Color.YELLOW);
    for (Item item: items) {
      g2d.fillOval(item.spawnX, item.spawnY, 10, 10);
    }

    g2d.setColor(Color.RED);
    for (Enemy enemy: enemies) {
      g2d.fillOval(enemy.spawnX, enemy.spawnY, 20, 20);
    }

    g2d.setColor(new Color(14, 209, 69));
    g2d.fillOval(playerSpawnX, playerSpawnY, 20, 20);

    g2d.dispose();

    // Write the image to a file
    try {
      File output = new File(fileName);
      ImageIO.write(image, "png", output);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
