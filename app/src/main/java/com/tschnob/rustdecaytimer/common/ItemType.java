package com.tschnob.rustdecaytimer.common;

import java.util.concurrent.TimeUnit;

public enum ItemType {
    TWIG("Twig Foundation", TimeUnit.HOURS.toMillis(6), TimeUnit.HOURS.toMillis(6)),
    WOOD("Wood Foundation", TimeUnit.HOURS.toMillis(12), TimeUnit.HOURS.toMillis(24)),
    STONE("Stone Foundation", TimeUnit.HOURS.toMillis(18), TimeUnit.HOURS.toMillis(48)),
    SHEET_METAL("Sheet Metal Foundation", TimeUnit.HOURS.toMillis(18), TimeUnit.HOURS.toMillis(72)),
    ARMORED("Armored Foundation", TimeUnit.HOURS.toMillis(24), TimeUnit.HOURS.toMillis(120)),
    SECRET_STASH("Secret Stash", 0, TimeUnit.DAYS.toMillis(3));

    private String prettyName;
    private long delay;
    private long duration;

    ItemType(String prettyName, long delay, long duration) {
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
