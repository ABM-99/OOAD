module bank.bankmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    
    // Database dependencies
    requires java.sql;
    requires com.zaxxer.hikari;

    opens bankmanagementsystem to javafx.fxml;
    opens bankmanagementsystem.controller to javafx.fxml;
    opens bankmanagementsystem.model to javafx.fxml;
    
    exports bankmanagementsystem;
    exports bankmanagementsystem.controller;
    exports bankmanagementsystem.model;
}