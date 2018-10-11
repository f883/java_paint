package paint_java;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;

enum PaintModes { brush, rectangle, line, ellipse, ereaser, star };

public class MainWindowController {
    private PaintModes _mode = PaintModes.brush;
    private Color _color = new Color(1, 1, 1, 1);
    private Color _ereaserColor = new Color(1, 1, 1, 1);
    private Image _image;
    private String _currentImagePath = "";
    private int _mousePressedX;
    private int _mousePressedY;
    private int _mouseReleasedX;
    private int _mouseReleasedY;
    private FileChooser _fileChooser = new FileChooser();

    @FXML
    public TextField textBoxSize;
    @FXML
    public Group paintingGroup;
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
    public void loadImage(){
        _currentImagePath = _fileChooser.showOpenDialog(null).getPath();
        _image = new Image("file:" + _currentImagePath);
        //_image = new Image("file:source/test.jpg");
        imageView.setFitHeight(_image.getHeight());
        imageView.setFitWidth(_image.getWidth());
        imageView.setImage(_image);
    }

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
    public void onRectangleModeSelected(){
        _mode = PaintModes.rectangle;
        labelMode.setText("tool: " + _mode.name());
    }
    @FXML
    public void onEllipseModeSelected(){
        _mode = PaintModes.ellipse;
        labelMode.setText("tool: " + _mode.name());
    }
    @FXML
    public void onEreaserModeSelected(){
        _mode = PaintModes.ereaser;
        labelMode.setText("tool: " + _mode.name());
    }
    @FXML
    public void onStarModeSelected(){
        _mode = PaintModes.star;
        labelMode.setText("tool: " + _mode.name());
    }

    @FXML
    public void onColorSetButtonPressed(){
        FXMLLoader loader = new FXMLLoader(Main.rootMain.getClass()
                .getResource("SelectColorWindow.fxml"));
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
        dialogStage.initOwner(Main.rootStage);
        dialogStage.setResizable(false);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
        _color = controllerSetColor.getColor();
        colorRectangle.setFill(_color);
    }

    @FXML
    public void saveImage(){
        _fileChooser.setInitialFileName("test.png");
        String path = _fileChooser.showSaveDialog(null).getPath();

        if (!path.isEmpty()){
            try
            {
                WritableImage snapshot = paintingGroup.snapshot(new SnapshotParameters(), null);
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
        textBoxSize.setText("1");

        loadImage();

        imageView.setOnMouseDragged(event -> {
            if (event.getX() >= 0 && event.getY() >= 0 &&
                    event.getX() <= _image.getWidth() && event.getY() <= _image.getHeight()) {
                if (_mode == PaintModes.brush || _mode == PaintModes.ereaser) {
                    var line = new Line(_mousePressedX, _mousePressedY, event.getX(), event.getY());
                    if (_mode == PaintModes.brush)
                        line.setStroke(_color);
                    else
                        line.setStroke(_ereaserColor);


                    line.strokeWidthProperty().setValue(Integer.valueOf(textBoxSize.getText()));
                    paintingGroup.getChildren().add(line);

                    _mousePressedX = (int) event.getX();
                    _mousePressedY = (int) event.getY();
                }
            }
        });
        imageView.setOnMousePressed(event -> {
            if (event.getX() >= 0 && event.getY() >= 0) {
                _mousePressedX = (int) event.getX();
                _mousePressedY = (int) event.getY();
            }
        });
        imageView.setOnMouseReleased(event -> {
            if (event.getX() >= 0 && event.getY() >= 0 &&
                    event.getX() <= _image.getWidth() && event.getY() <= _image.getHeight()) {
                _mouseReleasedX = (int) event.getX();
                _mouseReleasedY = (int) event.getY();

                int tempX = Math.min(_mouseReleasedX, _mousePressedX);
                int tempY = Math.min(_mouseReleasedY, _mousePressedY);

                if (_mode == PaintModes.rectangle) {
                    javafx.scene.shape.Rectangle rectangle = new Rectangle(tempX,
                            tempY, Math.abs(_mousePressedX - _mouseReleasedX),
                            Math.abs(_mousePressedY - _mouseReleasedY));
                    rectangle.setFill(_color);
                    paintingGroup.getChildren().add(rectangle);
                }
                if (_mode == PaintModes.line){
                    javafx.scene.shape.Line line = new Line(_mousePressedX,
                            _mousePressedY, _mouseReleasedX, _mouseReleasedY);
                    line.setStroke(_color);
                    line.strokeWidthProperty().setValue(Integer.valueOf(textBoxSize.getText()));
                    paintingGroup.getChildren().add(line);
                }
                if (_mode == PaintModes.ellipse) {
                    //labelX.setText(String.valueOf(event.getX()));
                    //labelY.setText(String.valueOf(event.getY()));
                    javafx.scene.shape.Ellipse ellipse = new Ellipse(_mousePressedX,
                            _mousePressedY, Math.abs(_mousePressedX - _mouseReleasedX),
                            Math.abs(_mousePressedY - _mouseReleasedY));
                    ellipse.setFill(_color);
                    paintingGroup.getChildren().add(ellipse);
                }
            }
        });
    }
}
