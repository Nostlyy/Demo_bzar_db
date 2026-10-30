package pe.edu.upeu.project_final_gp1.implementacion;

import pe.edu.upeu.project_final_gp1.interfaces.IDeudorDao;
import pe.edu.upeu.project_final_gp1.modelo.Cliente;
import pe.edu.upeu.project_final_gp1.modelo.Deudor;
import pe.edu.upeu.project_final_gp1.utils.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeudorDaoImpl implements IDeudorDao {
    @Override public void registrar(Deudor d) throws SQLException {
        validar(d);
        d.setDeuda(redondear(d.getDeuda()));
        if (d.getDeuda() <= 0) throw new IllegalArgumentException("La deuda inicial debe ser mayor a S/ 0.00.");
        String sql = "INSERT INTO deudor(nombre,dni,telefono,deuda) VALUES(?,?,?,?)";
        try (Connection cn = ConexionBD.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement st = cn.prepareStatement(sql)) {
                st.setString(1, d.getNombre().trim()); st.setString(2, d.getDni().trim());
                st.setString(3, d.getTelefono().trim()); st.setDouble(4, d.getDeuda()); st.executeUpdate();
                new ClienteDaoImpl().guardarOActualizar(cn, new Cliente(d.getNombre(), d.getDni(), d.getTelefono()));
                cn.commit();
            } catch (Exception e) {
                try { cn.rollback(); } catch (SQLException ignored) {}
                if (e instanceof SQLException se) throw se;
                if (e instanceof RuntimeException re) throw re;
                throw new SQLException("No se pudo guardar el deudor.", e);
            } finally { try { cn.setAutoCommit(true); } catch (SQLException ignored) {} }
        }
    }

    @Override public void actualizar(Deudor d) throws SQLException {
        if (d.getNombre().isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (!Double.isFinite(d.getDeuda()) || d.getDeuda() < 0) throw new IllegalArgumentException("La deuda no puede ser negativa.");
        d.setDeuda(redondear(d.getDeuda()));
        if (d.getDeuda() <= 0.005) {
            guardarFichaCliente(d);
            eliminar(d.getIdDeudor());
            return;
        }
        String sql = "UPDATE deudor SET nombre=?,telefono=?,deuda=? WHERE iddeudor=?";
        try (Connection cn = ConexionBD.getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement st = cn.prepareStatement(sql)) {
                st.setString(1, d.getNombre().trim()); st.setString(2, d.getTelefono().trim());
                st.setDouble(3, d.getDeuda()); st.setInt(4, d.getIdDeudor());
                if (st.executeUpdate() == 0) throw new SQLException("El deudor ya no existe.");
                new ClienteDaoImpl().guardarOActualizar(cn, new Cliente(d.getNombre(), d.getDni(), d.getTelefono()));
                cn.commit();
            } catch (Exception e) {
                try { cn.rollback(); } catch (SQLException ignored) {}
                if (e instanceof SQLException se) throw se;
                if (e instanceof RuntimeException re) throw re;
                throw new SQLException("No se pudo actualizar el deudor.", e);
            } finally { try { cn.setAutoCommit(true); } catch (SQLException ignored) {} }
        }
    }

    @Override public void aumentarDeuda(int id, double monto) throws SQLException {
        if (!Double.isFinite(monto) || monto <= 0) throw new IllegalArgumentException("El monto que se agregará a la deuda debe ser mayor a cero.");
        monto = redondear(monto);
        if (monto <= 0) throw new IllegalArgumentException("El monto de la deuda debe ser al menos S/ 0.01.");
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("UPDATE deudor SET deuda=deuda+? WHERE iddeudor=?")) {
            st.setDouble(1, monto); st.setInt(2, id);
            if (st.executeUpdate() == 0) throw new SQLException("No se encontró el deudor seleccionado.");
        }
    }

    @Override public void registrarPago(int id, double monto) throws SQLException {
        if (!Double.isFinite(monto) || monto <= 0) throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        monto = redondear(monto);
        if (monto <= 0) throw new IllegalArgumentException("El pago debe ser al menos S/ 0.01.");
        try (Connection cn = ConexionBD.getConnection()) {
            cn.setAutoCommit(false);
            try {
                String nombre; String dni; double deuda;
                try (PreparedStatement q = cn.prepareStatement("SELECT nombre,dni,deuda FROM deudor WHERE iddeudor=?")) {
                    q.setInt(1, id);
                    try (ResultSet rs = q.executeQuery()) {
                        if (!rs.next()) throw new SQLException("No se encontró el deudor seleccionado.");
                        nombre = rs.getString("nombre"); dni = rs.getString("dni"); deuda = rs.getDouble("deuda");
                    }
                }
                if (monto - deuda > 0.005) throw new IllegalArgumentException("El pago no puede superar la deuda actual (S/ " + String.format(java.util.Locale.US, "%.2f", deuda) + ").");
                double nuevaDeuda = Math.round((deuda - monto) * 100.0) / 100.0;
                try (PreparedStatement insert = cn.prepareStatement("INSERT INTO cobro_deuda(nombre_cliente,dni_cliente,fecha,monto) VALUES(?,?,?,?)")) {
                    insert.setString(1, nombre); insert.setString(2, dni); insert.setString(3, LocalDate.now().toString()); insert.setDouble(4, monto); insert.executeUpdate();
                }
                if (nuevaDeuda <= 0.005) {
                    try (PreparedStatement del = cn.prepareStatement("DELETE FROM deudor WHERE iddeudor=?")) { del.setInt(1, id); del.executeUpdate(); }
                } else {
                    try (PreparedStatement up = cn.prepareStatement("UPDATE deudor SET deuda=? WHERE iddeudor=?")) { up.setDouble(1, nuevaDeuda); up.setInt(2, id); up.executeUpdate(); }
                }
                cn.commit();
            } catch (Exception e) {
                try { cn.rollback(); } catch (SQLException ignored) {}
                if (e instanceof SQLException se) throw se;
                if (e instanceof RuntimeException re) throw re;
                throw new SQLException("No se pudo registrar el pago.", e);
            } finally { try { cn.setAutoCommit(true); } catch (SQLException ignored) {} }
        }
    }

    @Override public boolean eliminar(int id) throws SQLException {
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("DELETE FROM deudor WHERE iddeudor=?")) {
            st.setInt(1, id); return st.executeUpdate() > 0;
        }
    }

    @Override public List<Deudor> listAll() throws SQLException { return consultar(""); }
    @Override public List<Deudor> buscar(String texto) throws SQLException { return consultar(texto == null ? "" : texto.trim()); }

    private List<Deudor> consultar(String texto) throws SQLException {
        List<Deudor> result = new ArrayList<>();
        String sql = "SELECT iddeudor,nombre,dni,telefono,deuda FROM deudor WHERE nombre LIKE ? COLLATE NOCASE OR dni LIKE ? COLLATE NOCASE ORDER BY nombre COLLATE NOCASE";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, "%" + texto + "%"); st.setString(2, "%" + texto + "%");
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) result.add(new Deudor(rs.getInt("iddeudor"), rs.getString("nombre"), rs.getString("dni"), rs.getString("telefono"), rs.getDouble("deuda")));
            }
        }
        return result;
    }

    private void guardarFichaCliente(Deudor d) throws SQLException {
        try (Connection cn = ConexionBD.getConnection()) {
            new ClienteDaoImpl().guardarOActualizar(cn, new Cliente(d.getNombre(), d.getDni(), d.getTelefono()));
        }
    }

    private static double redondear(double value) { return Math.round(value * 100.0) / 100.0; }

    private void validar(Deudor d) {
        if (d == null || d.getNombre().isBlank()) throw new IllegalArgumentException("El nombre del deudor es obligatorio.");
        if (d.getDni().isBlank()) throw new IllegalArgumentException("El DNI del deudor es obligatorio.");
        if (d.getDeuda() < 0 || !Double.isFinite(d.getDeuda())) throw new IllegalArgumentException("La deuda no puede ser negativa.");
    }
}
