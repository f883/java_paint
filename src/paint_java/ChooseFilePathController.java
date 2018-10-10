package paint_java;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChooseFilePathController {
    private String _savePath;

    public String getSavePath(){
        return _savePath;
    }

    @FXML
    public TextField textFieldPath;
    @FXML
    public void initialize() {
        textFieldPath.setText("/Users/vasya/Desktop/test.jpg");
    }

    @FXML
    public void onCancelButtonPressed(){
        _savePath = "";
        Stage stage = (Stage)textFieldPath.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onSaveButtonPressed(){
        _savePath = textFieldPath.getText();
        Stage stage = (Stage)textFieldPath.getScene().getWindow();
        stage.close();
    }
}
