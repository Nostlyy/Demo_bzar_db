package pe.edu.upeu.project_final_gp1.test;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.project_final_gp1.modelo.DetalleVenta;
import pe.edu.upeu.project_final_gp1.modelo.Producto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModeloTest {
    @Test
    void detalleActualizaElSubtotalAlCambiarCantidad() {
        DetalleVenta detalle = new DetalleVenta("P001", "Arroz", 2, 4.50);
        assertEquals(9.00, detalle.getSubtotal(), 0.001);

        detalle.setCantidad(3);
        assertEquals(13.50, detalle.getSubtotal(), 0.001);

        detalle.setPrecioUnitario(5.00);
        assertEquals(15.00, detalle.getSubtotal(), 0.001);
    }

    @Test
    void productoAceptaDatosDeInventario() {
        Producto p = new Producto("P002", "Azucar", 4.25, 12);
        assertEquals("P002", p.getId());
        assertEquals("Azucar", p.getNombre());
        assertEquals(4.25, p.getPrecio(), 0.001);
        assertEquals(12, p.getStock());
    }
}
