package pe.edu.upeu.project_final_gp1.implementacion;

import pe.edu.upeu.project_final_gp1.interfaces.IFinanzasDao;
import pe.edu.upeu.project_final_gp1.modelo.Egreso;
import pe.edu.upeu.project_final_gp1.utils.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanzasDaoImpl implements IFinanzasDao {
    @Override public void registrarEgreso(Egreso e) throws SQLException {
        if (e == null || e.getDescripcion().isBlank()) throw new IllegalArgumentException("La descripción del egreso es obligatoria.");
        if (!Double.isFinite(e.getMonto()) || e.getMonto() <= 0) throw new IllegalArgumentException("El monto del egreso debe ser mayor a cero.");
        e.setMonto(Math.round(e.getMonto() * 100.0) / 100.0);
        if (e.getMonto() <= 0) throw new IllegalArgumentException("El monto debe ser al menos S/ 0.01.");
        String sql = "INSERT INTO egreso(fecha,descripcion,monto) VALUES(?,?,?)";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, LocalDate.now().toString()); st.setString(2, e.getDescripcion().trim()); st.setDouble(3, e.getMonto()); st.executeUpdate();
        }
    }

    @Override public List<Egreso> listarEgresos(LocalDate fecha) throws SQLException {
        List<Egreso> result = new ArrayList<>();
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("SELECT idegreso,fecha,descripcion,monto FROM egreso WHERE fecha=? ORDER BY idegreso DESC")) {
            st.setString(1, fecha.toString());
            try (ResultSet rs = st.executeQuery()) { while (rs.next()) result.add(new Egreso(rs.getInt("idegreso"), rs.getString("fecha"), rs.getString("descripcion"), rs.getDouble("monto"))); }
        }
        return result;
    }

    @Override public double calcularIngresos(LocalDate fecha) throws SQLException {
        String sql = "SELECT " +
                "COALESCE((SELECT SUM(monto_pagado) FROM venta WHERE fecha=?),0) + " +
                "COALESCE((SELECT SUM(monto) FROM cobro_deuda WHERE fecha=?),0)";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, fecha.toString()); st.setString(2, fecha.toString());
            try (ResultSet rs = st.executeQuery()) { return rs.next() ? rs.getDouble(1) : 0; }
        }
    }

    @Override public double calcularEgresos(LocalDate fecha) throws SQLException {
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("SELECT COALESCE(SUM(monto),0) FROM egreso WHERE fecha=?")) {
            st.setString(1, fecha.toString()); try (ResultSet rs = st.executeQuery()) { return rs.next() ? rs.getDouble(1) : 0; }
        }
    }

    @Override public double calcularDeudaPendiente() throws SQLException {
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement st = cn.prepareStatement("SELECT COALESCE(SUM(deuda),0) FROM deudor"); ResultSet rs = st.executeQuery()) { return rs.next() ? rs.getDouble(1) : 0; }
    }
}
