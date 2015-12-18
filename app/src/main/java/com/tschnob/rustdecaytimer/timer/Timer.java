package com.tschnob.rustdecaytimer.timer;

import com.tschnob.rustdecaytimer.common.FoundationType;

import java.util.Date;

public class Timer {

    private FoundationType foundationType;
    private Date logOffTime;

    public Timer() {}

    public Timer(FoundationType foundationType, Date logOffTime) {
        this.foundationType = foundationType;
        this.logOffTime = logOffTime;
    }

    public FoundationType getFoundationType() {
        return foundationType;
    }

    public void setFoundationType(FoundationType foundationType) {
        this.foundationType = foundationType;
    }

    public Date getLogOffTime() {
        return logOffTime;
    }

    public void setLogOffTime(Date logOffTime) {
        this.logOffTime = logOffTime;
    }
}
