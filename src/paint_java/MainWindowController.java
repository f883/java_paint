package paint_java;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;

enum PaintModes { brush, line };

public class MainWindowController {
    private PaintModes _mode = PaintModes.brush;
    private Color _color = new Color(1, 1, 1, 1);
    private Image _image;
    private String _currentImagePath = "";
    private int _mousePressedX;
    private int _mousePressedY;
    private int _mouseReleasedX;
    private int _mouseReleasedY;

    @FXML
    public Group group;
    @FXML
    public ImageView imageView;
    @FXML
    public Button buttonModeBrush;
    @FXML
    public Button buttonModeLine;
    @FXML
    public Label labelX;
    @FXML
    public Label labelY;
    @FXML
    public Label labelMode;
    @FXML
    public javafx.scene.shape.Rectangle colorRectangle;
    @FXML
    public Label labelTest;
    @FXML
    public Button saveButton;

    @FXML
    public void onBrushModeSelected(){
        _mode = PaintModes.brush;
        labelMode.setText("tool: " + _mode.name());
    }

    @FXML
    public void onLineModeSelected(){
        _mode = PaintModes.line;
        labelMode.setText("tool: " + _mode.name());
    }

    @FXML
    public void onColorSetButtonPressed(){
        FXMLLoader loader = new FXMLLoader(Main.main1.getClass().getResource("SelectColorWindow.fxml"));
        AnchorPane page = new AnchorPane();
        try {
            page = loader.load();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        SetColorController controllerSetColor = loader.getController();
        controllerSetColor.setColor(_color);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Set color");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Main.root1);
        dialogStage.setResizable(false);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
        _color = controllerSetColor.getColor();
        colorRectangle.setFill(_color);
    }

    @FXML
    public void onMouseMovedTest() {
        labelTest.setText(String.valueOf(MouseInfo.getPointerInfo().getLocation().x));
    }

    @FXML
    public void onSaveButtonPressed(){
        FileChooser fch = new FileChooser();
        String path = fch.showSaveDialog(null).getPath();

        if (!path.isEmpty()){
            try
            {
                WritableImage snapshot = group.snapshot(new SnapshotParameters(), null);
                File file = new File(path);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
                ImageIO.write(renderedImage, "png", file);
            }
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    public void initialize(){
        labelMode.setText("tool: " + _mode.name());
        colorRectangle.setFill(_color);

        _image = new Image("file:source/test.jpg");
        imageView.setFitHeight(_image.getHeight());
        imageView.setFitWidth(_image.getWidth());
        imageView.setImage(_image);

        imageView.setOnMouseDragged(event -> {
            if (_mode == PaintModes.brush) {
                var line = new Line(_mousePressedX, _mousePressedY, event.getX(), event.getY());
                line.setFill(_color);
                group.getChildren().add(line);

                _mousePressedX = (int) event.getX();
                _mousePressedY = (int) event.getY();
            }
        });
        imageView.setOnMousePressed(event -> {
            _mousePressedX = (int) event.getX();
            _mousePressedY = (int) event.getY();
        });
        imageView.setOnMouseReleased(event -> {
            _mouseReleasedX = (int) event.getX();
            _mouseReleasedY = (int) event.getY();

            if (_mode == PaintModes.line) {
                int tempX = Math.min(_mouseReleasedX, _mousePressedX);
                int tempY = Math.min(_mouseReleasedY, _mousePressedY);

                javafx.scene.shape.Rectangle rectangle = new Rectangle(tempX,
                        tempY, Math.abs(_mousePressedX - _mouseReleasedX), Math.abs(_mousePressedY - _mouseReleasedY));
                rectangle.setFill(_color);
                group.getChildren().add(rectangle);
            }
        });
    }
}
