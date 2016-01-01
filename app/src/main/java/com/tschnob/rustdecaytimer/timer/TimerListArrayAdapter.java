package com.tschnob.rustdecaytimer.timer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.notification.DecayAlarmManager;

import java.io.IOException;
import java.util.List;

public class TimerListArrayAdapter extends ArrayAdapter<Timer> {

    private String TAG = getClass().getName();

    private LayoutInflater layoutInflater;
    private List<Timer> timers;

    public TimerListArrayAdapter(Context context, @NonNull List<Timer> timers) {
        super(context, R.layout.timer_list_item, timers);
        layoutInflater = LayoutInflater.from(context);

        this.timers = timers;
    }

    @Override
    public int getCount() {
        if (timers == null) {
            return 0;
        }

        return timers.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.timer_list_item, parent, false);
        }

        TextView decayStart = (TextView) convertView.findViewById(R.id.decay_start_time);
        TextView decayFinish = (TextView) convertView.findViewById(R.id.decay_finish_time);
        ImageView foundationType = (ImageView) convertView.findViewById(R.id.foundation_type_image);
        ImageView cancelTimer = (ImageView) convertView.findViewById(R.id.cancel_timer);

        Timer timer = timers.get(position);
        TimeHelper timeHelper = new TimeHelper();

        switch (timer.getFoundationType()) {

            case WOOD:
                foundationType.setImageResource(R.mipmap.foundation_type_wood);
                break;
            case STONE:
                foundationType.setImageResource(R.mipmap.foundation_type_stone);
                break;
            case SHEET_METAL:
                foundationType.setImageResource(R.mipmap.foundation_type_metal);
                break;
            case TWIG:
                foundationType.setImageResource(R.mipmap.foundation_type_sticks);
                break;
            case ARMORED:
                foundationType.setImageResource(R.mipmap.high_quality_metal);
                break;
            default:
                throw new IllegalArgumentException("Unsupported foundation type");
        }

        decayStart.setText(timeHelper.timeUntilDecayStart(timer).toString());
        decayFinish.setText(timeHelper.timeUntilDecayFinish(timer).toString());

        cancelTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerCache cache = new TimerCache(getContext());

                Timer removedTimer = timers.remove(position);

                try {
                    cache.storeTimers(timers);
                    new DecayAlarmManager().cancelAlarmsForTimer(getContext(), removedTimer);
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't save timers after deleting one", e);
                    timers.add(position, removedTimer);
                    Toast.makeText(
                            getContext(),
                            getContext().getString(R.string.remove_timer_failed),
                            Toast.LENGTH_SHORT
                    ).show();
                }

                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
