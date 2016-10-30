package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.ClientSender;
import model.ServerReceiver;
import model.Validator;

import javax.swing.*;

public class Controller {

    private ClientSender client = null;
    private ServerReceiver server = null;

    @FXML
    private TextField ServerHostField;
    @FXML
    private TextField ServerPortField;
    @FXML
    private Button ServerListenButton;
    @FXML
    private Button ServerInterruptButton;
    @FXML
    private TextField ClientHostField;
    @FXML
    private TextField ClientPortField;
    @FXML
    private TextField ClientSizeField;
    @FXML
    private Button ClientConnectButton;
    @FXML
    private Button ClientDisconnectButton;
    @FXML
    private Button ClientSendButton;
    @FXML
    private TextArea ClientMessageField;
    @FXML
    private TextField ClientFileField;
    @FXML
    private CheckBox ClientChecksumBox;

    @FXML
    public void handleServerListenButton() {
        if (Validator.isValidHost(ServerHostField.getText(), ServerPortField.getText())) {
            if (server == null)
                server = new ServerReceiver(ServerHostField.getText(), ServerPortField.getText());
            server.start();
            switchDisablingServerButtons();
            // TODO add some kind of window for displaying successfully listening or logging
        } else {
            System.out.println("Wrong input format");
            // TODO add logging
            // TODO add some kind of warning message, what causing problem
        }
    }

    @FXML
    public void handleServerInterruptButton() {
        server.interruptListening();
        server.interrupt();
        try {
            server.join();
        } catch (InterruptedException e) {
            // TODO add logging
            e.printStackTrace();
        }
        server = null;
        switchDisablingServerButtons();
    }

    @FXML
    public void handleClientConnectButton() {
        if (Validator.isValidHost(ClientHostField.getText(), ClientPortField.getText())) {
            if (client == null)
                client = new ClientSender(ClientHostField.getText(), ClientPortField.getText());
            client.start();
            switchDisablingClientButtons();
            // TODO add some kind of window for displaying successfully sending or logging
        } else {
            System.out.println("Wrong input format");
            // TODO add logging
            // TODO add some kind of warning message, what causing problem
        }
    }

    @FXML
    public void handleClientDisconnectButton() {
        client.stopConnection();
        client.interrupt();
        try {
            client.join();
        } catch (InterruptedException e) {
            // TODO add logging
            e.printStackTrace();
        }
        client = null;
        switchDisablingClientButtons();
    }

    @FXML
    public void handleClientSendButton() {
        if (Validator.isValidSize(ClientSizeField.getText())) {
            if (ClientFileField.getText().isEmpty()) {
                client.send(ClientMessageField.getText().getBytes(), ClientSizeField.getText(), ClientSender.MESSAGE, ClientChecksumBox.isSelected());
                System.out.println("Message sent");
            }
            else {
                client.send((new model.FileReader(ClientFileField.getText())).getBytes(), ClientSizeField.getText(), ClientSender.FILE, ClientChecksumBox.isSelected());
                System.out.println("File sent");
            }
        } else {
            System.out.println("Wrong fragment size");
            // TODO add some kind of warning message, what causing problem
        }
    }

    @FXML
    private void handleClientFileField() {
        ClientFileField.setText("");
    }

    @FXML
    public void handleClientFileButton() {
        JFileChooser fileChooser = new JFileChooser();

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            updateClientFileField(fileChooser.getSelectedFile().getAbsolutePath());
    }

    private void updateClientFileField(String filePath) {
        ClientFileField.setText(filePath);
    }

    private void switchDisablingServerButtons() {
        if (ServerListenButton.isDisabled()) {
            ServerListenButton.setDisable(false);
            ServerInterruptButton.setDisable(true);
        }
        else {
            ServerListenButton.setDisable(true);
            ServerInterruptButton.setDisable(false);
        }
    }

    private void switchDisablingClientButtons() {
        if (ClientConnectButton.isDisabled()) {
            ClientConnectButton.setDisable(false);
            ClientDisconnectButton.setDisable(true);
            ClientSendButton.setDisable(true);
        }
        else {
            ClientConnectButton.setDisable(true);
            ClientDisconnectButton.setDisable(false);
            ClientSendButton.setDisable(false);
        }
    }
}
