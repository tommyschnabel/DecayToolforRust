package com.tschnob.rustdecaytimer.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.timer.TimerFragment;
import com.tschnob.rustdecaytimer.timer.add.AddTimerDialog;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TimerFragment timerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Show the timer fragment
        timerFragment = new TimerFragment();
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
                        super.onDismiss(dialog);
                        timerFragment.updateTimers();
                    }
                }
                        .show(getSupportFragmentManager(), getString(R.string.add_timer_tag));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.action_bar_buttons);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.menu_item_refresh) {
            timerFragment.updateTimers();
            return true;
        }
        return false;
    }
}
