package pe.edu.upeu.project_final_gp1.interfaces;

import pe.edu.upeu.project_final_gp1.modelo.Deudor;
import java.sql.SQLException;
import java.util.List;

public interface IDeudorDao {
    void registrar(Deudor deudor) throws SQLException;
    void actualizar(Deudor deudor) throws SQLException;
    void aumentarDeuda(int idDeudor, double monto) throws SQLException;
    void registrarPago(int idDeudor, double monto) throws SQLException;
    boolean eliminar(int idDeudor) throws SQLException;
    List<Deudor> listAll() throws SQLException;
    List<Deudor> buscar(String texto) throws SQLException;
}
