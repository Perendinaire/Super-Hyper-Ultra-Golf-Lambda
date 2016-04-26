package com.perendinaire.superhyperultragolflambda;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    public static final int RETURN_GAME = 1;
    public static final String GAME_SCORE = "GameScore";
    public static final String LEVEL_NUM = "LevelNum";
    private static final int[] levelButtons = { R.id.button0 , R.id.button1 , R.id.button2 , R.id.button3 , R.id.button4 , R.id.button5 };
    private static final int[] levelStringIDs = { R.string.level0, R.string.level1, R.string.level2, R.string.level3, R.string.level4, R.string.level5 };

    /**
     * Main activity contains introduction, level select and score details
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //lock the screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * Enter the chosen level, when that level is selected
     */
    public void startLevel(View theView) {
        Intent theIntent = new Intent(this, PlayActivity.class);
        for (int i = 0; i < levelButtons.length; i++) {
            if (findViewById(levelButtons[i]) == theView) {
                theIntent.putExtra(LEVEL_NUM, i);
                startActivityForResult(theIntent, RETURN_GAME);
            }
        }
    }

    /**
     * Get and show the score for a particular level, after exiting from that level
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent theResponse) {
        if(requestCode == RETURN_GAME) {
            int theLevelNum = theResponse.getIntExtra(LEVEL_NUM, 0);
            int theScore = theResponse.getIntExtra(GAME_SCORE, -1);

            //Find the button and default text which are related to the level
            Button theLevelButton = (Button) findViewById(levelButtons[theLevelNum]);
            String theLevelText = getResources().getString(levelStringIDs[theLevelNum]);

            //Show the score, or some default value if the score was -1
            theLevelButton.setText(theLevelText + ((theScore == -1)? "--" : ""+theScore));
            theLevelButton.invalidate();
        }
    }

    /**
     * Set the main screen to fullscreen whenever it has focus
     * @param hasFocus whether the window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //set UI visibility to a combination of all "fullscreen" flags
        if (hasFocus) {
            findViewById(R.id.main_layout).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}