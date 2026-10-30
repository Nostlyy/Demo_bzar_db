package pe.edu.upeu.project_final_gp1.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Producto {
    private final StringProperty id = new SimpleStringProperty("");
    private final StringProperty nombre = new SimpleStringProperty("");
    private final DoubleProperty precio = new SimpleDoubleProperty();
    private final IntegerProperty stock = new SimpleIntegerProperty();

    public Producto() {}
    public Producto(String id, String nombre, double precio, int stock) {
        setId(id); setNombre(nombre); setPrecio(precio); setStock(stock);
    }
    public String getId() { return id.get(); }
    public void setId(String value) { id.set(value == null ? "" : value); }
    public StringProperty idProperty() { return id; }
    public String getNombre() { return nombre.get(); }
    public void setNombre(String value) { nombre.set(value == null ? "" : value); }
    public StringProperty nombreProperty() { return nombre; }
    public double getPrecio() { return precio.get(); }
    public void setPrecio(double value) { precio.set(value); }
    public DoubleProperty precioProperty() { return precio; }
    public int getStock() { return stock.get(); }
    public void setStock(int value) { stock.set(value); }
    public IntegerProperty stockProperty() { return stock; }
    @Override public String toString() {
        return getId() + " - " + getNombre() + " | Stock: " + getStock() + " | S/ " + String.format(java.util.Locale.US, "%.2f", getPrecio());
    }
}
