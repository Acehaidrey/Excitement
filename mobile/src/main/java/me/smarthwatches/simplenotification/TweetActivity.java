package me.smarthwatches.simplenotification;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import io.fabric.sdk.android.Fabric;


public class TweetActivity extends Activity {

    private static final String TAG = "TweetActivityTAG";
    private static final String TWITTER_KEY = "eHx3R0ALRj2dCCZMa9uMVFU7Y";
    private static final String TWITTER_SECRET = "emLlHtKN4v6wik76ZkspBgboIxGKNWZ6T7rW4po9ZfXrSN6xYz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetUi());
        setContentView(R.layout.activity_tweet);
        fetch();

    }


    public void fetch() {
        // display the fetched tweet
        long id = getIntent().getExtras().getLong("ID");
        TweetUtils.loadTweet(id, new LoadCallback<Tweet>() {
            @Override
            public void success(Tweet tweet) {
                Log.d(TAG, "LoadCallBack success");
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.tweetXml);
                TweetView tv = new TweetView(TweetActivity.this, tweet);
                rl.addView(tv);
            }

            @Override
            public void failure(TwitterException e) {
                Log.d(TAG, "LoadCallBack failed");
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "new Intent thing");
        super.onNewIntent(intent);
        this.setIntent(intent);
        fetch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
