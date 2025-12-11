package com.example.deviceposture.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertingData {
    private int total;
    private int skipped;
    private int items;
    private List<AlertEvent> events;

    // Getters and Setters
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getSkipped() { return skipped; }
    public void setSkipped(int skipped) { this.skipped = skipped; }

    public int getItems() { return items; }
    public void setItems(int items) { this.items = items; }

    public List<AlertEvent> getEvents() { return events; }
    public void setEvents(List<AlertEvent> events) { this.events = events; }
}
