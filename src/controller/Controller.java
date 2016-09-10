package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.ServerListener;
import model.Validator;

import javax.swing.*;
import java.io.File;

public class Controller {

    @FXML
    private TextField ServerHostField;
    @FXML
    private TextField ServerPortField;


    @FXML
    public void handleServerListenButton(ActionEvent event) {
        if (Validator.isValidHost(ServerHostField.getText(), ServerPortField.getText())) {
            ServerListener server = new ServerListener(ServerHostField.getText(), ServerPortField.getText());
            System.out.println(server.getAddress().toString());
            System.out.println(server.getPort());
        }
        else {
            System.out.println("Wrong input format");
            //TODO add some kind of warning message, what causing problem
        }
    }





    @FXML
    private TextField ClientFileField;

    @FXML
    public void handleClientFileButton(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            updateClientFileField(selectedFile.getAbsolutePath());
        }
    }

    private void updateClientFileField(String filePath) {
        ClientFileField.setText(filePath);
    }
}
