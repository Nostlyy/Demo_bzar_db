package pe.edu.upeu.project_final_gp1.modelo;

import javafx.beans.property.*;

public class Deudor {
    private final IntegerProperty idDeudor = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty("");
    private final StringProperty dni = new SimpleStringProperty("");
    private final StringProperty telefono = new SimpleStringProperty("");
    private final DoubleProperty deuda = new SimpleDoubleProperty();

    public Deudor() {}
    public Deudor(int id, String nombre, String dni, String telefono, double deuda) {
        setIdDeudor(id); setNombre(nombre); setDni(dni); setTelefono(telefono); setDeuda(deuda);
    }
    public int getIdDeudor() { return idDeudor.get(); }
    public void setIdDeudor(int value) { idDeudor.set(value); }
    public IntegerProperty idDeudorProperty() { return idDeudor; }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String value) { nombre.set(value == null ? "" : value); }
    public StringProperty nombreProperty() { return nombre; }
    public String getDni() { return dni.get(); }
    public void setDni(String value) { dni.set(value == null ? "" : value); }
    public StringProperty dniProperty() { return dni; }
    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String value) { telefono.set(value == null ? "" : value); }
    public StringProperty telefonoProperty() { return telefono; }
    public double getDeuda() { return deuda.get(); }
    public void setDeuda(double value) { deuda.set(value); }
    public DoubleProperty deudaProperty() { return deuda; }
}
