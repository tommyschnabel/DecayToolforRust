package com.tschnob.rustdecaytimer.update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.tschnob.rustdecaytimer.common.FoundationType;
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

        String id = intent.getAction();

        if (id == null || id.isEmpty()) {
            return;
        }

        DecayNotificationManager notificationManager = new DecayNotificationManager();
        for (Timer timer : timers) {
            String timerId = timer.getUniqueId();
            if (id.equals(timerId)) {
                notificationManager.issueNotification(context, timer);
                setNextAlarmForTimer(context, timer);
            }
        }
    }

    public void setNextAlarmForTimer(Context context, Timer timer) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        FoundationType foundationType = timer.getFoundationType();

        long timeUntilDecayStart = timer.getLogOffTime().getTime() + foundationType.getDelay();
        long timeUntilDecayFinish = timer.getLogOffTime().getTime() + foundationType.getDelay() + foundationType.getDuration();
        long now = System.currentTimeMillis();
        Long timeToAlarm = null;

        if (now < timeUntilDecayStart) {
            timeToAlarm = timeUntilDecayStart - now;
        } else if (now < timeUntilDecayFinish) {

            //Notify an hour before the decay is finished
            timeToAlarm = timeUntilDecayFinish - TimeUnit.HOURS.toMillis(1) - now;
        } else if (timeUntilDecayFinish <= now) {
            timeToAlarm = timeUntilDecayFinish - now;
        }

        Log.i(
                TAG,
                "Alarm will fire in " + timeToAlarm
                + " for " + timer.getFoundationType().toString()
        );

        if (timeToAlarm != null && timeToAlarm > 0) {

            alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + timeToAlarm,
                    getAlarmPendingIntent(context, timer)
            );
        }
    }

    public void cancelAlarmForTimer(Context context, Timer timer) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getAlarmPendingIntent(context, timer));
    }

    private static PendingIntent getAlarmPendingIntent(Context context, Timer timer) {

        //Make the intents to update the notification alarm
        Intent intent = new Intent(context, DecayAlarmManager.class);
        intent.setAction(timer.getUniqueId());
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
