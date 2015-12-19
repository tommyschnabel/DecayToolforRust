package com.tschnob.rustdecaytimer.common;

import java.util.concurrent.TimeUnit;

public enum FoundationType {
    STICK("Stick", TimeUnit.MINUTES.toMillis(6), TimeUnit.MINUTES.toMillis(6)),
    WOOD("Wood", TimeUnit.HOURS.toMillis(12), TimeUnit.HOURS.toMillis(24)),
    STONE("Stone", TimeUnit.HOURS.toMillis(18), TimeUnit.HOURS.toMillis(48)),
    SHEET_METAL("Sheet Metal", TimeUnit.HOURS.toMillis(18), TimeUnit.HOURS.toMillis(72)),
    ARMORED("Armored", TimeUnit.HOURS.toMillis(24), TimeUnit.HOURS.toMillis(120));

    private String prettyName;
    private long delay;
    private long duration;

    FoundationType(String prettyName, long delay, long duration) {
        this.prettyName = prettyName;
        this.delay = delay;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return prettyName;
    }

    public long getDelay() {
        return delay;
    }

    public long getDuration() {
        return duration;
    }
}
