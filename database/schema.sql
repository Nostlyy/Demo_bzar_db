-- Esquema de Bazar 1. Este proyecto no utiliza tablas de hotel ni de reservas.
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS producto (
    idproducto TEXT PRIMARY KEY COLLATE NOCASE,
    nombre TEXT NOT NULL,
    precio REAL NOT NULL CHECK(precio >= 0),
    stock INTEGER NOT NULL CHECK(stock >= 0)
);

-- Directorio permanente de clientes del bazar. No corresponde a un módulo hotelero.
CREATE TABLE IF NOT EXISTS cliente (
    dni TEXT PRIMARY KEY COLLATE NOCASE,
    nombre TEXT NOT NULL,
    telefono TEXT NOT NULL DEFAULT '',
    fecha_registro TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS venta (
    idventa INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT NOT NULL,
    cliente_nombre TEXT NOT NULL,
    cliente_dni TEXT NOT NULL,
    cliente_telefono TEXT NOT NULL DEFAULT '',
    total REAL NOT NULL CHECK(total >= 0),
    monto_pagado REAL NOT NULL CHECK(monto_pagado >= 0),
    saldo_pendiente REAL NOT NULL CHECK(saldo_pendiente >= 0),
    tipo_pago TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS detalle_venta (
    iddetalle INTEGER PRIMARY KEY AUTOINCREMENT,
    idventa INTEGER NOT NULL,
    idproducto TEXT NOT NULL,
    nombre_producto TEXT NOT NULL,
    cantidad INTEGER NOT NULL CHECK(cantidad > 0),
    precio_unitario REAL NOT NULL CHECK(precio_unitario >= 0),
    subtotal REAL NOT NULL CHECK(subtotal >= 0),
    FOREIGN KEY(idventa) REFERENCES venta(idventa) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS deudor (
    iddeudor INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    dni TEXT NOT NULL UNIQUE COLLATE NOCASE,
    telefono TEXT NOT NULL DEFAULT '',
    deuda REAL NOT NULL CHECK(deuda >= 0)
);

CREATE TABLE IF NOT EXISTS egreso (
    idegreso INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    monto REAL NOT NULL CHECK(monto > 0)
);

CREATE TABLE IF NOT EXISTS cobro_deuda (
    idcobro INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_cliente TEXT NOT NULL,
    dni_cliente TEXT NOT NULL,
    fecha TEXT NOT NULL,
    monto REAL NOT NULL CHECK(monto > 0)
);

CREATE INDEX IF NOT EXISTS idx_cliente_nombre ON cliente(nombre COLLATE NOCASE);
CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha);
CREATE INDEX IF NOT EXISTS idx_egreso_fecha ON egreso(fecha);
CREATE INDEX IF NOT EXISTS idx_deudor_nombre ON deudor(nombre);
CREATE INDEX IF NOT EXISTS idx_detalle_venta_idventa ON detalle_venta(idventa);

-- Conserva el historial de clientes existentes cuando se aplica este esquema a una base previa.
INSERT OR IGNORE INTO cliente(dni,nombre,telefono)
SELECT cliente_dni, MAX(cliente_nombre), MAX(cliente_telefono)
FROM venta WHERE trim(cliente_dni) <> '' GROUP BY cliente_dni;

INSERT OR IGNORE INTO cliente(dni,nombre,telefono)
SELECT dni,nombre,telefono FROM deudor WHERE trim(dni) <> '';
