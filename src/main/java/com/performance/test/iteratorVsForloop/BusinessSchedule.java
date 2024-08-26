package com.performance.test.iteratorVsForloop;

import java.time.LocalDate;

public class BusinessSchedule {
    private final LocalDate date;
    private final String changeType;
    private final String description;

    public BusinessSchedule(String changeType, String description, LocalDate date) {
        this.date = date;
        this.changeType = changeType;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getChangeType() {
        return changeType;
    }

    public String getDescription() {
        return description;
    }
}