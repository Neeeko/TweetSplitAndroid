package com.neekoentertainment.tweetsplit.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

    private void displayGeneratedTweets(final ArrayList<String> tweetsList) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Button tweetButton = (Button) findViewById(R.id.postTweetsButton);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TweetsAdapter tweetsAdapter = new TweetsAdapter(tweetsList, this);
        recyclerView.setAdapter(tweetsAdapter);
        tweetButton.setVisibility(View.VISIBLE);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String token = "2416131656-KjedhWCVr4YMiJWwAlbnHNG1jv2jzkWZLjS7U8j";
                String secret = "v4EvxyMjh0z1rqQ5qGDfHwkj8EgOTL1LOrj3rloCgIbN6";
                AccessToken a = new AccessToken(token,secret);
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer("GxmbrAND02fs9c34sjRJP4rRh",
                        "cdyDCd5Qt5bstcQwQygRYMrrV2mrzT30HeDMP8AfcmYKz4JgOW");
                twitter.setOAuthAccessToken(a);
                try {
                    for (int i = 0; i < tweetsList.size(); i++) {
                        twitter.updateStatus(tweetsList.get(i));
                    }
                } catch (TwitterException e) {
                    Log.e("TwitterError", e.getMessage());
                }*/
                Snackbar.make(findViewById(R.id.rootView), getString(R.string.posted_snackbar),
                        Snackbar.LENGTH_LONG).show();
            }
        });
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
        public void onBindViewHolder(final TweetsAdapter.TweetViewHolder holder, int position) {
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
        }

        @Override
        public int getItemCount() {
            return mTweetsList.size();
        }

        public class TweetViewHolder extends RecyclerView.ViewHolder {

            private TextView tweetContent;
            private Button copyButton;

            public TweetViewHolder(View itemView) {
                super(itemView);
                tweetContent = (TextView) itemView.findViewById(R.id.tweetContent);
                copyButton = (Button) itemView.findViewById(R.id.copyButton);
            }
        }
    }
}
