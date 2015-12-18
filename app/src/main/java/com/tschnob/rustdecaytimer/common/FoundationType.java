package com.tschnob.rustdecaytimer.common;

import java.util.concurrent.TimeUnit;

public enum FoundationType {
    STICK("Stick", TimeUnit.HOURS.toMillis(6L), TimeUnit.HOURS.toMillis(6L)),
    WOOD("Wood", TimeUnit.HOURS.toMillis(12L), TimeUnit.HOURS.toMillis(24L)),
    STONE("Stone", TimeUnit.HOURS.toMillis(18L), TimeUnit.HOURS.toMillis(48L)),
    SHEET_METAL("Sheet Metal", TimeUnit.HOURS.toMillis(18L), TimeUnit.HOURS.toMillis(72L)),
    ARMORED("Armored", TimeUnit.HOURS.toMillis(24L), TimeUnit.HOURS.toMillis(120L));

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
