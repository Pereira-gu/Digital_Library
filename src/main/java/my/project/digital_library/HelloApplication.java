package my.project.digital_library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    // Referência estática para permitir o acesso ao HostServices de qualquer lugar
    private static HelloApplication instance;

    public static HelloApplication getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        instance = this; // Define a instância ao iniciar

        DatabaseManager.initDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Minha Biblioteca Digital de PDFs");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}