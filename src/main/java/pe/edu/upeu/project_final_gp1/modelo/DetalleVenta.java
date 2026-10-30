package pe.edu.upeu.project_final_gp1.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetalleVenta {
    private final StringProperty idProducto = new SimpleStringProperty("");
    private final StringProperty nombreProducto = new SimpleStringProperty("");
    private final IntegerProperty cantidad = new SimpleIntegerProperty();
    private final DoubleProperty precioUnitario = new SimpleDoubleProperty();
    private final DoubleProperty subtotal = new SimpleDoubleProperty();

    public DetalleVenta() {}
    public DetalleVenta(String idProducto, String nombreProducto, int cantidad, double precioUnitario) {
        setIdProducto(idProducto); setNombreProducto(nombreProducto); setCantidad(cantidad); setPrecioUnitario(precioUnitario);
        setSubtotal(cantidad * precioUnitario);
    }
    public String getIdProducto() { return idProducto.get(); }
    public void setIdProducto(String value) { idProducto.set(value == null ? "" : value); }
    public StringProperty idProductoProperty() { return idProducto; }
    public String getNombreProducto() { return nombreProducto.get(); }
    public void setNombreProducto(String value) { nombreProducto.set(value == null ? "" : value); }
    public StringProperty nombreProductoProperty() { return nombreProducto; }
    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int value) { cantidad.set(value); setSubtotal(value * getPrecioUnitario()); }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario.get(); }
    public void setPrecioUnitario(double value) { precioUnitario.set(value); setSubtotal(getCantidad() * value); }
    public DoubleProperty precioUnitarioProperty() { return precioUnitario; }
    public double getSubtotal() { return subtotal.get(); }
    public void setSubtotal(double value) { subtotal.set(Math.round(value * 100.0) / 100.0); }
    public DoubleProperty subtotalProperty() { return subtotal; }
}
