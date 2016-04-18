package com.tschnob.rustdecaytimer.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tschnob.rustdecaytimer.timer.Timer;
import com.tschnob.rustdecaytimer.timer.TimerSharedPrefs;

import java.io.IOException;
import java.util.List;

public class PhoneRestartBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        List<Timer> timers;

        try {
            timers = new TimerSharedPrefs(context).getTimers();
        } catch (IOException e) {
            Log.e(getClass().getName(), "Couldn't get timers");

            //Rethrow exception so maybe this error will get reported back to us
            throw new RuntimeException(e);
        }

        //Delete and reset all the alarms for each timer,
        // since the alarms are based on device uptime,
        // which is pretty stupid in the first place
        DecayAlarmManager alarmManager = new DecayAlarmManager();

        Log.i(getClass().getName(), "Resetting all timers for user due to phone restart");
        for (Timer timer : timers) {
            alarmManager.cancelAllAlarmsForTimer(context, timer);
            alarmManager.setSavedAlarms(context, timer);
        }
    }
}
