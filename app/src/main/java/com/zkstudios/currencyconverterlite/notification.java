package com.zkstudios.currencyconverterlite;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class notification extends Application {
    public static final String CHANNEL_1_ID = "Showed in the notification bar";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Showed in the notification bar",
                    NotificationManager.IMPORTANCE_MIN
            );

            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel1.setDescription("Notification for the apps.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}