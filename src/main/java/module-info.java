module com.example.pet_inventory {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.example.pet_inventory to javafx.fxml;
    opens com.example.pet_inventory.controller to javafx.fxml;
    opens com.example.pet_inventory.models to javafx.base, javafx.fxml;

    exports com.example.pet_inventory;
    exports com.example.pet_inventory.controller;



}