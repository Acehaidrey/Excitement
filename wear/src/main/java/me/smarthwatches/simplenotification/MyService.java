package me.smarthwatches.simplenotification;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/** Sense accelerometer changes to see if someone is excited */
public class MyService extends Service implements SensorEventListener {
    private static final String DEBUG_TAG = "AccelLoggerService";
    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(DEBUG_TAG,"SERVICE STARTEDDDDDDDDD");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY; // want service to continue running until its explicitly stopped so return sticky
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Math.abs(event.values[0]) > 9.8 || Math.abs(event.values[1]) > 9.8 || Math.abs(event.values[2]) > 9.8) {
            showNotification();
        }
        mSensorManager.unregisterListener(this);
    }

    /** Notification to show person is excited */
    private void showNotification() {

        Log.d(DEBUG_TAG, "enters shownotification??");

        Intent viewIntent = new Intent(this, WearActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        Intent googleApiIntent = new Intent(getApplicationContext(), MyMessageIntentService.class);
        PendingIntent pendingGoogleApiIntent = PendingIntent.getService(getApplicationContext(), 0, googleApiIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.excited)
                .setContentTitle("Excited!?")
                .setContentText("Want to capture it?")
                .setContentIntent(viewPendingIntent);
        NotificationCompat.Action camAction = new NotificationCompat.Action.Builder(R.drawable.camera,
                getString(R.string.picture), pendingGoogleApiIntent)
                .extend(new NotificationCompat.Action.WearableExtender().setAvailableOffline(false)).build();

        notificationBuilder.addAction(camAction);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        int notificationId = 001;
        notificationManager.notify(notificationId, notificationBuilder.build());

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
