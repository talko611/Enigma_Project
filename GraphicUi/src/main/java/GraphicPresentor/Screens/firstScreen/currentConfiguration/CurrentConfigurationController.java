package GraphicPresentor.Screens.firstScreen.currentConfiguration;

import GraphicPresentor.UiAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
        import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CurrentConfigurationController {

    @FXML
    private TableView<UiRotor> rotorsTable;
    @FXML
    private TableColumn<?, ?> rotorIdCol;
    @FXML
    private TableColumn<?, ?> currLetterCol;
    @FXML
    private TableColumn<?, ?> notchDisCol;
    @FXML
    private TableView<UiPlug> plugsTable;
    @FXML
    private TableColumn<?, ?> plugFromCol;
    @FXML
    private TableColumn<?, ?> plugToCol;
    @FXML
    private Label reflectorId;
    @FXML
    private Label currentMachineConfiguration;

    private UiAdapter uiAdapter;
    public void setUiAdapter(UiAdapter uiAdapter){
        this.uiAdapter = uiAdapter;
        currentMachineConfiguration.textProperty().bind(uiAdapter.currentConfigurationProperty());
    }

    @FXML
    void initialize(){
        rotorIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        currLetterCol.setCellValueFactory(new PropertyValueFactory<>("offset"));
        notchDisCol.setCellValueFactory(new PropertyValueFactory<>("distance"));
        plugFromCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        plugToCol.setCellValueFactory(new PropertyValueFactory<>("to"));
    }

    public void getCurrentConfiguration(){
        String currentConfigurationLine = uiAdapter.getCurrentConfiguration();
        List<String> splitedConfigLine = Arrays.asList(currentConfigurationLine.split("[<>]"));
        List<String> configLineParts = splitedConfigLine.stream().filter(i-> !i.equals("")).collect(Collectors.toList());
        List<String> rotorsIds = cleanRotorId(configLineParts.get(0));
        List<String> offsetList = cleanOffsetsLetters(configLineParts.get(1));
        List<String> notches = cleanNotchDistance(configLineParts.get(1));
        setRotorsData(rotorsIds, offsetList,notches);
        reflectorId.setText(configLineParts.get(2));
        if(configLineParts.size() == 4){
            List<String> plugsPairs = Arrays.asList(configLineParts.get(3).split(","));
            setPlugsData(plugsPairs);
        }
    }
    private List<String> cleanRotorId(String rotorIds){
        return Arrays.stream(rotorIds.split(","))
                .filter(i -> !i.equals("")).collect(Collectors.toList());
    }

    private List<String> cleanOffsetsLetters(String offsets) {
        List<String> splited = Arrays.asList(offsets.split(","));
        return splited.stream().map(i -> String.valueOf(i.charAt(0))).collect(Collectors.toList());
    }

    private List<String> cleanNotchDistance(String notches){
        List<String> splited = Arrays.asList(notches.split(","));
        return splited.stream().map(i -> i.substring(2, i.length() -1)).collect(Collectors.toList());
    }

    private void setRotorsData(List<String> rotorsId, List<String> offsetsLetter, List<String> distances){
        ObservableList<UiRotor> data = FXCollections.observableArrayList();
        for(int i = rotorsId.size() -1; i >=0; --i){
            data.add(new UiRotor(
                    rotorsId.get(i), offsetsLetter.get(i).equals("") ? "space" : offsetsLetter.get(i), distances.get(i)));
        }
        rotorsTable.setItems(data);
    }
    private void setPlugsData(List<String> pairs){
        ObservableList<UiPlug> data = FXCollections.observableArrayList();
        pairs.forEach(i->{
            List<String> current = Arrays.asList(i.split("|"));
            data.add(new UiPlug(Objects.equals(current.get(0), " ") ? "space" : current.get(0), current.get(2)));
        });
        plugsTable.setItems(data);
    }



    public static class UiRotor{
        private SimpleStringProperty id;
        private SimpleStringProperty offset;
        private SimpleStringProperty distance;

        private UiRotor(String id, String offset, String distance){
            this.id = new SimpleStringProperty(id);
            this.offset = new SimpleStringProperty(offset);
            this.distance = new SimpleStringProperty(distance);

        }

        public SimpleStringProperty idProperty() {
            return id;
        }

        public SimpleStringProperty offsetProperty() {
            return offset;
        }

        public SimpleStringProperty distanceProperty() {
            return distance;
        }

        public void setId(String id) {
            this.id.set(id);
        }

        public void setOffset(String offset) {
            this.offset.set(offset);
        }

        public void setDistance(String distance) {
            this.distance.set(distance);
        }
    }

    public static class UiPlug{
        private SimpleStringProperty from;
        private SimpleStringProperty to;

        public UiPlug(String from, String to){
            this.from = new SimpleStringProperty(from);
            this.to = new SimpleStringProperty(to);
        }

        public SimpleStringProperty fromProperty() {
            return from;
        }

        public SimpleStringProperty toProperty() {
            return to;
        }

        public void setFrom(String from) {
            this.from.set(from);
        }

        public void setTo(String to) {
            this.to.set(to);
        }
    }

}
