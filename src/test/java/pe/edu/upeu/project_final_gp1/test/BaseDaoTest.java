package pe.edu.upeu.project_final_gp1.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import pe.edu.upeu.project_final_gp1.utils.ConexionBD;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

abstract class BaseDaoTest {
    @BeforeAll
    static void configurarBaseDePrueba() throws Exception {
        if (System.getProperty("bazar1.test.db") == null) {
            Path carpeta = Files.createTempDirectory("bazar1-test-");
            Path archivo = carpeta.resolve("bazar1-test.db");
            System.setProperty("app.db.path", archivo.toString());
            System.setProperty("bazar1.test.db", archivo.toString());
        }
    }

    @BeforeEach
    void limpiarBase() throws Exception {
        try (Connection cn = ConexionBD.getConnection(); Statement st = cn.createStatement()) {
            st.executeUpdate("DELETE FROM detalle_venta");
            st.executeUpdate("DELETE FROM venta");
            st.executeUpdate("DELETE FROM cobro_deuda");
            st.executeUpdate("DELETE FROM egreso");
            st.executeUpdate("DELETE FROM deudor");
            st.executeUpdate("DELETE FROM producto");
            st.executeUpdate("DELETE FROM cliente");
            st.executeUpdate("DELETE FROM sqlite_sequence WHERE name IN ('venta','detalle_venta','deudor','egreso','cobro_deuda')");
        }
    }
}
