package com.tschnob.rustdecaytimer.timer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Time {

    private long hours;
    private long minutes;

    public Time() {} //Needed for Jackson to deserialize from JSON

    public Time(long hours, long minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public Time(long time) {
        this.hours = TimeUnit.MILLISECONDS.toHours(time);
        this.minutes = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.HOURS.toMillis(this.hours));
    }

    public Date toDate() {
        return new Date(toTime());
    }

    public Long toTime() {
        long time = TimeUnit.HOURS.toMillis(hours);
        time += TimeUnit.MINUTES.toMillis(minutes);

        return time;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return hours + "h, " + minutes + "m";
    }
}
