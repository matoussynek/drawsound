/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawsoundfx;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rwmidi.MidiOutput;
import rwmidi.MidiOutputDevice;
import rwmidi.RWMidi;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author MatousDesktop
 */
public class drawSoundFXMLController implements Initializable {

    @FXML
    public Slider slider80;
    public Slider slider81;
    public Slider slider82;
    public Slider slider83;
    public Button mappingButt80;
    public Button mappingButt81;
    public Button mappingButt82;
    public Button mappingButt83;
    public Label label80;
    public Label label81;
    public Label label82;
    public Label label83;
    public Label imageLabel;
    public ImageView imgView;
    private MidiOutput device;
    private boolean mapping = false;
    private AtomicInteger currentCCNumber = new AtomicInteger(0);
    private MappingThread mt;
    private Thread thread;
    private Stage stage;
    private TeVirtualMIDIPort midiPort;
    private String midiPortName = "drawSound_midiPort";

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

    @FXML
    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image..");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            imgView.setImage(new Image(selectedFile.toURI().toString()));
            imageLabel.setText(selectedFile.toURI().toString());
        }
    }

    private void sendMessage(int CCNumber, int intValue) {

        device.sendController(1, CCNumber, intValue);
    }
}
