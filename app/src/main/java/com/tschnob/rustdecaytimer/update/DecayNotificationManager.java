package com.tschnob.rustdecaytimer.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.main.MainActivity;
import com.tschnob.rustdecaytimer.timer.Time;
import com.tschnob.rustdecaytimer.timer.TimeHelper;
import com.tschnob.rustdecaytimer.timer.Timer;

public class DecayNotificationManager {

    public static int OPEN_APP = 1;

    public void issueNotification(Context context, Timer timer) {

        TimeHelper timeHelper = new TimeHelper();
        long timeUntilDecayStart = timeHelper.timeUntilDecayStart(timer).toDate().getTime();
        long timeUntilDecayFinish = timeHelper.timeUntilDecayFinish(timer).toDate().getTime();
        long now = System.currentTimeMillis();

        String timerText;

        //Hasn't started decay yet
        if (now > timeUntilDecayStart) {
            timerText = String.format(
                    context.getString(R.string.notification_timer_will_start),
                    timer.getFoundationType().toString(),
                    new Time(timeUntilDecayStart)
            );
        } else if (now > timeUntilDecayFinish) { //Has started decay, hasn't finished
            timerText = String.format(
                    context.getString(R.string.notification_timer_will_end),
                    timer.getFoundationType().toString(),
                    new Time(timeUntilDecayFinish)
            );
        } else { //Completely decayed
            timerText = String.format(
                    context.getString(R.string.notification_timer_expired),
                    timer.getFoundationType().toString()
            );
        }

        NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.rock)
                .setContentTitle(timerText)
                .setContentText(context.getString(R.string.app_name))
                .setContentIntent(getOpenAppPendingIntent(context))
                .setOngoing(false)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(timerText));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.valueOf(timer.getUniqueId()), n.build());
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
