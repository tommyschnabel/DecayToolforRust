package com.tschnob.rustdecaytimer.timer;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public String getUniqueId() {
        TimeHelper helper = new TimeHelper();
        Long finish = helper.timeUntilDecayFinish(this).toDate().getTime();
        Long start = helper.timeUntilDecayStart(this).toDate().getTime();
        return String.valueOf((finish + start));
    }
}
