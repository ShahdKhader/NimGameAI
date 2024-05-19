package com.example.ai1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
public class HelloController {

    @FXML
    private Button start;

    @FXML
    void startClicked(ActionEvent event) {
        try{
            Parent root;
            root = FXMLLoader.load(getClass().getResource("choosegame.fxml"));
            Stage stage = (Stage) start.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
