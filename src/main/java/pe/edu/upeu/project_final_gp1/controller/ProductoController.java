package pe.edu.upeu.project_final_gp1.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.project_final_gp1.implementacion.ProductoDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IProductoDao;
import pe.edu.upeu.project_final_gp1.modelo.Producto;
import pe.edu.upeu.project_final_gp1.utils.CurrencyCellFactory;

import java.sql.SQLException;

public class ProductoController {
    @FXML private TextField txtId, txtNombre, txtPrecio, txtStock, txtBuscar;
    @FXML private TableView<Producto> tableProductos;
    @FXML private TableColumn<Producto, String> colId, colNombre;
    @FXML private TableColumn<Producto, Number> colPrecio, colStock;

    private final IProductoDao dao = new ProductoDaoImpl();

    @FXML public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colPrecio.setCellValueFactory(c -> c.getValue().precioProperty());
        colPrecio.setCellFactory(CurrencyCellFactory.create());
        colStock.setCellValueFactory(c -> c.getValue().stockProperty());
        tableProductos.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) {
                txtId.setText(p.getId()); txtNombre.setText(p.getNombre());
                txtPrecio.setText(String.valueOf(p.getPrecio())); txtStock.setText(String.valueOf(p.getStock()));
            }
        });
        if (txtBuscar != null) txtBuscar.textProperty().addListener((obs, old, value) -> cargarProductos(value));
        cargarProductos("");
    }

    @FXML private void handleRegistrar() {
        try {
            Producto p = leerFormulario();
            if (dao.searchById(p.getId()) != null) { aviso("Ya existe un producto con ese código. Selecciónalo para actualizarlo.", Alert.AlertType.WARNING); return; }
            dao.insert(p); cargarProductos(txtBuscar.getText()); limpiarFormulario();
            aviso("Producto registrado correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException | SQLException e) { aviso(mensaje(e), Alert.AlertType.ERROR); }
    }

    @FXML private void handleActualizar() {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { aviso("Selecciona un producto de la tabla para actualizarlo.", Alert.AlertType.WARNING); return; }
        try {
            Producto p = leerFormulario();
            if (!p.getId().equalsIgnoreCase(seleccionado.getId())) throw new IllegalArgumentException("El código del producto no se puede cambiar. Limpia el formulario para registrar otro producto.");
            if (!dao.update(p)) throw new SQLException("No se encontró el producto que intentas actualizar.");
            cargarProductos(txtBuscar.getText()); limpiarFormulario(); aviso("Producto actualizado correctamente.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException | SQLException e) { aviso(mensaje(e), Alert.AlertType.ERROR); }
    }

    @FXML private void handleEliminar() {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { aviso("Selecciona el producto que deseas eliminar.", Alert.AlertType.WARNING); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar el producto '" + seleccionado.getNombre() + "'?", ButtonType.CANCEL, ButtonType.OK);
        confirm.setTitle("Eliminar producto"); confirm.setHeaderText(null);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            if (!dao.delete(seleccionado.getId())) throw new SQLException("No se encontró el producto.");
            cargarProductos(txtBuscar.getText()); limpiarFormulario(); aviso("Producto eliminado.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) { aviso(mensaje(e), Alert.AlertType.ERROR); }
    }

    @FXML private void handleLimpiar() { limpiarFormulario(); }
    @FXML private void handleBuscar() { cargarProductos(txtBuscar.getText()); }

    private Producto leerFormulario() {
        String id = txtId.getText() == null ? "" : txtId.getText().trim();
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (id.isBlank()) throw new IllegalArgumentException("El código del producto es obligatorio.");
        if (nombre.isBlank()) throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        double precio; int stock;
        try { precio = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.')); }
        catch (Exception e) { throw new IllegalArgumentException("Ingresa un precio válido. Ejemplo: 12.50."); }
        try { stock = Integer.parseInt(txtStock.getText().trim()); }
        catch (Exception e) { throw new IllegalArgumentException("El stock debe ser un número entero."); }
        if (!Double.isFinite(precio) || precio < 0) throw new IllegalArgumentException("El precio debe ser un número finito y no negativo.");
        precio = Math.round(precio * 100.0) / 100.0;
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        return new Producto(id, nombre, precio, stock);
    }

    private void cargarProductos(String filtro) {
        try { tableProductos.setItems(FXCollections.observableArrayList(filtro == null || filtro.isBlank() ? dao.listAll() : dao.searchByNombre(filtro))); }
        catch (SQLException e) { aviso("No se pudo cargar el inventario: " + mensaje(e), Alert.AlertType.ERROR); }
    }
    private void limpiarFormulario() {
        tableProductos.getSelectionModel().clearSelection(); txtId.clear(); txtNombre.clear(); txtPrecio.clear(); txtStock.clear();
    }
    private String mensaje(Exception e) { return e.getMessage() == null ? e.toString() : e.getMessage(); }
    private void aviso(String texto, Alert.AlertType tipo) { Alert a = new Alert(tipo); a.setTitle("Gestión de productos"); a.setHeaderText(null); a.setContentText(texto); a.showAndWait(); }
}
