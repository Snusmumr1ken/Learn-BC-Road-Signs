package com.example.learnbcroadsigns;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    final String DEBUG = "DEBUG";

    List<Question> questions;
    List<Sign> signs;
    Map<String, Sign> idSign;
    Map<Sign.Type, List<String>> typeToDescriptions;

    int questionIdx = 0;
    Question currentQuestion;
    int correctAnswers = 0;
    int wrongAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            initializeSigns();

            Log.d(DEBUG, "List signs initialized: " + signs);
            Log.d(DEBUG, "idSign map initialized: " + idSign);
            Log.d(DEBUG, "typeToDescriptions map initialized: " + typeToDescriptions);

            initializeQuestions();

            Log.d(DEBUG, "List questions initialized: " + questions);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentQuestion = questions.get(questionIdx);
        Log.d(DEBUG, "Current question: " + currentQuestion);
        showQuestion(currentQuestion);
    }

    private void showQuestion(Question question) {
        final TextView questionLabel = findViewById(R.id.question_label);
        questionLabel.setText(question.question);

        final ImageView image = findViewById(R.id.question_image);
        Log.d(DEBUG, "Current question: " + question.question);
        final int resourceId = getResources().getIdentifier("@drawable/" + question.sign.id, "drawable", getPackageName());
        final Drawable drawable = getDrawable(resourceId);
        image.setImageDrawable(drawable);

        RadioGroup radioGroup = findViewById(R.id.radio_answers);
        Drawable simpleBackground = getDrawable(R.drawable.textshape);

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            final RadioButton button = (RadioButton) radioGroup.getChildAt(i);
            button.setBackground(simpleBackground);
            button.setText(question.answers.get(i));
            button.setEnabled(true);
        }
    }

    private void initializeQuestions() throws JSONException {
        Log.d(DEBUG, "initializeQuestions()");

        questions = new ArrayList<>();

        final JSONObject obj = new JSONObject(readJSONFromAsset("questions.json"));
        final JSONArray questionsAsArray = obj.getJSONArray("questions");

        for (int i = 0; i < questionsAsArray.length(); i++) {
            final JSONObject curr = questionsAsArray.getJSONObject(i);

            final String signId = curr.getString("signId");
            final String questionStr = curr.getString("question");
            final String answerType = curr.getString("answer");
            final Sign currSign = idSign.get(signId);
            final String answer = (answerType.equals("description")) ? currSign.description : currSign.type.toString();

            final List<String> possibleAnswers = (answerType.equals("description")) ?
                    typeToDescriptions.get(currSign.type) :
                    Stream.of(Sign.Type.values())
                        .map(Enum::name)
                        .collect(Collectors.toList());

            final Question question = new Question(
                    currSign,
                    questionStr,
                    answer,
                    possibleAnswers);

            Log.d(DEBUG, "New question added: " + question);
            questions.add(question);
        }
        Collections.shuffle(questions);
    }

    private void initializeSigns() throws JSONException {
        Log.d(DEBUG, "initializeSigns()");

        signs = new ArrayList<>();
        idSign = new HashMap<>();
        typeToDescriptions = new HashMap<>();

        final JSONObject obj = new JSONObject(readJSONFromAsset("signs.json"));
        final JSONArray signsAsArray = obj.getJSONArray("signs");

        for (int i = 0; i < signsAsArray.length(); i++) {
            final JSONObject curr = signsAsArray.getJSONObject(i);

            final String id = curr.getString("id");
            final String description = curr.getString("description");
            final String type = curr.getString("type");

            List<String> descriptions = typeToDescriptions.getOrDefault(Sign.Type.valueOf(type), new ArrayList<>());
            descriptions.add(description);
            typeToDescriptions.put(Sign.Type.valueOf(type), descriptions);

            final Sign sign = new Sign(id, description, type);
            Log.d(DEBUG, "Sign found: " + sign);
            signs.add(sign);
            idSign.put(id, sign);
        }
    }

    private String readJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = getAssets().open(fileName);
            final int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void onVerifyButtonClicked(View view) {
        RadioGroup radioGroup = findViewById(R.id.radio_answers);

        RadioButton clickedButton = null;
        RadioButton correctButton = null;

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton) radioGroup.getChildAt(i);
            button.setEnabled(false);

            if (button.getText().equals(currentQuestion.answer)) {
                correctButton = button;
            }

            if (button.isChecked()) {
                clickedButton = button;
            }
        }

        Drawable greenBack = getDrawable(R.drawable.correct_answer);
        correctButton.setBackground(greenBack);

        if (clickedButton != null) {
            if (clickedButton != correctButton) {
                // if it's a wrong answer
                Drawable redBack = getDrawable(R.drawable.wrong_answer);
                clickedButton.setBackground(redBack);
                wrongAnswers++;
            } else {
                correctAnswers++;
            }
        } else {
            // if there is no answer at all
            wrongAnswers++;
        }

        view.setVisibility(View.INVISIBLE);
        Button nextQuestionButton = findViewById(R.id.next_button);
        nextQuestionButton.setVisibility(View.VISIBLE);

        questionIdx++;

        setCorrectWrongCounters();
    }

    private void setCorrectWrongCounters() {
        TextView correctAnswersCountView = findViewById(R.id.correct_answers_count);
        TextView wrongAnswersCountView = findViewById(R.id.wrong_answers_count);

        String correct = "Correct: " + correctAnswers;
        String wrong = "Wrong: " + wrongAnswers;

        correctAnswersCountView.setText(correct);
        wrongAnswersCountView.setText(wrong);
    }

    public void onNextButtonClicked(View view) {
        view.setVisibility(View.INVISIBLE);
        Button verifyButton = findViewById(R.id.verify_button);
        verifyButton.setVisibility(View.VISIBLE);

        if (questionIdx == questions.size()) {
            Collections.shuffle(questions);
            questionIdx = 0;
        }

        currentQuestion = questions.get(questionIdx);
        showQuestion(currentQuestion);
    }
}
