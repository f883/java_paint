package paint_java;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SetColorController {
    private Color _color = new Color(0, 0, 0, 1);

    public Color getColor(){
        return _color;
    }
    public void setColor(Color color){
        _color = color;
    }

    @FXML
    public Slider redSlider;
    @FXML
    public Slider greenSlider;
    @FXML
    public Slider blueSlider;
    @FXML
    public Rectangle colorRectangle;
    @FXML
    public Button setColorButton;

    @FXML
    public void onRedSliderMoved(){
        _color = new Color(redSlider.getValue() / 100.0, _color.getGreen(), _color.getBlue(), 1);
        colorRectangle.setFill(_color);
    }
    @FXML
    public void onGreenSliderMoved(){
        _color = new Color(_color.getRed(), greenSlider.getValue() / 100.0, _color.getBlue(), 1);
        colorRectangle.setFill(_color);
    }
    @FXML
    public void onBlueSliderMoved(){
        _color = new Color(_color.getRed(), _color.getGreen(), blueSlider.getValue() / 100.0, 1);
        colorRectangle.setFill(_color);
    }
    @FXML
    public void onSetButtonPressed(){
        Stage stage = (Stage)setColorButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() {
        colorRectangle.setFill(_color);
    }
}
