package me.group.freelancerpanel.controllers;

import javafx.beans.property.*;

public class Invoice {
    private final IntegerProperty invoiceID;
    private final StringProperty createdAt;
    private final DoubleProperty amount;
    private final StringProperty status;
    private final StringProperty paymentLink;
    private final StringProperty paidAt;
    private final StringProperty dueAt;
    private final StringProperty clientName;
    private final StringProperty title;
    private final StringProperty memo;

    public Invoice(int invoiceID, String createdAt, double amount, String status, String paymentLink,
                   String paidAt, String dueAt, String clientName, String title, String memo) {
        this.invoiceID = new SimpleIntegerProperty(invoiceID);
        this.createdAt = new SimpleStringProperty(createdAt);
        this.amount = new SimpleDoubleProperty(amount);
        this.status = new SimpleStringProperty(status);
        this.paymentLink = new SimpleStringProperty(paymentLink);
        this.paidAt = new SimpleStringProperty(paidAt);
        this.dueAt = new SimpleStringProperty(dueAt);
        this.clientName = new SimpleStringProperty(clientName);
        this.title = new SimpleStringProperty(title);
        this.memo = new SimpleStringProperty(memo);
    }

    // Getters for properties
    public IntegerProperty invoiceIDProperty() { return invoiceID; }
    public StringProperty createdAtProperty() { return createdAt; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty statusProperty() { return status; }
    public StringProperty paymentLinkProperty() { return paymentLink; }
    public StringProperty paidAtProperty() { return paidAt; }
    public StringProperty dueAtProperty() { return dueAt; }
    public StringProperty clientNameProperty() { return clientName; }
    public StringProperty titleProperty() { return title; }
    public StringProperty memoProperty() { return memo; }
}
