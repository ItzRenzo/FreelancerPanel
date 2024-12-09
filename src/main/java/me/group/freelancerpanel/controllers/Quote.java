package me.group.freelancerpanel.controllers;

import javafx.beans.property.*;

public class Quote {
    private final IntegerProperty quoteId;
    private final StringProperty title;
    private final StringProperty clientName;
    private final DoubleProperty proposedPrice;
    private final StringProperty startDate;
    private final StringProperty deadline;
    private final StringProperty paymentTerms;
    private final StringProperty status;

    public Quote(int quoteId, String title, String clientName, double proposedPrice, String startDate, String deadline, String paymentTerms, String status) {
        this.quoteId = new SimpleIntegerProperty(quoteId);
        this.title = new SimpleStringProperty(title);
        this.clientName = new SimpleStringProperty(clientName);
        this.proposedPrice = new SimpleDoubleProperty(proposedPrice);
        this.startDate = new SimpleStringProperty(startDate);
        this.deadline = new SimpleStringProperty(deadline);
        this.paymentTerms = new SimpleStringProperty(paymentTerms);
        this.status = new SimpleStringProperty(status);
    }

    public int getQuoteId() { return quoteId.get(); }
    public IntegerProperty quoteIdProperty() { return quoteId; }

    public String getTitle() { return title.get(); }
    public StringProperty titleProperty() { return title; }

    public String getClientName() { return clientName.get(); }
    public StringProperty clientNameProperty() { return clientName; }

    public double getProposedPrice() { return proposedPrice.get(); }
    public DoubleProperty proposedPriceProperty() { return proposedPrice; }

    public String getStartDate() { return startDate.get(); }
    public StringProperty startDateProperty() { return startDate; }

    public String getDeadline() { return deadline.get(); }
    public StringProperty deadlineProperty() { return deadline; }

    public String getPaymentTerms() { return paymentTerms.get(); }
    public StringProperty paymentTermsProperty() { return paymentTerms; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}

