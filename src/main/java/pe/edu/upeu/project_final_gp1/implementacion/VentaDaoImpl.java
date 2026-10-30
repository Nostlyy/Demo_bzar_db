package pe.edu.upeu.project_final_gp1.implementacion;

import pe.edu.upeu.project_final_gp1.interfaces.IVentaDao;
import pe.edu.upeu.project_final_gp1.modelo.Cliente;
import pe.edu.upeu.project_final_gp1.modelo.DetalleVenta;
import pe.edu.upeu.project_final_gp1.modelo.Venta;
import pe.edu.upeu.project_final_gp1.utils.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDaoImpl implements IVentaDao {
    @Override public Venta registrarVenta(Venta venta) throws SQLException {
        if (venta == null || venta.getDetalles().isEmpty()) throw new IllegalArgumentException("Agrega al menos un producto a la venta.");
        if (venta.getNombreCliente().trim().isEmpty() || venta.getDniCliente().trim().isEmpty()) throw new IllegalArgumentException("El nombre y el DNI del cliente son obligatorios.");

        if (!Double.isFinite(venta.getMontoPagado())) throw new IllegalArgumentException("El pago inicial debe ser un importe válido.");
        double total = 0;
        for (DetalleVenta d : venta.getDetalles()) {
            if (d.getCantidad() <= 0 || !Double.isFinite(d.getPrecioUnitario()) || d.getPrecioUnitario() < 0) throw new IllegalArgumentException("La cantidad y el precio deben ser válidos.");
            total += d.getCantidad() * d.getPrecioUnitario();
        }
        if (!Double.isFinite(total)) throw new IllegalArgumentException("El total de la venta excede el rango permitido.");
        total = redondear(total);
        double pagado = redondear(venta.getMontoPagado());
        if (pagado < 0 || pagado - total > 0.005) throw new IllegalArgumentException("El pago inicial no puede ser negativo ni superar el total.");
        double saldo = redondear(Math.max(0, total - pagado));
        venta.setTotal(total); venta.setMontoPagado(pagado); venta.setSaldoPendiente(saldo);
        venta.setTipoPago(saldo > 0.005 ? "Crédito" : "Contado");
        if (venta.getFecha() == null || venta.getFecha().isBlank()) venta.setFecha(LocalDate.now().toString());

        try (Connection cn = ConexionBD.getConnection()) {
            cn.setAutoCommit(false);
            try {
                // Mantiene un directorio único de clientes reutilizable en ventas futuras.
                new ClienteDaoImpl().guardarOActualizar(cn,
                        new Cliente(venta.getNombreCliente(), venta.getDniCliente(), venta.getTelefonoCliente()));

                String sqlVenta = "INSERT INTO venta(fecha,cliente_nombre,cliente_dni,cliente_telefono,total,monto_pagado,saldo_pendiente,tipo_pago) VALUES(?,?,?,?,?,?,?,?)";
                try (PreparedStatement st = cn.prepareStatement(sqlVenta)) {
                    st.setString(1, venta.getFecha()); st.setString(2, venta.getNombreCliente().trim());
                    st.setString(3, venta.getDniCliente().trim()); st.setString(4, venta.getTelefonoCliente().trim());
                    st.setDouble(5, total); st.setDouble(6, pagado); st.setDouble(7, saldo); st.setString(8, venta.getTipoPago());
                    st.executeUpdate();
                }
                try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) venta.setIdVenta(rs.getInt(1));
                }

                String sqlStock = "UPDATE producto SET stock=stock-? WHERE idproducto=? COLLATE NOCASE AND stock>=?";
                String sqlDetalle = "INSERT INTO detalle_venta(idventa,idproducto,nombre_producto,cantidad,precio_unitario,subtotal) VALUES(?,?,?,?,?,?)";
                try (PreparedStatement stock = cn.prepareStatement(sqlStock); PreparedStatement detalle = cn.prepareStatement(sqlDetalle)) {
                    for (DetalleVenta d : venta.getDetalles()) {
                        stock.setInt(1, d.getCantidad()); stock.setString(2, d.getIdProducto()); stock.setInt(3, d.getCantidad());
                        if (stock.executeUpdate() == 0) throw new SQLException("Stock insuficiente o producto inexistente: " + d.getNombreProducto());
                        detalle.setInt(1, venta.getIdVenta()); detalle.setString(2, d.getIdProducto());
                        detalle.setString(3, d.getNombreProducto()); detalle.setInt(4, d.getCantidad());
                        detalle.setDouble(5, d.getPrecioUnitario()); detalle.setDouble(6, redondear(d.getCantidad() * d.getPrecioUnitario()));
                        detalle.executeUpdate();
                    }
                }

                if (saldo > 0.005) {
                    String upsert = "INSERT INTO deudor(nombre,dni,telefono,deuda) VALUES(?,?,?,?) " +
                            "ON CONFLICT(dni) DO UPDATE SET nombre=excluded.nombre, telefono=excluded.telefono, deuda=deudor.deuda+excluded.deuda";
                    try (PreparedStatement st = cn.prepareStatement(upsert)) {
                        st.setString(1, venta.getNombreCliente().trim()); st.setString(2, venta.getDniCliente().trim());
                        st.setString(3, venta.getTelefonoCliente().trim()); st.setDouble(4, saldo); st.executeUpdate();
                    }
                }
                cn.commit();
                return venta;
            } catch (Exception e) {
                try { cn.rollback(); } catch (SQLException ignored) {}
                if (e instanceof SQLException se) throw se;
                if (e instanceof RuntimeException re) throw re;
                throw new SQLException("No se pudo registrar la venta.", e);
            } finally {
                try { cn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }
    }

    @Override public List<Venta> listAll() throws SQLException {
        List<Venta> result = new ArrayList<>();
        String sql = "SELECT idventa,fecha,cliente_nombre,cliente_dni,cliente_telefono,total,monto_pagado,saldo_pendiente,tipo_pago FROM venta ORDER BY idventa DESC";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Venta v = new Venta(); v.setIdVenta(rs.getInt("idventa")); v.setFecha(rs.getString("fecha"));
                v.setNombreCliente(rs.getString("cliente_nombre")); v.setDniCliente(rs.getString("cliente_dni"));
                v.setTelefonoCliente(rs.getString("cliente_telefono")); v.setTotal(rs.getDouble("total"));
                v.setMontoPagado(rs.getDouble("monto_pagado")); v.setSaldoPendiente(rs.getDouble("saldo_pendiente"));
                v.setTipoPago(rs.getString("tipo_pago")); result.add(v);
            }
        }
        return result;
    }

    private static double redondear(double value) { return Math.round(value * 100.0) / 100.0; }
}
