package pe.edu.upeu.project_final_gp1.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class MainController {
    @FXML private StackPane contentArea;

    @FXML public void initialize() { showHome(); }
    @FXML private void showHome() { loadView("home-view.fxml"); }
    @FXML private void showProductos() { loadView("productos-view.fxml"); }
    @FXML private void showVentas() { loadView("ventas-view.fxml"); }
    @FXML private void showDeudores() { loadView("deudores-view.fxml"); }
    @FXML private void showFlujoCaja() { loadView("flujo-view.fxml"); }

    private void loadView(String fxml) {
        String ruta = "/pe/edu/upeu/project_final_gp1/views/" + fxml;
        try {
            URL resource = getClass().getResource(ruta);
            if (resource == null) throw new IOException("No existe el recurso " + ruta);
            Parent view = new FXMLLoader(resource).load();
            contentArea.getChildren().setAll(view);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No se pudo abrir el módulo");
            alert.setHeaderText("Error al cargar la vista");
            alert.setContentText("Vista: " + fxml + "\n" + (e.getMessage() == null ? e.toString() : e.getMessage()));
            alert.showAndWait();
        }
    }
}
