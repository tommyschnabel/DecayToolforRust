package com.tschnob.rustdecaytimer.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.tschnob.rustdecaytimer.timer.Time;

public class NotificationMetaData {

    public enum Type {
        BEFORE("Before"),
        WHEN("When");

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Event {
        DECAY_START("Decay Start"),
        DECAY_FINISH("Decay Finish");

        private String name;

        Event(String name) {
            this.name = name;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    private Type type;
    private Event event;
    private Optional<Time> timeBeforeEvent = Optional.absent();
    private String id;

    public NotificationMetaData() {}

    public static NotificationMetaData create(Type type, Event event, Time timeBeforeEvent) {
        NotificationMetaData notification = new NotificationMetaData();
        notification.setType(type);
        notification.setEvent(event);
        notification.setTimeBeforeEvent(Optional.fromNullable(timeBeforeEvent));
        return notification;
    }

    public static NotificationMetaData create(Type type, Event event) {
        NotificationMetaData notification = new NotificationMetaData();
        notification.setType(type);
        notification.setEvent(event);
        return notification;
    }

    public static NotificationMetaData create(String id) {
        NotificationMetaData notification = new NotificationMetaData();
        notification.setId(id);
        return notification;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Optional<Time> getTimeBeforeEvent() {
        return timeBeforeEvent;
    }

    public void setTimeBeforeEvent(Optional<Time> timeBeforeEvent) {
        this.timeBeforeEvent = timeBeforeEvent;
    }

//    public void setTimeBeforeEvent(Time timeBeforeEvent) {
//        this.timeBeforeEvent = Optional.fromNullable(timeBeforeEvent);
//    }
}
