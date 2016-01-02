package com.tschnob.rustdecaytimer.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.tschnob.rustdecaytimer.timer.Time;
import com.tschnob.rustdecaytimer.timer.TimeHelper;
import com.tschnob.rustdecaytimer.timer.Timer;
import com.tschnob.rustdecaytimer.timer.TimerCache;

import java.io.IOException;
import java.util.List;

public class DecayAlarmManager extends BroadcastReceiver {
    private String TAG = getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Decay alarm fired");
        TimerCache timerCache = new TimerCache(context);

        List<Timer> timers;
        try {
            timers = timerCache.getTimers();
        } catch (IOException e) {
            Log.e(TAG, "Problem getting timers", e);
            return;
        }

        NotificationMetaData metaData;

        try {
            metaData = new ObjectMapper().readValue(intent.getAction(), NotificationMetaData.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't decode notification meta data", e);
        }

        String id = metaData.getId();

        if (id == null || id.isEmpty()) {
            Log.e(TAG, "Notification didn't have an id set, can't figure out which timer it belongs to");
            return;
        }

        DecayNotificationManager notificationManager = new DecayNotificationManager();
        for (Timer timer : timers) {
            String timerId = timer.getUniqueId();
            if (id.equals(timerId)) {
                notificationManager.issueNotification(context, timer, metaData);
            }
        }
    }

    public List<NotificationMetaData> setDefaultAlarms(Context context, Timer timer) {
        String id = timer.getUniqueId();
        List<NotificationMetaData> defaultAlarms = ImmutableList.of(
                NotificationMetaData.create(
                        id,
                        NotificationMetaData.EventType.BEFORE,
                        NotificationMetaData.Event.DECAY_START,
                        new Time(1, 0)
                ),
                NotificationMetaData.create(
                        id,
                        NotificationMetaData.EventType.WHEN,
                        NotificationMetaData.Event.DECAY_START
                ),
                NotificationMetaData.create(
                        id,
                        NotificationMetaData.EventType.BEFORE,
                        NotificationMetaData.Event.DECAY_FINISH,
                        new Time(1, 0)
        ),
                NotificationMetaData.create(
                        id,
                        NotificationMetaData.EventType.WHEN,
                        NotificationMetaData.Event.DECAY_FINISH
                )
        );

        setAlarms(context, timer, defaultAlarms);
        return defaultAlarms;
    }

    public void setSavedAlarms(Context context, Timer timer) {
        NotificationsCache notificationsCache = new NotificationsCache(context);
        List<NotificationMetaData> notifications;
        try {
            notifications = notificationsCache.getNotifications(timer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setAlarms(context, timer, notifications);
    }

    public void setAlarms(Context context, Timer timer, List<NotificationMetaData> notifications) {
        for (NotificationMetaData notification : notifications) {
            setAlarm(context, timer, notification);
        }
    }

    private void setAlarm(Context context, Timer timer, NotificationMetaData notification) {
        long timeToAlarm;
        TimeHelper timeHelper = new TimeHelper();

        if (notification.getEvent() == NotificationMetaData.Event.DECAY_START) {
            timeToAlarm = timeHelper.timeUntilDecayStart(timer).toTime();
        } else { //DECAY_FINISH
            timeToAlarm = timeHelper.timeUntilDecayFinish(timer).toTime();
        }

        if (notification.getEventType() == NotificationMetaData.EventType.BEFORE) {
            if (notification.getTimeBeforeEvent() == null) {
                throw new RuntimeException("Time before event needed for setting alarms");
            }

            timeToAlarm -= notification.getTimeBeforeEvent().toTime();
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(
                AlarmManager.ELAPSED_REALTIME,
                timeToAlarm + SystemClock.elapsedRealtime(),
                getAlarmPendingIntent(context, notification)
        );
    }

    public void cancelAlarmsForTimer(Context context, Timer timer) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        List<NotificationMetaData> notifications;

        try {
            //Get the notifications
            notifications = new NotificationsCache(context).getNotifications(timer);
        } catch (IOException e) {
            Log.e(TAG, "Couldn't get notifications, so couldn't cancel alarms for timer");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        //Cancel each notification
        for (NotificationMetaData notification : notifications) {
            alarmManager.cancel(getAlarmPendingIntent(context, notification));
        }
    }

    private static PendingIntent getAlarmPendingIntent(Context context, NotificationMetaData metaData) {
        String action;
        try {
            action = new ObjectMapper().writeValueAsString(metaData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't encode notification meta data", e);
        }

        //Make the intents to update the notification alarm
        Intent intent = new Intent(context, DecayAlarmManager.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
