package pe.edu.upeu.project_final_gp1.implementacion;

import pe.edu.upeu.project_final_gp1.interfaces.IProductoDao;
import pe.edu.upeu.project_final_gp1.modelo.Producto;
import pe.edu.upeu.project_final_gp1.utils.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDaoImpl implements IProductoDao {
    @Override public boolean insert(Producto p) throws SQLException {
        String sql = "INSERT INTO producto(idproducto,nombre,precio,stock) VALUES(?,?,?,?)";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, p.getId().trim()); st.setString(2, p.getNombre().trim());
            st.setDouble(3, p.getPrecio()); st.setInt(4, p.getStock());
            return st.executeUpdate() > 0;
        }
    }

    @Override public boolean update(Producto p) throws SQLException {
        String sql = "UPDATE producto SET nombre=?,precio=?,stock=? WHERE idproducto=? COLLATE NOCASE";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, p.getNombre().trim()); st.setDouble(2, p.getPrecio());
            st.setInt(3, p.getStock()); st.setString(4, p.getId().trim());
            return st.executeUpdate() > 0;
        }
    }

    @Override public boolean delete(String id) throws SQLException {
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("DELETE FROM producto WHERE idproducto=? COLLATE NOCASE")) {
            st.setString(1, id.trim()); return st.executeUpdate() > 0;
        }
    }

    @Override public Producto searchById(String id) throws SQLException {
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("SELECT idproducto,nombre,precio,stock FROM producto WHERE idproducto=? COLLATE NOCASE")) {
            st.setString(1, id.trim());
            try (ResultSet rs = st.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    @Override public List<Producto> listAll() throws SQLException {
        List<Producto> result = new ArrayList<>();
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("SELECT idproducto,nombre,precio,stock FROM producto ORDER BY nombre COLLATE NOCASE"); ResultSet rs = st.executeQuery()) {
            while (rs.next()) result.add(map(rs));
        }
        return result;
    }

    @Override public List<Producto> searchByNombre(String texto) throws SQLException {
        List<Producto> result = new ArrayList<>();
        String q = texto == null ? "" : texto.trim();
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("SELECT idproducto,nombre,precio,stock FROM producto WHERE idproducto LIKE ? COLLATE NOCASE OR nombre LIKE ? COLLATE NOCASE ORDER BY nombre COLLATE NOCASE")) {
            st.setString(1, "%" + q + "%"); st.setString(2, "%" + q + "%");
            try (ResultSet rs = st.executeQuery()) { while (rs.next()) result.add(map(rs)); }
        }
        return result;
    }

    private Producto map(ResultSet rs) throws SQLException {
        return new Producto(rs.getString("idproducto"), rs.getString("nombre"), rs.getDouble("precio"), rs.getInt("stock"));
    }
}
