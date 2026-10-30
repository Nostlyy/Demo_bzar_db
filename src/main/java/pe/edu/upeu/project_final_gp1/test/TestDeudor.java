package pe.edu.upeu.project_final_gp1.test;

import pe.edu.upeu.project_final_gp1.implementacion.DeudorDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IDeudorDao;
import pe.edu.upeu.project_final_gp1.modelo.Deudor;

import java.sql.SQLException;
import java.util.List;

public class TestDeudor {
    static IDeudorDao dao = new DeudorDaoImpl();

    public void insertar() throws SQLException {
        Deudor d = new Deudor(0, "Deudor de prueba", "99999997", "900000002", 40.00);
        dao.registrar(d);
        System.out.println("Deudor registrado");
    }

    public void listar() throws SQLException {
        List<Deudor> lista = dao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Deudor d : lista) {
                System.out.println("ID: " + d.getIdDeudor());
                System.out.println("NOMBRE: " + d.getNombre());
                System.out.println("DNI: " + d.getDni());
                System.out.println("DEUDA: " + d.getDeuda());
            }
        } else {
            System.out.println("No hay registros");
        }
    }

    public void registrarPago() throws SQLException {
        List<Deudor> lista = dao.buscar("99999997");
        if (!lista.isEmpty()) {
            dao.registrarPago(lista.get(0).getIdDeudor(), 10.00);
            System.out.println("Pago registrado");
        } else {
            System.out.println("Primero registra el deudor de prueba");
        }
    }

    public static void main(String[] args) throws SQLException {
        TestDeudor t = new TestDeudor();
        t.listar();
        // t.insertar();
        // t.registrarPago();
    }
}
