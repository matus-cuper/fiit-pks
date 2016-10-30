package model;

import controller.dialog.FileReceiver;
import controller.dialog.MessageReceiver;
import model.fragment.CorruptedDataException;
import model.fragment.Fragment;
import model.fragment.Header;
import model.fragment.MyChecksum;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * Created by Matus Cuper on 10.9.2016.
 *
 * ServerReceiver open UDP socket for listening on specific address (localhost) and port
 */
public class ServerReceiver extends Thread {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private boolean listen;

    public ServerReceiver(String address, String port) {
        listen = true;
        try {
            this.address = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
        } catch (UnknownHostException e) {
            //TODO add logging
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            socket = new DatagramSocket(port);
            while (listen) {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[Header.SIZE], Header.SIZE);
                socket.receive(datagramPacket);
                Fragment fragment = new Fragment(datagramPacket.getData());

                try {
                    fragment.isValid(datagramPacket.getData());
                    sendDataOKFragment(datagramPacket, fragment);

                    if (dataIncoming(fragment.getHeader().getType()))
                        receiveData(datagramPacket, fragment);
                } catch (CorruptedDataException e) {
                    sendDataResentFragment(datagramPacket, fragment);
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            //TODO add logging
            if (listen)
                e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO add logging
        }
    }

    private boolean dataIncoming(int fragmentType) {
        return fragmentType == Fragment.DATA_FIRST_MESSAGE || fragmentType == Fragment.DATA_FIRST_FILE;
    }

    synchronized private void receiveData(DatagramPacket initialDatagramPacket, Fragment initialFragment) {
        int fragmentType = initialFragment.getHeader().getType();
        int chunkSize = initialFragment.getHeader().getLength();
        int lastChunkSize = 0;
        int chunksCounter;
        int chunksCount = initialFragment.getHeader().getSerialNumber();
        StringBuilder data = new StringBuilder();

        try {
            for (int i = 0; i <= chunksCount; ++i) {
                DatagramPacket datagramPacket = new DatagramPacket(new byte[chunkSize], chunkSize);
                socket.receive(datagramPacket);
                Fragment fragment = new Fragment(datagramPacket.getData());

                lastChunkSize = fragment.getHeader().getLength();

                // Handle last incoming fragment, fixed buffer size with empty bytes computes different checksum
                if (fragment.getHeader().getLength() != chunkSize) {
                    fragment = new Fragment(Arrays.copyOfRange(datagramPacket.getData(), 0, fragment.getHeader().getLength()));
                }

                try {
                    fragment.isValid(datagramPacket.getData());
                    sendDataOKFragment(datagramPacket, fragment);
                    data.append(new StringBuilder(new String( (fragment.getData() != null) ? fragment.getData() : new byte[1] )));
                } catch (CorruptedDataException e) {
                    sendDataResentFragment(datagramPacket, fragment);
                    --i;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO add logging
        }

        lastChunkSize = (lastChunkSize == Header.SIZE) ? chunkSize : lastChunkSize;
        chunksCounter = (lastChunkSize == chunkSize && initialFragment.getHeader().getSerialNumber() == 1) ? 1 : initialFragment.getHeader().getSerialNumber() + 1;

        if (fragmentType == Fragment.DATA_FIRST_MESSAGE)
            receiveMessage(String.valueOf(data).getBytes(), chunkSize, lastChunkSize, chunksCounter);
        else
            receiveFile(String.valueOf(data).getBytes(), chunkSize, lastChunkSize, chunksCounter);
    }

    synchronized private void receiveMessage(byte[] data, int fragmentSize, int lastFragmentSize, int chunkCounter) {
        new MessageReceiver(new String(data), fragmentSize, lastFragmentSize, chunkCounter);
    }

    synchronized private void receiveFile(byte[] data, int fragmentSize, int lastFragmentSize, int chunkCounter) {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[100], 100);
        String fileName;

        try {
            socket.receive(datagramPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Fragment fragment = new Fragment(datagramPacket.getData());
        fileName = new String(fragment.getData());
        FileWriter.setFileName(fileName);
        System.out.println("file name in server receiver " + FileWriter.getFileName());

        try {
            new FileWriter(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FileReceiver(fileName, fragmentSize, lastFragmentSize, chunkCounter);
    }

    synchronized private void sendDataOKFragment(DatagramPacket datagramPacket, Fragment fragment) throws IOException {
        Header header = new Header(fragment.getHeader().getLength(), fragment.getHeader().getSerialNumber(), Fragment.DATA_OK);
        Fragment newFragment = new Fragment(new MyChecksum(header.getBytes()), header.getBytes());
        DatagramPacket newDatagramPacket = new DatagramPacket(newFragment.getBytes(), Header.SIZE, datagramPacket.getAddress(), datagramPacket.getPort());

        if (socket != null)
            socket.send(newDatagramPacket);
    }

    synchronized private void sendDataResentFragment(DatagramPacket datagramPacket, Fragment fragment) throws IOException {
        Header header = new Header(fragment.getHeader().getLength(), fragment.getHeader().getSerialNumber(), Fragment.DATA_RESENT);
        Fragment newFragment = new Fragment(new MyChecksum(header.getBytes()), header.getBytes());
        DatagramPacket newDatagramPacket = new DatagramPacket(newFragment.getBytes(), Header.SIZE, datagramPacket.getAddress(), datagramPacket.getPort());

        if (socket != null)
            socket.send(newDatagramPacket);
    }

    public void interruptListening() {
        listen = false;
        socket.close();
    }
}
