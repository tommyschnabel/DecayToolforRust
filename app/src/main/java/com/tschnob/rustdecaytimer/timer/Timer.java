package com.tschnob.rustdecaytimer.timer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tschnob.rustdecaytimer.common.ItemType;

import java.util.Date;

public class Timer {

    private ItemType itemType;
    private Date logOffTime;

    public Timer() {}

    public Timer(ItemType itemType, Date logOffTime) {
        this.itemType = itemType;
        this.logOffTime = logOffTime;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Date getLogOffTime() {
        return logOffTime;
    }

    public void setLogOffTime(Date logOffTime) {
        this.logOffTime = logOffTime;
    }

    @JsonIgnore
    public String getUniqueId() {
        return String.valueOf(logOffTime.getTime()) + itemType;
    }
}
