package pe.edu.upeu.project_final_gp1.test;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.project_final_gp1.implementacion.FinanzasDaoImpl;
import pe.edu.upeu.project_final_gp1.modelo.Egreso;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FinanzasDaoTest extends BaseDaoTest {
    private final FinanzasDaoImpl dao = new FinanzasDaoImpl();

    @Test
    void registrarEgresoYConsultarCaja() throws Exception {
        Egreso e = new Egreso();
        e.setDescripcion("Pago de luz");
        e.setMonto(35.50);
        dao.registrarEgreso(e);

        assertEquals(1, dao.listarEgresos(LocalDate.now()).size());
        assertEquals(35.50, dao.calcularEgresos(LocalDate.now()), 0.001);
        assertEquals(0.00, dao.calcularIngresos(LocalDate.now()), 0.001);
    }

    @Test
    void noGuardarEgresoSinMontoValido() {
        Egreso e = new Egreso();
        e.setDescripcion("Pago de prueba");
        e.setMonto(0.00);
        assertThrows(IllegalArgumentException.class, () -> dao.registrarEgreso(e));
    }
}
