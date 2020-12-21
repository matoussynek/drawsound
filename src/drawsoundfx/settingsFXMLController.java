package drawsoundfx;

import drawsoundfx.utils.DrawSound;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class settingsFXMLController implements Initializable {

    @FXML
    private AnchorPane pane;

    @FXML
    private RadioButton defaultButton;

    @FXML
    private RadioButton externalButton;

    @FXML
    private ListView<String> listView;
    private ObservableList<String> outputList;

    @FXML
    private Button applyOButton;

    @FXML
    private Button cancelOButton;

    @FXML
    private Button applyMButton;

    @FXML
    private Button cancelMButton;

    @FXML
    private ChoiceBox<Integer> redChannel;

    @FXML
    private ChoiceBox<Integer> greenChannel;

    @FXML
    private ChoiceBox<Integer> blueChannel;

    @FXML
    private ChoiceBox<Integer> brightnessChannel;

    @FXML
    private ChoiceBox<Integer> diversityChannel;

    @FXML
    private ChoiceBox<Integer> edgesChannel;

    @FXML
    private ChoiceBox<Integer> redNumber;

    @FXML
    private ChoiceBox<Integer> greenNumber;

    @FXML
    private ChoiceBox<Integer> blueNumber;

    @FXML
    private ChoiceBox<Integer> brightnessNumber;

    @FXML
    private ChoiceBox<Integer> diversityNumber;

    @FXML
    private ChoiceBox<Integer> edgesNumber;


    private List<Integer> channelsList = new ArrayList<>(Arrays. asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
    private List<Integer> ccNumbersList = new ArrayList<>();
    private ToggleGroup tg = new ToggleGroup();
    private Stage stage;
    private String selectedExternalDevice = null;
    private DrawSound drawSound;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 1; i <= 119; i++)
            ccNumbersList.add(i);
        pane.getStylesheets().add("/resources/styles/styles.css");
        pane.setId("imagePane");
        tg.getToggles().addAll(defaultButton, externalButton);

        defaultButton.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) ->{
                    if (new_val){
                        listView.setDisable(true);
                        selectedExternalDevice = null;
                    }
                    else{
                        listView.setDisable(false);
                    }
                });
        applyOButton.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (externalButton.isSelected()) {
                    if (listView.getSelectionModel().getSelectedItem() != null){
                        selectedExternalDevice = listView.getSelectionModel().getSelectedItem();
                        drawSound.setExternalMidiOutput(selectedExternalDevice);
                    }
                }
                else {
                    drawSound.setDefaultMidiOutput();
                }
                stage.close();
            }
        });
        cancelOButton.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                stage.close();
            }
        });

        applyMButton.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                drawSound.setCcMapping(redNumber.getSelectionModel().getSelectedItem().intValue(),
                        redChannel.getSelectionModel().getSelectedItem().intValue(),
                        greenNumber.getSelectionModel().getSelectedItem().intValue(),
                        greenChannel.getSelectionModel().getSelectedItem().intValue(),
                        blueNumber.getSelectionModel().getSelectedItem().intValue(),
                        blueChannel.getSelectionModel().getSelectedItem().intValue(),
                        brightnessNumber.getSelectionModel().getSelectedItem().intValue(),
                        brightnessChannel.getSelectionModel().getSelectedItem().intValue(),
                        diversityNumber.getSelectionModel().getSelectedItem().intValue(),
                        diversityChannel.getSelectionModel().getSelectedItem().intValue(),
                        edgesNumber.getSelectionModel().getSelectedItem().intValue(),
                        edgesChannel.getSelectionModel().getSelectedItem().intValue());
                stage.close();
            }
        });
        cancelMButton.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                stage.close();
            }
        });

        redChannel.setItems(FXCollections.<Integer>observableArrayList(channelsList));
        greenChannel.setItems(FXCollections.<Integer>observableArrayList(channelsList));
        blueChannel.setItems(FXCollections.<Integer>observableArrayList(channelsList));
        brightnessChannel.setItems(FXCollections.<Integer>observableArrayList(channelsList));
        diversityChannel.setItems(FXCollections.<Integer>observableArrayList(channelsList));
        edgesChannel.setItems(FXCollections.<Integer>observableArrayList(channelsList));
        redChannel.getSelectionModel().select(new Integer(1));
        greenChannel.getSelectionModel().select(new Integer(1));
        blueChannel.getSelectionModel().select(new Integer(1));
        brightnessChannel.getSelectionModel().select(new Integer(1));
        diversityChannel.getSelectionModel().select(new Integer(1));
        edgesChannel.getSelectionModel().select(new Integer(1));
        redNumber.setItems(FXCollections.<Integer>observableArrayList(ccNumbersList));
        greenNumber.setItems(FXCollections.<Integer>observableArrayList(ccNumbersList));
        blueNumber.setItems(FXCollections.<Integer>observableArrayList(ccNumbersList));
        brightnessNumber.setItems(FXCollections.<Integer>observableArrayList(ccNumbersList));
        diversityNumber.setItems(FXCollections.<Integer>observableArrayList(ccNumbersList));
        edgesNumber.setItems(FXCollections.<Integer>observableArrayList(ccNumbersList));
        redNumber.getSelectionModel().select(new Integer(80));
        greenNumber.getSelectionModel().select(new Integer(81));
        blueNumber.getSelectionModel().select(new Integer(82));
        brightnessNumber.getSelectionModel().select(new Integer(83));
        diversityNumber.getSelectionModel().select(new Integer(84));
        edgesNumber.getSelectionModel().select(new Integer(85));

    }

    public void init(DrawSound drawSound, String selectedExternalDevice){
        this.drawSound = drawSound;
        this.selectedExternalDevice = selectedExternalDevice;
        outputList = FXCollections.<String>observableArrayList(drawSound.getOutputDevices());
        listView.setItems(outputList);
        if (selectedExternalDevice != null){
            externalButton.setSelected(true);
            listView.getSelectionModel().select(selectedExternalDevice);
        }
        else
            defaultButton.setSelected(true);
        setCCMessages(drawSound.getCcMapping());
    }

    private void setCCMessages(Map<String, Pair<Integer, Integer>> ccMapping) {
        redChannel.getSelectionModel().select(ccMapping.get("red").getValue());
        greenChannel.getSelectionModel().select(ccMapping.get("green").getValue());
        blueChannel.getSelectionModel().select(ccMapping.get("blue").getValue());
        brightnessChannel.getSelectionModel().select(ccMapping.get("brightness").getValue());
        diversityChannel.getSelectionModel().select(ccMapping.get("diversity").getValue());
        edgesChannel.getSelectionModel().select(ccMapping.get("edges").getValue());
        redNumber.getSelectionModel().select(ccMapping.get("red").getKey());
        greenNumber.getSelectionModel().select(ccMapping.get("green").getKey());
        blueNumber.getSelectionModel().select(ccMapping.get("blue").getKey());
        brightnessNumber.getSelectionModel().select(ccMapping.get("brightness").getKey());
        diversityNumber.getSelectionModel().select(ccMapping.get("diversity").getKey());
        edgesNumber.getSelectionModel().select(ccMapping.get("edges").getKey());

    }
}
