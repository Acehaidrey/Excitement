package me.smarthwatches.simplenotification;


import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

public class MessageListenerService extends WearableListenerService {

    private static final String TAG = "MessageListenerService";
    private static final String WEARCAPABILITY = "WearCapability";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "Got a message");
        Intent intent = new Intent(getApplicationContext(), LoadCameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
