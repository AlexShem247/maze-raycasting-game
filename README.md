# Randomised Maze Ray Casting Game

The **Randomised Maze Ray Casting Game** is a maze exploration game where players navigate through randomly generated mazes, collect all coins, and avoid zombies to progress through increasingly difficult levels. The game leverages on [Ray Casting](#ray-casting), a technique using in early video games to efficiently render a 3D world from a 2D playing field, and a modified [Depth-First Search (DFS) Maze Generation Algorithm](#random-maze-generation), offering an immersive gameplay experience.

*Game written in Java using the libGDX library.*

Developed by **Alexander Shemaly** in 2023.

## Table of Contents

- [Installation](#installation)
- [How to Play](#how-to-play)
- [Ray Casting](#ray-casting)
- [Random Maze Generation](#random-maze-generation)
- [Why I made the Game?](#why-i-made-the-game)
- [Copyright](#license)
- [License](#license)

## Installation

To install the game on your computer, get the latest version from the [Releases page](https://github.com/AlexShem247/maze-raycasting-game/releases).

1. **On Windows:**
   - Download the Windows `.exe` and run the program.

2. **On a Computer with Java Installed:**
   - Download the `.JAR` file and run the program using the JVM.

## How to Play

1. **Start the Round:**
   - A preview of the maze is shown on screen.
        - The green dot represents your starting position.
        - The yellow dots represent the location of the coins.
        - The red dots are the starting locations of the zombies.

2. **Navigate the Maze:**
   - Use the **WASD** keys to move your character through the maze:
     - **W**: Move forward
     - **A**: Move left
     - **S**: Move backward
     - **D**: Move right

3. **Collect Coins:**
   - Explore the maze to find and collect all the coins. The level will not complete until all coins are gathered.

4. **Avoid Zombies:**
   - Zombies roam the maze and will chase you if they see you. Avoid contact to prevent health loss.

5. **Advance to the Next Level:**
   - Once all coins are collected, a more difficult level will be unlocked.

## Ray Casting

Ray casting works by projecting rays from the player’s position into the environment to determine what objects (walls, coins, zombies) are visible and how far away they are. Each ray is cast at a specific angle corresponding to the player’s field of view, and the game calculates where these rays intersect with the walls of the maze. These intersections are used to determine the height and position of walls on the screen, creating the illusion of depth.

### Basic Concepts

1. **Rays and Angles:**
   - The player’s viewpoint is the origin from which rays are cast.
   - Rays are projected at regular angular intervals across the player’s field of view, covering a range of angles from the left to the right edge of the screen.

2. **Intersection Calculation:**
   - For each ray, the game calculates where it intersects with the walls of the maze. This is done by solving equations that represent the lines (or walls) in the maze.
   - The intersection point tells us how far away the wall is and at what height it should be drawn on the screen.

3. **Distance Calculation:**
   - The distance from the player to the wall is calculated using the formula:
     $$
     d = \frac{{\text{distance to wall}}}{{\cos(\theta)}}
     $$
     where $d$ is the perpendicular distance to the wall, and $\theta$ is the angle of the ray relative to the player’s viewing direction. This correction accounts for the "fish-eye" distortion that would otherwise occur.

4. **Projection:**
   - The height of the wall on the screen is inversely proportional to the distance calculated. The closer the wall, the taller it appears, and vice versa.
   - The height $h$ of the wall slice on the screen is given by:
     $$
     h = \frac{{C}}{{d}}
     $$
     where $C$ is a constant that scales the height based on the screen resolution and the player's field of view.

To learn more, see [Ray casting](https://en.wikipedia.org/wiki/Ray_casting).

## Random Maze Generation

The Depth-First Search (DFS) technique generates a maze by systematically visiting each cell, carving out paths as it goes. This process mimics the way one might explore a real maze: moving forward when possible and backtracking when necessary to find unexplored areas.

### Basic Steps

1. **Grid Initialisation:**
   - The maze begins as a grid where each cell is initially surrounded by walls on all four sides.

2. **Choosing the Starting Point:**
   - A random cell is chosen as the starting point. This cell is marked as visited, and the algorithm begins to explore from here.

3. **Path Carving:**
   - From the current cell, the algorithm randomly selects an unvisited neighboring cell.
   - It removes the wall between the current cell and the chosen neighbor, creating a passage, and then moves to the neighboring cell, marking it as visited.
   - This process is repeated, with the algorithm carving out a path and moving deeper into the maze.

4. **Backtracking:**
   - If the algorithm reaches a cell with no unvisited neighbors, it backtracks to the previous cell in the stack and continues exploring from there.
   - This backtracking ensures that every possible path is explored and connected, resulting in a fully traversable maze.

5. **Completing the Maze:**
   - The algorithm continues until all cells have been visited, at which point the maze is complete.

To learn more, see [Maze generation algorithm](https://en.wikipedia.org/wiki/Maze_generation_algorithm).

### Dead End Removal

Once the initial maze is generated, an additional step is taken to remove dead ends, preventing people from getting stuck when a zombie chases them into the corner.

- **Dead End Detection:**
  - After the maze is fully generated, the algorithm scans each cell to identify dead ends.
  - A dead end is identified if the cell has only one open passage (i.e., three walls surrounding it).

- **Dead End Removal:**
  - When a dead end is detected, the algorithm removes one of the surrounding walls, thereby creating an additional passage and eliminating the dead end.
  - This process reduces the number of one-way paths in the maze, making it more open and challenging to navigate.

## Why I made the Game?
My fascination with 2.5D graphics and the complex mathematics behind them inspired me to create this game. I’ve always been intrigued by how simple 2D elements can be transformed into immersive 3D-like environments through techniques like ray casting. Additionally, I wanted to apply and deepen my understanding of algorithms and mathematical concepts that I had previously studied. 
## Copyright
Here is a copyright section for the README file that acknowledges the assets used in your game:

---

## Copyright

The game uses the following assets:

- **Wall Textures:** Provided by [OpenGameArt](https://opengameart.org/content/lab-textures?page=1)
- **Zombie & Damage Sound Effect:** From Minecraft, credited to Mojang
- **Coin Animation:** Sourced from [The Spriters Resource](https://www.spriters-resource.com/mobile/coinmaster/sheet/147479/)
- **Coin Sound Effect:** Mario Coin Sound, credited to Nintendo

All rights to the original assets belong to their respective creators. These assets are used in accordance with their licensing terms and are acknowledged here to give proper credit.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Prepare to be mind blown by the power of mathematics and 2D shape drawing.
