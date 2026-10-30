package pe.edu.upeu.project_final_gp1.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.project_final_gp1.implementacion.FinanzasDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IFinanzasDao;
import pe.edu.upeu.project_final_gp1.modelo.Egreso;
import pe.edu.upeu.project_final_gp1.utils.CurrencyCellFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

public class FlujoCajaController {
    @FXML private Label lblFecha, lblIngresos, lblEgresos, lblNeto, lblDeudaPendiente;
    @FXML private TextField txtDescripcion, txtMonto;
    @FXML private TableView<Egreso> tableEgresos;
    @FXML private TableColumn<Egreso, String> colEgresoFecha, colEgresoDescripcion;
    @FXML private TableColumn<Egreso, Number> colEgresoMonto;
    private final IFinanzasDao dao = new FinanzasDaoImpl();

    @FXML public void initialize() {
        colEgresoFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colEgresoDescripcion.setCellValueFactory(c -> c.getValue().descripcionProperty());
        colEgresoMonto.setCellValueFactory(c -> c.getValue().montoProperty());
        colEgresoMonto.setCellFactory(CurrencyCellFactory.create());
        actualizar();
    }

    @FXML private void handleRegistrarEgreso() {
        String descripcion = txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim();
        if (descripcion.isBlank()) { aviso("Ingresa la descripción del egreso.", Alert.AlertType.WARNING); return; }
        double monto;
        try { monto = Double.parseDouble(txtMonto.getText().trim().replace(',', '.')); }
        catch (Exception e) { aviso("Ingresa un monto válido.", Alert.AlertType.WARNING); return; }
        try {
            dao.registrarEgreso(new Egreso(0, LocalDate.now().toString(), descripcion, monto));
            txtDescripcion.clear(); txtMonto.clear(); actualizar(); aviso("Egreso registrado correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException | SQLException e) { aviso("No se pudo registrar el egreso: " + mensaje(e), Alert.AlertType.ERROR); }
    }
    @FXML private void handleActualizar() { actualizar(); }

    private void actualizar() {
        LocalDate hoy = LocalDate.now(); lblFecha.setText("Resumen del " + hoy);
        try {
            double ingresos = dao.calcularIngresos(hoy), egresos = dao.calcularEgresos(hoy), deuda = dao.calcularDeudaPendiente();
            lblIngresos.setText(formato(ingresos)); lblEgresos.setText(formato(egresos)); lblNeto.setText(formato(ingresos - egresos)); lblDeudaPendiente.setText(formato(deuda));
            tableEgresos.setItems(FXCollections.observableArrayList(dao.listarEgresos(hoy)));
        } catch (SQLException e) { aviso("No se pudo actualizar el flujo de caja: " + mensaje(e), Alert.AlertType.ERROR); }
    }
    private String formato(double n) { return String.format(Locale.US, "S/ %.2f", n); }
    private String mensaje(Exception e) { return e.getMessage() == null ? e.toString() : e.getMessage(); }
    private void aviso(String texto, Alert.AlertType tipo) { Alert a = new Alert(tipo); a.setTitle("Flujo de caja"); a.setHeaderText(null); a.setContentText(texto); a.showAndWait(); }
}
