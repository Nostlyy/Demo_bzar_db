package pe.edu.upeu.project_final_gp1.test;

import pe.edu.upeu.project_final_gp1.implementacion.ProductoDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IProductoDao;
import pe.edu.upeu.project_final_gp1.modelo.Producto;

import java.sql.SQLException;
import java.util.List;

public class TestProducto {
    static IProductoDao dao = new ProductoDaoImpl();

    public void insertar() throws SQLException {
        Producto p = new Producto("TEST-P01", "Producto de prueba", 12.50, 20);
        boolean result = dao.insert(p);
        System.out.println(result ? "Insert success" : "Insert error");
    }

    public void actualizar() throws SQLException {
        Producto p = new Producto("TEST-P01", "Producto actualizado", 15.00, 18);
        boolean result = dao.update(p);
        System.out.println(result ? "Update success" : "Update error");
    }

    public void eliminar() throws SQLException {
        boolean result = dao.delete("TEST-P01");
        System.out.println(result ? "Delete success" : "Delete error");
    }

    public void buscar() throws SQLException {
        Producto p = dao.searchById("TEST-P01");
        if (p != null) {
            System.out.println("ID: " + p.getId());
            System.out.println("NOMBRE: " + p.getNombre());
            System.out.println("PRECIO: " + p.getPrecio());
            System.out.println("STOCK: " + p.getStock());
        } else {
            System.out.println("No hay registros");
        }
    }

    public void listar() throws SQLException {
        List<Producto> lista = dao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Producto p : lista) {
                System.out.println("ID: " + p.getId());
                System.out.println("NOMBRE: " + p.getNombre());
                System.out.println("PRECIO: " + p.getPrecio());
                System.out.println("STOCK: " + p.getStock());
            }
        } else {
            System.out.println("No hay registros");
        }
    }

    public static void main(String[] args) throws SQLException {
        TestProducto t = new TestProducto();
        t.listar();
        // t.insertar();
        // t.buscar();
        // t.actualizar();
        // t.eliminar();
    }
}
