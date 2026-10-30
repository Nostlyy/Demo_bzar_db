package pe.edu.upeu.project_final_gp1.test;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.project_final_gp1.implementacion.ClienteDaoImpl;
import pe.edu.upeu.project_final_gp1.modelo.Cliente;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDaoTest extends BaseDaoTest {
    private final ClienteDaoImpl dao = new ClienteDaoImpl();

    @Test
    void guardarBuscarYActualizarFicha() throws Exception {
        dao.guardarOActualizar(new Cliente("Maria Lopez", "12345678", "900111222"));

        List<Cliente> lista = dao.buscar("Maria");
        assertEquals(1, lista.size());
        assertEquals("12345678", lista.get(0).getDni());
        assertEquals("900111222", lista.get(0).getTelefono());

        dao.guardarOActualizar(new Cliente("Maria Lopez Diaz", "12345678", "900333444"));
        lista = dao.buscar("12345678");
        assertEquals(1, lista.size());
        assertEquals("Maria Lopez Diaz", lista.get(0).getNombre());
        assertEquals("900333444", lista.get(0).getTelefono());
    }
}
