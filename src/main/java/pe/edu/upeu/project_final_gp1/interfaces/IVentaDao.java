package pe.edu.upeu.project_final_gp1.interfaces;

import pe.edu.upeu.project_final_gp1.modelo.Venta;
import java.sql.SQLException;
import java.util.List;

public interface IVentaDao {
    Venta registrarVenta(Venta venta) throws SQLException;
    List<Venta> listAll() throws SQLException;
}
