package me.group.freelancerpanel.controllers;

import javafx.beans.property.*;

public class Client {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty discord;
    private final IntegerProperty totalCommission;
    private final DoubleProperty totalRevenue;
    private final StringProperty since;

    @Override
    public String toString() {
        return getName();
    }

    public Client(int id, String name, String email, String discord, int totalCommission, double totalRevenue, String since) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.discord = new SimpleStringProperty(discord);
        this.totalCommission = new SimpleIntegerProperty(totalCommission);
        this.totalRevenue = new SimpleDoubleProperty(totalRevenue);
        this.since = new SimpleStringProperty(since);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getEmail() { return email.get(); }
    public String getDiscord() { return discord.get(); }
    public int getTotalCommission() { return totalCommission.get(); }
    public double getTotalRevenue() { return totalRevenue.get(); }
    public String getSince() { return since.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty discordProperty() { return discord; }
    public IntegerProperty totalCommissionProperty() { return totalCommission; }
    public DoubleProperty totalRevenueProperty() { return totalRevenue; }
    public StringProperty sinceProperty() { return since; }
}
