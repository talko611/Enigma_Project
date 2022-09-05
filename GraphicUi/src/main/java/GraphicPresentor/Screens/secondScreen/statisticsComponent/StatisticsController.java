package GraphicPresentor.Screens.secondScreen.statisticsComponent;

import Engine.Engine;
import Engine.engineAnswers.StatisticsAnswer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StatisticsController {

    @FXML private TableView<UiStatistics> statisticsTb;
    @FXML private TableColumn<?, ?> srcCol;
    @FXML private TableColumn<?, ?> encryptCol;
    @FXML private TableColumn<?, ?> durationCol;
    @FXML private TableColumn<?, ?> configurationCol;

    private Engine engine;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    @FXML
    void initialize(){
        srcCol.setCellValueFactory(new PropertyValueFactory<>("src"));
        encryptCol.setCellValueFactory(new PropertyValueFactory<>("encrypted"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        configurationCol.setCellValueFactory(new PropertyValueFactory<>("configuration"));
    }

    public void setStatistics(){
        try{
            StatisticsAnswer answer = engine.getStatistics();
            ObservableList<UiStatistics> data = FXCollections.observableArrayList();
            answer.getStats().forEach((config, messages) ->{
                messages.forEach(message ->{
                    data.add(new UiStatistics(message.getSrc(), message.getOut(), message.getDuration(), config));
                });
            });
            statisticsTb.setItems(data);
        }catch (CloneNotSupportedException e){
            System.out.println("Couldn't load statistics");
        }



    }

    public static class UiStatistics{
        private SimpleStringProperty src;
        private SimpleStringProperty encrypted;
        private SimpleStringProperty duration;
        private SimpleStringProperty configuration;

        public UiStatistics (String src, String encrypted, long duration, String configuration){
            this.src = new SimpleStringProperty(src);
            this.encrypted = new SimpleStringProperty(encrypted);
            this.duration = new SimpleStringProperty(String.valueOf(duration));
            this.configuration = new SimpleStringProperty(configuration);
        }


        public SimpleStringProperty srcProperty() {
            return src;
        }

        public SimpleStringProperty encryptedProperty() {
            return encrypted;
        }

        public SimpleStringProperty durationProperty() {
            return duration;
        }

        public SimpleStringProperty configurationProperty() {
            return configuration;
        }

        public void setSrc(String src) {
            this.src.set(src);
        }

        public void setEncrypted(String encrypted) {
            this.encrypted.set(encrypted);
        }

        public void setDuration(String duration) {
            this.duration.set(duration);
        }

        public void setConfiguration(String configuration) {
            this.configuration.set(configuration);
        }
    }
}
