package com.example.learnbcroadsigns;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DisclaimerActivity extends AppCompatActivity {
    static final String DEBUG = "DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        Log.d(DEBUG, "DisclaimerActivity started");

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        if (settings.getBoolean("user_gave_consent", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        TextView disclaimerView = findViewById(R.id.disclaimer_text);

        try {
            String string = "";
            InputStream is = getAssets().open("disclaimer_text.txt");
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                try {
                    if ((string = reader.readLine()) == null) break;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                stringBuilder.append(string + "\n");
            }
            disclaimerView.setText(stringBuilder.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onAcceptButtonClicked(View view) {
        Log.d(DEBUG, "User accepted disclaimer");

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("user_gave_consent", true);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
