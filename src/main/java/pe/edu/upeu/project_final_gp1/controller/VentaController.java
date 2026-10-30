package pe.edu.upeu.project_final_gp1.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Dialog;
import pe.edu.upeu.project_final_gp1.implementacion.ClienteDaoImpl;
import pe.edu.upeu.project_final_gp1.implementacion.ProductoDaoImpl;
import pe.edu.upeu.project_final_gp1.implementacion.VentaDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IProductoDao;
import pe.edu.upeu.project_final_gp1.interfaces.IVentaDao;
import pe.edu.upeu.project_final_gp1.modelo.Cliente;
import pe.edu.upeu.project_final_gp1.modelo.DetalleVenta;
import pe.edu.upeu.project_final_gp1.modelo.Producto;
import pe.edu.upeu.project_final_gp1.modelo.Venta;
import pe.edu.upeu.project_final_gp1.utils.ConexionBD;
import pe.edu.upeu.project_final_gp1.utils.CurrencyCellFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;
import java.util.Locale;

public class VentaController {
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TextField txtCantidad, txtCliente, txtDniCliente, txtTelefonoCliente, txtPagoInicial;
    @FXML private ComboBox<String> cmbTipoPago;
    @FXML private Label lblTotal;
    @FXML private TableView<DetalleVenta> tableCarrito;
    @FXML private TableColumn<DetalleVenta, String> colCarritoProducto;
    @FXML private TableColumn<DetalleVenta, Number> colCarritoCantidad, colCarritoPrecio, colCarritoSubtotal;
    @FXML private TableView<Venta> tableVentas;
    @FXML private TableColumn<Venta, Number> colVentaId, colVentaTotal, colVentaPagado, colVentaSaldo;
    @FXML private TableColumn<Venta, String> colVentaFecha, colVentaCliente, colVentaTipo;

    private final IProductoDao productosDao = new ProductoDaoImpl();
    private final IVentaDao ventasDao = new VentaDaoImpl();
    private final ClienteDaoImpl clientesDao = new ClienteDaoImpl();
    private final ObservableList<DetalleVenta> carrito = FXCollections.observableArrayList();
    private final ContextMenu sugerenciasCliente = new ContextMenu();
    private boolean completandoCliente;
    private double totalActual;

    @FXML public void initialize() {
        colCarritoProducto.setCellValueFactory(c -> c.getValue().nombreProductoProperty());
        colCarritoCantidad.setCellValueFactory(c -> c.getValue().cantidadProperty());
        colCarritoPrecio.setCellValueFactory(c -> c.getValue().precioUnitarioProperty());
        colCarritoSubtotal.setCellValueFactory(c -> c.getValue().subtotalProperty());
        colCarritoPrecio.setCellFactory(CurrencyCellFactory.create());
        colCarritoSubtotal.setCellFactory(CurrencyCellFactory.create());
        tableCarrito.setItems(carrito);
        colVentaId.setCellValueFactory(c -> c.getValue().idVentaProperty());
        colVentaFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        colVentaCliente.setCellValueFactory(c -> c.getValue().nombreClienteProperty());
        colVentaTotal.setCellValueFactory(c -> c.getValue().totalProperty());
        colVentaPagado.setCellValueFactory(c -> c.getValue().montoPagadoProperty());
        colVentaSaldo.setCellValueFactory(c -> c.getValue().saldoPendienteProperty());
        colVentaTipo.setCellValueFactory(c -> c.getValue().tipoPagoProperty());
        colVentaTotal.setCellFactory(CurrencyCellFactory.create());
        colVentaPagado.setCellFactory(CurrencyCellFactory.create());
        colVentaSaldo.setCellFactory(CurrencyCellFactory.create());
        cmbTipoPago.setItems(FXCollections.observableArrayList("Contado", "Crédito"));
        cmbTipoPago.setValue("Contado");
        txtPagoInicial.setDisable(true);
        sugerenciasCliente.setAutoHide(true);
        sugerenciasCliente.setHideOnEscape(true);
        txtCliente.textProperty().addListener((obs, anterior, nuevo) -> mostrarSugerenciasCliente(txtCliente, nuevo));
        txtDniCliente.textProperty().addListener((obs, anterior, nuevo) -> mostrarSugerenciasCliente(txtDniCliente, nuevo));
        cmbTipoPago.valueProperty().addListener((obs, anterior, nuevo) -> actualizarCampoPago(anterior, nuevo));
        cargarProductos(); cargarVentas(); actualizarTotal();
    }

    @FXML private void handleAgregarProducto() {
        Producto p = cmbProducto.getValue();
        if (p == null) { aviso("Selecciona un producto del inventario.", Alert.AlertType.WARNING); return; }
        int cantidad;
        try { cantidad = Integer.parseInt(txtCantidad.getText().trim()); }
        catch (Exception e) { aviso("Ingresa una cantidad entera válida.", Alert.AlertType.WARNING); return; }
        if (cantidad <= 0) { aviso("La cantidad debe ser mayor a cero.", Alert.AlertType.WARNING); return; }
        int enCarrito = carrito.stream().filter(d -> d.getIdProducto().equalsIgnoreCase(p.getId())).mapToInt(DetalleVenta::getCantidad).sum();
        if (cantidad + enCarrito > p.getStock()) { aviso("Stock insuficiente. Disponible: " + p.getStock() + ". Ya agregado al carrito: " + enCarrito + ".", Alert.AlertType.WARNING); return; }
        DetalleVenta existente = carrito.stream().filter(d -> d.getIdProducto().equalsIgnoreCase(p.getId())).findFirst().orElse(null);
        if (existente != null) existente.setCantidad(existente.getCantidad() + cantidad);
        else carrito.add(new DetalleVenta(p.getId(), p.getNombre(), cantidad, p.getPrecio()));
        tableCarrito.refresh(); txtCantidad.setText("1"); actualizarTotal();
    }

    @FXML private void handleQuitarProducto() {
        DetalleVenta seleccionado = tableCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { aviso("Selecciona un producto del carrito para quitarlo.", Alert.AlertType.WARNING); return; }
        carrito.remove(seleccionado); actualizarTotal();
    }

    @FXML private void handleGuardarCliente() {
        String nombre = txtCliente.getText() == null ? "" : txtCliente.getText().trim();
        String dni = txtDniCliente.getText() == null ? "" : txtDniCliente.getText().trim();
        String telefono = txtTelefonoCliente.getText() == null ? "" : txtTelefonoCliente.getText().trim();
        if (nombre.isBlank() || dni.isBlank()) {
            aviso("Para guardar la ficha, completa al menos el nombre y el DNI o identificador del cliente.", Alert.AlertType.WARNING);
            return;
        }
        try {
            clientesDao.guardarOActualizar(new Cliente(nombre, dni, telefono));
            sugerenciasCliente.hide();
            aviso("Ficha guardada. En futuras ventas podrás buscar al cliente escribiendo sus iniciales, nombre o DNI.", Alert.AlertType.INFORMATION);
        } catch (SQLException | IllegalArgumentException e) {
            aviso("No se pudo guardar la ficha del cliente: " + mensaje(e), Alert.AlertType.ERROR);
        }
    }

    @FXML private void handleRegistrarVenta() {
        if (carrito.isEmpty()) { aviso("Agrega al menos un producto antes de registrar la venta.", Alert.AlertType.WARNING); return; }
        String nombre = txtCliente.getText() == null ? "" : txtCliente.getText().trim();
        String dni = txtDniCliente.getText() == null ? "" : txtDniCliente.getText().trim();
        String telefono = txtTelefonoCliente.getText() == null ? "" : txtTelefonoCliente.getText().trim();
        if (nombre.isBlank() || dni.isBlank()) { aviso("Completa el nombre y el DNI del cliente.", Alert.AlertType.WARNING); return; }
        double pagado;
        if ("Contado".equals(cmbTipoPago.getValue())) pagado = totalActual;
        else {
            try { pagado = Double.parseDouble(txtPagoInicial.getText().trim().replace(',', '.')); }
            catch (Exception e) { aviso("Ingresa un pago inicial válido.", Alert.AlertType.WARNING); return; }
        }
        if (!Double.isFinite(pagado) || pagado < 0 || pagado - totalActual > 0.005) { aviso("El pago inicial debe estar entre S/ 0.00 y el total de la venta.", Alert.AlertType.WARNING); return; }

        Venta v = new Venta(); v.setFecha(LocalDate.now().toString()); v.setNombreCliente(nombre); v.setDniCliente(dni); v.setTelefonoCliente(telefono); v.setMontoPagado(pagado);
        carrito.forEach(d -> v.agregarDetalle(new DetalleVenta(d.getIdProducto(), d.getNombreProducto(), d.getCantidad(), d.getPrecioUnitario())));
        try {
            Venta guardada = ventasDao.registrarVenta(v);
            String boleta = generarTextoBoleta(guardada);
            String archivo = null;
            try { archivo = guardarBoleta(guardada, boleta).toString(); }
            catch (IOException e) { System.err.println("La venta se registró, pero no se pudo guardar el archivo de boleta: " + e.getMessage()); }
            cargarProductos(); cargarVentas(); carrito.clear(); actualizarTotal();
            limpiarFichaCliente(); txtCantidad.setText("1"); cmbTipoPago.setValue("Contado");
            mostrarBoleta(boleta, archivo);
        } catch (SQLException | IllegalArgumentException e) { aviso("No se pudo registrar la venta: " + mensaje(e), Alert.AlertType.ERROR); }
    }

    @FXML private void handleLimpiarVenta() {
        carrito.clear(); limpiarFichaCliente(); txtCantidad.setText("1");
        cmbTipoPago.setValue("Contado"); actualizarTotal();
    }

    private void mostrarSugerenciasCliente(TextField campo, String texto) {
        if (completandoCliente) return;
        sugerenciasCliente.hide();
        String filtro = texto == null ? "" : texto.trim();
        if (filtro.isBlank() || !campo.isFocused()) return;
        try {
            List<Cliente> resultados = clientesDao.buscar(filtro);
            if (resultados.isEmpty()) return;
            sugerenciasCliente.getItems().clear();
            for (Cliente cliente : resultados) {
                String telefono = cliente.getTelefono().isBlank() ? "Sin teléfono" : cliente.getTelefono();
                MenuItem opcion = new MenuItem(cliente.getNombre() + "   ·   DNI " + cliente.getDni() + "   ·   " + telefono);
                opcion.setOnAction(event -> seleccionarCliente(cliente));
                sugerenciasCliente.getItems().add(opcion);
            }
            sugerenciasCliente.show(campo, Side.BOTTOM, 0, 0);
        } catch (SQLException e) {
            System.err.println("No se pudieron cargar sugerencias de clientes: " + e.getMessage());
        }
    }

    private void seleccionarCliente(Cliente cliente) {
        completandoCliente = true;
        try {
            txtCliente.setText(cliente.getNombre());
            txtDniCliente.setText(cliente.getDni());
            txtTelefonoCliente.setText(cliente.getTelefono());
            txtCliente.positionCaret(txtCliente.getText().length());
        } finally {
            completandoCliente = false;
            sugerenciasCliente.hide();
            sugerenciasCliente.getItems().clear();
        }
    }

    private void limpiarFichaCliente() {
        sugerenciasCliente.hide();
        sugerenciasCliente.getItems().clear();
        completandoCliente = true;
        try {
            txtCliente.clear();
            txtDniCliente.clear();
            txtTelefonoCliente.clear();
        } finally {
            completandoCliente = false;
        }
    }

    private void cargarProductos() {
        try {
            var lista = productosDao.listAll(); cmbProducto.setItems(FXCollections.observableArrayList(lista));
            cmbProducto.setValue(null);
            cmbProducto.setPromptText(lista.isEmpty() ? "Registra productos en Inventario" : "Selecciona producto");
        } catch (SQLException e) { aviso("No se pudo cargar el inventario: " + mensaje(e), Alert.AlertType.ERROR); }
    }
    private void cargarVentas() {
        try { tableVentas.setItems(FXCollections.observableArrayList(ventasDao.listAll())); }
        catch (SQLException e) { aviso("No se pudo cargar el historial de ventas: " + mensaje(e), Alert.AlertType.ERROR); }
    }
    private void actualizarTotal() {
        totalActual = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        totalActual = Math.round(totalActual * 100.0) / 100.0;
        lblTotal.setText(formato(totalActual));
        if ("Contado".equals(cmbTipoPago.getValue())) txtPagoInicial.setText(String.format(Locale.US, "%.2f", totalActual));
    }
    private void actualizarCampoPago(String anterior, String nuevo) {
        boolean contado = "Contado".equals(nuevo);
        txtPagoInicial.setDisable(contado);
        if (contado) txtPagoInicial.setText(String.format(Locale.US, "%.2f", totalActual));
        else if ("Contado".equals(anterior) || txtPagoInicial.getText().isBlank()) txtPagoInicial.setText("0.00");
    }

    private String generarTextoBoleta(Venta v) {
        StringBuilder b = new StringBuilder();
        b.append("==============================================\n");
        b.append("          BOLETA DE VENTA - SISTEMA\n");
        b.append("==============================================\n");
        b.append("Boleta N° : ").append(String.format(Locale.US, "%06d", v.getIdVenta())).append('\n');
        b.append("Fecha     : ").append(v.getFecha()).append('\n');
        b.append("Cliente   : ").append(v.getNombreCliente()).append('\n');
        b.append("DNI       : ").append(v.getDniCliente()).append('\n');
        if (!v.getTelefonoCliente().isBlank()) b.append("Teléfono  : ").append(v.getTelefonoCliente()).append('\n');
        b.append("----------------------------------------------\n");
        b.append(String.format(Locale.US, "%-18s %5s %10s %10s%n", "Producto", "Cant.", "P.Unit.", "Subtotal"));
        b.append("----------------------------------------------\n");
        for (DetalleVenta d : v.getDetalles()) {
            String product = d.getNombreProducto().length() > 18 ? d.getNombreProducto().substring(0, 18) : d.getNombreProducto();
            b.append(String.format(Locale.US, "%-18s %5d %10.2f %10.2f%n", product, d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal()));
        }
        b.append("----------------------------------------------\n");
        b.append(String.format(Locale.US, "TOTAL:        S/ %.2f%n", v.getTotal()));
        b.append(String.format(Locale.US, "PAGADO:       S/ %.2f%n", v.getMontoPagado()));
        b.append(String.format(Locale.US, "SALDO:        S/ %.2f%n", v.getSaldoPendiente()));
        b.append("FORMA DE PAGO: ").append(v.getTipoPago()).append('\n');
        b.append("==============================================\n");
        b.append("        Gracias por su preferencia\n");
        return b.toString();
    }
    private Path guardarBoleta(Venta v, String texto) throws IOException {
        Path parent = ConexionBD.getDatabasePath().toAbsolutePath().getParent();
        Path dir = (parent == null ? Path.of("boletas") : parent.resolve("boletas"));
        Files.createDirectories(dir);
        Path archivo = dir.resolve(String.format(Locale.US, "Boleta_%06d.txt", v.getIdVenta()));
        Files.writeString(archivo, texto, java.nio.charset.StandardCharsets.UTF_8);
        return archivo;
    }
    private void mostrarBoleta(String texto, String archivo) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Boleta de venta"); dialog.setHeaderText(archivo == null ? "Venta registrada. Boleta generada en pantalla." : "Venta registrada. Boleta guardada en: " + archivo);
        TextArea area = new TextArea(texto); area.setEditable(false); area.setWrapText(false); area.setPrefColumnCount(54); area.setPrefRowCount(22);
        dialog.getDialogPane().setContent(new VBox(area)); dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE); dialog.showAndWait();
    }
    private String formato(double n) { return String.format(Locale.US, "S/ %.2f", n); }
    private String mensaje(Exception e) { return e.getMessage() == null ? e.toString() : e.getMessage(); }
    private void aviso(String texto, Alert.AlertType tipo) { Alert a = new Alert(tipo); a.setTitle("Ventas y boletas"); a.setHeaderText(null); a.setContentText(texto); a.showAndWait(); }
}
