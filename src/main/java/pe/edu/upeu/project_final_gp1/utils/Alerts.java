package pe.edu.upeu.project_final_gp1.utils;

import javafx.scene.control.Alert;

public class Alerts {
    public static Alert viewAlerts(String titulo, String mensaje, String tipo) {
        Alert alert;
        if (tipo.equals("WARNING")) {
           alert= new Alert(Alert.AlertType.WARNING);
        } else if (tipo.equals("CONFIRMATION")) {
            alert=  new Alert(Alert.AlertType.CONFIRMATION);
        } else {
            alert= new Alert(Alert.AlertType.ERROR);
        }
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert;
    }
}
