package pe.edu.upeu.project_final_gp1.test;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.project_final_gp1.implementacion.DeudorDaoImpl;
import pe.edu.upeu.project_final_gp1.modelo.Deudor;

import static org.junit.jupiter.api.Assertions.*;

class DeudorDaoTest extends BaseDaoTest {
    private final DeudorDaoImpl dao = new DeudorDaoImpl();

    @Test
    void registrarAumentarYRecibirPago() throws Exception {
        dao.registrar(new Deudor(0, "Carlos Ruiz", "D001", "900111222", 50.00));
        Deudor d = dao.buscar("D001").get(0);
        assertEquals(50.00, d.getDeuda(), 0.001);

        dao.aumentarDeuda(d.getIdDeudor(), 10.00);
        assertEquals(60.00, dao.buscar("D001").get(0).getDeuda(), 0.001);

        dao.registrarPago(d.getIdDeudor(), 20.00);
        assertEquals(40.00, dao.buscar("D001").get(0).getDeuda(), 0.001);
    }

    @Test
    void rechazarPagoMayorALaDeuda() throws Exception {
        dao.registrar(new Deudor(0, "Carlos Ruiz", "D001", "900111222", 20.00));
        int id = dao.buscar("D001").get(0).getIdDeudor();
        assertThrows(IllegalArgumentException.class, () -> dao.registrarPago(id, 30.00));
        assertEquals(20.00, dao.buscar("D001").get(0).getDeuda(), 0.001);
    }
}
