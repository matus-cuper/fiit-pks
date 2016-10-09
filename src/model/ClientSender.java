package model;

import model.fragment.Checksum;
import model.fragment.Fragment;
import model.fragment.Header;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matus Cuper on 10.9.2016.
 *
 * ServerReceiver open UDP socket for sending packets to specific address and port
 */
public class ClientSender {

    private InetAddress address;
    private int port;
    private DatagramSocket socket;
    private String data;

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

    public void start() {
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    public void send(String data, String size) {
        this.data = data;
        this.send(size);
    }

    private void send(String size) {
        int dataAndHeadSize = this.data.length() + Header.HEADER_SIZE;
        // TODO change message length into size - utility verifier
        int frameSize = Integer.parseInt(size);

        // Send first fragment, data incoming
        this.sendOneFragment(Header.HEADER_SIZE, 0, Fragment.DATA_FIRST, "");

        if (dataAndHeadSize > frameSize)
            this.sendData(frameSize);
        else
            this.sendOneFragment(dataAndHeadSize, 1, Fragment.DATA_LAST, data);

        // Send last fragment, all data was sent
        this.sendOneFragment(Header.HEADER_SIZE, 0, Fragment.DATA_LAST, "");
    }

    private void sendData(int size) {
        int fragmentDataSize = size - Header.HEADER_SIZE;
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
            this.sendOneFragment(size, i + 1, Fragment.DATA_SENT, fragmentsData.get(i));
        }

        // Send last data fragment only if fragment contains data
        if (lastFragmentsSize > Header.HEADER_SIZE)
            this.sendOneFragment(lastFragmentsSize, fragmentsData.size(), Fragment.DATA_SENT, fragmentsData.get(fragmentsData.size() - 1));

        data = null;
    }

    private void sendOneFragment(int fragmentSize, int fragmentSerialNumber, int fragmentType, String fragmentData) {
        Header header = new Header(fragmentSize, fragmentSerialNumber, fragmentType);
        Fragment fragment = new Fragment(header, new Checksum(header.toString() + fragmentData), fragmentData);
        DatagramPacket datagramPacket = new DatagramPacket(fragment.getBytes(), fragmentSize, this.address, this.port);
        this.sendDatagramPacket(datagramPacket);
    }

    private void sendDatagramPacket(DatagramPacket datagramPacket) {
        try {
            this.socket.send(datagramPacket);
        } catch (IOException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    public void stop() {
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
}
