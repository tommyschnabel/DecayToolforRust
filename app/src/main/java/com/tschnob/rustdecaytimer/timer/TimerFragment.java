package com.tschnob.rustdecaytimer.timer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tschnob.rustdecaytimer.R;

public class TimerFragment extends Fragment {

    private ListView timerList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        timerList = (ListView) rootView.findViewById(R.id.timer_list);


        return rootView;
    }
}
