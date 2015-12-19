package com.tschnob.rustdecaytimer.timer;

import com.tschnob.rustdecaytimer.common.FoundationType;

public class TimeHelper {

    public Time timeUntilDecayStart(Timer timer) {
        long timeUntil = getStartTime(timer) - System.currentTimeMillis();
        return new Time(Math.max(timeUntil, 0));
    }

    public Time timeUntilDecayFinish(Timer timer) {
        long timeUntil = getFinishTime(timer) - System.currentTimeMillis();
        return new Time(Math.max(timeUntil, 0));
    }

    public long getStartTime(Timer timer) {
        return timer.getLogOffTime().getTime() + timer.getFoundationType().getDelay();
    }

    public long getFinishTime(Timer timer) {
        FoundationType type = timer.getFoundationType();
        return timer.getLogOffTime().getTime() + type.getDelay() + type.getDuration();
    }
}
