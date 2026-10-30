# Bazar 1 — Sistema de gestión comercial

Proyecto JavaFX con Maven adaptado al sistema de bazar. Conserva los métodos Java del proyecto Bazar original, organizados con el estilo compacto del proyecto final GP1. La interfaz usa la paleta azul y amarilla del proyecto de referencia, con formularios y tablas más claros.

## Módulos

- **Inicio / resumen:** accesos y resumen de operaciones de la tienda.
- **Inventario y productos:** registro, búsqueda, actualización, eliminación y control de existencias.
- **Ventas y boletas:** carrito, validación de stock, contado/crédito, registro de la venta y comprobante interno TXT.
- **Clientes y deudas:** clientes con saldos pendientes, ajustes de deuda y abonos.
- **Caja y egresos:** ingresos efectivamente cobrados, gastos, saldo del día y deuda pendiente.

## Directorio de clientes guardado

El sistema conserva una ficha de cliente en SQLite con DNI/identificador, nombre y teléfono. En el módulo **Ventas y boletas**:

1. Escribe el nombre, sus primeras letras/iniciales o el DNI.
2. Aparecerán coincidencias registradas; selecciona el cliente y se completarán los campos de nombre, DNI y teléfono.
3. Puedes usar **Guardar ficha** para registrar un cliente sin efectuar una venta inmediata.
4. Si registras una venta con un cliente nuevo, su ficha se guarda automáticamente dentro de la misma transacción que la venta.
5. Si el cliente ya existe, su ficha se actualiza por DNI. Si el teléfono se deja vacío, se conserva el teléfono que ya estaba guardado.

Al inicializar la base actual se importan automáticamente clientes que aparezcan en el historial de ventas y en la tabla de deudores, siempre que tengan DNI/identificador. Los registros persisten entre ejecuciones del sistema.

## Base de datos SQLite

- Archivo de DB Browser for SQLite: `data/bazar1.db`
- Script SQL: `database/schema.sql`
- Tablas: `producto`, `cliente`, `venta`, `detalle_venta`, `deudor`, `egreso` y `cobro_deuda`.
- No se incluyen tablas ni pantallas hoteleras como `habitacion` o `reserva`.

Al ejecutar la aplicación desde la carpeta raíz del proyecto, se usa `data/bazar1.db`. Puedes configurar otra ruta con `-Dapp.db.path="ruta/al/archivo.db"`.

## Abrir en IntelliJ IDEA

1. Descomprime el ZIP.
2. Abre la carpeta del proyecto que contiene `pom.xml` en IntelliJ IDEA.
3. Configura JDK 21 y espera a que Maven cargue las dependencias.
4. Ejecuta `pe.edu.upeu.project_final_gp1.HelloApplication`, o usa en la terminal de Windows `mvnw.cmd clean javafx:run`.
5. Para ejecutar los tests automatizados, usa `mvnw.cmd test`.

## Abrir en DB Browser for SQLite

1. Abre DB Browser for SQLite y pulsa **Open Database**.
2. Selecciona `data/bazar1.db`.
3. En **Database Structure** encontrarás las siete tablas del sistema de bazar.
4. En **Browse Data**, elige la tabla `cliente` para consultar las fichas guardadas.

**Nota:** la boleta generada es un comprobante interno en texto plano y no una boleta electrónica autorizada por SUNAT. La compilación y ejecución final debe confirmarse en IntelliJ con las dependencias Maven disponibles.
