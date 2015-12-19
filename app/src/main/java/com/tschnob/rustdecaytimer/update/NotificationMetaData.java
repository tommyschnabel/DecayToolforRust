package com.tschnob.rustdecaytimer.update;

public class NotificationMetaData {

    public enum Type {
        DEACY_START_WARNING,
        START_DECAY,
        ONE_HOUR_TO_FINISH,
        FINISH_DECAY
    }

    private Type type;
    private String id;

    public NotificationMetaData(String id) {
        this.id = id;
    }

    public NotificationMetaData() {}

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
