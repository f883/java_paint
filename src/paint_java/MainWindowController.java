package paint_java;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
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
    private String _currentImagePath;
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
    public ImageView pencilIcon;
    @FXML
    public ImageView lineIcon;
    @FXML
    public ImageView rectangleIcon;
    @FXML
    public ImageView ellipseIcon;
    @FXML
    public ImageView ereaserIcon;
    @FXML
    public ImageView starIcon;

    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Label scaleLabel;
    @FXML
    public TextField sizeTextBox;
    @FXML
    public Group paintingGroup;
    @FXML
    public ImageView imageView;
    @FXML
    public Label labelX;
    @FXML
    public Label labelY;
    @FXML
    public Label labelMode;
    @FXML
    public javafx.scene.shape.Rectangle colorRectangle;
    @FXML
    public AnchorPane pane;

    private double getSize(){
        double res;
        try{
            res = Double.valueOf(sizeTextBox.getText());
        }
        catch (Exception e){
            return 2;
        }

        if (res > 0 && res < 400){
            return res*_scale;
        }
        else
            return 2;
    }

    private void mergeImage(){
        imageView.setFitHeight(_origImageHeight);
        imageView.setFitWidth(_origImageWight);
        double lastHscrollPosition = scrollPane.getHvalue();
        double lastVscrollPosition = scrollPane.getVvalue();

        WritableImage snapshot = paintingGroup.snapshot(new SnapshotParameters(), null);

        paintingGroup.getChildren().clear();

        imageView.setEffect(null);
        _invisibleRectangle.setEffect(null);
        _image = snapshot;
        imageView.setImage(_image);

        paintingGroup.getChildren().add(imageView);
        paintingGroup.getChildren().add(_invisibleRectangle);

        double newScale = 1;
        while (_scale < newScale){ // восстановка масштаба
            imageView.setFitHeight(imageView.getFitHeight() * 0.5);
            newScale *= 0.5;
        }

        scrollPane.setHvalue(lastHscrollPosition); // восстановка положения в scrollPane
        scrollPane.setVvalue(lastVscrollPosition);
    }

    public void loadImage(String fileName){
        paintingGroup.getChildren().clear();
        paintingGroup.getChildren().add(imageView);
        _image = new Image("file:" + fileName);
        imageView.setFitHeight(_image.getHeight());
        imageView.setFitWidth(_image.getWidth());
        imageView.setImage(_image);
        _scale = 1;

        _invisibleRectangle.setHeight(imageView.getImage().getHeight());
        _invisibleRectangle.setWidth(imageView.getImage().getWidth());
        _invisibleRectangle.setFill(new Color(0, 0, 1, 0));
        paintingGroup.getChildren().add(_invisibleRectangle);

        _origImageHeight = _image.getHeight();
        _origImageWight = _image.getWidth();
    }

    @FXML
    public void saveImageWithDialog(){
        _fileChooser.setInitialFileName("image.png"); // "test.png"
        _currentImagePath = _fileChooser.showSaveDialog(null).getPath();
        saveImage();
    }

    public void saveImage(){
        while (_scale < 1)
            setScale(2);

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
                node.setEffect(new GaussianBlur(30));
                i++;
            }
        }
        catch (Exception e){            }
        mergeImage();
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
        mergeImage();

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
    }

    @FXML
    private void setSharpness(){
        mergeImage();
        PixelReader pr = _image.getPixelReader();
        WritableImage newImage = new WritableImage(pr, (int) _image.getWidth(), (int) _image.getHeight());
        PixelWriter newImagePW = newImage.getPixelWriter();

        double[] filter = {-1, -1, -1, -1, 9, -1, -1, -1, -1};

        for (int y = 1; y < _image.getHeight() - 1; y++) {
            for (int x = 1; x < _image.getWidth() - 1; x++) {
                double red = 0;
                double green = 0;
                double blue = 0;

                Color[] c = new Color[9];

                c[0] = pr.getColor(x - 1, y - 1);
                c[1] = pr.getColor(x, y - 1);
                c[2] = pr.getColor(x + 1, y - 1);

                c[3] = pr.getColor(x - 1, y);
                c[4] = pr.getColor(x, y);
                c[5] = pr.getColor(x + 1, y);

                c[6] = pr.getColor(x - 1, y + 1);
                c[7] = pr.getColor(x, y + 1);
                c[8] = pr.getColor(x + 1, y + 1);

                for (int i = 0; i < 9; i++){
                    red += c[i].getRed() * filter[i];
                }
                for (int i = 0; i < 9; i++){
                    green += c[i].getGreen() * filter[i];
                }
                for (int i = 0; i < 9; i++){
                    blue += c[i].getBlue() * filter[i];
                }

                if (red > 1)
                    red = 1;
                if (green > 1)
                    green = 1;
                if (blue > 1)
                    blue = 1;

                if (red < 0)
                    red = 0;
                if (green < 0)
                    green = 0;
                if (blue < 0)
                    blue = 0;

                newImagePW.setColor(x, y, new Color(red, green, blue, 1));
            }
        }

        _image = newImage;
        imageView.setImage(newImage);
    }

    @FXML
    private void setDistraction(){
        mergeImage();
        PixelReader pr = _image.getPixelReader();
        WritableImage newImage = new WritableImage(pr, (int) _image.getWidth(), (int) _image.getHeight());
        PixelWriter newImagePW = newImage.getPixelWriter();

        double[] filter = {1./9., 1./9., 1./9., 1./9., 1./9., 1./9., 1./9., 1./9., 1./9.};
        for (int y = 1; y < _image.getHeight() - 1; y++) {
            for (int x = 1; x < _image.getWidth() - 1; x++) {
                double red = 0;
                double green = 0;
                double blue = 0;

                Color[] c = new Color[9];

                c[0] = pr.getColor(x - 1, y - 1);
                c[1] = pr.getColor(x, y - 1);
                c[2] = pr.getColor(x + 1, y - 1);

                c[3] = pr.getColor(x - 1, y);
                c[4] = pr.getColor(x, y);
                c[5] = pr.getColor(x + 1, y);

                c[6] = pr.getColor(x - 1, y + 1);
                c[7] = pr.getColor(x, y + 1);
                c[8] = pr.getColor(x + 1, y + 1);

                for (int i = 0; i < 9; i++){
                    red += c[i].getRed() * filter[i];
                }
                for (int i = 0; i < 9; i++){
                    green += c[i].getGreen() * filter[i];
                }
                for (int i = 0; i < 9; i++){
                    blue += c[i].getBlue() * filter[i];
                }

                if (red > 1)
                    red = 1;
                if (green > 1)
                    green = 1;
                if (blue > 1)
                    blue = 1;

                if (red < 0)
                    red = 0;
                if (green < 0)
                    green = 0;
                if (blue < 0)
                    blue = 0;

                newImagePW.setColor(x, y, new Color(red, green, blue, 1));
            }
        }

        _image = newImage;
        imageView.setImage(newImage);
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

    @FXML
    public void onPlusViewButtonPressed(){
        if (_scale < 1) {
            setScale(2);
        }
    }

    @FXML
    public void onMinusViewButtonPressed(){
        if (_scale > 0.1) {
            setScale(0.5);
        }
    }

    private void setScale(double coefficient){
        _scale = _scale  * coefficient;
        scaleLabel.setText(String.valueOf(_scale * 100) + "%");

        imageView.setFitHeight(imageView.getFitHeight() * coefficient);

        try{
            int i = 0;
            while (true){
                Object t = paintingGroup.getChildren().get(i);
                switch (t.getClass().getSimpleName()){
                    case "Line":{
                        Line u = (Line)t;
                        u.setStartX(u.getStartX() * coefficient);
                        u.setStartY(u.getStartY() * coefficient);
                        u.setEndX(u.getEndX() * coefficient);
                        u.setEndY(u.getEndY() * coefficient);
                        u.setStrokeWidth(u.getStrokeWidth() * coefficient);
                        break;
                    }
                    case "Rectangle":{
                        Rectangle u = (Rectangle) t;
                        u.setX(u.getX() * coefficient);
                        u.setY(u.getY()  * coefficient);
                        u.setHeight(u.getHeight()  * coefficient);
                        u.setWidth(u.getWidth()  * coefficient);
                        break;
                    }
                    case "Ellipse":{
                        Ellipse u = (Ellipse)t;
                        u.setCenterX(u.getCenterX() * coefficient);
                        u.setCenterY(u.getCenterY() * coefficient);
                        u.setRadiusX(u.getRadiusX() * coefficient);
                        u.setRadiusY(u.getRadiusY() * coefficient);
                        break;
                    }
                    case "Polygon":{
                        Polygon u = (Polygon)t;
                        Polygon np = new Polygon();

                        np.setFill(u.getFill());
                        np.setStroke(u.getStroke());

                        for (int j = 0; j < 20; j++){
                            double val = u.getPoints().get(j);
                            np.getPoints().add(val * coefficient);
                        }

                        paintingGroup.getChildren().remove(i);
                        paintingGroup.getChildren().add(i, np);
                        break;
                    }
                }
                i++;
            }
        }
        catch (Exception e){}
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
    public void quit(){
        System.exit(0);
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
                star.getPoints().add(Math.sin(Math.toRadians(i*36 - 90)) * (size / 3.0) + centerX); // х короткой
                star.getPoints().add(Math.cos(Math.toRadians(i*36 - 90)) * (size / 3.0) + centerY); // у короткой
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

        return Math.sqrt(dX*dX + dY*dY);
    }

    @FXML
    public void initialize(){
        pencilIcon.setImage(new Image("file:" +
                "icons/pencil.png"));
        lineIcon.setImage(new Image("file:" +
                "icons/line.png"));
        rectangleIcon.setImage(new Image("file:" +
                "icons/shapeimage_17.png"));
        ellipseIcon.setImage(new Image("file:" +
                "icons/circle.png"));
        ereaserIcon.setImage(new Image("file:" +
                "icons/eraser.png"));
        starIcon.setImage(new Image("file:" +
                "icons/star.png"));

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

                    line.strokeWidthProperty().setValue(getSize());
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
                    line.strokeWidthProperty().setValue(getSize());
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
        sizeTextBox.setText("1");

        loadImageWithDialog();
    }
}
