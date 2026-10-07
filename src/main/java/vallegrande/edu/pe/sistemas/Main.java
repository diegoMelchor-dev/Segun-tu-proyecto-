package vallegrande.edu.pe.sistemas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vallegrande.edu.pe.sistemas.controller.MainController;
import vallegrande.edu.pe.sistemas.view.MainView;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/vallegrande/edu/pe/sistemas/MainView.fxml")
        );
        Parent root = loader.load();

        MainView view = loader.getController();
        new MainController(view);

        stage.setTitle("Sistemas");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}