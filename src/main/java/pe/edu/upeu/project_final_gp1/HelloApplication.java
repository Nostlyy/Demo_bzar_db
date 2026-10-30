package pe.edu.upeu.project_final_gp1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/pe/edu/upeu/project_final_gp1/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Bazar 1 | Sistema de Gestión");
        stage.setMinWidth(1080);
        stage.setMinHeight(700);
        stage.setWidth(1280);
        stage.setHeight(850);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
