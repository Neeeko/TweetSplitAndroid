package com.neekoentertainment.tweetsplit.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neekoentertainment.tweetsplit.R;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    //TODO: NOT THE PRODUCTION KEYS :)
    private static final String CONSUMER_KEY = "";
    private static final String CONSUMER_SECRET = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initFabric();
    }

    private void init() {
        final EditText editText = findViewById(R.id.editText);
        final Button button = findViewById(R.id.generateTweetsButton);
        final CheckBox checkBox = findViewById(R.id.checkBox);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty() &&
                        !editText.getText().toString().trim().equals("")) {
                    ArrayList<String> tweetsList = generateTweets(editText.getText().toString(),
                            checkBox.isChecked());
                    displayGeneratedTweets(tweetsList);
                } else {
                    editText.setError(getString(R.string.empty_tweet_error));
                }
            }
        });
    }

    private void initFabric() {
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());
    }

    private ArrayList<String> generateTweets(String tweetsContent, boolean addTweetIndicator) {
        ArrayList<String> tweetsList;

        int tweetLength = addTweetIndicator ? 136 : 140;

        tweetsContent = tweetsContent.replace('\n', ' ');

        // Splits the String each 136 or 140 chars. A Tweet is 136 long if the user wants to add
        // a tweet counter at the beginning of each tweets.
        tweetsList = handleTweetContent(tweetsContent, tweetLength);


        if (addTweetIndicator) {
            for (int j = 0; j < tweetsList.size(); j++) {
                String tweet = (j + 1) + "/" + tweetsList.size() + " " + tweetsList.get(j);
                tweetsList.set(j, tweet);
            }
        }
        return tweetsList;
    }

    private ArrayList<String> handleTweetContent(String tweetContent, int tweetLength) {
        ArrayList<String> tweetsList = new ArrayList<>();
        // Only one tweet necessary
        if (tweetContent.length() <= tweetLength) {
            tweetsList.add(tweetContent);
        } else {
            // We split the tweet content into words that we then try to concatenate to get the
            // required length
            ArrayList<String> wordsList = new ArrayList<>(Arrays.asList(tweetContent.split("\\s+")));
            StringBuilder tweet = new StringBuilder();

            // A word longer than the tweet length isn't considered valid
            for (int i = 0; i < wordsList.size(); i++) {
                if (wordsList.get(i).length() > tweetLength) {
                    wordsList.remove(i);
                }
            }

            int i = 0;
            while (i < wordsList.size()) {
                if (wordsList.get(i).length() + tweet.length() <= tweetLength) {
                    tweet.append(" ").append(wordsList.get(i));
                    i++;
                } else {
                    tweetsList.add(tweet.toString().trim());
                    tweet = new StringBuilder();
                }
            }
            if (!tweet.toString().isEmpty()) {
                tweetsList.add(tweet.toString().trim());
            }
        }
        return tweetsList;
    }

    private void displayGeneratedTweets(ArrayList<String> tweetsList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TweetsAdapter tweetsAdapter = new TweetsAdapter(tweetsList, this);
        recyclerView.setAdapter(tweetsAdapter);
    }

    public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.TweetViewHolder> {

        private ArrayList<String> mTweetsList;
        private Context mContext;

        public TweetsAdapter(ArrayList<String> tweetsList, Context context) {
            mTweetsList = tweetsList;
            mContext = context;
        }

        @Override
        public TweetsAdapter.TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_view, parent, false);
            return new TweetViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final TweetsAdapter.TweetViewHolder holder, final int position) {
            holder.tweetContent.setText(mTweetsList.get(position));
            holder.copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboardManager =
                            (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(getString(R.string.clipboard_label),
                            mTweetsList.get(holder.getAdapterPosition()));
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(mContext, getString(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                }
            });
            holder.tweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postTweet(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTweetsList.size();
        }

        private void postTweet(int position) {
            TweetComposer.Builder builder = new TweetComposer.Builder(MainActivity.this)
                    .text(mTweetsList.get(position));
            builder.show();
        }

        public class TweetViewHolder extends RecyclerView.ViewHolder {

            private TextView tweetContent;
            private Button copyButton;
            private Button tweetButton;

            public TweetViewHolder(View itemView) {
                super(itemView);
                tweetContent = itemView.findViewById(R.id.tweetContent);
                copyButton = itemView.findViewById(R.id.copyButton);
                tweetButton = itemView.findViewById(R.id.tweetButton);
            }
        }
    }
}
