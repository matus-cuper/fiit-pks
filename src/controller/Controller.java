package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.ClientSender;
import model.ServerReceiver;
import model.Validator;

import javax.swing.*;
import java.io.File;

public class Controller {

    private ClientSender client;

    @FXML
    private TextField ServerHostField;
    @FXML
    private TextField ServerPortField;
    @FXML
    private TextField ClientHostField;
    @FXML
    private TextField ClientPortField;
    @FXML
    private TextField ClientSizeField;
    @FXML
    private TextArea ClientMessageField;
    @FXML
    private TextField ClientFileField;
    // TODO Reoder methods
    @FXML
    public void handleServerListenButton(ActionEvent event) {
        if (Validator.isValidHost(ServerHostField.getText(), ServerPortField.getText())) {
            ServerReceiver server = new ServerReceiver(ServerHostField.getText(), ServerPortField.getText());
            server.start();
        } else {
            System.out.println("Wrong input format");
            // TODO add some kind of warning message, what causing problem
        }
    }

    @FXML
    public void handleClientConnectButton() {
        if (Validator.isValidHost(ClientHostField.getText(), ClientPortField.getText())) {
            client = new ClientSender(ClientHostField.getText(), ClientPortField.getText());
            // TODO need to kill thread, because it runs forever
            client.start();
            System.out.println("Connection started");
        } else {
            System.out.println("Wrong input format");
            // TODO add some kind of warning message, what causing problem
        }
    }

    @FXML
    public void handleClientSendButton() {
        if (Validator.isValidSize(ClientSizeField.getText())) {
            if (ClientFileField.getText().isEmpty()) {
                client.send(ClientMessageField.getText().getBytes(), ClientSizeField.getText(), ClientSender.MESSAGE);
                System.out.println("Message sent");
            }
            else {
                client.send((new model.FileReader(ClientFileField.getText())).getBytes(), ClientSizeField.getText(), ClientSender.FILE);
                System.out.println("File sent");
            }
        } else {
            System.out.println("Wrong fragment size");
            // TODO add some kind of warning message, what causing problem
        }
    }

    @FXML
    public void handleClientDisconnectButton() {
        client.stopConnection();
        System.out.println("Connection ended");
    }

    @FXML
    public void handleClientFileButton() {
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

    @FXML
    private void handleClientFileField() {
        ClientFileField.setText("");
    }
}
