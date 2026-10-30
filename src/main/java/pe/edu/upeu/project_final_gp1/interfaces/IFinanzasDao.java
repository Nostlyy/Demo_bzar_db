package pe.edu.upeu.project_final_gp1.interfaces;

import pe.edu.upeu.project_final_gp1.modelo.Egreso;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IFinanzasDao {
    void registrarEgreso(Egreso egreso) throws SQLException;
    List<Egreso> listarEgresos(LocalDate fecha) throws SQLException;
    double calcularIngresos(LocalDate fecha) throws SQLException;
    double calcularEgresos(LocalDate fecha) throws SQLException;
    double calcularDeudaPendiente() throws SQLException;
}
