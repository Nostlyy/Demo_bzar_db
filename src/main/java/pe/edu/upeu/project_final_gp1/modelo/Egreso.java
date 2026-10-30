package pe.edu.upeu.project_final_gp1.modelo;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Egreso {
    private final IntegerProperty idEgreso = new SimpleIntegerProperty();
    private final StringProperty fecha = new SimpleStringProperty(LocalDate.now().toString());
    private final StringProperty descripcion = new SimpleStringProperty("");
    private final DoubleProperty monto = new SimpleDoubleProperty();

    public Egreso() {}
    public Egreso(int id, String fecha, String descripcion, double monto) {
        setIdEgreso(id); setFecha(fecha); setDescripcion(descripcion); setMonto(monto);
    }
    public int getIdEgreso() { return idEgreso.get(); }
    public void setIdEgreso(int value) { idEgreso.set(value); }
    public IntegerProperty idEgresoProperty() { return idEgreso; }
    public String getFecha() { return fecha.get(); }
    public void setFecha(String value) { fecha.set(value == null ? LocalDate.now().toString() : value); }
    public StringProperty fechaProperty() { return fecha; }
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String value) { descripcion.set(value == null ? "" : value); }
    public StringProperty descripcionProperty() { return descripcion; }
    public double getMonto() { return monto.get(); }
    public void setMonto(double value) { monto.set(value); }
    public DoubleProperty montoProperty() { return monto; }
}
