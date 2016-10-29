package controller.dialog;

import sample.InformationWindow;

/**
 * Created by Matus Cuper on 29.10.2016.
 *
 * Class for raising window after received message from client
 */
public class MessageReceiver {

    public MessageReceiver(String message, int fragmentSize) {
        InformationWindow.infoBox("Fragment size is " + fragmentSize + " bytes\n" + message, "Received message");
    }
}
