package com.tschnob.rustdecaytimer.notification.add;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.common.ThingHappenedCallback;
import com.tschnob.rustdecaytimer.notification.DecayAlarmManager;
import com.tschnob.rustdecaytimer.notification.NotificationMetaData;
import com.tschnob.rustdecaytimer.notification.NotificationsSharedPrefs;
import com.tschnob.rustdecaytimer.timer.Time;
import com.tschnob.rustdecaytimer.timer.Timer;

import java.io.IOException;
import java.util.List;

public class AddNotificationDialog extends DialogFragment {

    private static int MAX_HOURS = 120;
    private static int MAX_MINUTES = 60;

    private String TAG = getClass().getName();

    private ThingHappenedCallback notificationAddedCallback;
    private Timer timer;

    private int beforeEventPosition = 0;
    private NotificationMetaData.Event[] eventValues;
    private NotificationMetaData.EventType[] eventTypeValues;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_notification, null);

        //Populate the events so we can show them in a spinner
        eventValues = NotificationMetaData.Event.values();
        String[] events = new String[eventValues.length];
        for (int i = 0; i < NotificationMetaData.EventType.values().length; i++) {
            NotificationMetaData.Event eventType = eventValues[i];
            events[i] = eventType.toString();
        }

        //Populate the event types so we can show them in a spinner
        eventTypeValues = NotificationMetaData.EventType.values();
        String[] eventTypes = new String[eventTypeValues.length];
        for (int i = 0; i < NotificationMetaData.EventType.values().length; i++) {
            NotificationMetaData.EventType eventType = eventTypeValues[i];
            if (eventType == NotificationMetaData.EventType.BEFORE) {
                beforeEventPosition = i;
            }

            eventTypes[i] = eventType.toString();
        }

        //Get the spinners from the view
        final Spinner eventsSpinner = (Spinner) view.findViewById(R.id.event_spinner);
        final Spinner eventTypeSpinner = (Spinner) view.findViewById(R.id.event_type_spinner);

        //Set the spinner's adapters with the populated arrays from above
        eventsSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, events));
        eventTypeSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, eventTypes));

        //This is the view that will go away if you pick the event type 'BEFORE', which will
        // trigger a notification before the event's time
        final View beforeEventTimePicker = view.findViewById(R.id.before_event_time_picker);

        //Hide the time picker if the notification is for when the even happens
        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == beforeEventPosition) {
                    beforeEventTimePicker.setVisibility(View.VISIBLE);
                } else {
                    beforeEventTimePicker.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Get the time pickers from the view
        final NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.before_event_hour_picker);
        final NumberPicker minutePicker = (NumberPicker) view.findViewById(R.id.before_event_minute_picker);

        //Setup the time pickers
        hourPicker.setMaxValue(MAX_HOURS);
        minutePicker.setMaxValue(MAX_MINUTES);
        hourPicker.setMinValue(0);
        minutePicker.setMinValue(0);

        //Build the dialog
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(getString(R.string.add_notification_dialog_title))
                .setNegativeButton(R.string.button_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just dismiss, don't need to do anything here
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationMetaData notification = new NotificationMetaData();

                        //Set the id
                        notification.setId(timer.getUniqueId());

                        //Get the event
                        notification.setEvent(
                                eventValues[eventsSpinner.getSelectedItemPosition()]
                        );

                        //Get the event type
                        notification.setEventType(
                                eventTypeValues[eventTypeSpinner.getSelectedItemPosition()]
                        );

                        //Get the time to alert before the event from the picker if that's what's selected
                        if (notification.getEventType() == NotificationMetaData.EventType.BEFORE) {
                            Time beforeTime = new Time();
                            beforeTime.setHours(hourPicker.getValue());
                            beforeTime.setMinutes(minutePicker.getValue());

                            /**
                             * Change event type to {@link NotificationMetaData.EventType.WHEN} if
                             * the before time is 0h, 0m
                             */
                            if (beforeTime.getHours() == 0 && beforeTime.getMinutes() == 0) {
                                notification.setEventType(NotificationMetaData.EventType.WHEN);
                            } else {
                                notification.setTimeBeforeEvent(beforeTime);
                            }

                        }

                        //Save the notification
                        NotificationsSharedPrefs notificationsSharedPrefs = new NotificationsSharedPrefs(getContext());
                        try {
                            List<NotificationMetaData> notifications = notificationsSharedPrefs.getNotifications(timer);

                            notifications.add(notification);
                            notificationsSharedPrefs.storeNotifications(notifications, timer);

                            //Trigger the callback since the save succeeded
                            notificationAddedCallback.onThingHappened();

                            //Update the alarms for the timer
                            DecayAlarmManager decayAlarmManager = new DecayAlarmManager();
                            decayAlarmManager.cancelAllAlarmsForTimer(getContext(), timer);
                            decayAlarmManager.setAlarms(getContext(), timer, notifications);

                        } catch (IOException e) {
                            Log.e(TAG, "Something went wrong saving the new notification");
                            Toast.makeText(
                                    getContext(),
                                    getString(R.string.add_notification_failed),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    }
                })
                .create();
    }

    public AddNotificationDialog setNotificationAddedCallback(ThingHappenedCallback notificationAddedCallback) {
        this.notificationAddedCallback = notificationAddedCallback;
        return this;
    }

    public AddNotificationDialog setTimer(Timer timer) {
        this.timer = timer;
        return this;
    }
}
