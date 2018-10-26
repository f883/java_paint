package paint_java;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import java.util.*;
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
    private double _origImageWight;
    private double _origImageHeight;
    private double _scale = 1;
    private double _scrollPaneVval = 0;
    private double _scrollPaneHval = 0;

    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Label scaleLabel;
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

    public void loadImage(String fileName){
        paintingGroup.getChildren().clear();
        paintingGroup.getChildren().add(imageView);
        _image = new Image("file:" + fileName);
        //_image = new Image("file:source/test.jpg");
        imageView.setFitHeight(_image.getHeight());
        imageView.setFitWidth(_image.getWidth());
        imageView.setImage(_image);
        _origImageHeight = _image.getHeight();
        _origImageWight = _image.getWidth();

        double tm = 1;
        while (tm > _scale){
            imageView.setFitHeight(imageView.getFitHeight() / 2);
            tm = tm / 2;
        }
        scrollPane.setVvalue(_scrollPaneVval);
        scrollPane.setHvalue(_scrollPaneHval);
    }

    public String getImagePath(){
        return _fileChooser.showOpenDialog(null).getPath();
    }

    @FXML
    public void loadImageWithDialog(){
        _currentImagePath = getImagePath();
        loadImage(_currentImagePath);
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
    public void onPlusViewButtonPressed(){
        if (_scale < 1) {
            _scale = _scale * 2;
            scaleLabel.setText(String.valueOf(_scale * 100) + "%");

            imageView.setFitHeight(imageView.getFitHeight() * 2);

            //textBoxSize.setText(String.valueOf(Double.valueOf(textBoxSize.getText()) * 2));
        }
    }

    @FXML
    public void onMinusViewButtonPressed(){
        if (_scale > 0.1) {
            _scale = _scale / 2;
            scaleLabel.setText(String.valueOf(_scale * 100) + "%");

            imageView.setFitHeight(imageView.getFitHeight() / 2);

            //textBoxSize.setText(String.valueOf(Double.valueOf(textBoxSize.getText()) / 2));
        }
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
    public void saveImageWithDialog(){
        _fileChooser.setInitialFileName(_currentImagePath); // "test.png"
        _currentImagePath = _fileChooser.showSaveDialog(null).getPath();

        saveImage();
    }

    public void saveImage(){
        imageView.setFitHeight(_origImageHeight);
        imageView.setFitWidth(_origImageWight);

        boolean is_end = false;
        int i = 1;
        ArrayList<Line> tmlList = new ArrayList();

        while (!is_end){
            try {
                var tt = (Line) paintingGroup.getChildren().get(i++);
                tt.setStartX(tt.getStartX() * (1.0 / _scale));
                tt.setStartY(tt.getStartY() * (1.0 / _scale));
                tt.setEndX(tt.getEndX() * (1.0 / _scale));
                tt.setEndY(tt.getEndY() * (1.0 / _scale));
                tt.strokeWidthProperty().setValue(Double.valueOf(textBoxSize.getText()));
                tmlList.add(tt);
            }
            catch (Exception e){
                is_end = true;
            }
        }

        paintingGroup.getChildren().clear();

        paintingGroup.getChildren().add(0, imageView);

        for (int j = 0; j < tmlList.size(); j++)
            paintingGroup.getChildren().add(tmlList.get(j));

        try
        {
            WritableImage snapshot = paintingGroup.snapshot(new SnapshotParameters(), null);
            File file = new File(_currentImagePath);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
            ImageIO.write(renderedImage, "png", file);
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void initialize(){
        labelMode.setText("tool: " + _mode.name());
        colorRectangle.setFill(_color);
        textBoxSize.setText("1");

        loadImageWithDialog();

        imageView.setOnMouseDragged(event -> {
            if (event.getX() >= 0 &&
                    event.getY() >= 0 && //  - (Integer.valueOf(textBoxSize.getText()))
                    event.getX() <= _image.getWidth()*_scale && event.getY() <= _image.getHeight()*_scale) {
                if (_mode == PaintModes.brush || _mode == PaintModes.ereaser) {
                    var line = new Line(_mousePressedX, _mousePressedY, event.getX(), event.getY());
                    if (_mode == PaintModes.brush)
                        line.setStroke(_color);
                    else
                        line.setStroke(_ereaserColor);

                    line.strokeWidthProperty().setValue(Integer.valueOf(textBoxSize.getText())*_scale);
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
            _scrollPaneHval = scrollPane.getHvalue();
            _scrollPaneVval = scrollPane.getVvalue();
            saveImage();
            loadImage(_currentImagePath);
            if (event.getX() >= 0 &&
                    event.getY() >= 0 &&
                    event.getX() <= _image.getWidth()*_scale && event.getY() <= _image.getHeight()*_scale) {
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
