package sample;

import javax.swing.*;

/**
 * Raise window with received message
 *
 * Created by Matus Cuper on 29.10.2016.
 */
public class MessageWindow {

    public static void infoBox(String infoMessage, String title) {
        JOptionPane.showMessageDialog(null, infoMessage, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
