package game;


import edu.augustana.csc490.gamestarter.Line;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.util.Log;

import maze.*;

import java.util.ArrayList;
import java.util.Random;
/**
 * Created by Ethan Halsall on 3/30/2015.
 */
public class Game {

    //game data
    private Maze maze;
    private MazeLineArray mazeWalls;

    private Path playerPath;
    private Path[] AISolutions;
    private Path[] AIPaths;

    private int[] playerPoints;
    private int time;
    private Mouse playerMouse;
    private Mouse[] opponentMice;
    private PowerUpMap powerUps;

    private int numOpponents;
    private int AIDifficulty;
    private boolean isNetworked;

    private Random rand;

    //mouse data
    public Picture playerMouseImage = new Picture();
    public Paint playerMousePaint = new Paint();

    //default game settings

    //maze dimensions
    public static final int HEIGHT = 10;
    public static final int WIDTH = 10;
    //maze types
    public static final int NUM_MAZE_TYPES = 3;
    public static final int RECURSIVE_BACKTRACKER_MAZE = 1;
    public static final int HUNT_AND_KILL_MAZE = 2;
    public static final int PRIM_MAZE = 3;

    //player settings
    public static final int NUM_OPPONENTS = 3;
    public static final int TIME = 120;

    //AI settings
    public static final int AI_DIFFICULTY = 5;

    //network settings
    public static final boolean IS_NETWORKED = false;



    //creates a new game with the standard game data defined above in the final feilds
    public Game(){
        rand = new Random();
        initializeGame(WIDTH, HEIGHT, rand.nextInt(NUM_MAZE_TYPES), TIME, NUM_OPPONENTS, AI_DIFFICULTY, IS_NETWORKED);
    }

    public Game(int width, int height, int mazeType){
        initializeGame(width, height, mazeType, TIME, NUM_OPPONENTS, AI_DIFFICULTY, IS_NETWORKED);
    }

    private void initializeGame(int mazeWidth, int mazeHeight, int mazeType, int time, int numOpponents, int AIDifficulty, boolean isNetworked){
        maze = createMaze(mazeWidth, mazeHeight, mazeType);
        powerUps = new PowerUpMap(maze);
        this.time = time;
        this.numOpponents = numOpponents;
        this.AIDifficulty = AIDifficulty;
        playerMouse = new PlayerMouse(playerMousePaint, playerMouseImage);
        opponentMice = new Mouse[numOpponents];

        this.isNetworked = isNetworked;
        for (int i = 0; i < numOpponents; i++) {
            if (isNetworked) {
                opponentMice[i] = new NetworkedMouse();
            }else {
                opponentMice[i] = new AIMouse();
            }
        }
       // mazeWalls = new MazeLineArray(maze, screenWidth, screenHeight);
    }

    private Maze createMaze(int width, int height, int mazeType){
        MazeGenerator mazeGen;
        switch (mazeType){
            case PRIM_MAZE:
                mazeGen = new PrimMaze(width, height);
                break;
            case RECURSIVE_BACKTRACKER_MAZE:
                mazeGen = new RecursiveBacktrackerMaze(width, height);
                break;
            case HUNT_AND_KILL_MAZE:
                mazeGen = new HuntAndKillMaze(width, height);
                break;
            default:
                mazeGen = new RecursiveBacktrackerMaze(width, height);
        }
        return mazeGen.getMaze();
    }

    public boolean movePlayerMouse(int x, int y, int direction){
        if(!maze.isWallPresent(x, y, direction)){
            return playerMouse.moveMouse(x, y);
        }
        return false;
    }


    public void paintMaze(Canvas c, Paint p, int screenWidth, int screenHeight){
        Cell start = maze.getStart();
        Cell end = maze.getEnd();

        //check to see if the maze line array needs to be generated or regenerated.
        if(mazeWalls == null){
            mazeWalls = new MazeLineArray(maze, screenWidth, screenHeight);
        } else if(screenHeight != mazeWalls.getScreenHeight() || screenWidth != mazeWalls.getScreenWidth()) {
            mazeWalls = new MazeLineArray(maze, screenWidth, screenHeight);
        } else if(!maze.equals(mazeWalls.getMaze())){
            mazeWalls = new MazeLineArray(maze, screenWidth, screenHeight);
        }

        for (int i = 0; i < mazeWalls.getSize(); i++){
            Line line = mazeWalls.getLineAtIndex(i);
            c.drawLine(line.startX, line.startY, line.endX, line.endY, p);
            //Log.i("line", line.startX + " " + line.startY + " " + line.endX + " " +line.endY);
        }

    }
}
