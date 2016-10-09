package model;

import model.fragment.Checksum;
import model.fragment.Fragment;
import model.fragment.Header;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Matus Cuper on 10.9.2016.
 *
 * ServerReceiver open UDP socket for sending packets to specific address and port
 */
public class ClientSender extends Thread {

    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    private String data;
    private int fragmentSize;
    private static Semaphore semaphore = new Semaphore(1);

    public ClientSender(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.data = null;
    }

    public ClientSender(String address, String port) {
        this.data = null;
        this.socket = null;
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
        this.startConnection();
        this.sendOneFragment(Header.HEADER_SIZE, 0, Fragment.START_CONNECTION, "");
        try {
            while (true) {
                while (data == null && socket != null) {
                    semaphore.acquire();
                    this.sendOneFragment(Header.HEADER_SIZE, 0, Fragment.HOLD_CONNECTION, "");
                    semaphore.release();
                    sleep(1000);
                }

                // TODO refactor code
                if (socket != null) {
                    semaphore.acquire();
                    this.send();
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

    public void send(String data, String size) {
        this.data = data;
        // TODO change message length into size - utility verifier
        this.fragmentSize = Integer.parseInt(size);
    }

    private synchronized void send() {
        int dataAndHeadSize = this.data.length() + Header.HEADER_SIZE;

        // TODO add number of fragments to process
        // Send first fragment, data incoming
        this.sendOneFragment(Header.HEADER_SIZE, 0, Fragment.DATA_FIRST, "");

        // TODO remove this from everywhere
        if (dataAndHeadSize > this.fragmentSize)
            this.sendData();
        else
            this.sendOneFragment(dataAndHeadSize, 1, Fragment.DATA_LAST, data);

        // Send last fragment, all data was sent
        this.sendOneFragment(Header.HEADER_SIZE, 0, Fragment.DATA_LAST, "");
    }

    private synchronized void sendData() {
        int fragmentDataSize = this.fragmentSize - Header.HEADER_SIZE;
        int frameSizedFragments = (this.data.length() / fragmentDataSize);
        int lastFragmentsSize = (this.data.length() % fragmentDataSize) + Header.HEADER_SIZE;

        // Break up data into chunks with specified size
        int index = 0;
        List<String> fragmentsData = new ArrayList<>();
        while (index < this.data.length()) {
            fragmentsData.add(this.data.substring(index, Math.min(index + fragmentDataSize, this.data.length())));
            index += fragmentDataSize;
        }

        // Send all data with same fragment size
        for (int i = 0; i < frameSizedFragments; ++i) {
            this.sendOneFragment(this.fragmentSize, i + 1, Fragment.DATA_SENT, fragmentsData.get(i));
        }

        // Send last data fragment only if fragment contains data
        if (lastFragmentsSize > Header.HEADER_SIZE)
            this.sendOneFragment(lastFragmentsSize, fragmentsData.size(), Fragment.DATA_SENT, fragmentsData.get(fragmentsData.size() - 1));
    }

    private synchronized void sendOneFragment(int fragmentSize, int fragmentSerialNumber, int fragmentType, String fragmentData) {
        Header header = new Header(fragmentSize, fragmentSerialNumber, fragmentType);
        Fragment fragment = new Fragment(header, new Checksum(header.toString() + fragmentData), fragmentData);
        DatagramPacket datagramPacket = new DatagramPacket(fragment.getBytes(), fragmentSize, this.address, this.port);
        this.sendDatagramPacket(datagramPacket);
    }

    private synchronized void sendDatagramPacket(DatagramPacket datagramPacket) {
        try {
            this.socket.send(datagramPacket);
        } catch (IOException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    public void stopConnection() {

        try {
            semaphore.acquire();
            this.sendOneFragment(Header.HEADER_SIZE, 0, Fragment.STOP_CONNECTION, "");
            semaphore.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        data = null;
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getFragmentSize() {
        return fragmentSize;
    }

    public void setFragmentSize(int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }
}
