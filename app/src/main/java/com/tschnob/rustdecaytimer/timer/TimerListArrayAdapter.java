package com.tschnob.rustdecaytimer.timer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tschnob.rustdecaytimer.R;

import java.util.List;

public class TimerListArrayAdapter extends ArrayAdapter<Timer> {

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.timer_list_item, parent, false);
        }

        TextView decayStart = (TextView) convertView.findViewById(R.id.decay_start_time);
        TextView decayFinish = (TextView) convertView.findViewById(R.id.decay_finish_time);
        ImageView foundationType = (ImageView) convertView.findViewById(R.id.foundation_type_image);

        Timer timer = timers.get(position);

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
            case STICK:
            case ARMORED:
            default:

        }

        return convertView;
    }
}
