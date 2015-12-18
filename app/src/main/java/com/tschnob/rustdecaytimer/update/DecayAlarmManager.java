package com.tschnob.rustdecaytimer.update;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

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
            if (id.equals(timer.getUniqueId())) {
                notificationManager.issueNotification(context, timer);
            }
        }
    }

    public void addAlarm(Context context, Timer timer) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        TimeHelper timeHelper = new TimeHelper();
        long timeUntilDecayStart = timeHelper.timeUntilDecayStart(timer).toDate().getTime();
        long timeUntilDecayFinish = timeHelper.timeUntilDecayFinish(timer).toDate().getTime();
        long now = System.currentTimeMillis();

        long alarmTime = SystemClock.elapsedRealtime();

        if (now < timeUntilDecayStart) {
            alarmTime += timeUntilDecayStart;
        } else if (now < timeUntilDecayFinish) {

            //Notify an hour before the decay is finished
            alarmTime += timeUntilDecayFinish - TimeUnit.HOURS.toMillis(1);
        } else {
            alarmTime += timeUntilDecayFinish;
        }

        alarmManager.set(
                AlarmManager.ELAPSED_REALTIME,
                alarmTime,
                getAlarmPendingIntent(context, timer)
        );
    }

    private static PendingIntent getAlarmPendingIntent(Context context, Timer timer) {

        //Make the intents to update the notification alarm
        Intent intent = new Intent(context, DecayAlarmManager.class);
        intent.setAction(timer.getUniqueId());
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
