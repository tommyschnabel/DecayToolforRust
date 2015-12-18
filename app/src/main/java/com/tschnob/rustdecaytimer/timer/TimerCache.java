package com.tschnob.rustdecaytimer.timer;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class TimerCache {

    private static String TIMER_SHARED_PREFS = "timer shared prefs";
    private static String TIMERS = "timers";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private List<Timer> timers;

    public TimerCache(Context context) {
        prefs = context.getSharedPreferences(TIMER_SHARED_PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public List<Timer> getTimers() throws IOException {
        if (timers == null) {
            String cachedTimers = prefs.getString(TIMERS, "");

            if (!cachedTimers.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                timers = mapper.readValue(cachedTimers, mapper.getTypeFactory()
                        .constructCollectionType(List.class, Timer.class));
            }
        }

        return timers;
    }

    public void storeTimers(List<Timer> timers) throws IOException {
        this.timers = timers;
        editor.putString(TIMERS, new ObjectMapper().writeValueAsString(timers));
        editor.commit();
    }
}
