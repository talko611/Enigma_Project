package GraphicPresentor;


import Engine.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;


public class GraphicPresentor extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Engine engine = new EngineImp();

        URL url = getClass().getResource("/ComponentsFXML/MainComponentFXML.fxml");
        loader.setLocation(url);
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setEngine(engine);
        controller.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root,700,500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}


