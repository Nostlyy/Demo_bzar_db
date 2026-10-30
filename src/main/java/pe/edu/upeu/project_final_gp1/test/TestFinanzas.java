package pe.edu.upeu.project_final_gp1.test;

import pe.edu.upeu.project_final_gp1.implementacion.FinanzasDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IFinanzasDao;
import pe.edu.upeu.project_final_gp1.modelo.Egreso;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TestFinanzas {
    static IFinanzasDao dao = new FinanzasDaoImpl();

    public void insertarEgreso() throws SQLException {
        Egreso e = new Egreso();
        e.setDescripcion("Egreso de prueba");
        e.setMonto(25.00);
        dao.registrarEgreso(e);
        System.out.println("Egreso registrado");
    }

    public void listarEgresos() throws SQLException {
        List<Egreso> lista = dao.listarEgresos(LocalDate.now());
        if (lista != null && !lista.isEmpty()) {
            for (Egreso e : lista) {
                System.out.println("ID: " + e.getIdEgreso());
                System.out.println("FECHA: " + e.getFecha());
                System.out.println("DESCRIPCION: " + e.getDescripcion());
                System.out.println("MONTO: " + e.getMonto());
            }
        } else {
            System.out.println("No hay egresos para hoy");
        }
    }

    public static void main(String[] args) throws SQLException {
        TestFinanzas t = new TestFinanzas();
        t.listarEgresos();
        // t.insertarEgreso();
    }
}
