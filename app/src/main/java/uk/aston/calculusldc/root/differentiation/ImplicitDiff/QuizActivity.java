package uk.aston.calculusldc.root.differentiation.ImplicitDiff;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.agog.mathdisplay.MTMathView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.StringTokenizer;

import uk.aston.calculusldc.MainActivity;
import uk.aston.calculusldc.R;
import uk.aston.calculusldc.root.Database.Score;
import uk.aston.calculusldc.root.Database.ScoreViewModel;
import uk.aston.calculusldc.root.differentiation.SavedFragment;
import uk.aston.calculusldc.root.differentiation.SearchFragment;

public class QuizActivity extends AppCompatActivity
{

    private final QuestionBank mQuestionLibrary = new QuestionBank();

    private TextView mScoreView;   // view for current total score
    private TextView mScoreTextView;
    private MTMathView mQuestionView;  //current question to answer
    private MTMathView mQuestionView1;  //current question to answer
    private Button mButtonChoice1; // multiple choice 1 for mQuestionView
    private Button mButtonChoice2; // multiple choice 2 for mQuestionView
    private Button mButtonChoice3; // multiple choice 3 for mQuestionView
    private Button mButtonChoice4; // multiple choice 4 for mQuestionView

    private MTMathView mAnswerView;

    private String mAnswer;  // correct answer for question in mQuestionView
    private int mScore = 0;  // current total score
    private int mQuestionNumber = 0; // current question number

    private ScoreViewModel mScoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_chain_rule);
        // setup screen for the first question with four alternative to answer
        mScoreView = findViewById(R.id.score);
        mQuestionView = findViewById(R.id.question);
        mScoreTextView = findViewById(R.id.score_text);

        mButtonChoice1 = findViewById(R.id.choice1);
        mButtonChoice1.setText("hello");

        mButtonChoice2 = findViewById(R.id.choice2);
        mButtonChoice3 = findViewById(R.id.choice3);
        mButtonChoice4 = findViewById(R.id.choice4);

        mScoreViewModel = ViewModelProviders.of(this).get(ScoreViewModel.class);

        //mQuestionLibrary.initQuestions(getApplicationContext(), QuizActivity.this);
        updateQuestion();
        // show current total score for the user
        updateScore(mScore);


        mQuestionView.setLatex("$ x^2+y^2+2xy-6x+2y=3");
        mQuestionView.setFontSize(70);


        BottomNavigationView navView = findViewById(R.id.quizNav);

        //navView.setSelectedItemId(R.id.homeFragment);

        // Perform item selected listener
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {

                    case R.id.searchFragment:
//                        startActivity(new Intent(getApplicationContext(), SearchFragment.class));
//                        overridePendingTransition(0,0);
                        Fragment searchFragment = new SearchFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        transaction.replace(R.id.activity_quiz, searchFragment);
                        transaction.addToBackStack(null);

                        mScoreView.setVisibility(View.GONE);
                        mScoreTextView.setVisibility(View.GONE);
                        mQuestionView.setVisibility(View.GONE);
                        mButtonChoice1.setVisibility(View.GONE);
                        mButtonChoice2.setVisibility(View.GONE);
                        mButtonChoice3.setVisibility(View.GONE);
                        mButtonChoice4.setVisibility(View.GONE);

                        transaction.commit();

                        return true;
                    case R.id.savedFragment:

                        Fragment savedFragment = new SavedFragment();
                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();

                        transaction1.replace(R.id.activity_quiz, savedFragment);
                        transaction1.addToBackStack(null);

                        mScoreView.setVisibility(View.GONE);
                        mScoreTextView.setVisibility(View.GONE);
                        mQuestionView.setVisibility(View.GONE);
                        mButtonChoice1.setVisibility(View.GONE);
                        mButtonChoice2.setVisibility(View.GONE);
                        mButtonChoice3.setVisibility(View.GONE);
                        mButtonChoice4.setVisibility(View.GONE);

                        transaction1.commit();
                        return true;
                    case R.id.homeFragment:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }

    private void updateQuestion(){
        // check if we are not outside array bounds for questions
        if(mQuestionNumber<mQuestionLibrary.getLength() ){
            // set the text for new question,
            // and new 4 alternative to answer on four buttons
            //mQuestionView.setText(mQuestionLibrary.getQuestion(mQuestionNumber));

            //mQuestionView.setLatex(mQuestionLibrary.getQuestion(mQuestionNumber).toString());

            if(mQuestionNumber == 1)
            {
                mQuestionView1 = findViewById(R.id.question);
                mQuestionView1.setLatex("$x^2+y^2+3xy-3x+7y=8");
                mQuestionView1.setFontSize(70);
            }
            else if (mQuestionNumber == 2)
            {

                mQuestionView1.setLatex("$x^2+y^2+xy-8x+3y=4");

            } else if (mQuestionNumber == 3)
            {
                mQuestionView1.setLatex("$x^2+y^2+2xy-8x+5y=6");
            }

            mButtonChoice1.setText(mQuestionLibrary.getChoice(mQuestionNumber, 1));
            mButtonChoice2.setText(mQuestionLibrary.getChoice(mQuestionNumber, 2));
            mButtonChoice3.setText(mQuestionLibrary.getChoice(mQuestionNumber, 3));
            mButtonChoice4.setText(mQuestionLibrary.getChoice(mQuestionNumber,4));
            mAnswer = mQuestionLibrary.getCorrectAnswer(mQuestionNumber);
            mQuestionNumber++;
       }
        else {
            Toast.makeText(QuizActivity.this, "It was the last question!", Toast.LENGTH_SHORT).show();


            Score score = new Score();
            score.setmTopic("Implicit Differentiation");

            String scoreString = mScoreView.getText().toString();
            double scoreDouble = scoreStringtoDoubleConverter(scoreString);

            score.setMscore(scoreDouble);


            try
            {
                mScoreViewModel.insert(score);
                mScoreViewModel.update(score);
                Log.d(TAG,"insertion worked");
            }catch(SQLiteConstraintException e){
                Log.d(TAG, "insertion failed");
            }


            //switch to new activity
            Intent intent = new Intent(QuizActivity.this, HighestScoreActivity.class);
            intent.putExtra("score", mScore); // pass the current score to the second screen
            startActivity(intent);
        }
    }

    // show current total score for the user
    private void updateScore(int point) {
        mScoreView.setText(""+mScore+"/"+mQuestionLibrary.getLength());
    }

    public void onClick(View view) {
        //all logic for all answers buttons in one method
        Button answer = (Button) view;
        // if the answer is correct, increase the score
        if (answer.getText().equals(mAnswer)){
            mScore = mScore + 1;
             Toast.makeText(QuizActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(QuizActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
        // show current total score for the user
        updateScore(mScore);
        // once user answer the question, we move on to the next one, if any
        updateQuestion();
    }


    public Double scoreStringtoDoubleConverter(String scoreString)
    {
        StringTokenizer tokenizer = new StringTokenizer(scoreString, "/");
        Double score = Double.parseDouble(tokenizer.nextToken());

        return score;
    }


 }