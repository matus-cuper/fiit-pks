package sample;

import javax.swing.*;

/**
 * Created by Matus Cuper on 30.10.2016.
 *
 * Raise window with some error
 */
public class ErrorWindow {

    public static void errorBox(String error, String title) {
        JOptionPane.showMessageDialog(null, error, title, JOptionPane.ERROR_MESSAGE);
    }
}
