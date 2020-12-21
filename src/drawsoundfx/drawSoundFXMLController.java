/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawsoundfx;

import drawsoundfx.utils.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;


/**
 * @author Matous Synek
 */
public class drawSoundFXMLController implements Initializable {

    @FXML
    public Slider slider80;
    public Slider slider81;
    public Slider slider82;
    public Slider slider83;
    public Slider slider84;
    public Slider slider85;
    public Slider redFilterSlider;
    public Slider greenFilterSlider;
    public Slider blueFilterSlider;
    public Slider greyscaleSlider;
    public Slider edgeDistance;
    public Button mappingButt80;
    public Button mappingButt81;
    public Button mappingButt82;
    public Button mappingButt83;
    public Button mappingButt84;
    public Button mappingButt85;
    public Label label80;
    public Label label81;
    public Label label82;
    public Label label83;
    public Label label84;
    public Label label85;
    public Label imageLabel;
    public Label redFilterLabel;
    public Label greenFilterLabel;
    public Label blueFilterLabel;
    public Label greyscaleLabel;
    public Label edgeStrengthLabel;
    public ImageView imgView;
    public AnchorPane pane;
    public AnchorPane imageViewPane;
    public Button edgesButton;

    private Stage stage;
    private int redFilter = 100;
    private int greenFilter = 100;
    private int blueFilter = 100;
    private int greyscaleFilter = 0;

    private boolean edgesDisplayed = false;

    private String selectedExternalDevice = null;

    private BufferedImage originalImage;
    private BufferedImage displayedImage;
    private BufferedImage tempImage;

    private DrawSound drawSound = new DrawSound();

    public void setStage(Stage stage) {

        this.stage = stage;
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                drawSound.getMidiPort().shutdown();
                System.out.println("App closing...");
                System.exit(0);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        pane.getStylesheets().add("/resources/styles/styles.css");

        drawSound.initialize();

        slider80.setDisable(true);
        slider81.setDisable(true);
        slider82.setDisable(true);
        slider83.setDisable(true);
        slider84.setDisable(true);
        slider85.setDisable(true);

        edgeDistance.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                drawSound.setEdgeStrength(newValue.doubleValue());
                int percentage = (int) ((newValue.doubleValue() / edgeDistance.getMax())*100);
                edgeStrengthLabel.setText(String.valueOf(percentage)/*String.format("%.2f", newValue.doubleValue())*/);
            }
        });
        redFilterSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                redFilter = 100 - newValue.intValue();
                redFilterLabel.setText(String.valueOf(newValue.intValue()));
            }
        });
        greenFilterSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                greenFilter = 100 - newValue.intValue();
                greenFilterLabel.setText(String.valueOf(newValue.intValue()));
            }
        });
        blueFilterSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                blueFilter = 100 - newValue.intValue();
                blueFilterLabel.setText(String.valueOf(newValue.intValue()));
            }
        });
        greyscaleSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                greyscaleFilter = newValue.intValue();
                greyscaleLabel.setText(String.valueOf(newValue.intValue()));
            }
        });
    }

    @FXML
    public void mapCC80() {
        drawSound.mapCC("red", mappingButt80);
    }

    @FXML
    public void mapCC81() {
        drawSound.mapCC("green", mappingButt81);
    }

    @FXML
    public void mapCC82() {
        drawSound.mapCC("blue", mappingButt82);
    }

    @FXML
    public void mapCC83() {
        drawSound.mapCC("brightness", mappingButt83);
    }

    @FXML
    public void mapCC84() {
        drawSound.mapCC("diversity", mappingButt84);
    }

    @FXML
    public void mapCC85() {
        drawSound.mapCC("edges", mappingButt85);
    }

    @FXML
    public void quit() {
        Platform.exit();
    }

    private void CC80Change() {
        drawSound.sendMessage(80, drawSound.getrChannelCC());
        label80.setText(String.valueOf(drawSound.getrChannelCC()));
        slider80.setValue(drawSound.getrChannelCC());
    }

    private void CC81Change() {
        drawSound.sendMessage(81, drawSound.getgChannelCC());
        label81.setText(String.valueOf(drawSound.getgChannelCC()));
        slider81.setValue(drawSound.getgChannelCC());
    }

    private void CC82Change() {
        drawSound.sendMessage(82, drawSound.getbChannelCC());
        label82.setText(String.valueOf(drawSound.getbChannelCC()));
        slider82.setValue(drawSound.getbChannelCC());
    }

    private void CC83Change() {
        drawSound.sendMessage(83, drawSound.getBrightnessCC());
        label83.setText(String.valueOf(drawSound.getBrightnessCC()));
        slider83.setValue(drawSound.getBrightnessCC());
    }

    private void CC84Change() {
        drawSound.sendMessage(84, drawSound.getColorDiversityCC());
        label84.setText(String.valueOf(drawSound.getColorDiversityCC()));
        slider84.setValue(drawSound.getColorDiversityCC());
    }

    private void CC85Change() {
        drawSound.sendMessage(85, drawSound.getEdgesCC());
        label85.setText(String.valueOf(drawSound.getEdgesCC()));
        slider85.setValue(drawSound.getEdgesCC());
    }

    @FXML
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image..");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                String imgPath = selectedFile.getAbsolutePath().replaceAll("/", Matcher.quoteReplacement(File.separator));
                originalImage = ImageIO.read(new File(imgPath));
                imageLabel.setText(imgPath);
                displayImage(originalImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void exportImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export image to PNG");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(
                "Portable Network Graphics (PNG)", "png"));
        File file = fileChooser.showSaveDialog(stage.getScene().getWindow());
        if (file != null) {
            try {
                ImageIO.write(displayedImage, "png", file);
            } catch (IOException ex) {
                System.err.println("Error saving file..");
            }
        }
    }

    @FXML
    public void displayOriginal() {
        if (originalImage == null) {
            System.err.println("Image not selected...");
            return;
        }
        displayImage(originalImage);
        redFilterSlider.setValue(0);
        greenFilterSlider.setValue(0);
        blueFilterSlider.setValue(0);
        greyscaleSlider.setValue(0);
    }

    @FXML
    public void showEdges() {
        if (originalImage == null) {
            System.err.println("Image not selected...");
            return;
        }
        if (!edgesDisplayed) {
            tempImage = displayedImage;
            BufferedImage edges = drawSound.getEdgesImage(displayedImage);
            edgesButton.setStyle("-fx-background-color: #546a7b!important;-fx-text-fill:  #DEDEDE;");
            displayImage(edges);
            edgesDisplayed = true;
        } else {
            edgesDisplayed = false;
            edgesButton.setStyle("-fx-background-color: #CECECE!important;-fx-text-fill:  #585858;");
            displayImage(tempImage);
        }
    }

    @FXML
    public void updateEdgeDistance() {
        if (edgesDisplayed) {
            drawSound.setEdgeStrength(edgeDistance.getValue());
            BufferedImage edges = drawSound.getEdgesImage(tempImage);
            displayImage(edges);
        }

    }

    @FXML
    public void processEffects() {
        if (originalImage == null) {
            System.err.println("Image not selected...");
            return;
        }
        displayImage(ImageProcessing.createColorImage(originalImage, redFilter, greenFilter, blueFilter));
        displayImage(ImageProcessing.getGreyScaleImage(displayedImage, greyscaleFilter));
    }


    private void displayImage(BufferedImage image) {
        if (image == null) {
            System.err.println("Image not selected...");
            return;
        }
        imgView.setImage(ImageProcessing.convertToFxImage(image));
        centerImage();
        displayedImage = image;
        drawSound.processImage(displayedImage);
        CC80Change();
        CC81Change();
        CC82Change();
        CC83Change();
        CC84Change();
        CC85Change();
    }

    private void centerImage() {
        Image img = imgView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imgView.getFitWidth() / img.getWidth();
            double ratioY = imgView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if (ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imgView.setX((imgView.getFitWidth() - w) / 2 + 5);
            imgView.setY((imgView.getFitHeight() - h) / 2);

        }
    }

    @FXML
    public void setMidiOutput() {

        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("settingsFXML.fxml"));
            Parent parent = (Parent) root.load();
            Scene scene = new Scene(parent);
            final Stage settingsStage = new Stage();
            settingsStage.initStyle(StageStyle.UNDECORATED);
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.initOwner(stage);
            settingsStage.setScene(scene);
            settingsStage.setResizable(false);

            settingsFXMLController controller = root.getController();
            controller.setStage(settingsStage);
            controller.init(drawSound, drawSound.getOutputDeviceString());

            settingsStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void showAbout() {

        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("aboutFXML.fxml"));
            Parent parent = (Parent) root.load();
            Scene scene = new Scene(parent);
            final Stage aboutStage = new Stage();
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.initOwner(stage);
            aboutStage.setScene(scene);
            aboutStage.setResizable(false);
            aboutStage.getIcons().add(stage.getIcons().get(0));
            aboutStage.setTitle("About");
            aboutStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
