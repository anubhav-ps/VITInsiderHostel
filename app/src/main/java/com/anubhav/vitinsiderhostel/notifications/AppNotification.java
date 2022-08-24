package com.anubhav.vitinsiderhostel.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.anubhav.vitinsiderhostel.models.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class AppNotification extends FirebaseMessagingService  {

    private static AppNotification appNotification;


    public AppNotification(){
        super();
    }

    public static AppNotification getInstance(){
        if (appNotification==null) {
            appNotification = new AppNotification();
        }
        return appNotification;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Toast.makeText(getApplicationContext(), "Notification received", Toast.LENGTH_SHORT).show();
        String title = Objects.requireNonNull(message.getNotification()).getTitle();
        String body = message.getNotification().getBody();

        final String CHANNEL_ID = "APP_NOTIFICATION";

        NotificationChannel channel= new NotificationChannel(CHANNEL_ID,"MY_NOTIFICATION", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1,notification.build());

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
    }

    @Override
    public void onSendError(@NonNull String msgId, @NonNull Exception exception) {
        super.onSendError(msgId, exception);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    @Override
    protected Intent getStartCommandIntent(Intent originalIntent) {
        return super.getStartCommandIntent(originalIntent);
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }

    public void unSubscribeAllTopics(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("APP_UPDATES");
        String topic1 = "JOINED_" + User.getInstance().getRoomNo();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic1);
        String topic2 = "NOTICE_" + User.getInstance().getStudentBlock();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic2);
        String topic3 = "CHANGE_" + User.getInstance().getRoomNo();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic3);
    }

    public void subscribeAllTopics(){
        FirebaseMessaging.getInstance().subscribeToTopic("APP_UPDATES");
        String topic1 = "JOINED_" + User.getInstance().getRoomNo();
        FirebaseMessaging.getInstance().subscribeToTopic(topic1);
        String topic2 = "NOTICE_" + User.getInstance().getStudentBlock();
        FirebaseMessaging.getInstance().subscribeToTopic(topic2);
        String topic3 = "CHANGE_" + User.getInstance().getRoomNo();
        FirebaseMessaging.getInstance().subscribeToTopic(topic3);
    }

}
