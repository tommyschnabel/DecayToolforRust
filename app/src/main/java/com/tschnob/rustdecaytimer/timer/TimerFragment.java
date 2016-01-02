package com.tschnob.rustdecaytimer.timer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.common.OnCancelListener;
import com.tschnob.rustdecaytimer.main.MainActivity;
import com.tschnob.rustdecaytimer.notification.DecayAlarmManager;
import com.tschnob.rustdecaytimer.notification.NotificationsCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment
                           implements OnCancelListener {
    private String TAG = getClass().getName();

    private ListView timerList;
    private List<Timer> timers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        timerList = (ListView) rootView.findViewById(R.id.timer_list);
        TimerCache timerCache = new TimerCache(context);

        try {
            timers = timerCache.getTimers();
        } catch (IOException e) {
            Log.e(TAG, "Problem getting timers", e);
            timers = new ArrayList<>();
        }

        timerList.setAdapter(
                new TimerListArrayAdapter(
                        getActivity(),
                        timers,
                        this
                )
        );
        timerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).toNotificationFragment(timers.get(position));
                }
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.timer_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateTimers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    public void updateTimers() {
        TimerCache timerCache = new TimerCache(getActivity());

        try {
            timers = timerCache.getTimers();
            timerList.setAdapter(new TimerListArrayAdapter(getActivity(), timers, this));
        } catch (IOException e) {
            Log.e(TAG, "Problem getting timers", e);
        }
    }

    @Override
    public void onCancel(int position) {

        TimerCache cache = new TimerCache(getContext());
        Timer removedTimer = timers.remove(position);

        try {
            cache.storeTimers(timers);
            new DecayAlarmManager().cancelAlarmsForTimer(getContext(), removedTimer);
            new NotificationsCache(getContext()).deleteNotificationsForTimer(removedTimer);
        } catch (IOException e) {
            Log.e(TAG, "Couldn't save timers after deleting one", e);
            timers.add(position, removedTimer);
            Toast.makeText(
                    getContext(),
                    getContext().getString(R.string.remove_timer_failed),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
