package com.anubhav.vitinsiderhostel.notifications;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.anubhav.vitinsiderhostel.activities.HomePageActivity;
import com.anubhav.vitinsiderhostel.enums.Path;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AppNotification extends FirebaseMessagingService {


    private static AppNotification appNotification;
    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference tokenSection = db.collection(Path.FCM_TOKEN.getPath());


    public AppNotification() {
        super();
    }

    public static AppNotification getInstance() {
        if (appNotification == null) {
            appNotification = new AppNotification();
        }
        return appNotification;
    }

    public static boolean isAppIsInBackground(Context context) {

        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationOAbove(message);
        } else {
            notificationOBelow(message);
        }

    }

    private void notificationOBelow(RemoteMessage message) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationOAbove(RemoteMessage message) {

        String title = Objects.requireNonNull(message.getNotification()).getTitle();
        String body = message.getNotification().getBody();
        Intent resultIntent = new Intent(getApplicationContext(), HomePageActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ONotification oNotification = new ONotification(this);
        Notification.Builder builder = oNotification.getOreoNotification(title, body);
        int i = 0;
        oNotification.getManager().notify(i, builder.build());

        if (isAppIsInBackground(getApplicationContext())) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(i, builder.build());
        }

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
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            tokenMap.put("lastUpdated", new Timestamp(new Date()));
            tokenSection.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(tokenMap);
        }
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }

    @Override
    protected Intent getStartCommandIntent(Intent originalIntent) {
        return super.getStartCommandIntent(originalIntent);
    }

    public void unSubscribeAllTopics() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("APP_UPDATES");
        String topic1 = "JOINED_" + User.getInstance().getRoomNo();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic1);
        String topic2 = "NOTICE_" + User.getInstance().getStudentBlock();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic2);
    }

    public void subscribeAllTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("APP_UPDATES");
        String topic1 = "JOINED_" + User.getInstance().getRoomNo();
        FirebaseMessaging.getInstance().subscribeToTopic(topic1);
        String topic2 = "NOTICE_" + User.getInstance().getStudentBlock();
        FirebaseMessaging.getInstance().subscribeToTopic(topic2);
    }
}
