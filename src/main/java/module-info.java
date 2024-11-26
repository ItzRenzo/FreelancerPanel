module me.group.freelancerpanel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires java.sql;
    requires java.desktop;

    opens me.group.freelancerpanel to javafx.fxml;
    opens me.group.freelancerpanel.controllers to javafx.fxml;

    exports me.group.freelancerpanel;
    exports me.group.freelancerpanel.controllers;
}