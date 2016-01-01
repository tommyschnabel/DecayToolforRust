package com.tschnob.rustdecaytimer.notification;

import com.tschnob.rustdecaytimer.timer.Time;

public class NotificationMetaData {

    public enum EventType {
        BEFORE("Before"),
        WHEN("When");

        private String name;

        EventType(String name) {
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

    private EventType eventType;
    private Event event;
    private Time timeBeforeEvent;
    private String id;

    public NotificationMetaData() {}

    public static NotificationMetaData create(String id, EventType eventType, Event event, Time timeBeforeEvent) {
        NotificationMetaData notification = new NotificationMetaData();
        notification.setId(id);
        notification.setEventType(eventType);
        notification.setEvent(event);
        notification.setTimeBeforeEvent(timeBeforeEvent);
        return notification;
    }

    public static NotificationMetaData create(String id, EventType eventType, Event event) {
        NotificationMetaData notification = new NotificationMetaData();
        notification.setId(id);
        notification.setEventType(eventType);
        notification.setEvent(event);
        return notification;
    }

    public static NotificationMetaData create(String id) {
        NotificationMetaData notification = new NotificationMetaData();
        notification.setId(id);
        return notification;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
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

    public Time getTimeBeforeEvent() {
        return timeBeforeEvent;
    }

    public void setTimeBeforeEvent(Time timeBeforeEvent) {
        this.timeBeforeEvent = timeBeforeEvent;
    }
}
