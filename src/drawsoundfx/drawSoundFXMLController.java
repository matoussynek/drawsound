/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawsoundfx;

import drawsoundfx.utils.MappingThread;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rwmidi.MidiOutput;
import rwmidi.MidiOutputDevice;
import rwmidi.RWMidi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import com.jhlabs.image.GainFilter;

/**
 *
 * @author MatousDesktop
 */
public class drawSoundFXMLController implements Initializable {
    
    @FXML
    public Slider slider;
    public Button mappingButt;
    public ImageView imgView;
    private MidiOutput device;
    private boolean mapping = false;
    private MappingThread mt;
    private Thread thread;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        MidiOutputDevice devices[] = RWMidi.getOutputDevices();
        for (int i = 0; i < devices.length; i++) {
            System.out.println(i + ": " + devices[i].getName());
            if (devices[i].getName().contains("drawSound")){
                device = RWMidi.getOutputDevices()[i].createOutput();
            }
        }



        slider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                System.out.println(newValue.intValue());
                sendMessage(newValue.intValue());
            }
        });
    }
    @FXML
    public void mapCC(){

        if (mapping){
            mt.stop();
            mapping = false;
            mappingButt.setStyle("-fx-background-color: #DEDEDE;");
        }
        else{
            mt = new MappingThread(device, 1, 12);
            thread = new Thread(mt);
            thread.start();
            mapping = true;
            mappingButt.setStyle("-fx-background-color: #93BEDF;");
        }

    }

    @FXML
    public void selectImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image..");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            imgView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    private void sendMessage(int intValue) {

        device.sendController(1, 12, intValue);
    }


    public class MidiInputReceiver implements Receiver {

        public String name;

        public MidiInputReceiver(String name) {
            this.name = name;
        }

        public void send(MidiMessage msg, long timeStamp) {

            if (msg instanceof ShortMessage) {
                ShortMessage shortMessage = (ShortMessage) msg;

                int channel = shortMessage.getChannel();
                int pitch = shortMessage.getData1();
                int vel = shortMessage.getData2();
                System.out.println("Channel: " + channel);
                System.out.println("Pitch: " + pitch);
                System.out.println("vel: " + vel);

            }

        }

        public void close() {}

    }
}
