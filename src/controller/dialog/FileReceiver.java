package controller.dialog;

import sample.InformationWindow;

/**
 * Created by Matus Cuper on 29.10.2016.
 *
 * Class for raising window after received file from client
 */
public class FileReceiver {

    public FileReceiver(String filename, int fragmentSize, int lastFragmentSize, int chunkCounter) {
        InformationWindow.infoBox("Fragment size is " + fragmentSize + " bytes\n"
                + "Last fragment size is " + lastFragmentSize + " bytes\n"
                + "Totally received " + chunkCounter + " fragments\n"
                + "File name is " + filename, "Received file");
    }
}
