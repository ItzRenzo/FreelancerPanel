package me.group.freelancerpanel.controllers;

import javafx.beans.property.*;

public class Commission {
    private final IntegerProperty commissionID;
    private final StringProperty title;
    private final StringProperty client; // client_name
    private final DoubleProperty totalValue;
    private final DoubleProperty totalPaid;
    private final StringProperty startDate;
    private final StringProperty deadline;
    private final StringProperty product; // product_name
    private final StringProperty status;

    public Commission(int commissionID, String title, String client, double totalValue,
                      double totalPaid, String startDate, String deadline, String product, String status) {
        this.commissionID = new SimpleIntegerProperty(commissionID);
        this.title = new SimpleStringProperty(title);
        this.client = new SimpleStringProperty(client);
        this.totalValue = new SimpleDoubleProperty(totalValue);
        this.totalPaid = new SimpleDoubleProperty(totalPaid);
        this.startDate = new SimpleStringProperty(startDate);
        this.deadline = new SimpleStringProperty(deadline);
        this.product = new SimpleStringProperty(product);
        this.status = new SimpleStringProperty(status);
    }

    // Getters for properties
    public IntegerProperty commissionIDProperty() { return commissionID; }
    public StringProperty titleProperty() { return title; }
    public StringProperty clientProperty() { return client; }
    public DoubleProperty totalValueProperty() { return totalValue; }
    public DoubleProperty totalPaidProperty() { return totalPaid; }
    public StringProperty startDateProperty() { return startDate; }
    public StringProperty deadlineProperty() { return deadline; }
    public StringProperty productProperty() { return product; }
    public StringProperty statusProperty() { return status; }

    // Override toString to display only the commission title
    @Override
    public String toString() {
        return title.get(); // This will be shown in the ComboBox
    }
}


