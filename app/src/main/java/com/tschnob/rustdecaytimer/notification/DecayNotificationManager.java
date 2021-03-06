package com.tschnob.rustdecaytimer.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.main.MainActivity;
import com.tschnob.rustdecaytimer.timer.TimeHelper;
import com.tschnob.rustdecaytimer.timer.Timer;

public class DecayNotificationManager {
    private String TAG = getClass().getName();

    public static int OPEN_APP = 1;

    public void issueNotification(Context context, Timer timer, NotificationMetaData notification) {

        TimeHelper timeHelper = new TimeHelper();
        String timerText;

        if (notification.getEvent() == NotificationMetaData.Event.DECAY_START
                && notification.getEventType() == NotificationMetaData.EventType.BEFORE) {

            timerText = String.format(
                    context.getString(R.string.notification_timer_will_start),
                    timer.getItemType().toString(),
                    timeHelper.timeUntilDecayStart(timer)
            );
        } else if (notification.getEvent() == NotificationMetaData.Event.DECAY_START
                && notification.getEventType() == NotificationMetaData.EventType.WHEN) {

            timerText = String.format(
                    context.getString(R.string.notification_timer_will_end),
                    timer.getItemType().toString(),
                    timeHelper.timeUntilDecayFinish(timer)
            );
        } else if (notification.getEvent() == NotificationMetaData.Event.DECAY_FINISH
                && notification.getEventType() == NotificationMetaData.EventType.BEFORE) {

            timerText = String.format(
                    context.getString(R.string.notification_timer_will_end),
                    timer.getItemType().toString(),
                    timeHelper.timeUntilDecayFinish(timer)
            );
        } else if (notification.getEvent() == NotificationMetaData.Event.DECAY_FINISH
                && notification.getEventType() == NotificationMetaData.EventType.WHEN) {

            timerText = String.format(
                    context.getString(R.string.notification_timer_expired),
                    timer.getItemType().toString()
            );
        } else {
            throw new IllegalArgumentException("Unhandled notification event/type");
        }

        NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.rock)
                .setContentText(timerText)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(getOpenAppPendingIntent(context))
                .setOngoing(false)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(timerText));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Long.valueOf(timer.getLogOffTime().getTime()).intValue(), n.build());

        Log.i(TAG, "Issued notification: " + timerText);
    }

    private PendingIntent getOpenAppPendingIntent(Context context) {

        Intent onClickIntent = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(
                context,
                OPEN_APP,
                onClickIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
    }
}
