package com.tschnob.rustdecaytimer.timer;

public class TimeHelper {

    public Time timeUntil(long start, long finish) {
        long diff = finish - start;

        if (diff <= 0) {
            return new Time(0, 0);
        }

        return new Time(diff);
    }

    public Time timeUntil(long start, long duration, long finish) {
        return timeUntil(start + duration, finish);
    }

}
