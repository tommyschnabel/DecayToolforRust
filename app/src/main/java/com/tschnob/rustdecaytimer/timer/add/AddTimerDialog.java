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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.common.FoundationType;
import com.tschnob.rustdecaytimer.timer.Timer;
import com.tschnob.rustdecaytimer.timer.TimerCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AddTimerDialog extends DialogFragment {

    private String TAG = getClass().getName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_timer, null);

        //TODO Make this not shitty
        String[] foundationTypes = Lists.transform(Arrays.asList(FoundationType.values()), new Function<FoundationType, String>() {
            @Override
            public String apply(FoundationType input) {
                return input.toString();
            }
        }).toArray(new String[FoundationType.values().length]);

        final Spinner spinner = (Spinner) view.findViewById(R.id.foundation_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, foundationTypes);
        spinner.setAdapter(adapter);

        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.log_off_time_picker);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_timer_header)
                .setView(view)
                .setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timer timer = new Timer();
                        timer.setFoundationType(FoundationType.values()[spinner.getSelectedItemPosition()]);

                        int hoursSinceLogoff = timePicker.getCurrentHour();
                        int minutesSinceLogoff = timePicker.getCurrentMinute();

                        long time = System.currentTimeMillis();
                        time -= TimeUnit.HOURS.toMillis(hoursSinceLogoff);
                        time -= TimeUnit.MINUTES.toMillis(minutesSinceLogoff);

                        timer.setLogOffTime(new Date(time));

                        TimerCache timerCache = new TimerCache(getActivity());
                        List<Timer> timers;
                        try {
                            timers = timerCache.getTimers();
                        } catch (IOException e) {
                            Log.e(TAG, "Problem getting timers", e);
                            timers = new ArrayList<>();
                        }
                        timers.add(timer);

                        try {
                            timerCache.storeTimers(timers);
                            dialog.dismiss();
                        } catch (IOException e) {
                            Log.e(TAG, "Problem saving timers", e);
                            Toast.makeText(
                                    getActivity(),
                                    getActivity().getString(R.string.save_timer_failed),
                                    Toast.LENGTH_SHORT).show();
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

    public abstract void onDismiss(DialogInterface dialog);
}
