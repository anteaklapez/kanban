package com.hivetech.kanban.dto;

public class TaskWebSocketEvent {
    private String eventType;
    private Object data;

    public TaskWebSocketEvent() {
    }

    public TaskWebSocketEvent(String eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
