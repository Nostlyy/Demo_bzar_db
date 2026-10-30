package pe.edu.upeu.project_final_gp1.interfaces;

import pe.edu.upeu.project_final_gp1.modelo.Producto;
import java.sql.SQLException;
import java.util.List;

public interface IProductoDao {
    boolean insert(Producto producto) throws SQLException;
    boolean update(Producto producto) throws SQLException;
    boolean delete(String id) throws SQLException;
    Producto searchById(String id) throws SQLException;
    List<Producto> listAll() throws SQLException;
    List<Producto> searchByNombre(String texto) throws SQLException;
}
