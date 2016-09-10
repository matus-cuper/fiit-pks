package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.io.File;

public class Controller {

    @FXML
    private TextField ServerHostField;
    @FXML
    private TextField ServerPortField;
    @FXML
    private CheckBox ServerChecksumBox;



    @FXML
    public void handleServerListenButton(ActionEvent event) {
        System.out.println(ServerHostField.getText());
        System.out.println(ServerPortField.getText());
        System.out.println(ServerChecksumBox.isSelected());
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
