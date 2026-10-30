package pe.edu.upeu.project_final_gp1.test;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.project_final_gp1.implementacion.ProductoDaoImpl;
import pe.edu.upeu.project_final_gp1.modelo.Producto;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDaoTest extends BaseDaoTest {
    private final ProductoDaoImpl dao = new ProductoDaoImpl();

    @Test
    void insertarBuscarActualizarYEliminar() throws Exception {
        Producto p = new Producto("P001", "Arroz", 4.50, 20);
        assertTrue(dao.insert(p));

        Producto encontrado = dao.searchById("p001");
        assertNotNull(encontrado);
        assertEquals("Arroz", encontrado.getNombre());
        assertEquals(20, encontrado.getStock());

        p.setNombre("Arroz extra");
        p.setPrecio(5.00);
        p.setStock(18);
        assertTrue(dao.update(p));
        assertEquals("Arroz extra", dao.searchById("P001").getNombre());
        assertEquals(1, dao.searchByNombre("extra").size());

        assertTrue(dao.delete("P001"));
        assertNull(dao.searchById("P001"));
    }

    @Test
    void noPermitirCodigoDuplicado() throws Exception {
        dao.insert(new Producto("P001", "Arroz", 4.50, 20));
        assertThrows(SQLException.class,
                () -> dao.insert(new Producto("P001", "Azucar", 5.00, 10)));
    }
}
