package pe.edu.upeu.project_final_gp1.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.project_final_gp1.implementacion.DeudorDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IDeudorDao;
import pe.edu.upeu.project_final_gp1.modelo.Deudor;
import pe.edu.upeu.project_final_gp1.utils.CurrencyCellFactory;

import java.sql.SQLException;

public class DeudorController {
    @FXML
    private TextField txtNombre, txtDni, txtTelefono, txtDeuda, txtMontoOperacion, txtBuscar;
    @FXML
    private TableView<Deudor> tableDeudores;
    @FXML private TableColumn<Deudor, String> colNombre, colDni, colTelefono;
    @FXML
    private TableColumn<Deudor, Number> colDeuda;
    private final IDeudorDao dao = new DeudorDaoImpl();

    @FXML public void initialize() {
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colDni.setCellValueFactory(c -> c.getValue().dniProperty());
        colTelefono.setCellValueFactory(c -> c.getValue().telefonoProperty());
        colDeuda.setCellValueFactory(c -> c.getValue().deudaProperty());
        colDeuda.setCellFactory(CurrencyCellFactory.create());
        tableDeudores.getSelectionModel().selectedItemProperty().addListener((obs, old, d) -> {
            if (d != null) {
                txtNombre.setText(d.getNombre()); txtDni.setText(d.getDni()); txtTelefono.setText(d.getTelefono()); txtDeuda.setText(String.format(java.util.Locale.US, "%.2f", d.getDeuda())); txtDni.setDisable(true);
            } else if (old != null) {
                limpiar();
            }
        });
        txtBuscar.textProperty().addListener((obs, old, value) -> cargar(value));
        cargar("");
    }

    @FXML private void handleGuardar() {
        Deudor seleccionado = tableDeudores.getSelectionModel().getSelectedItem();
        try {
            String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
            String dni = txtDni.getText() == null ? "" : txtDni.getText().trim();
            String telefono = txtTelefono.getText() == null ? "" : txtTelefono.getText().trim();
            if (nombre.isBlank()) throw new IllegalArgumentException("El nombre del deudor es obligatorio.");
            if (dni.isBlank()) throw new IllegalArgumentException("El DNI del deudor es obligatorio.");
            double deuda;
            try { deuda = Double.parseDouble(txtDeuda.getText().trim().replace(',', '.')); }
            catch (Exception e) { throw new IllegalArgumentException("Ingresa una deuda válida."); }
            if (!Double.isFinite(deuda) || deuda < 0) throw new IllegalArgumentException("La deuda debe ser un número válido y no negativo.");
            deuda = Math.round(deuda * 100.0) / 100.0;
            if (seleccionado == null) {
                Deudor d = new Deudor(0, nombre, dni, telefono, deuda); dao.registrar(d);
                aviso("Deudor registrado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                seleccionado.setNombre(nombre); seleccionado.setTelefono(telefono); seleccionado.setDeuda(deuda);
                dao.actualizar(seleccionado); aviso(deuda <= 0.005 ? "La deuda quedó cancelada y el registro se retiró de la lista." : "Datos del deudor actualizados.", Alert.AlertType.INFORMATION);
            }
            cargar(txtBuscar.getText()); limpiar();
        } catch (IllegalArgumentException | SQLException e) { aviso(mensaje(e), Alert.AlertType.ERROR); }
    }

    @FXML private void handleAumentarDeuda() {
        Deudor d = tableDeudores.getSelectionModel().getSelectedItem();
        if (d == null) { aviso("Selecciona un deudor de la tabla.", Alert.AlertType.WARNING); return; }
        try {
            double monto = leerMontoOperacion(); dao.aumentarDeuda(d.getIdDeudor(), monto);
            cargar(txtBuscar.getText()); limpiar(); aviso("Deuda incrementada correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException | SQLException e) { aviso(mensaje(e), Alert.AlertType.ERROR); }
    }

    @FXML private void handleRegistrarPago() {
        Deudor d = tableDeudores.getSelectionModel().getSelectedItem();
        if (d == null) { aviso("Selecciona el deudor que realizará el pago.", Alert.AlertType.WARNING); return; }
        try {
            double monto = leerMontoOperacion(); dao.registrarPago(d.getIdDeudor(), monto);
            cargar(txtBuscar.getText()); limpiar(); aviso("Pago registrado. El cobro se incluirá en el flujo de caja de hoy.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException | SQLException e) { aviso(mensaje(e), Alert.AlertType.ERROR); }
    }

    @FXML private void handleLimpiar() { limpiar(); }
    @FXML private void handleBuscar() { cargar(txtBuscar.getText()); }

    private double leerMontoOperacion() {
        try { return Double.parseDouble(txtMontoOperacion.getText().trim().replace(',', '.')); }
        catch (Exception e) { throw new IllegalArgumentException("Ingresa el monto de la operación."); }
    }
    private void cargar(String filtro) {
        try { tableDeudores.setItems(FXCollections.observableArrayList(filtro == null || filtro.isBlank() ? dao.listAll() : dao.buscar(filtro))); }
        catch (SQLException e) { aviso("No se pudo cargar la lista de deudores: " + mensaje(e), Alert.AlertType.ERROR); }
    }
    private void limpiar() {
        tableDeudores.getSelectionModel().clearSelection(); txtNombre.clear(); txtDni.clear(); txtTelefono.clear(); txtDeuda.clear(); txtMontoOperacion.clear(); txtDni.setDisable(false);
    }
    private String mensaje(Exception e) { return e.getMessage() == null ? e.toString() : e.getMessage(); }
    private void aviso(String texto, Alert.AlertType tipo) { Alert a = new Alert(tipo); a.setTitle("Gestión de deudores"); a.setHeaderText(null); a.setContentText(texto); a.showAndWait(); }
}
