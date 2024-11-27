package com.example.chat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private Button login;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        login.setOnAction(event -> login());
    }    

    private void login() {
        String usernameInput=username.getText();
        if (!usernameInput.isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatApp.fxml"));
                Stage stage = (Stage) login.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                ChatAppController controller = loader.getController();
                controller.setUsername(usernameInput);
                stage.setTitle("Banter Box");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
