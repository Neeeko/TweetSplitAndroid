package com.neekoentertainment.tweetsplit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        final EditText editText = (EditText) findViewById(R.id.editText);
        final Button button = (Button) findViewById(R.id.generateTweetsButton);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty() && !editText.getText().toString().trim().equals("")) {
                    ArrayList<String> tweetsList = generateTweets(editText.getText().toString(), checkBox.isChecked());
                    displayGeneratedTweets(tweetsList);
                } else {
                    editText.setError(getString(R.string.empty_tweet_error));
                }
            }
        });
    }

    private ArrayList<String> generateTweets(String tweetsContent, boolean addTweetIndicator) {
        ArrayList<String> tweetsList = new ArrayList<>();

        int tweetLength = addTweetIndicator ? 136 : 140;

        // Splits the String each 136 chars. A Tweet is 136 long because if the user wants to add
        // a tweet counter at the beginning of each tweets.
        for (int i = 0; i < tweetsContent.length(); i += tweetLength) {
            tweetsList.add(tweetsContent.substring(i, Math.min(tweetsContent.length(), i + tweetLength)));
        }

        if (addTweetIndicator) {
            for (int j = 0; j < tweetsList.size(); j++) {
                String tweet = (j + 1) + "/" + tweetsList.size() + " " + tweetsList.get(j);
                tweetsList.set(j, tweet);
            }
        }
        return tweetsList;
    }

    // TODO: Code this function (see Trello)
    private void displayGeneratedTweets(ArrayList<String> tweetsList) {
        for (int i = 0; i < tweetsList.size(); i++) {
            Log.d("Test", tweetsList.get(i));
        }
    }
}
