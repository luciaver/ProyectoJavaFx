module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // JDK modules que Hibernate necesita internamente
    requires java.naming;
    requires java.xml;
    requires java.transaction.xa;

    // Hibernate
    requires org.hibernate.orm.core;
    requires jakarta.persistence;

    opens org.example            to javafx.graphics, javafx.fxml;
    opens org.example.controller to javafx.fxml;
    opens org.example.model      to javafx.base, org.hibernate.orm.core;
    opens org.example.dao        to org.hibernate.orm.core;

    exports org.example;
    exports org.example.controller;
    exports org.example.model;
    exports org.example.service;
    exports org.example.dao;
}