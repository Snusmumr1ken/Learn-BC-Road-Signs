package com.example.learnbcroadsigns;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
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

        showQuestion(questions.get(0));
    }

    private void showQuestion(Question question) {
        final TextView questionLabel = findViewById(R.id.question_label);
        questionLabel.setText(question.question);

        final ImageView image = findViewById(R.id.question_image);
        Log.d(DEBUG, question.sign.id);
        final int resourceId = getResources().getIdentifier("@drawable/" + question.sign.id, "drawable", getPackageName());
        final Drawable drawable = getDrawable(resourceId);
        image.setImageDrawable(drawable);

        final RadioButton o1 = findViewById(R.id.answer_option_1);
        o1.setText(question.answers.get(0));
        final RadioButton o2 = findViewById(R.id.answer_option_2);
        o2.setText(question.answers.get(1));
        final RadioButton o3 = findViewById(R.id.answer_option_3);
        o3.setText(question.answers.get(2));
        final RadioButton o4 = findViewById(R.id.answer_option_4);
        o4.setText(question.answers.get(3));
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
}