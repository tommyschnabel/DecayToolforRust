package com.tschnob.rustdecaytimer.timer;

import com.tschnob.rustdecaytimer.common.ItemType;

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
        return timer.getLogOffTime().getTime() + timer.getItemType().getDelay();
    }

    public long getFinishTime(Timer timer) {
        ItemType type = timer.getItemType();
        return timer.getLogOffTime().getTime() + type.getDelay() + type.getDuration();
    }
}
