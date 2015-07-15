package me.smarthwatches.simplenotification;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

/** Send messages to handheld. */
public class MyMessageIntentService extends IntentService {
    private static final String TAG = "MessageIntentService";
    private static final String WEARCAPABILITY = "WearCapability";
    private GoogleApiClient myGooApiClient;
    private Set<Node> nodeSet = null;
    private Node myNode = null;

    /** Constructor -- won't use for our purposes. */
    public MyMessageIntentService() {
        super("MyMessageIntentService");
    }

    /** Initialize a Google Api Client in order to send messages to handheld. */
    public void initGoogleAPIClient() {
        myGooApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected: " + bundle);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended: " + i);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })

                .addApi(Wearable.API)
                .build();

        //  connect your client to make onConnected call
        myGooApiClient.connect();

    }

    /** Finding the capability to communicate messages to handheld from wear. */
    public void findCapability() {
        CapabilityApi.GetCapabilityResult capResult = Wearable.CapabilityApi.getCapability(myGooApiClient,
                WEARCAPABILITY, CapabilityApi.FILTER_REACHABLE)
                .await();

        nodeSet = capResult.getCapability().getNodes();
        // get last node because it's the right one. don't know why but it is
        for (Node node : nodeSet) {
            myNode = node;
        }
    }

    /** Send the message. */
    public void messageSend() {
        if (myNode != null) {
            Wearable.MessageApi.sendMessage(myGooApiClient, myNode.getId(), WEARCAPABILITY, null).await();
        } else {
            Log.d(TAG, "myNode is null");
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Message service running");
        if (intent != null) {
            Log.d(TAG, "onHandleIntent: intent not null");
            initGoogleAPIClient();
            findCapability();
            messageSend();
            myGooApiClient.disconnect();

        }
    }
}