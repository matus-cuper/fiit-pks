package model;

import controller.dialog.FileReceiver;
import controller.dialog.MessageReceiver;
import model.fragment.*;

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
    private Data data;
    private boolean listen;

    public ServerReceiver(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public ServerReceiver(String address, String port) {
        listen = true;
        try {
            System.out.println(address);
            this.address = InetAddress.getByName(address);
            System.out.println(this.address.toString());
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
                // TODO read size of data from first fragment
                DatagramPacket datagramPacket = new DatagramPacket(new byte[Header.SIZE], Header.SIZE);
                socket.receive(datagramPacket);
                Fragment fragment = new Fragment(datagramPacket.getData());
                fragment.isValid(datagramPacket.getData());
                sendDataOKFragment(datagramPacket, fragment);

                if(dataIncoming(fragment.getHeader().getType()))
                    receiveData(datagramPacket, fragment);

//                System.out.println( datagramPacket.getAddress() + " " + datagramPacket.getPort()
//                        + " length " + fragment.getHeader().getLength()
//                        + " number " + fragment.getHeader().getSerialNumber()
//                        + " type " + fragment.getHeader().getType()
//                        + " data : " + fragment.getDataPrintable());
            }
        } catch (CorruptedDataException e) {
            e.printStackTrace();
            // TODO implement
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
                fragment.isValid(datagramPacket.getData());

                // Ugly hack
                data.append(new StringBuilder(new String( (fragment.getData() != null) ? fragment.getData() : new byte[1] )));
//                System.out.println( datagramPacket.getAddress() + " " + datagramPacket.getPort()
//                        + " length " + fragment.getHeader().getLength()
//                        + " number " + fragment.getHeader().getSerialNumber()
//                        + " type " + fragment.getHeader().getType()
//                        + " data : " + fragment.getDataPrintable());
            }
        } catch (CorruptedDataException e) {
            e.printStackTrace();
            // TODO implement
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
            // TODO add receiving file name
    }

    synchronized private void receiveMessage(byte[] data, int fragmentSize, int lastFragmentSize, int chunkCounter) {
        new MessageReceiver(new String(data), fragmentSize, lastFragmentSize, chunkCounter);
    }

    synchronized private void receiveFile(byte[] data, int fragmentSize, int lastFragmentSize, int chunkCounter) {
        new FileReceiver("test.txt", fragmentSize, lastFragmentSize, chunkCounter);
    }

    synchronized private void sendDataOKFragment(DatagramPacket datagramPacket, Fragment fragment) throws IOException {
        Header header = new Header(fragment.getHeader().getLength(), fragment.getHeader().getSerialNumber(), Fragment.DATA_OK);
        Fragment newFragment = new Fragment(new MyChecksum(header.getBytes()), header.getBytes());
        DatagramPacket newDatagramPacket = new DatagramPacket(newFragment.getBytes(), Header.SIZE, datagramPacket.getAddress(), datagramPacket.getPort());

        if (socket != null)
            socket.send(newDatagramPacket);
    }

    public void interruptListening() {
        listen = false;
        socket.close();
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
