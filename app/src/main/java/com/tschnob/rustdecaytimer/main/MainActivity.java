package com.tschnob.rustdecaytimer.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.common.ThingHappenedCallback;
import com.tschnob.rustdecaytimer.notification.NotificationFragment;
import com.tschnob.rustdecaytimer.notification.add.AddNotificationDialog;
import com.tschnob.rustdecaytimer.timer.Timer;
import com.tschnob.rustdecaytimer.timer.TimerFragment;
import com.tschnob.rustdecaytimer.timer.add.AddTimerDialog;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TimerFragment timerFragment;
    private NotificationFragment notificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setDefaultTitle();
        setSupportActionBar(toolbar);

        //Show the timer fragment
        timerFragment = new TimerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, timerFragment)
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerFragment.isVisible()) {
                    new AddTimerDialog()
                            .setTimerAddedCallback(new ThingHappenedCallback() {
                                @Override
                                public void onThingHappened() {

                                    if (timerFragment.isVisible()) {
                                        timerFragment.updateTimers();
                                    }
                                }
                            })
                            .show(getSupportFragmentManager(), getString(R.string.add_timer_tag));

                } else if (notificationFragment.isVisible()) {

                    new AddNotificationDialog()
                            .setNotificationAddedCallback(new ThingHappenedCallback() {
                                @Override
                                public void onThingHappened() {
                                    notificationFragment.onNotificationAdded();
                                }
                            })
                            .setTimer(notificationFragment.getTimer())
                            .show(getSupportFragmentManager(), getString(R.string.add_notification_dialog_tag));
                }
            }
        });
    }

    private void setDefaultTitle() {
        toolbar.setTitle(R.string.title_activity_main);
    }

    @Override
    public void onBackPressed() {
        if (notificationFragment != null && notificationFragment.isVisible()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_placeholder, timerFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .disallowAddToBackStack()
                    .commit();
            setDefaultTitle();
            return;
        }

        super.onBackPressed();
    }

    public void toNotificationFragment(Timer timer) {
        notificationFragment = new NotificationFragment();
        notificationFragment.setTimer(timer);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, notificationFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .disallowAddToBackStack()
                .commit();

        toolbar.setTitle(getString(R.string.title_notification_fragment));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (timerFragment != null && timerFragment.isVisible()) {
            timerFragment.updateTimers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.action_bar_buttons);
        return true;
    }
}
