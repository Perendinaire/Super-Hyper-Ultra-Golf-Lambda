package com.perendinaire.superhyperultragolflambda;

import android.util.Log;

import java.util.Random;

/**
 * Level holds a collection of levels, as well as the ability to randomly generate levels
 */
class Level {
    //0=space, 1=square collider, 2= circle collider, 3= player, 4= roamer, 5= homing, 6=goal
    public static final int[][][] theLevels = new int[][][]{
            {
                    {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 6, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 3, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0},
                    {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0},
                    {0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0}
            }, {
                    {0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                    {0, 3, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 0, 1, 1, 1, 2, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {0, 0, 1, 1, 1, 1, 0, 0, 0, 2, 0, 0, 1, 1, 0, 1},
                    {0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1},
                    {1, 0, 0, 0, 2, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
                    {1, 2, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 6, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1}
            }, {
                    {0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                    {0, 3, 0, 1, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 1, 4, 0, 0, 1, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 4, 4, 1, 0, 0, 2, 1, 1, 1, 1, 0, 1},
                    {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 4, 0, 0, 4, 0},
                    {0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 6, 0},
                    {0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}
            },
            {
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5},
                    {0, 5, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                    {2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 2, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1},
                    {0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0},
                    {0, 6, 5, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, 0},
                    {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}
            },
            {
                    {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 4, 4, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0},
                    {0, 3, 1, 0, 0, 0, 1, 0, 1, 6, 1, 0, 1, 0, 1, 0},
                    {0, 1, 0, 0, 1, 0, 1, 0, 1, 2, 1, 1, 1, 1, 2, 0},
                    {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                    {0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 2, 0},
                    {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 2, 0, 0, 1, 0, 4},
                    {0, 0, 0, 1, 5, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                    {0, 0, 1, 5, 0, 1, 0, 0, 2, 0, 0, 2, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1}
            }
    };

    /**
     * Randomly generates a level which is mostly playable in the game
     *
     * @return the randomly generated level
     */
    public static int[][] getRandomLevel() {
        //levels are 16:10
        int[][] levelArray = new int[10][16];
        Random theRandom = new Random();

        // Chance of element occurring, that is, 1/N
        final int homing = 90;
        final int bouncer = 16;
        final int wall = 6;
        final int roaming = 70;

        levelArray = insertPlayerAndGoal(levelArray, theRandom);
        //Insert other game elements into the level
        for (int col = 0; col < levelArray[0].length; col++) {
            for (int row = 0; row < levelArray.length; row++) {
                if (levelArray[row][col] == 0) {
                    if (theRandom.nextInt(homing) == 0) {
                        levelArray[row][col] = 5;
                    } else if (theRandom.nextInt(roaming) == 0) {
                        levelArray[row][col] = 4;
                    } else if (theRandom.nextInt(wall) == 0) {
                        levelArray[row][col] = 1;
                    } else if (theRandom.nextInt(bouncer) == 0) {
                        levelArray[row][col] = 2;
                    }
                }
            }
        }
        Log.e("Level", "Is returning a map");
        return levelArray;
    }

    /**
     * Insert the player and goal randomly into a level
     */
    private static int[][] insertPlayerAndGoal(int[][] levelArray, Random theRandom) {
        // Chance of player or goal occurring, that is, 1/N
        final int playerGoal = 32;
        boolean hasGoal = false;
        while (true) {
            for (int row = 0; row < levelArray.length; row++) {
                for (int col = 0; col < levelArray[0].length; col++) {
                    if (theRandom.nextInt(playerGoal) == 0) {
                        if (!hasGoal) {
                            // Add the goal
                            levelArray[row][col] = 6;
                            hasGoal = true;
                        } else {
                            // Add the player
                            if (levelArray[row][col] != 6) {
                                levelArray[row][col] = 3;
                                Log.e("Level", "Has player");
                                return levelArray;
                            }
                        }
                    }
                }
            }
        }
    }
}
