package GraphicPresentor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;


public class GraphicPresentor extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainWindow.fxml")));
        Scene scene = new Scene(load,700,500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}


