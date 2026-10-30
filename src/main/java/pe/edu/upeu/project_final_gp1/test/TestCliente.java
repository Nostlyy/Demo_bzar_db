package pe.edu.upeu.project_final_gp1.test;

import pe.edu.upeu.project_final_gp1.implementacion.ClienteDaoImpl;
import pe.edu.upeu.project_final_gp1.modelo.Cliente;

import java.sql.SQLException;
import java.util.List;

public class TestCliente {
    static ClienteDaoImpl dao = new ClienteDaoImpl();

    public void guardar() throws SQLException {
        Cliente c = new Cliente("Cliente de prueba", "99999998", "900000001");
        dao.guardarOActualizar(c);
        System.out.println("Cliente guardado");
    }

    public void buscar() throws SQLException {
        List<Cliente> lista = dao.buscar("99999998");
        if (!lista.isEmpty()) {
            for (Cliente c : lista) {
                System.out.println("NOMBRE: " + c.getNombre());
                System.out.println("DNI: " + c.getDni());
                System.out.println("TELEFONO: " + c.getTelefono());
            }
        } else {
            System.out.println("No hay registros");
        }
    }

    public static void main(String[] args) throws SQLException {
        TestCliente t = new TestCliente();
        t.buscar();
        // t.guardar();
    }
}
