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
import com.tschnob.rustdecaytimer.common.FoundationType;

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

        Time timeToDecayStart;
        Time timeToDecayFinish;

        long logOffTime = timer.getLogOffTime().getTime();
        FoundationType type;

        switch (timer.getFoundationType()) {

            case WOOD:
                foundationType.setImageResource(R.mipmap.foundation_type_wood);
                type = FoundationType.WOOD;
                break;
            case STONE:
                foundationType.setImageResource(R.mipmap.foundation_type_stone);
                type = FoundationType.STONE;
                break;
            case SHEET_METAL:
                foundationType.setImageResource(R.mipmap.foundation_type_metal);
                type = FoundationType.SHEET_METAL;
                break;
            case STICK:
                type = FoundationType.STICK;
                //TODO Find images for sticks and armored
                foundationType.setImageResource(R.mipmap.foundation_type_stone);
                break;
            case ARMORED:
                type = FoundationType.ARMORED;
                //TODO Find images for sticks and armored
                foundationType.setImageResource(R.mipmap.foundation_type_stone);
                break;
            default:
                throw new IllegalArgumentException("Unsupported foundation type");
        }

        timeToDecayStart = timeHelper.timeUntil(
                logOffTime,
                type.getDelay(),
                System.currentTimeMillis()
        );
        decayStart.setText(timeToDecayStart.toString());

        timeToDecayFinish = timeHelper.timeUntil(
                logOffTime,
                type.getDuration(),
                timeToDecayStart.toDate().getTime()
        );
        decayFinish.setText(timeToDecayFinish.toString());

        cancelTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerCache cache = new TimerCache(getContext());

                Timer removedTimer = timers.remove(position);

                try {
                    cache.storeTimers(timers);
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
