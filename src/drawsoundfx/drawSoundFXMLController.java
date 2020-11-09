/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawsoundfx;

import drawsoundfx.utils.EdgeDetection;
import drawsoundfx.utils.TeVirtualMIDIPort;
import drawsoundfx.utils.MappingThread;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rwmidi.MidiOutput;
import rwmidi.MidiOutputDevice;
import rwmidi.RWMidi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    public Button redFilterButton;
    public Button greenFilterButton;
    public Button blueFilterButton;
    private MidiOutput device;
    private boolean mapping = false;
    private AtomicInteger currentCCNumber = new AtomicInteger(0);
    private MappingThread mt;
    private Thread thread;
    private Stage stage;
    private TeVirtualMIDIPort midiPort;
    private String midiPortName = "drawSoundMidiPort";
    private int[][][] colorHistogram;
    private int redChannel = 0;
    private int greenChannel = 0;
    private int blueChannel = 0;
    private int brightness = 0;
    private int uniqueCols = 0;
    private int edges = 0;
    private Set<Integer> uniqueColsSet = new HashSet<>();
    private int redFilter = 0xFF0000;
    private int greenFilter = 0xFF00;
    private int blueFilter = 0xFF;
    private int greyscale = 0;
    private double distance = 0.0;
    private boolean edgesDisplayed = false;

    private BufferedImage originalImage;
    private BufferedImage displayedImage;
    private BufferedImage tempImage;
    private EdgeDetection detector;

    public void setStage(Stage stage) {

        this.stage = stage;
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                midiPort.shutdown();
                System.out.println("App closing...");
                System.exit(0);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        midiPort = new TeVirtualMIDIPort(midiPortName);
        midiPort.start();

        System.out.println("Virtual MIDI port created as: " + midiPortName);

        MidiOutputDevice devices[] = RWMidi.getOutputDevices();
        for (int i = 0; i < devices.length; i++) {
//            System.out.println(i + ": " + devices[i].getName());
            if (devices[i].getName().contains(midiPortName)) {
                device = RWMidi.getOutputDevices()[i].createOutput();
                System.out.println("Connected to: " + devices[i].getName());
            }
        }


        slider80.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sendMessage(80, newValue.intValue());
                label80.setText(String.valueOf(newValue.intValue()));
            }
        });
        slider81.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sendMessage(81, newValue.intValue());
                label81.setText(String.valueOf(newValue.intValue()));
            }
        });
        slider82.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sendMessage(82, newValue.intValue());
                label82.setText(String.valueOf(newValue.intValue()));
            }
        });
        slider83.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sendMessage(83, newValue.intValue());
                label83.setText(String.valueOf(newValue.intValue()));
            }
        });
        slider84.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sendMessage(84, newValue.intValue());
                label84.setText(String.valueOf(newValue.intValue()));
            }
        });
        slider85.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sendMessage(85, newValue.intValue());
                label85.setText(String.valueOf(newValue.intValue()));
            }
        });
        edgeDistance.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                distance = newValue.doubleValue();
            }
        });
    }

    @FXML
    public void mapCC80() {
        mapCC(80, mappingButt80);
    }

    @FXML
    public void mapCC81() {
        mapCC(81, mappingButt81);
    }

    @FXML
    public void mapCC82() {
        mapCC(82, mappingButt82);
    }

    @FXML
    public void mapCC83() {
        mapCC(83, mappingButt83);
    }

    @FXML
    public void mapCC84() {
        mapCC(84, mappingButt84);
    }

    @FXML
    public void mapCC85() {
        mapCC(85, mappingButt85);
    }

    private void mapCC(int CCNumber, Button button) {

        if (CCNumber == currentCCNumber.get() || currentCCNumber.get() == 0) {
            if (mapping) {
                mt.stop();
                mapping = false;
                button.setStyle("-fx-background-color: #DEDEDE;");
                currentCCNumber.set(0);
            } else {
                currentCCNumber.set(CCNumber);
                mt = new MappingThread(device, 1, CCNumber);
                thread = new Thread(mt);
                thread.start();
                mapping = true;
                button.setStyle("-fx-background-color: #93BEDF;");

            }
        }

    }

    private void CC80Change() {
        sendMessage(80, redChannel);
        label80.setText(String.valueOf(redChannel));
        slider80.setValue(redChannel);
    }

    private void CC81Change() {
        sendMessage(81, greenChannel);
        label81.setText(String.valueOf(greenChannel));
        slider81.setValue(greenChannel);
    }

    private void CC82Change() {
        sendMessage(82, blueChannel);
        label82.setText(String.valueOf(blueChannel));
        slider82.setValue(blueChannel);
    }

    private void CC83Change() {
        sendMessage(83, brightness);
        label83.setText(String.valueOf(brightness));
        slider83.setValue(brightness);
    }

    private void CC84Change() {
        sendMessage(84, uniqueCols);
        label84.setText(String.valueOf(uniqueCols));
        slider84.setValue(uniqueCols);
    }

    private void CC85Change() {
        sendMessage(85, edges);
        label85.setText(String.valueOf(edges));
        slider85.setValue(edges);
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
                detector = new EdgeDetection(originalImage, distance);
                imageLabel.setText(imgPath);
                displayImage(originalImage);
            } catch (IOException e) {
                e.printStackTrace();
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
            redFilterButton.setStyle("-fx-background-color: #DEDEDE;");
        } else {
            redFilter = 0;
            redFilterButton.setStyle("-fx-background-color: #93BEDF;");
        }
        int mask = 0xFF000000 + redFilter + greenFilter + blueFilter;
        System.out.println("red mask " + mask);
        displayImage(createColorImage(originalImage, mask));
    }

    @FXML
    public void filterGreen() {
        if (originalImage == null) {
            System.err.println("Image not selected...");
            return;
        }
        if (greenFilter == 0) {
            greenFilter = 0xFF00;
            greenFilterButton.setStyle("-fx-background-color: #DEDEDE;");
        } else {
            greenFilter = 0;
            greenFilterButton.setStyle("-fx-background-color: #93BEDF;");
        }
        int mask = 0xFF000000 + redFilter + greenFilter + blueFilter;
        displayImage(createColorImage(originalImage, mask));
    }

    @FXML
    public void filterBlue() {
        if (originalImage == null) {
            System.err.println("Image not selected...");
            return;
        }
        if (blueFilter == 0) {
            blueFilter = 0xFF;
            blueFilterButton.setStyle("-fx-background-color: #DEDEDE;");
        } else {
            blueFilter = 0;
            blueFilterButton.setStyle("-fx-background-color: #93BEDF;");
        }
        int mask = 0xFF000000 + redFilter + greenFilter + blueFilter;
        displayImage(createColorImage(originalImage, mask));
    }

    @FXML
    public void greyScale() {
        displayImage(getGreyScaleImage(originalImage));
    }

    @FXML
    public void showEdges() {
        if (!edgesDisplayed) {
            detector.setSourceImage(displayedImage);
            detector.setDistance(distance);
            detector.detectEdges();
            tempImage = displayedImage;
            BufferedImage edges = detector.getOutputImage();
            displayImage(edges);
            System.out.println("Distance: " + distance);
            edgesDisplayed = true;
        }
        else{
            edgesDisplayed = false;
            displayImage(tempImage);
        }
    }

    private BufferedImage getGreyScaleImage(BufferedImage image){
        if (image == null) {
            System.err.println("Image not selected...");
            return null;
        }
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }

    private BufferedImage createColorImage(BufferedImage originalImage, int mask) {
        BufferedImage colorImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), originalImage.getType());

        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int pixel = originalImage.getRGB(x, y) & mask;
                colorImage.setRGB(x, y, pixel);
            }
        }

        return colorImage;
    }

    private void displayImage(BufferedImage image) {
        if (image == null) {
            System.err.println("Image not selected...");
            return;
        }
        imgView.setImage(convertToFxImage(image));
        displayedImage = image;
        processImage();
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }

    private void sendMessage(int CCNumber, int intValue) {

        device.sendController(1, CCNumber, intValue);
    }

    private void processImage() {
        System.out.println("Start processing...");
        createHistogram(displayedImage);
        System.out.println("End processing...");
    }

    private void createHistogram(BufferedImage image) {
        int brightnessSum = 0;
        colorHistogram = new int[4][4][4];
        redChannel = 0;
        greenChannel = 0;
        blueChannel = 0;
        uniqueColsSet = new HashSet<>();
        System.out.println("Start image buffering...");
        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getRGB(x, y);
                uniqueColsSet.add(color);
                int alpha = (color & 0xff000000) >> 24;
                int red = (color & 0x00ff0000) >> 16;
                int green = (color & 0x0000ff00) >> 8;
                int blue = color & 0x000000ff;
                colorHistogram[red / 64][green / 64][blue / 64]++;
                int grayscalePixel = (int) ((0.21 * red) + (0.71 * green) + (0.07 * blue));
                brightnessSum += grayscalePixel;
            }
        for (int i = 0; i < colorHistogram.length; i++)
            for (int j = 0; j < colorHistogram[i].length; j++)
                for (int p = 0; p < colorHistogram[i][j].length; p++) {
                    redChannel += i * colorHistogram[i][j][p];
                    greenChannel += j * colorHistogram[i][j][p];
                    blueChannel += p * colorHistogram[i][j][p];
                }
        System.out.println("End image buffering...");
        int colorSum = redChannel + blueChannel + greenChannel;
        redChannel = (int) map(redChannel, 0, colorSum, 0, 127);
        greenChannel = (int) map(greenChannel, 0, colorSum, 0, 127);
        blueChannel = (int) map(blueChannel, 0, colorSum, 0, 127);
        brightness = (int) map((brightnessSum / (image.getWidth() * image.getHeight())), 0, 255, 0, 127);
        uniqueCols = (int) map(uniqueColsSet.size(), 0, (image.getWidth() * image.getHeight()), 0, 127);
        detector.setDistance(distance);
        detector.setSourceImage(image);
        detector.detectEdges();
        edges = (int) map(detector.getEdges(), 0, (image.getWidth() * image.getHeight()), 0, 127);
        CC80Change();
        CC81Change();
        CC82Change();
        CC83Change();
        CC84Change();
        CC85Change();
        System.out.println("R: " + redChannel + " G: " + greenChannel + " B: " + blueChannel + " BRIGHTNESS: " + brightness + " CC: " + uniqueColsSet.size() + "/" + (image.getWidth() * image.getHeight()));
    }

    private float map(float var, float iMin, float iMax, float oMin, float oMax) {
        return oMin + (oMax - oMin) * ((var - iMin) / (iMax - iMin));
    }
}
