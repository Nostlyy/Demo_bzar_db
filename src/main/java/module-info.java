module pe.edu.upeu.project_final_gp1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires javafx.graphics;

    opens pe.edu.upeu.project_final_gp1 to javafx.fxml;
    opens pe.edu.upeu.project_final_gp1.modelo to java.base, javafx.base, javafx.fxml;
    opens pe.edu.upeu.project_final_gp1.controller to javafx.fxml;

    exports pe.edu.upeu.project_final_gp1;
    exports pe.edu.upeu.project_final_gp1.utils;
    exports pe.edu.upeu.project_final_gp1.modelo;
    exports pe.edu.upeu.project_final_gp1.interfaces;
    exports pe.edu.upeu.project_final_gp1.implementacion;
    exports pe.edu.upeu.project_final_gp1.controller;
    exports pe.edu.upeu.project_final_gp1.test;
}
