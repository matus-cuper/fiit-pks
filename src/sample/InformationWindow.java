package sample;

import javax.swing.*;

/**
 * Raise window with some information
 *
 * Created by Matus Cuper on 29.10.2016.
 */
public class InformationWindow {

    public static void infoBox(String info, String title) {
        JOptionPane.showMessageDialog(null, info, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
