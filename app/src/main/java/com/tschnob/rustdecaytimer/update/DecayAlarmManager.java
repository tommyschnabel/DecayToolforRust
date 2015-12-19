package com.tschnob.rustdecaytimer.update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tschnob.rustdecaytimer.timer.TimeHelper;
import com.tschnob.rustdecaytimer.timer.Timer;
import com.tschnob.rustdecaytimer.timer.TimerCache;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public void setAlarms(Context context, Timer timer) {
        setAlarmForDecayStartWarning(context, timer);
        setAlarmForDecayStart(context, timer);
        setAlarmForDecayWarning(context, timer);
        setAlarmForDecayFinish(context, timer);
    }

    private void setAlarmForDecayStartWarning(Context context, Timer timer) {
        NotificationMetaData metaData = new NotificationMetaData(timer.getUniqueId());
        metaData.setType(NotificationMetaData.Type.DEACY_START_WARNING);
        setAlarm(context, new TimeHelper().timeUntilDecayStart(timer).toTime() - TimeUnit.HOURS.toMillis(1), metaData);
    }

    private void setAlarmForDecayStart(Context context, Timer timer) {
        NotificationMetaData metaData = new NotificationMetaData(timer.getUniqueId());
        metaData.setType(NotificationMetaData.Type.START_DECAY);
        setAlarm(context, new TimeHelper().timeUntilDecayStart(timer).toTime(), metaData);
    }

    private void setAlarmForDecayWarning(Context context, Timer timer) {
        NotificationMetaData metaData = new NotificationMetaData(timer.getUniqueId());
        metaData.setType(NotificationMetaData.Type.ONE_HOUR_TO_FINISH);
        setAlarm(
                context,
                new TimeHelper().timeUntilDecayFinish(timer).toTime() - TimeUnit.HOURS.toMillis(1),
                metaData
        );
    }

    private void setAlarmForDecayFinish(Context context, Timer timer) {
        NotificationMetaData metaData = new NotificationMetaData(timer.getUniqueId());
        metaData.setType(NotificationMetaData.Type.FINISH_DECAY);
        setAlarm(context, new TimeHelper().timeUntilDecayFinish(timer).toTime(), metaData);
    }

    private void setAlarm(Context context, long timeToAlarm, NotificationMetaData metaData) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(
                AlarmManager.ELAPSED_REALTIME,
                timeToAlarm + SystemClock.elapsedRealtime(),
                getAlarmPendingIntent(context, metaData)
        );
    }

    public void cancelAlarmForTimer(Context context, Timer timer) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        NotificationMetaData metaData = new NotificationMetaData(timer.getUniqueId());

        /**
         * TODO Update this if I set all three at once
         * Since we don't have a great way to figure out what type of notification it is,
         * we'll just try to cancel all the types. There's probably a way to figure it out,
         * but a good way isn't apparent to me right now
         */
        for (NotificationMetaData.Type type : NotificationMetaData.Type.values()) {
            metaData.setType(type);
            alarmManager.cancel(getAlarmPendingIntent(context, metaData));
        }
    }

    private static PendingIntent getAlarmPendingIntent(Context context, NotificationMetaData metaData) {
        String action;
        try {
            action = new ObjectMapper().writeValueAsString(metaData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't encode notificaiton meta data", e);
        }

        //Make the intents to update the notification alarm
        Intent intent = new Intent(context, DecayAlarmManager.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
