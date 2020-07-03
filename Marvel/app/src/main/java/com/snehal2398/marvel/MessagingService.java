package com.snehal2398.marvel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by Snehal on 25-03-2018.
 */

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getNotification().getBody());
    }

    public void showNotification(String message)
    {
        PendingIntent p1=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);
        Notification notification= new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.iron))
                .setContentTitle("Marvel Fan Base")
                .setContentText(message)
                .setContentIntent(p1)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }
}
