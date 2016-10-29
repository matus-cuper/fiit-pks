package controller.listeners;

import sample.MessageWindow;

/**
 * Class for raising window after received message from client
 *
 * Created by Matus Cuper on 29.10.2016.
 */
public class MessageReceiver {

    public MessageReceiver(String message) {
        MessageWindow.infoBox(message, "Received message");
    }
}
