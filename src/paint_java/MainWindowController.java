package paint_java;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.util.Duration;

import javax.imageio.ImageIO;

// TODO применение эффектов после гауссиана
// TODO проверка размера
// TODO очистка переменных при открытии нового

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
    private FileChooser _fileChooser;
    private double _scale = 1;
    private Rectangle _invisibleRectangle; // позволяет рисовать на только что созданных линиях.
    private double _origImageHeight;
    private double _origImageWight;

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
    @FXML
    public AnchorPane pane;

    public void loadImage(String fileName){
        paintingGroup.getChildren().clear();
        paintingGroup.getChildren().add(imageView);
        _image = new Image("file:" + fileName);
        imageView.setFitHeight(_image.getHeight());
        imageView.setFitWidth(_image.getWidth());
        imageView.setImage(_image);

        _scale = 1;

//        double tm = 1;
//        while (tm > _scale){
//            imageView.setFitHeight(imageView.getFitHeight() / 2);
//            tm = tm / 2;
//        }

        _invisibleRectangle.setHeight(imageView.getImage().getHeight());
        _invisibleRectangle.setWidth(imageView.getImage().getWidth());
        _invisibleRectangle.setFill(new Color(0, 0, 1, 0));
        paintingGroup.getChildren().add(_invisibleRectangle);

        _origImageHeight = _image.getHeight();
        _origImageWight = _image.getWidth();
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
    public void setGaussianBlur(){
        try{
            int i = 0;
            while (true){
                Node node = paintingGroup.getChildren().get(i);
                applyGaussian(node);
                i++;
            }
        }
        catch (Exception e){            }
    }

    @FXML
    public void setHighContrast(){
        try{
            int i = 0;
            while (true){
                Node node = paintingGroup.getChildren().get(i);
                applyHighContrast(node);
                i++;
            }
        }
        catch (Exception e){}
    }

    @FXML
    public void setLowContrast(){
        try{
            int i = 0;
            while (true){
                Node node = paintingGroup.getChildren().get(i);
                applyLowContrast(node);
                i++;
            }
        }
        catch (Exception e){  }
    }

    @FXML
    private void setNegative(){
        PixelReader pr = _image.getPixelReader();
        WritableImage wi = new WritableImage(pr, (int) _image.getWidth(), (int) _image.getHeight());
        PixelWriter pw = wi.getPixelWriter();
        for (int y = 0; y < _image.getHeight(); y++) {
            for (int x = 0; x < _image.getWidth(); x++) {
                Color c = pr.getColor(x, y);
                Color n = new Color(1. - c.getRed(), 1. - c.getGreen(),1. - c.getBlue(), 1);
                pw.setColor(x, y, n);
            }
        }
        _image = wi;
        imageView.setImage(wi);

        try{
            int i = 0;
            while (true){
                Object t = paintingGroup.getChildren().get(i); // негатив, размытие, проверка размера, очистка переменных при открытии нового
                switch (t.getClass().getSimpleName()) {
                    case "Line": {
                        Line u = (Line) t;
                        Color c = (Color)u.getStroke();
                        Color n = new Color(1. - c.getRed(), 1. - c.getGreen(),1. - c.getBlue(), c.getOpacity());
                        u.setStroke(n);
                        break;
                    }
                    case "Rectangle": {
                        Rectangle u = (Rectangle) t;
                        Color c = (Color)u.getFill();
                        Color n = new Color(1. - c.getRed(), 1. - c.getGreen(),1. - c.getBlue(), c.getOpacity());
                        u.setFill(n);
                        u.setStroke(n);
                        break;
                    }
                    case "Ellipse": {
                        Ellipse u = (Ellipse) t;
                        Color c = (Color)u.getFill();
                        Color n = new Color(1. - c.getRed(), 1. - c.getGreen(),1. - c.getBlue(), c.getOpacity());
                        u.setFill(n);
                        break;
                    }
                    case "Polygon": {
                        Polygon u = (Polygon)t;
                        Color c = (Color)u.getFill();
                        Color n = new Color(1. - c.getRed(), 1. - c.getGreen(),1. - c.getBlue(), c.getOpacity());
                        u.setFill(n);;
                        break;
                    }
                }
                i++;
            }
        }
        catch (Exception e){            }

    }

    private void applyHighContrast(Node node) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.contrastProperty().setValue(1);
        node.setEffect(colorAdjust);
    }

    private void applyLowContrast(Node node) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.contrastProperty().setValue(0);
        node.setEffect(colorAdjust);
    }

    private void applyGaussian(Node node) {
        GaussianBlur blur = new GaussianBlur(0);
        node.setEffect(blur);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(blur.radiusProperty(), 40.);
        KeyFrame kf = new KeyFrame(Duration.millis(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    @FXML
    public void onPlusViewButtonPressed(){
        if (_scale < 1) {
            _scale = _scale * 2;
            scaleLabel.setText(String.valueOf(_scale * 100) + "%");

            imageView.setFitHeight(imageView.getFitHeight() * 2);

            try{
                int i = 0;
                while (true){
                    Object t = paintingGroup.getChildren().get(i); // негатив, размытие, проверка размера, очистка переменных при открытии нового
                    switch (t.getClass().getSimpleName()) {
                        case "Line": {
                            Line u = (Line) t;
                            u.setStartX(u.getStartX() * 2);
                            u.setStartY(u.getStartY() * 2);
                            u.setEndX(u.getEndX() * 2);
                            u.setEndY(u.getEndY() * 2);
                            u.setStrokeWidth(u.getStrokeWidth() * 2);
                            break;
                        }
                        case "Rectangle": {
                            Rectangle u = (Rectangle) t;
                            u.setX(u.getX() * 2);
                            u.setY(u.getY() * 2);
                            u.setHeight(u.getHeight() * 2);
                            u.setWidth(u.getWidth() * 2);
                            break;
                        }
                        case "Ellipse": {
                            Ellipse u = (Ellipse) t;
                            u.setCenterX(u.getCenterX() * 2);
                            u.setCenterY(u.getCenterY() * 2);
                            u.setRadiusX(u.getRadiusX() * 2);
                            u.setRadiusY(u.getRadiusY() * 2);
                            break;
                        }
                        case "Polygon": { // TODO fix resize
                            Polygon u = (Polygon)t;
                            paintingGroup.getChildren().remove(u);
                            Polygon newPol = new Polygon();
                            newPol.setFill(u.getFill());

                            //System.out.println("##########");

                            try{
                                int j = 0;
                                while (true){
                                    double val = u.getPoints().get(j);
                                    newPol.getPoints().add(val * 2.);
                                    //System.out.println("!!! " + u.getPoints().get(j) + " " + newPol.getPoints().get(j));
                                    j++;
                                }
                            }
                            catch (Exception e){  }
                            paintingGroup.getChildren().add(newPol);
                            break;
                        }
                    }
                    i++;
                }
            }
            catch (Exception e){            }
        }
    }

    @FXML
    public void onMinusViewButtonPressed(){
        if (_scale > 0.1) {
            _scale = _scale / 2;
            scaleLabel.setText(String.valueOf(_scale * 100) + "%");

            imageView.setFitHeight(imageView.getFitHeight() / 2);

            try{
                int i = 0;
                while (true){
                    Object t = paintingGroup.getChildren().get(i);
                    switch (t.getClass().getSimpleName()){
                        case "Line":{
                            Line u = (Line)t;
                            u.setStartX(u.getStartX() / 2);
                            u.setStartY(u.getStartY() / 2);
                            u.setEndX(u.getEndX() / 2);
                            u.setEndY(u.getEndY() / 2);
                            u.setStrokeWidth(u.getStrokeWidth() / 2);
                            break;
                        }
                        case "Rectangle":{
                            Rectangle u = (Rectangle) t;
                            u.setX(u.getX() / 2);
                            u.setY(u.getY() / 2);
                            u.setHeight(u.getHeight() / 2);
                            u.setWidth(u.getWidth() / 2);
                            break;
                        }
                        case "Ellipse":{
                            Ellipse u = (Ellipse)t;
                            u.setCenterX(u.getCenterX() / 2);
                            u.setCenterY(u.getCenterY() / 2);
                            u.setRadiusX(u.getRadiusX() / 2);
                            u.setRadiusY(u.getRadiusY() / 2);
                            break;
                        }
                        case "Polygon":{
                            Polygon u = (Polygon)t;
                            paintingGroup.getChildren().remove(u);
                            Paint paint = u.getFill();
                            Polygon newPol = new Polygon();
                            newPol.setFill(paint);

                            //System.out.println("$$$$$$$$"); // TODO почему это вызывается дважды

                            try{
                                int j = 0;
                                while (true){
                                    double val = u.getPoints().get(j);
                                    newPol.getPoints().add(val / 2.);
                                    //System.out.println(u.getPoints().get(j) + " " + newPol.getPoints().get(j));
                                    j++;
                                }
                            }
                            catch (Exception e){  }
                            paintingGroup.getChildren().add(newPol);
                            break;
                        }
                    }
                    i++;
                }
            }
            catch (Exception e){            }
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

    @FXML
    public void quit(){
        System.exit(0);
    }

    public void saveImage(){
        imageView.setFitHeight(_origImageHeight);
        imageView.setFitWidth(_origImageWight);

        try
        {
            WritableImage snapshot = paintingGroup.snapshot(new SnapshotParameters(), null);
            File file = new File("image.jpg");
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
            ImageIO.write(renderedImage, "jpg", file);
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private Polygon createStar(double centerX, double centerY, double size){
        Polygon star = new Polygon();
        star.strokeWidthProperty().setValue(0);

        for (int i = 0; i < 10; i++){
            if (i%2 == 0) {
                star.getPoints().add(Math.sin(Math.toRadians(i*36 - 90)) * size + centerX); // х длинной
                star.getPoints().add(Math.cos(Math.toRadians(i*36 - 90)) * size + centerY); // у длинной
            }
            else {
                star.getPoints().add(Math.sin(Math.toRadians(i*36 - 90)) * (size / 3.0) + centerX); // х длинной
                star.getPoints().add(Math.cos(Math.toRadians(i*36 - 90)) * (size / 3.0) + centerY); // у длинной
            }
        }

        star.strokeWidthProperty().setValue(2);
        star.setFill(_color);
        star.strokeProperty().set(_color);

        return star;
    }

    private double calculateDistance(double startX, double startY, double endX, double endY){
        double dX = Math.abs(startX - endX);
        double dY = Math.abs(startY - endY);

        return Math.sqrt(dX*dX + dY*dY); // TODO резкость фильтр, офф ресайз при гауссиане
    }

    @FXML
    public void initialize(){
        _fileChooser = new FileChooser();
        _invisibleRectangle = new Rectangle();

        _invisibleRectangle.setOnMouseMoved(event -> {
            labelX.setText(String.valueOf(event.getX()));
            labelY.setText(String.valueOf(event.getY()));
        });

        _invisibleRectangle.setOnMouseDragged(event -> {
            if (event.getX() >= 0 &&
                    event.getY() >= 0 &&
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

        _invisibleRectangle.setOnMousePressed(event -> {
            if (event.getX() >= 0 && event.getY() >= 0) {
                _mousePressedX = (int) event.getX();
                _mousePressedY = (int) event.getY();
            }
        });

        _invisibleRectangle.setOnMouseReleased(event -> {
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
                    line.strokeWidthProperty().setValue(Integer.valueOf(textBoxSize.getText())*_scale);
                    paintingGroup.getChildren().add(line);
                }
                if (_mode == PaintModes.ellipse) {
                    double X = (_mousePressedX + _mouseReleasedX)/2.;
                    double Y = (_mousePressedY + _mouseReleasedY)/2.;

                    javafx.scene.shape.Ellipse ellipse = new Ellipse(X, Y, Math.abs(X - _mouseReleasedX),
                            Math.abs(Y - _mouseReleasedY));
                    ellipse.setFill(_color);
                    paintingGroup.getChildren().add(ellipse);
                }
                if (_mode == PaintModes.star){
                    double size = calculateDistance(_mousePressedX, _mousePressedY, _mouseReleasedX, _mouseReleasedY);
                    Polygon star = createStar(_mousePressedX, _mousePressedY, size);
                    paintingGroup.getChildren().add(star);
                }
            }
            paintingGroup.getChildren().remove(_invisibleRectangle);
            paintingGroup.getChildren().add(_invisibleRectangle);
        });

        labelMode.setText("tool: " + _mode.name());
        colorRectangle.setFill(_color);
        textBoxSize.setText("1");

        loadImageWithDialog();
    }
}
