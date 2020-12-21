package drawsoundfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class aboutFXMLController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Hyperlink githubLink;

    @FXML
    private Hyperlink youtubeLink;

    @FXML
    private AnchorPane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        pane.getStylesheets().add("/resources/styles/styles.css");

        titleLabel.setText("   DrawSound\n" +
                "by Matouš Synek");

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
    }
}
