package GraphicPresentor.Screens.secondScreen.keyboardButtons;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;


public class KeyboardButtonController {

    @FXML private TilePane container;
    @FXML private Label letter;

    private BooleanProperty isTypeStateSelected;

    public void setIsTypeStateSelected(BooleanProperty isTypeStateSelected) {
        this.isTypeStateSelected = isTypeStateSelected;
    }

    @FXML
    void initialize(){
        container.setStyle("-fx-background-color: #434346");
        container.setStyle("-fx-border-color : White");
    }

    @FXML
    void keyPressed(KeyEvent event) {
        if(isTypeStateSelected.get() && event.getText().toUpperCase().equals(letter.getText())){
            container.setStyle("-fx-background-color: Red");
        }
    }

    @FXML
    void keyReleased(KeyEvent event) {
        if(isTypeStateSelected.get() && event.getText().toUpperCase().equals(letter.getText())){
            container.setStyle("-fx-background-color: #434346");
            container.setStyle("-fx-border-color : White");
        }
    }

    public void connectToParentComponentEvent(TextField message){
        message.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressed);
        message.addEventHandler(KeyEvent.KEY_RELEASED, this::keyReleased);
    }

}
