package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.ClientSender;
import model.ServerReceiver;
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
            ServerReceiver server = new ServerReceiver(ServerHostField.getText(), ServerPortField.getText());
            server.start();
        }
        else {
            System.out.println("Wrong input format");
            //TODO add some kind of warning message, what causing problem
        }
    }



    @FXML
    private TextField ClientHostField;
    @FXML
    private TextField ClientPortField;
    @FXML
    private TextField ClientSizeField;

    @FXML
    private Button ClientSendButton;
    @FXML
    private TextField ClientFileField;

    @FXML
    public void handleClientSendButton(ActionEvent event) {
        if (Validator.isValidHost(ClientHostField.getText(), ClientPortField.getText()) && Validator.isValidSize(ClientSizeField.getText())) {
            ClientSender client = new ClientSender(ClientHostField.getText(), ClientPortField.getText());
            client.start(ClientSizeField.getText());
        }
        else {
            System.out.println("Wrong input format");
            //TODO add some kind of warning message, what causing problem
        }
    }

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
