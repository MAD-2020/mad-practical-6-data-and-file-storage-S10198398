package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class Main4Activity extends AppCompatActivity {

    /* Hint:
        1. This creates the Whack-A-Mole layout and starts a countdown to ready the user
        2. The game difficulty is based on the selected level
        3. The levels are with the following difficulties.
            a. Level 1 will have a new mole at each 10000ms.
                - i.e. level 1 - 10000ms
                       level 2 - 9000ms
                       level 3 - 8000ms
                       ...
                       level 10 - 1000ms
            b. Each level up will shorten the time to next mole by 100ms with level 10 as 1000 second per mole.
            c. For level 1 ~ 5, there is only 1 mole.
            d. For level 6 ~ 10, there are 2 moles.
            e. Each location of the mole is randomised.
        4. There is an option return to the login page.
     */
    private static final String FILENAME = "Main4Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    CountDownTimer myCountdownTimer;
    CountDownTimer newMolePlaceTimer;
    private Integer scores, levels;
    private TextView scoreText;
    private MyDBHandler dbHandler = new MyDBHandler(this,null,null,1);;
    private String userName;
    private Button backBtn;

    private void readyTimer() {
        myCountdownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                final Toast makeToast = Toast.makeText(Main4Activity.this, "Get Ready in " + l / 1000 + " seconds!", Toast.LENGTH_SHORT);
                makeToast.show();
                Handler time = new Handler();
                time.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeToast.cancel();
                    }
                }, 1000);
                Log.v(TAG, "Ready Countdown!" + l / 1000);
            }

            @Override
            public void onFinish() {
                Log.v(TAG, "Ready Countdown Complete!");
                Toast.makeText(Main4Activity.this, "GO!", Toast.LENGTH_SHORT).show();
                placeMoleTimer(setLevel(levels));
            }
        };
        myCountdownTimer.start();
    }

    private void placeMoleTimer(final Integer i ){
        /* HINT:
           Creates new mole location each second.
           Log.v(TAG, "New Mole Location!");
           setNewMole();
           belongs here.
           This is an infinite countdown timer.
         */
        newMolePlaceTimer = new CountDownTimer(i,1000) {
            @Override
            public void onTick(long l) {
                setNewMole(levels);
                Log.v(TAG,"New Mole Location!");
            }
            @Override
            public void onFinish() {
                placeMoleTimer(setLevel(levels));
            }

        };
        newMolePlaceTimer.start();
    }

    private static final int[] BUTTON_IDS = {
            /* HINT:
                Stores the 9 buttons IDs here for those who wishes to use array to create all 9 buttons.
                You may use if you wish to change or remove to suit your codes.*/
            R.id.Button0,
            R.id.Button1,
            R.id.Button2,
            R.id.Button3,
            R.id.Button4,
            R.id.Button5,
            R.id.Button6,
            R.id.Button7,
            R.id.Button8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        /*Hint:
            This starts the countdown timers one at a time and prepares the user.
            This also prepares level difficulty.
            It also prepares the button listeners to each button.
            You may wish to use the for loop to populate all 9 buttons with listeners.
            It also prepares the back button and updates the user score to the database
            if the back button is selected.
         */
        scoreText = (TextView)findViewById(R.id.scoreText);
        scores = 0;
        scoreText.setText(String.valueOf(scores));
        Intent receivingEnd = getIntent();
        levels = receivingEnd.getIntExtra("Level",0);
        userName = receivingEnd.getStringExtra("Username");
        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myCountdownTimer != null) {
                    myCountdownTimer.cancel();
                }
                if(newMolePlaceTimer != null){
                    newMolePlaceTimer.cancel();
                }
                updateUserScore();
                Intent intent = new Intent(Main4Activity.this,Main3Activity.class);
                intent.putExtra("Username",userName);
                startActivity(intent);
            }
        });

        for(final int id : BUTTON_IDS){
            final Button button = (Button) findViewById(id);
            button.setText("O");
            button.setOnClickListener(new View.OnClickListener() {
                /*  HINT:
                This creates a for loop to populate all 9 buttons with listeners.
                You may use if you wish to remove or change to suit your codes.
                */
                @Override
                public void onClick(View view) {
                    doCheck(button);

                }
            });
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        readyTimer();
    }
    private void doCheck(Button checkButton)
    {
        /* Hint:
        Checks for hit or miss
        Log.v(TAG, FILENAME + ": Hit, score added!");
        Log.v(TAG, FILENAME + ": Missed, point deducted!");
        belongs here.
        */
        if (checkButton.getText() == "*")
        {
            Log.v(TAG,"Hit, score added!");
            scores += 1;
        }
        else{

            Log.v(TAG, "Missed, point deducted!");
            scores -= 1;
        }
        scoreText.setText(String.valueOf(scores));
    }

    public void setNewMole(Integer i)
    {
        for (int id :BUTTON_IDS){
            Button b=findViewById(id);
            b.setText("O");
        }
        if (i<=5)
        {
            Random ran = new Random();
            int randomLocation = ran.nextInt(9);
            Button selectedButton = findViewById(BUTTON_IDS[randomLocation]);
            selectedButton.setText("*");
        }
        else{
            Random ran = new Random();
            int randomLocation = ran.nextInt(9);
            Button selectedButton = findViewById(BUTTON_IDS[randomLocation]);
            selectedButton.setText("*");
            int randomLocation2 = ran.nextInt(9);
            Button selectedButton2 = findViewById(BUTTON_IDS[randomLocation2]);
            selectedButton2.setText("*");
        }

    }

    private void updateUserScore()
    {
        myCountdownTimer.cancel();
        if (newMolePlaceTimer!= null)
        {
            newMolePlaceTimer.cancel();
        }
        UserData userData = dbHandler.findUser(userName);
        if(userData.getScores().get(levels -1)<scores)
        {
            userData.getScores().set(levels -1,scores);
            dbHandler.deleteAccount(userName);
            dbHandler.addUser(userData);
        }
    }

    private Integer setLevel(Integer level)
    {
        if (level==1)
        {
            return 10000;
        }
        else if (level==2)
        {
            return 9000;
        }
        else if (level==3)
        {
            return 8000;
        }
        else if (level==4)
        {
            return 7000;
        }
        else if (level==5)
        {
            return 6000;
        }
        else if (level==6)
        {
            return 5000;
        }
        else if (level==7)
        {
            return 4000;
        } else if (level==8)
        {
            return 3000;
        } else if (level==9)
        {
            return 2000;
        }
        else{
            return 1000;
        }

    }

}
