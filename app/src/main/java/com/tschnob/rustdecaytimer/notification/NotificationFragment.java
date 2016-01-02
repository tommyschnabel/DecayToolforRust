package com.tschnob.rustdecaytimer.notification;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.common.OnCancelListener;
import com.tschnob.rustdecaytimer.timer.Timer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private String TAG = getClass().getName();

    private Timer timer;
    private List<NotificationMetaData> notifications;

    private NotificationsListArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        ListView notificationsList = (ListView) rootView.findViewById(R.id.notifications_list);
        NotificationsCache notificationsCache = new NotificationsCache(context);

        try {
             notifications = notificationsCache.getNotifications(timer);
        } catch (IOException e) {
            Log.e(TAG, "Problem getting notifications", e);
            notifications = new ArrayList<>();
        }
        adapter = new NotificationsListArrayAdapter(getActivity(), notifications);
        adapter.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(int position) {

                NotificationsCache cache = new NotificationsCache(getContext());

                NotificationMetaData removedNotification = notifications.remove(position);

                try {
                    cache.storeNotifications(notifications, timer);
                    DecayAlarmManager alarmManager = new DecayAlarmManager();
                    alarmManager.cancelAlarmsForTimer(getContext(), timer);
                    alarmManager.setAlarms(context, timer, notifications);
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't save notification after deleting one", e);
                    notifications.add(position, removedNotification);
                    Toast.makeText(
                            getContext(),
                            getContext().getString(R.string.remove_timer_failed),
                            Toast.LENGTH_SHORT
                    ).show();
                }

                adapter.notifyDataSetChanged();
            }
        });

        notificationsList.setAdapter(adapter);

        return rootView;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void onNotificationAdded() {
        NotificationsCache cache = new NotificationsCache(getContext());

        try {
            adapter.setNotifications(cache.getNotifications(timer));
        } catch (IOException e) {
            Log.e(TAG, "Couldn't get the new notifications, triggering a back press to go to last screen." +
                    " *Hopefully* things will load right if they click again");
            getActivity().onBackPressed();
        }
    }
}
