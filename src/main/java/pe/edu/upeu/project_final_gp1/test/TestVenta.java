package pe.edu.upeu.project_final_gp1.test;

import pe.edu.upeu.project_final_gp1.implementacion.ProductoDaoImpl;
import pe.edu.upeu.project_final_gp1.implementacion.VentaDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IVentaDao;
import pe.edu.upeu.project_final_gp1.modelo.DetalleVenta;
import pe.edu.upeu.project_final_gp1.modelo.Producto;
import pe.edu.upeu.project_final_gp1.modelo.Venta;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TestVenta {
    static IVentaDao dao = new VentaDaoImpl();
    static ProductoDaoImpl productos = new ProductoDaoImpl();

    public void insertar() throws SQLException {
        Producto p = productos.searchById("TEST-P01");
        if (p == null) {
            productos.insert(new Producto("TEST-P01", "Producto de prueba", 10.00, 10));
            p = productos.searchById("TEST-P01");
        }
        if (p.getStock() < 1) {
            System.out.println("El producto de prueba no tiene stock");
            return;
        }
        Venta v = new Venta();
        v.setFecha(LocalDate.now().toString());
        v.setNombreCliente("Cliente de prueba");
        v.setDniCliente("99999996");
        v.setTelefonoCliente("900000003");
        v.agregarDetalle(new DetalleVenta(p.getId(), p.getNombre(), 1, p.getPrecio()));
        v.setMontoPagado(p.getPrecio());
        Venta result = dao.registrarVenta(v);
        System.out.println("Venta registrada: " + result.getIdVenta());
    }

    public void listar() throws SQLException {
        List<Venta> lista = dao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Venta v : lista) {
                System.out.println("ID VENTA: " + v.getIdVenta());
                System.out.println("FECHA: " + v.getFecha());
                System.out.println("CLIENTE: " + v.getNombreCliente());
                System.out.println("TOTAL: " + v.getTotal());
                System.out.println("PAGADO: " + v.getMontoPagado());
                System.out.println("SALDO: " + v.getSaldoPendiente());
            }
        } else {
            System.out.println("No hay registros");
        }
    }

    public static void main(String[] args) throws SQLException {
        TestVenta t = new TestVenta();
        t.listar();
        // t.insertar();
    }
}
