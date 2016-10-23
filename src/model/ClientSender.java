package model;

import model.fragment.Fragment;
import model.fragment.Header;
import model.fragment.MyChecksum;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Matus Cuper on 10.9.2016.
 *
 * ServerReceiver open UDP socket for sending packets to specific address and port
 */
public class ClientSender extends Thread {

    public static final int MESSAGE = 1;
    public static final int FILE = 2;

    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    private byte[] data;
    private int dataType;
    private int fragmentSize;
    private static Semaphore semaphore = new Semaphore(1);

    public ClientSender(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        data = null;
    }

    public ClientSender(String address, String port) {
        data = null;
        socket = null;
        this.port = Integer.parseInt(port);
        // TODO add utility verifier
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    private void startConnection() {
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    public void run() {
        // TODO unify
        startConnection();
        sendOneFragment(Header.HEADER_SIZE, 0, Fragment.START_CONNECTION, null);
        try {
            while (true) {
                while (data == null && socket != null) {
                    semaphore.acquire();
                    sendOneFragment(Header.HEADER_SIZE, 0, Fragment.HOLD_CONNECTION, null);
                    semaphore.release();
                    sleep(1000);
                }

                // TODO refactor code
                if (socket != null) {
                    semaphore.acquire();
                    send();
                    data = null;
                    semaphore.release();
                }

                if (socket == null)
                    break;
            }

        } catch (InterruptedException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    public void send(byte[] data, String fragmentSize, int dataType) {
        this.data = data;
        this.dataType = dataType;
        // TODO change message length into size - utility verifier
        this.fragmentSize = Integer.parseInt(fragmentSize);
    }

    private synchronized void send() {
        int dataAndHeadSize = this.data.length + Header.HEADER_SIZE;

        // TODO add number of fragments to process, is there need to null used dataType?
        // Send first fragment, data incoming
        if (this.dataType == MESSAGE)
            sendOneFragment(Header.HEADER_SIZE, 0, Fragment.DATA_FIRST_MESSAGE, null);
        else
            sendOneFragment(Header.HEADER_SIZE, 0, Fragment.DATA_FIRST_FILE, null);

        // TODO remove this from everywhere
        if (dataAndHeadSize > this.fragmentSize)
            sendData();
        else
            sendOneFragment(dataAndHeadSize, 1, Fragment.DATA_LAST, data);

        // Send last fragment, all data was sent
        sendOneFragment(Header.HEADER_SIZE, 0, Fragment.DATA_LAST, null);
    }

    private synchronized void sendData() {
        int fragmentDataSize = fragmentSize - Header.HEADER_SIZE;
        int frameSizedFragments = (data.length / fragmentDataSize);
        int lastFragmentsSize = (data.length % fragmentDataSize) + Header.HEADER_SIZE;

        // Break up data into chunks with specified size
        int index = 0;
        List<byte[]> fragmentsData = new ArrayList<>();
        while (index < data.length) {
            fragmentsData.add(Arrays.copyOfRange(data, index, Math.min(index + fragmentDataSize, data.length)));
            //(index, Math.min(index + fragmentDataSize, data.length));
            index += fragmentDataSize;
        }

        // Send all data with same fragment size
        for (int i = 0; i < frameSizedFragments; ++i) {
            sendOneFragment(fragmentSize, i + 1, Fragment.DATA_SENT, fragmentsData.get(i));
        }

        // Send last data fragment only if fragment contains data
        if (lastFragmentsSize > Header.HEADER_SIZE)
            sendOneFragment(lastFragmentsSize, fragmentsData.size(), Fragment.DATA_SENT, fragmentsData.get(fragmentsData.size() - 1));
    }

    private synchronized void sendOneFragment(int fragmentSize, int fragmentSerialNumber, int fragmentType, byte[] fragmentData) {
        Header header = new Header(fragmentSize, fragmentSerialNumber, fragmentType);

        byte[] tmp;
        if (fragmentData != null) {
            tmp = new byte[header.getLength() + fragmentData.length];
            System.arraycopy(header.getHeader(), 0, tmp, 0, header.getLength());
            System.arraycopy(fragmentData, 0, tmp, header.getLength(), fragmentData.length);
        }
        else {
            tmp = header.getHeader();
        }

        Fragment fragment = new Fragment(new MyChecksum(tmp), header, fragmentData);
        DatagramPacket datagramPacket = new DatagramPacket(fragment.getBytes(), fragmentSize, address, port);
        sendDatagramPacket(datagramPacket);
    }

    private synchronized void sendDatagramPacket(DatagramPacket datagramPacket) {
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    public void stopConnection() {

        try {
            semaphore.acquire();
            sendOneFragment(Header.HEADER_SIZE, 0, Fragment.STOP_CONNECTION, null);
            semaphore.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        data = null;
        if (socket != null) {
            socket.close();
            socket = null;
        }
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getFragmentSize() {
        return fragmentSize;
    }

    public void setFragmentSize(int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }
}
