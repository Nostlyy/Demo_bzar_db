package pe.edu.upeu.project_final_gp1.implementacion;

import pe.edu.upeu.project_final_gp1.modelo.Cliente;
import pe.edu.upeu.project_final_gp1.utils.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDaoImpl {
    public List<Cliente> buscar(String termino) throws SQLException {
        String filtro = termino == null ? "" : termino.trim();
        List<Cliente> clientes = new ArrayList<>();
        if (filtro.isBlank()) return clientes;

        String sql = "SELECT nombre,dni,telefono FROM cliente " +
                "WHERE nombre LIKE ? COLLATE NOCASE OR nombre LIKE ? COLLATE NOCASE " +
                "OR dni LIKE ? COLLATE NOCASE OR telefono LIKE ? COLLATE NOCASE " +
                "ORDER BY nombre COLLATE NOCASE LIMIT 10";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, filtro + "%");
            st.setString(2, "% " + filtro + "%");
            st.setString(3, filtro + "%");
            st.setString(4, filtro + "%");
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) clientes.add(new Cliente(rs.getString("nombre"), rs.getString("dni"), rs.getString("telefono")));
            }
        }
        return clientes;
    }

    public void guardarOActualizar(Cliente cliente) throws SQLException {
        validar(cliente);
        try (Connection cn = ConexionBD.getConnection()) {
            guardarOActualizar(cn, cliente);
        }
    }

    public void guardarOActualizar(Connection cn, Cliente cliente) throws SQLException {
        validar(cliente);
        String sql = "INSERT INTO cliente(dni,nombre,telefono) VALUES(?,?,?) " +
                "ON CONFLICT(dni) DO UPDATE SET nombre=excluded.nombre, " +
                "telefono=CASE WHEN trim(excluded.telefono)='' THEN cliente.telefono ELSE excluded.telefono END, " +
                "fecha_actualizacion=CURRENT_TIMESTAMP";
        try (PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, cliente.getDni());
            st.setString(2, cliente.getNombre());
            st.setString(3, cliente.getTelefono());
            st.executeUpdate();
        }
    }

    private void validar(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("La ficha del cliente es obligatoria.");
        if (cliente.getNombre().isBlank()) throw new IllegalArgumentException("Ingresa el nombre del cliente.");
        if (cliente.getDni().isBlank()) throw new IllegalArgumentException("Ingresa el DNI o identificador del cliente.");
    }
}
