package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.ClientSender;
import model.FileReader;
import model.ServerReceiver;
import model.Validator;
import model.fragment.ChunkCountExceeded;
import sample.ErrorWindow;
import sample.InformationWindow;

import javax.swing.*;
import java.io.IOException;

public class Controller {

    private ClientSender client = null;
    private ServerReceiver server = null;

    @FXML
    private TextField sentFragmentsLabel;
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
            InformationWindow.infoBox("Server start listening on port " + ServerPortField.getText(), "Listening started");
        } else
            ErrorWindow.errorBox("Wrong port number " + ServerPortField.getText() + " for server", "Bad port number");
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
            InformationWindow.infoBox("Client start sending fragment to server " + ClientHostField.getText() + ":" + ClientPortField.getText(), "Sending started");
        } else
            ErrorWindow.errorBox("Wrong port number " + ClientPortField.getText() + " for server or IP address " + ClientHostField.getText(), "Bad port number or server IP");
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
        sentFragmentsLabel.setText(String.valueOf(ClientSender.sentFragments));
    }

    @FXML
    public void handleClientSendButton() {
        if (Validator.isValidSize(ClientSizeField.getText())) {
            try {
                if (ClientFileField.getText().isEmpty()) {
                    client.send(ClientMessageField.getText().getBytes(), ClientSizeField.getText(), ClientSender.MESSAGE, ClientChecksumBox.isSelected());
                    InformationWindow.infoBox("Message was sent to server " + ClientHostField.getText() + ":" + ClientPortField.getText(), "Message was sent");
                }
                else {
                    try {
                        FileReader fileReader = new model.FileReader(ClientFileField.getText());
                        client.send(fileReader.getBytes(), ClientSizeField.getText(), ClientSender.FILE, ClientChecksumBox.isSelected());
                        InformationWindow.infoBox("File was sent to server " + ClientHostField.getText() + ":" + ClientPortField.getText(), "File was sent");
                    } catch (IOException e) {
                        ErrorWindow.errorBox("File " + ClientFileField.getText() + " was not found", "File not found");
                    }
                }
            } catch (ChunkCountExceeded e) {
                ErrorWindow.errorBox("Data is too large to send it in fragment with size " + ClientSizeField.getText(), "Small fragment size selected");
            }
        } else
            ErrorWindow.errorBox("Wrong fragment size " + ClientSizeField.getText() + "\nMust be between 10 and 65498", "Wrong fragment size");
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
