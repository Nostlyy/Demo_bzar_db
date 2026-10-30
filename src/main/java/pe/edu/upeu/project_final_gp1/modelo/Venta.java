package pe.edu.upeu.project_final_gp1.modelo;

import javafx.beans.property.*;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private final IntegerProperty idVenta = new SimpleIntegerProperty();
    private final StringProperty fecha = new SimpleStringProperty("");
    private final StringProperty nombreCliente = new SimpleStringProperty("");
    private final StringProperty dniCliente = new SimpleStringProperty("");
    private final StringProperty telefonoCliente = new SimpleStringProperty("");
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final DoubleProperty montoPagado = new SimpleDoubleProperty();
    private final DoubleProperty saldoPendiente = new SimpleDoubleProperty();
    private final StringProperty tipoPago = new SimpleStringProperty("Contado");
    private final List<DetalleVenta> detalles = new ArrayList<>();

    public int getIdVenta() { return idVenta.get(); }
    public void setIdVenta(int value) { idVenta.set(value); }
    public IntegerProperty idVentaProperty() { return idVenta; }
    public String getFecha() { return fecha.get(); }
    public void setFecha(String value) { fecha.set(value == null ? "" : value); }
    public StringProperty fechaProperty() { return fecha; }
    public String getNombreCliente() { return nombreCliente.get(); }
    public void setNombreCliente(String value) { nombreCliente.set(value == null ? "" : value); }
    public StringProperty nombreClienteProperty() { return nombreCliente; }
    public String getDniCliente() { return dniCliente.get(); }
    public void setDniCliente(String value) { dniCliente.set(value == null ? "" : value); }
    public StringProperty dniClienteProperty() { return dniCliente; }
    public String getTelefonoCliente() { return telefonoCliente.get(); }
    public void setTelefonoCliente(String value) { telefonoCliente.set(value == null ? "" : value); }
    public StringProperty telefonoClienteProperty() { return telefonoCliente; }
    public double getTotal() { return total.get(); }
    public void setTotal(double value) { total.set(value); }
    public DoubleProperty totalProperty() { return total; }
    public double getMontoPagado() { return montoPagado.get(); }
    public void setMontoPagado(double value) { montoPagado.set(value); }
    public DoubleProperty montoPagadoProperty() { return montoPagado; }
    public double getSaldoPendiente() { return saldoPendiente.get(); }
    public void setSaldoPendiente(double value) { saldoPendiente.set(value); }
    public DoubleProperty saldoPendienteProperty() { return saldoPendiente; }
    public String getTipoPago() { return tipoPago.get(); }
    public void setTipoPago(String value) { tipoPago.set(value == null ? "Contado" : value); }
    public StringProperty tipoPagoProperty() { return tipoPago; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void agregarDetalle(DetalleVenta detalle) { if (detalle != null) detalles.add(detalle); }
}
