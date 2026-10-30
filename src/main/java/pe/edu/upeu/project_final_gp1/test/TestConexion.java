package pe.edu.upeu.project_final_gp1.test;

import pe.edu.upeu.project_final_gp1.utils.ConexionBD;

import java.sql.Connection;

public class TestConexion {
    public static void testConexion() {
        try (Connection cn = ConexionBD.getConnection()) {
            if (cn != null && !cn.isClosed()) {
                System.out.println("Conexion success");
            } else {
                System.out.println("Error de conexion");
            }
        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        testConexion();
    }
}
