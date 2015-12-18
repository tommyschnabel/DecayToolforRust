package com.tschnob.rustdecaytimer.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.timer.TimerFragment;
import com.tschnob.rustdecaytimer.timer.add.AddTimerDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Show the timer fragment
        final TimerFragment timerFragment = new TimerFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, timerFragment);
        transaction.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddTimerDialog() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        timerFragment.updateTimers();
                    }
                }
                        .show(getSupportFragmentManager(), getString(R.string.add_timer_tag));
            }
        });
    }

}
