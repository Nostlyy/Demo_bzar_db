package pe.edu.upeu.project_final_gp1.test;

import org.junit.jupiter.api.Test;
import pe.edu.upeu.project_final_gp1.utils.ConexionBD;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConexionBDTest extends BaseDaoTest {
    @Test
    void abrirConexion() throws Exception {
        try (Connection cn = ConexionBD.getConnection()) {
            assertNotNull(cn);
            assertTrue(cn.isValid(2));
        }
    }
}
