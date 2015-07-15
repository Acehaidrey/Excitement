package me.smarthwatches.simplenotification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FetchService extends Service {

    private static final String TWITTER_KEY = "eHx3R0ALRj2dCCZMa9uMVFU7Y";
    private static final String TWITTER_SECRET = "emLlHtKN4v6wik76ZkspBgboIxGKNWZ6T7rW4po9ZfXrSN6xYz";
    private static final String TAG = "FetchTAG";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        fetch();
        return START_NOT_STICKY;

    }

//    http://docs.fabric.io/android/twitter/access-rest-api.html
//    http://docs.fabric.io/javadocs/twittercore/1.3.4/com/twitter/sdk/android/core/TwitterCore.html
//    second link much more helpful

    public void fetch() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService twitterService = twitterApiClient.getSearchService();

//        https://dev.twitter.com/rest/reference/get/search/tweets
        twitterService.tweets(getString(R.string.hashtag), null, null, null, "recent", 15, null, null, null,
                true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> result) {
                        final TwitterSession mySession = Twitter.getSessionManager().getActiveSession();
                        for (Tweet t: result.data.tweets) {
                            if (t.user.id != mySession.getUserId()) {
                                String imglink = t.entities.media.get(0).mediaUrl;
                                Bitmap img = getBitmapFromTwitterURL(imglink);
                                notifyTweet(img, t.text, t.id);
                                break;
                            }
                        }
                    }
                    @Override
                    public void failure(TwitterException e) {
                        Log.d(TAG, "callback failed: " + e);
                    }
                }
        );
    }


// http://stackoverflow.com/questions/11831188/how-to-get-bitmap-from-a-url-in-android
    public static Bitmap getBitmapFromTwitterURL(final String url)  {

        ExecutorService execServ = Executors.newSingleThreadExecutor();
        Future<Bitmap> bm = execServ.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                try {
                    Log.d(TAG, "enter bitmap try");
                    InputStream input = (InputStream) new URL(url).getContent();
                    return BitmapFactory.decodeStream(input);
                } catch (MalformedInputException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        try {
            return bm.get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }


    public void notifyTweet(Bitmap img, String txt, long id) {
        Log.d(TAG, "reach notify tweet");
        Intent intent = new Intent(this, TweetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setLargeIcon(img)
                .setContentText(txt)
                .setContentTitle("Same Hashtag Tweet!")
                .setContentIntent(pendingIntent)
                .extend(new NotificationCompat.WearableExtender()
                    .setHintHideIcon(true)
                    .setBackground(img)
                );
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(img));

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        int notificationId = 001;
        notificationManager.notify(notificationId, notification.build());
    }

}
