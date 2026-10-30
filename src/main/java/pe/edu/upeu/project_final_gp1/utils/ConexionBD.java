package pe.edu.upeu.project_final_gp1.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public final class ConexionBD {
    private static Path databasePath;
    private static Path schemaInitializedPath;

    private ConexionBD() {}

    public static synchronized Connection getConnection() {
        try {
            Path path = resolveDatabasePath();
            Path parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            try {
                try (Statement st = connection.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
                if (!path.equals(schemaInitializedPath)) {
                    initializeSchema(connection);
                    schemaInitializedPath = path;
                    databasePath = path;
                    System.out.println("Base de datos Bazar 1: " + path.toAbsolutePath());
                }
                return connection;
            } catch (Exception e) {
                try { connection.close(); } catch (SQLException ignored) {}
                throw e;
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo abrir o inicializar la base de datos de Bazar 1. " + e.getMessage(), e);
        }
    }

    public static synchronized Path getDatabasePath() {
        if (databasePath != null) return databasePath;
        return resolveDatabasePath();
    }

    private static Path resolveDatabasePath() {
        String configured = System.getProperty("app.db.path");
        if (configured != null && !configured.isBlank()) return Path.of(configured).toAbsolutePath().normalize();
        return Path.of(System.getProperty("user.dir"), "data", "bazar1.db").toAbsolutePath().normalize();
    }

    private static void initializeSchema(Connection cn) throws SQLException {
        try (Statement st = cn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS producto (idproducto TEXT PRIMARY KEY COLLATE NOCASE, nombre TEXT NOT NULL, precio REAL NOT NULL CHECK(precio >= 0), stock INTEGER NOT NULL CHECK(stock >= 0))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS cliente (dni TEXT PRIMARY KEY COLLATE NOCASE, nombre TEXT NOT NULL, telefono TEXT NOT NULL DEFAULT '', fecha_registro TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, fecha_actualizacion TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS venta (idventa INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT NOT NULL, cliente_nombre TEXT NOT NULL, cliente_dni TEXT NOT NULL, cliente_telefono TEXT NOT NULL DEFAULT '', total REAL NOT NULL CHECK(total >= 0), monto_pagado REAL NOT NULL CHECK(monto_pagado >= 0), saldo_pendiente REAL NOT NULL CHECK(saldo_pendiente >= 0), tipo_pago TEXT NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS detalle_venta (iddetalle INTEGER PRIMARY KEY AUTOINCREMENT, idventa INTEGER NOT NULL, idproducto TEXT NOT NULL, nombre_producto TEXT NOT NULL, cantidad INTEGER NOT NULL CHECK(cantidad > 0), precio_unitario REAL NOT NULL CHECK(precio_unitario >= 0), subtotal REAL NOT NULL CHECK(subtotal >= 0), FOREIGN KEY(idventa) REFERENCES venta(idventa) ON DELETE CASCADE)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS deudor (iddeudor INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, dni TEXT NOT NULL UNIQUE COLLATE NOCASE, telefono TEXT NOT NULL DEFAULT '', deuda REAL NOT NULL CHECK(deuda >= 0))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS egreso (idegreso INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT NOT NULL, descripcion TEXT NOT NULL, monto REAL NOT NULL CHECK(monto > 0))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS cobro_deuda (idcobro INTEGER PRIMARY KEY AUTOINCREMENT, nombre_cliente TEXT NOT NULL, dni_cliente TEXT NOT NULL, fecha TEXT NOT NULL, monto REAL NOT NULL CHECK(monto > 0))");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_cliente_nombre ON cliente(nombre COLLATE NOCASE)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_egreso_fecha ON egreso(fecha)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_deudor_nombre ON deudor(nombre)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_detalle_venta_idventa ON detalle_venta(idventa)");
            // Importa al directorio los clientes que ya estaban registrados en ventas o deudas.
            st.executeUpdate("INSERT OR IGNORE INTO cliente(dni,nombre,telefono) SELECT cliente_dni, MAX(cliente_nombre), MAX(cliente_telefono) FROM venta WHERE trim(cliente_dni) <> '' GROUP BY cliente_dni");
            st.executeUpdate("INSERT OR IGNORE INTO cliente(dni,nombre,telefono) SELECT dni,nombre,telefono FROM deudor WHERE trim(dni) <> ''");
        }
    }
}
