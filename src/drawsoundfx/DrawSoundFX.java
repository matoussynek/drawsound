/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawsoundfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author MatousDesktop
 */
public class DrawSoundFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader root = new FXMLLoader(getClass().getResource("drawSoundFXML.fxml"));

        Scene scene = new Scene(root.load());

        stage.setScene(scene);
        stage.setTitle("DrawSound - A MIDI device based on an image input by Matous Synek");
        stage.getIcons().add(new Image("resources/icon.png"));
        stage.setResizable(false);

        drawSoundFXMLController controller = root.getController();
        controller.setStage(stage);

        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
