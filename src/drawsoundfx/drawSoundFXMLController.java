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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    public ImageView imgView;
    public AnchorPane pane;
    public AnchorPane imageViewPane;
    public Button redFilterButton;
    public Button greenFilterButton;
    public Button blueFilterButton;
    public Button greyscaleButton;
    public Button edgesButton;

    private Stage stage;
    private int redFilter = 0xFF0000;
    private int greenFilter = 0xFF00;
    private int blueFilter = 0xFF;

    private boolean edgesDisplayed = false;

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
            }
        });
    }

    @FXML
    public void mapCC80() {
        drawSound.mapCC(80, mappingButt80);
    }

    @FXML
    public void mapCC81() {
        drawSound.mapCC(81, mappingButt81);
    }

    @FXML
    public void mapCC82() {
        drawSound.mapCC(82, mappingButt82);
    }

    @FXML
    public void mapCC83() {
        drawSound.mapCC(83, mappingButt83);
    }

    @FXML
    public void mapCC84() {
        drawSound.mapCC(84, mappingButt84);
    }

    @FXML
    public void mapCC85() {
        drawSound.mapCC(85, mappingButt85);
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
    }

    @FXML
    public void filterRed() {
        if (originalImage == null) {
            System.err.println("Image not selected...");
            return;
        }
        if (redFilter == 0) {
            redFilter = 0xFF0000;
            redFilterButton.setStyle("-fx-background-color: #DEDEDE;-fx-text-fill:  #585858;");
        } else {
            redFilter = 0;
            redFilterButton.setStyle("-fx-background-color: #546a7b;-fx-text-fill:  #DEDEDE;");
        }
        int mask = 0xFF000000 + redFilter + greenFilter + blueFilter;
        System.out.println("red mask " + mask);
        displayImage(ImageProcessing.createColorImage(originalImage, mask));
    }

    @FXML
    public void filterGreen() {
        if (originalImage == null) {
            System.err.println("Image not selected...");
            return;
        }
        if (greenFilter == 0) {
            greenFilter = 0xFF00;
            greenFilterButton.setStyle("-fx-background-color: #DEDEDE;-fx-text-fill:  #585858;");
        } else {
            greenFilter = 0;
            greenFilterButton.setStyle("-fx-background-color: #546a7b;-fx-text-fill:  #DEDEDE;");
        }
        int mask = 0xFF000000 + redFilter + greenFilter + blueFilter;
        displayImage(ImageProcessing.createColorImage(originalImage, mask));
    }

    @FXML
    public void filterBlue() {
        if (originalImage == null) {
            System.err.println("Image not selected...");
            return;
        }
        if (blueFilter == 0) {
            blueFilter = 0xFF;
            blueFilterButton.setStyle("-fx-background-color: #DEDEDE;-fx-text-fill:  #585858;");
        } else {
            blueFilter = 0;
            blueFilterButton.setStyle("-fx-background-color: #546a7b;-fx-text-fill:  #DEDEDE;");
        }
        int mask = 0xFF000000 + redFilter + greenFilter + blueFilter;
        displayImage(ImageProcessing.createColorImage(originalImage, mask));
    }

    @FXML
    public void greyScale() {
        displayImage(ImageProcessing.getGreyScaleImage(originalImage));
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
            edgesButton.setStyle("-fx-background-color: #546a7b;-fx-text-fill:  #DEDEDE;");
            displayImage(edges);
            edgesDisplayed = true;
        } else {
            edgesDisplayed = false;
            edgesButton.setStyle("-fx-background-color: #DEDEDE;-fx-text-fill:  #585858;");
            displayImage(tempImage);
        }
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
    public void showAbout(){
        final Stage about = new Stage();
        about.setTitle("About");
        about.initModality(Modality.APPLICATION_MODAL);
        about.initOwner(stage);
        AnchorPane aboutPane = new AnchorPane();
        aboutPane.getStylesheets().add("/resources/styles/styles.css");

        AnchorPane designBorder = new AnchorPane();
        designBorder.setPrefSize(270,270);
        designBorder.setLayoutX(15);
        designBorder.setLayoutY(15);
        designBorder.setId("imagePane");
        aboutPane.getChildren().add(designBorder);

        ImageView aboutImageView = new ImageView();
        aboutImageView.setImage(stage.getIcons().get(0));
        aboutImageView.setFitHeight(64);
        aboutImageView.setFitWidth(64);
        aboutImageView.setX(118);
        aboutImageView.setY(40);
        aboutPane.getChildren().add(aboutImageView);

        Label aboutText = new Label();
        aboutText.setPrefSize(270, 50);
        aboutText.setLayoutX(15);
        aboutText.setLayoutY(125);
        aboutText.setAlignment(Pos.TOP_CENTER);
        aboutText.setText("   DrawSound\n" +
                          "by Matouš Synek");
        aboutPane.getChildren().add(aboutText);

        Label githubText = new Label();
        githubText.setPrefSize(90, 25);
        githubText.setLayoutX(70);
        githubText.setLayoutY(190);
        githubText.setText("GitHub.com");
        aboutPane.getChildren().add(githubText);

        Hyperlink githubLink = new Hyperlink();
        githubLink.setPrefSize(50, 25);
        githubLink.setLayoutX(175);
        githubLink.setLayoutY(190);
        githubLink.setText("link");
        githubLink.setAlignment(Pos.CENTER);
        githubLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/matoussynek/drawsound"));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                }
            }
        });
        aboutPane.getChildren().add(githubLink);

        Label youtubeText = new Label();
        youtubeText.setPrefSize(90, 25);
        youtubeText.setLayoutX(70);
        youtubeText.setLayoutY(230);
        youtubeText.setText("YouTube.com");
        aboutPane.getChildren().add(youtubeText);

        Hyperlink youtubeLink = new Hyperlink();
        youtubeLink.setPrefSize(50, 25);
        youtubeLink.setLayoutX(175);
        youtubeLink.setLayoutY(230);
        youtubeLink.setText("link");
        youtubeLink.setAlignment(Pos.CENTER);
        youtubeLink.setBorder(githubLink.getBorder());
        youtubeLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.youtube.com/channel/UClhcolDHv14KyoifGi6ufdg?view_as=subscriber"));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                }
            }
        });
        aboutPane.getChildren().add(youtubeLink);

        Scene dialogScene = new Scene(aboutPane, 290, 290);
        about.setScene(dialogScene);
        about.getIcons().add(stage.getIcons().get(0));
        about.setResizable(false);
        about.show();
    }
}
