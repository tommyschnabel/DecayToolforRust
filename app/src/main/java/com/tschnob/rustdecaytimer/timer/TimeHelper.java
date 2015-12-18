package com.tschnob.rustdecaytimer.timer;

public class TimeHelper {

    public Time timeUntil(long start, long end) {
        long diff = end - start;

        if (diff <= 0) {
            return new Time(0, 0);
        }

        return new Time(diff);
    }

    public Time timeUntil(long start, long duration, long now) {
        return timeUntil(now, start + duration);
    }

    public Time timeUntilDecayStart(Timer timer) {
        return timeUntil(
                timer.getLogOffTime().getTime(),
                timer.getFoundationType().getDelay(),
                System.currentTimeMillis()
        );
    }

    public Time timeUntilDecayFinish(Timer timer) {
        return timeUntil(
                timeUntilDecayStart(timer).toDate().getTime(),
                timer.getFoundationType().getDuration(),
                System.currentTimeMillis()
        );
    }
}
