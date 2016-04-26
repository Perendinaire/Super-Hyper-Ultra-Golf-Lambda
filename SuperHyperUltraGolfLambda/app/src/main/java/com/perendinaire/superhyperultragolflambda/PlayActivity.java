package com.perendinaire.superhyperultragolflambda;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayActivity extends ActionBarActivity implements SensorEventListener {
    //The view where graphics will be drawn
    private DrawingView theDrawingView;
    private boolean hasBeenInitialised = false;
    //Accelerometer values to be used in the game
    private SensorManager theSensorManager;
    private Sensor THE_ACCELEROMETER;
    private float[] sensorValues = {0, 0, 0};
    //Tuning parameters for the game
    private static final float DRAG = 0.97f;
    private static final float ACCEL = 3f;
    private static final float MAXSPEED = 20;
    private static final float HOMINGSPEED = 0.5f;
    private static final float ROAMINGSPEED = 1;

    private int theScore = 0;

    private int theLevelNum;
    private int[][] theLevel;
    //Various objects which make up the game - a player, a goal, colliders and enemies
    private Actor thePlayer;
    private boolean canShoot = false;
    private boolean playerWon = false;
    private boolean playerLost = false;

    private Actor theGoal;
    private final ArrayList<Actor> actors = new ArrayList<>();
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Collider> obstacles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theDrawingView = new DrawingView(this);
        setContentView(theDrawingView);
        // Action bar hidden using onWindowFocusChanged below
        // Lock the screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Set up accelerometer sensor
        theSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Check that we actually got an accelerometer
        if (theSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            THE_ACCELEROMETER = theSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            Log.e("SensorSetup", "Could not find any accelerometer!");
            Toast theMessage = Toast.makeText(getApplicationContext(),
                    "Could not find any accelerometer!", Toast.LENGTH_SHORT);
            theMessage.show();
            finish();
        }

        // Get the Level Number
        Intent recievedIntent = getIntent();
        theLevelNum = recievedIntent.getIntExtra(MainActivity.LEVEL_NUM, 0);
        // Randomly generate the random stage
        if(theLevelNum == 5) {
            theLevel = Level.getRandomLevel();
        }
        else {
            theLevel = Level.theLevels[theLevelNum];
        }
    }

    private class DrawingView extends View {
        //Relative sizes according to screen size
        private int width, height, mapUnit, miniUnit, nanoUnit, picoUnit;
        private final Paint thePaint = new Paint();
        private final int GREENVIS = getResources().getColor(R.color.GREENVIS);
        private final int MAGENTAVIS = getResources().getColor(R.color.MAGENTAVIS);

        public DrawingView(Context context) {
            super(context);
            //keep the screen on
            setKeepScreenOn(true);

            // Set up the paint object and text
            thePaint.setStyle(Paint.Style.FILL);
            thePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            thePaint.setTextSize(64);
            thePaint.setAntiAlias(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (!hasBeenInitialised) initialise();

            handleCollisions();

            handlePlayerPhysics();

            // Draw the background
            thePaint.setColor(GREENVIS);
            canvas.drawPaint(thePaint);

            //Draw the colliders
            for (Collider theObstacle : obstacles) {
                thePaint.setColor(theObstacle.defaultColor);
                theObstacle.draw(canvas, thePaint);
            }

            // Update and draw the balls
            for (Actor theActor : actors) {
                // Update positions and draw
                theActor.move();
                thePaint.setColor(theActor.defaultColor);
                // Colour intersecting/phasing balls --debug
                for (Collider theObstacle : obstacles) {
                    if (theObstacle.intersectBall(theActor.pos, theActor.rad)) {
                        thePaint.setColor(Color.GRAY);
                        break;
                    }
                }
                theActor.draw(canvas, thePaint);
            }

            //Draw the score
            thePaint.setColor(MAGENTAVIS);
            canvas.drawText("Shots: " + theScore, 32, 64, thePaint);

            checkPlayerWinLoseState(canvas);

            checkPlayerCanMove(canvas);

            //Force everything on the screen to redraw
            invalidate();
        }

        private void checkPlayerWinLoseState(Canvas theCanvas) {
            //LOSE if the player hits an enemy
            if (!playerWon) {
                for (Enemy theEnemy : enemies) {
                    if (theEnemy.enemyMove(thePlayer)) {
                        //TODO: Perhaps add a white-screen flash, for collision with enemies
                        playerLost = true;
                        theScore = -1;
                        handleEnd("YOU LOST :(", theCanvas);
                    }
                }
            }
            //WIN if the player hits the goal
            if (!playerLost && thePlayer.intersectBall(theGoal.pos, theGoal.rad)) {
                playerWon = true;
                handleEnd("YOU WON :)", theCanvas);
            }
        }

        private void checkPlayerCanMove(Canvas theCanvas) {
            //Let the player move just before the ball stops
            if (Math.abs(thePlayer.delta.x + thePlayer.delta.y) < 0.1) {
                canShoot = true;
                thePlayer.defaultColor = Color.WHITE;
                //Don't draw indicator when in fail or win state
                if (!playerWon && !playerLost) {
                    drawShotIndicator(theCanvas);
                }
            } else {
                canShoot = false;
                thePlayer.defaultColor = MAGENTAVIS;
            }
        }

        private void handlePlayerPhysics() {
            //apply deceleration to the player
            thePlayer.delta.x *= DRAG;
            thePlayer.delta.y *= DRAG;

            //Force max player speed
            if (thePlayer.delta.x > MAXSPEED) {
                thePlayer.delta.x = MAXSPEED;
            } else if (thePlayer.delta.x < -MAXSPEED) {
                thePlayer.delta.x = -MAXSPEED;
            }
            if (thePlayer.delta.y > MAXSPEED) {
                thePlayer.delta.y = MAXSPEED;
            } else if (thePlayer.delta.y < -MAXSPEED) {
                thePlayer.delta.y = -MAXSPEED;
            }
        }

        private void handleCollisions() {
            // Bounce off the walls
            for (Actor theActor : actors) {
                if (theActor.pos.x + theActor.rad > width) {
                    theActor.pos.x = width - theActor.rad;
                    theActor.delta.x = -theActor.delta.x;
                } else if (theActor.pos.x - theActor.rad < 0) {
                    theActor.pos.x = theActor.rad;
                    theActor.delta.x = -theActor.delta.x;
                }
                if (theActor.pos.y + theActor.rad > height) {
                    theActor.pos.y = height - theActor.rad;
                    theActor.delta.y = -theActor.delta.y;
                } else if (theActor.pos.y - theActor.rad < 0) {
                    theActor.pos.y = theActor.rad;
                    theActor.delta.y = -theActor.delta.y;
                }
            }
            // Bounce off colliders
            for (Actor theActor : actors) {
                for (Collider theObstacle : obstacles) {
                    theActor.delta = theObstacle.reflectBall(theActor);
                }
            }
        }

        /**
         * Helper method for handling game-end states
         */
        private void handleEnd(String theMessage, Canvas theCanvas) {
            theCanvas.drawText(theMessage, mapUnit * 4, height / 2 - mapUnit, thePaint);
            theCanvas.drawText("TOUCH TO RETURN...", mapUnit * 4, height / 2, thePaint);
            //Slow all moving objects down
            for(Actor theActor : actors) {
                theActor.delta.x /= 1.5f;
                theActor.delta.y /= 1.5f;
            }
        }

        /**
         * Draws a shot indicator which helps the player judge where the ball will move to
         */
        private void drawShotIndicator(Canvas theCanvas) {
            //determine shot strength and direction
            float strength = (Math.abs(sensorValues[0]) + Math.abs(sensorValues[1])) * picoUnit;
            if (strength < 0) strength = 0;
            if (strength > nanoUnit) strength = nanoUnit;
            thePaint.setColor(MAGENTAVIS);
            theCanvas.drawCircle(thePlayer.pos.x + sensorValues[1] * 8, thePlayer.pos.y + sensorValues[0] * 8, strength, thePaint);
        }

        /**
         * Set up the game stage, and populate with game objects. To be performed once.
         */
        private void initialise() {
            width = getWidth();
            //Levels assume 16:10 layout, so derive height from width
            height = width * 10 / 16;
            mapUnit = width / 16;
            miniUnit = mapUnit / 2;
            nanoUnit = miniUnit / 2;
            picoUnit = nanoUnit / 2;
            //Various colors for game objects
            int indigoPro = getResources().getColor(R.color.INDIGOPRO);
            int greenPro = getResources().getColor(R.color.GREENPRO);
            int deepOrangePro = getResources().getColor(R.color.DEEPORANGEPRO);
            int redPro = getResources().getColor(R.color.REDPRO);

            for (int col = 0; col < theLevel[0].length; col++) {
                for (int row = 0; row < theLevel.length; row++) {
                    switch (theLevel[row][col]) {
                        case 1: //Square - serves as a wall
                            obstacles.add(new Rectangle(makeV2(col, row, mapUnit, 0), mapUnit, mapUnit, indigoPro));
                            break;
                        case 2: //Circle - bounces the player around
                            obstacles.add(new Circle(makeV2(col, row, mapUnit, miniUnit), miniUnit, greenPro));
                            break;
                        case 3: //Player
                            thePlayer = new Actor(makeV2(col, row, mapUnit, miniUnit), miniUnit - picoUnit, new V2(0, 0), Color.WHITE);
                            actors.add(thePlayer);
                            break;
                        case 4: //Roamer - pseudorandomly either moves vertically or horizontally
                            V2 theDelta;
                            if (((col + row) % 2) == 0) {
                                theDelta = new V2(ROAMINGSPEED, 0);
                            } else {
                                theDelta = new V2(0, ROAMINGSPEED);
                            }
                            Enemy theRoaming = new Enemy(makeV2(col, row, mapUnit, miniUnit), nanoUnit, theDelta, deepOrangePro, ROAMINGSPEED);
                            actors.add(theRoaming);
                            enemies.add(theRoaming);
                            break;
                        case 5: //Homing
                            Homing theHoming = new Homing(makeV2(col, row, mapUnit, miniUnit), nanoUnit, new V2(0, 0), redPro, HOMINGSPEED);
                            actors.add(theHoming);
                            enemies.add(theHoming);
                            break;
                        case 6: //Goal
                            theGoal = new Actor(makeV2(col, row, mapUnit, miniUnit), miniUnit, new V2(0, 0), Color.BLACK);
                            actors.add(theGoal);
                            break;
                    }
                }
            }
            hasBeenInitialised = true;
        }

        /**
         * Helper method for simplifying the process of making game objects
         */
        private V2 makeV2(int theColumn, int theRow, int mapUnit, int offset) {
            return new V2(theColumn * mapUnit + offset, theRow * mapUnit + offset);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent theEvent) {
        // Handle just the accelerometer
        if (theEvent.sensor == THE_ACCELEROMETER && hasBeenInitialised) {
            sensorValues = theEvent.values;
        }
    }

    /**
     * Move the player, or end the game and return the score, when the screen is touched
     */
    @Override
    public boolean onTouchEvent(MotionEvent theEvent) {
        if (hasBeenInitialised && theEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (playerLost || playerWon) {
                Intent theIntent = new Intent();
                theIntent.putExtra(MainActivity.GAME_SCORE, theScore);
                theIntent.putExtra(MainActivity.LEVEL_NUM, theLevelNum);
                setResult(MainActivity.RETURN_GAME, theIntent);
                finish();
            }

            if (canShoot) {
                thePlayer.delta.x = sensorValues[1] * -ACCEL; //sensors flipped because landscape, inverted for "tilt up for power"
                thePlayer.delta.y = sensorValues[0] * -ACCEL;
                theScore++;

            } else {
                Toast theMessage = Toast.makeText(getApplicationContext(),
                        "The ball must be stationary before you can shoot again!", Toast.LENGTH_SHORT);
                theMessage.show();
            }
        }
        return true;
    }

    /**
     * End the game prematurely when "back" is pressed
     */
    @Override
    public void onBackPressed () {
        Intent theIntent = new Intent();
        theIntent.putExtra(MainActivity.GAME_SCORE, -1);
        theIntent.putExtra(MainActivity.LEVEL_NUM, theLevelNum);
        setResult(MainActivity.RETURN_GAME, theIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        theSensorManager.registerListener(this, THE_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        theSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor theSensor, int theAccuracy) {
    }

    /**
     * Set the game screen to fullscreen whenever it has focus
     *
     * @param hasFocus whether the window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //set UI visibility to a combination of all "fullscreen" flags
        if (hasFocus) {
            theDrawingView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}