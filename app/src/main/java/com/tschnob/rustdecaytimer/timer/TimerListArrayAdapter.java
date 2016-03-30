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
import com.tschnob.rustdecaytimer.common.OnCancelListener;

import java.util.List;

public class TimerListArrayAdapter extends ArrayAdapter<Timer> {

    private String TAG = getClass().getName();

    private LayoutInflater layoutInflater;
    private List<Timer> timers;
    private OnCancelListener onCancelListener;

    public TimerListArrayAdapter(
            Context context,
            @NonNull List<Timer> timers,
            OnCancelListener onCancelListener
    ) {
        super(context, R.layout.timer_list_item, timers);
        layoutInflater = LayoutInflater.from(context);

        this.timers = timers;
        this.onCancelListener = onCancelListener;
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
        ImageView itemType = (ImageView) convertView.findViewById(R.id.foundation_type_image);
        ImageView cancelTimerButton = (ImageView) convertView.findViewById(R.id.cancel_timer);

        Timer timer = timers.get(position);
        TimeHelper timeHelper = new TimeHelper();

        switch (timer.getItemType()) {

            case WOOD:
                itemType.setImageResource(R.mipmap.foundation_type_wood);
                break;
            case STONE:
                itemType.setImageResource(R.mipmap.foundation_type_stone);
                break;
            case SHEET_METAL:
                itemType.setImageResource(R.mipmap.foundation_type_metal);
                break;
            case TWIG:
                itemType.setImageResource(R.mipmap.foundation_type_sticks);
                break;
            case ARMORED:
                itemType.setImageResource(R.mipmap.high_quality_metal);
                break;
            case SECRET_STASH:
                itemType.setImageResource(R.mipmap.secret_stash);
                break;
            default:
                throw new IllegalArgumentException("Unsupported foundation type");
        }

        decayStart.setText(timeHelper.timeUntilDecayStart(timer).toString());
        decayFinish.setText(timeHelper.timeUntilDecayFinish(timer).toString());

        cancelTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Trigger the callback
                if (onCancelListener != null) {
                    onCancelListener.onCancel(position);
                }

                //Re-render things in case something changed
                TimerListArrayAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
