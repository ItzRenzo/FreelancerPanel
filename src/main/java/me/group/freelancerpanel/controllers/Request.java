package me.group.freelancerpanel.controllers;

import javafx.beans.property.*;

public class Request {
    private final IntegerProperty requestId;
    private final StringProperty description;
    private final StringProperty commissionTitle;
    private final DoubleProperty offeredAmount;
    private final DoubleProperty paid;
    private final StringProperty deadline;
    private final StringProperty submittedAt;
    private final StringProperty status;

    public Request(int requestId, String description, String commissionTitle, double offeredAmount,
                   double paid, String deadline, String submittedAt, String status) {
        this.requestId = new SimpleIntegerProperty(requestId);
        this.description = new SimpleStringProperty(description);
        this.commissionTitle = new SimpleStringProperty(commissionTitle);
        this.offeredAmount = new SimpleDoubleProperty(offeredAmount);
        this.paid = new SimpleDoubleProperty(paid);
        this.deadline = new SimpleStringProperty(deadline);
        this.submittedAt = new SimpleStringProperty(submittedAt);
        this.status = new SimpleStringProperty(status);
    }

    public IntegerProperty requestIdProperty() { return requestId; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty commissionTitleProperty() { return commissionTitle; }
    public DoubleProperty offeredAmountProperty() { return offeredAmount; }
    public DoubleProperty paidProperty() { return paid; }
    public StringProperty deadlineProperty() { return deadline; }
    public StringProperty submittedAtProperty() { return submittedAt; }
    public StringProperty statusProperty() { return status; }
}
