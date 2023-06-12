package com.example.learnbcroadsigns;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    final Sign sign;
    final String question;
    final String answer;
    List<String> answers;

    public Question(Sign sign, String question, String answer, List<String> possibleAnswers) {
        this.sign = sign;
        this.question = question;
        this.answer = answer;
        generateAnswers(possibleAnswers);
    }

    private void generateAnswers(List<String> possibleAnswers) {
        answers = new ArrayList<>();
        answers.add(answer);
        Collections.shuffle(possibleAnswers);
        int i = 0;
        while (answers.size() != 4) {
            if (!possibleAnswers.get(i).equals(answer)) {
                answers.add(possibleAnswers.get(i));
            }
            i++;
        }
        Collections.shuffle(answers);
    }

    @NonNull
    @Override
    public String toString() {
        return "Question{" +
                "sign=" + sign +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", answers=" + answers +
                '}';
    }
}
