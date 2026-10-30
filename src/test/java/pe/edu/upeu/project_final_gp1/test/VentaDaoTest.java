package pe.edu.upeu.project_final_gp1.test;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.project_final_gp1.implementacion.DeudorDaoImpl;
import pe.edu.upeu.project_final_gp1.implementacion.ProductoDaoImpl;
import pe.edu.upeu.project_final_gp1.implementacion.VentaDaoImpl;
import pe.edu.upeu.project_final_gp1.modelo.DetalleVenta;
import pe.edu.upeu.project_final_gp1.modelo.Producto;
import pe.edu.upeu.project_final_gp1.modelo.Venta;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VentaDaoTest extends BaseDaoTest {
    private final ProductoDaoImpl productos = new ProductoDaoImpl();
    private final VentaDaoImpl ventas = new VentaDaoImpl();

    @Test
    void registrarVentaActualizaStockYDeuda() throws Exception {
        productos.insert(new Producto("P001", "Arroz", 10.00, 5));
        Venta v = new Venta();
        v.setFecha(LocalDate.now().toString());
        v.setNombreCliente("Ana Perez");
        v.setDniCliente("12345678");
        v.setTelefonoCliente("900111222");
        v.setMontoPagado(5.00);
        v.agregarDetalle(new DetalleVenta("P001", "Arroz", 2, 10.00));

        Venta guardada = ventas.registrarVenta(v);
        assertTrue(guardada.getIdVenta() > 0);
        assertEquals(20.00, guardada.getTotal(), 0.001);
        assertEquals(5.00, guardada.getMontoPagado(), 0.001);
        assertEquals(15.00, guardada.getSaldoPendiente(), 0.001);
        assertEquals(3, productos.searchById("P001").getStock());
        assertEquals(15.00, new DeudorDaoImpl().buscar("12345678").get(0).getDeuda(), 0.001);
        assertEquals(1, ventas.listAll().size());
    }

    @Test
    void revertirVentaSiNoHayStockSuficiente() throws Exception {
        productos.insert(new Producto("P001", "Arroz", 10.00, 1));
        Venta v = new Venta();
        v.setNombreCliente("Ana Perez");
        v.setDniCliente("12345678");
        v.setMontoPagado(0.00);
        v.agregarDetalle(new DetalleVenta("P001", "Arroz", 2, 10.00));

        assertThrows(java.sql.SQLException.class, () -> ventas.registrarVenta(v));
        assertEquals(1, productos.searchById("P001").getStock());
        assertTrue(ventas.listAll().isEmpty());
    }
}
