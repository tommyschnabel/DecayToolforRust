package com.tschnob.rustdecaytimer.timer.add;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.common.ItemType;
import com.tschnob.rustdecaytimer.common.ThingHappenedCallback;
import com.tschnob.rustdecaytimer.notification.DecayAlarmManager;
import com.tschnob.rustdecaytimer.notification.NotificationMetaData;
import com.tschnob.rustdecaytimer.notification.NotificationsSharedPrefs;
import com.tschnob.rustdecaytimer.timer.Timer;
import com.tschnob.rustdecaytimer.timer.TimerSharedPrefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddTimerDialog extends DialogFragment {

    private String TAG = getClass().getName();

    private static int MAX_HOURS = 120;
    private static int MAX_MINUTES = 60;

    private ThingHappenedCallback timerAddedCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_timer, null);

        ItemType[] itemTypeValues = ItemType.values();
        String[] itemTypes = new String[itemTypeValues.length];
        for (int i = 0; i < ItemType.values().length; i++) {
            ItemType eventType = itemTypeValues[i];
            itemTypes[i] = eventType.toString();
        }

        final Spinner spinner = (Spinner) view.findViewById(R.id.foundation_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, itemTypes);
        spinner.setAdapter(adapter);

        final NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.hour_picker);
        final NumberPicker minutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);

        hourPicker.setMaxValue(MAX_HOURS);
        minutePicker.setMaxValue(MAX_MINUTES);
        hourPicker.setMinValue(0);
        minutePicker.setMinValue(0);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_timer_header)
                .setView(view)
                .setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timer timer = new Timer();
                        timer.setItemType(ItemType.values()[spinner.getSelectedItemPosition()]);

                        int hoursSinceLogoff = hourPicker.getValue();
                        int minutesSinceLogoff = minutePicker.getValue();

                        long time = System.currentTimeMillis();
                        time -= TimeUnit.HOURS.toMillis(hoursSinceLogoff);
                        time -= TimeUnit.MINUTES.toMillis(minutesSinceLogoff);

                        timer.setLogOffTime(new Date(time));

                        TimerSharedPrefs timerSharedPrefs = new TimerSharedPrefs(getActivity());
                        List<Timer> timers;
                        try {
                            timers = timerSharedPrefs.getTimers();
                        } catch (IOException e) {
                            Log.e(TAG, "Problem getting timers", e);
                            timers = new ArrayList<>();
                        }

                        if (timers == null) {
                            timers = new ArrayList<>();
                        }

                        timers.add(timer);

                        try {
                            timerSharedPrefs.storeTimers(timers);
                            dialog.dismiss();
                        } catch (IOException e) {
                            Log.e(TAG, "Problem saving timers", e);
                            Toast.makeText(
                                    getActivity(),
                                    getActivity().getString(R.string.save_timer_failed),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<NotificationMetaData> notifications =
                                new DecayAlarmManager().setDefaultAlarms(getContext(), timer);

                        try {
                            new NotificationsSharedPrefs(getContext()).storeNotifications(notifications, timer);
                        } catch (IOException e) {
                            Log.e(TAG, "Couldn't save new notifications");
                            throw new RuntimeException(e);
                        }

                        if (timerAddedCallback != null) {
                            timerAddedCallback.onThingHappened();
                        }
                    }
                })
                .setNegativeButton(R.string.button_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public AddTimerDialog setTimerAddedCallback(ThingHappenedCallback timerAddedCallback) {
        this.timerAddedCallback = timerAddedCallback;
        return this;
    }
}
