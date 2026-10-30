package pe.edu.upeu.project_final_gp1.modelo;

public class Cliente {
    private final String dni;
    private final String nombre;
    private final String telefono;

    public Cliente(String nombre, String dni, String telefono) {
        this.nombre = nombre == null ? "" : nombre.trim();
        this.dni = dni == null ? "" : dni.trim();
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    public String getNombre() { return nombre; }
    public String getDni() { return dni; }
    public String getTelefono() { return telefono; }

    @Override public String toString() {
        return nombre + (dni.isBlank() ? "" : " · " + dni);
    }
}
