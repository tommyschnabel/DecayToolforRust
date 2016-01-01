package com.tschnob.rustdecaytimer.notification;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tschnob.rustdecaytimer.timer.Timer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotificationsCache {

    private static String NOTIFICATION_SHARED_PREFS = "notification shared prefs";

    private static String NOTIFICATIONS = "notifications";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public NotificationsCache(Context context) {
        prefs = context.getSharedPreferences(NOTIFICATION_SHARED_PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public List<NotificationMetaData> getNotifications(Timer timer) throws IOException {
        String key = getKey(timer);
        List<NotificationMetaData> notifications = new ArrayList<>();
        String savedNotifications = prefs.getString(key, "");

        if (!savedNotifications.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();

            notifications = mapper.readValue(savedNotifications, mapper.getTypeFactory()
                    .constructCollectionType(List.class, NotificationMetaData.class));
        }

        return notifications;
    }

    public void storeNotifications(List<NotificationMetaData> notifications, Timer timer) throws IOException {
        String key = getKey(timer);
        ObjectMapper mapper = new ObjectMapper();

        editor.putString(key, mapper.writeValueAsString(notifications));
        editor.commit();
    }

    public void deleteNotificationsForTimer(Timer timer) throws IOException {
        editor.putString(getKey(timer), "");
        editor.commit();
    }

    private String getKey(Timer timer) {
        return NOTIFICATIONS + timer.getUniqueId();
    }

}
