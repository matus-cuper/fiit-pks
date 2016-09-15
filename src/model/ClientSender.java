package model;

import model.packet.Checksum;
import model.packet.Header;
import model.packet.Packet;

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

    public ClientSender(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public ClientSender(String address, String port) {
        this.socket = null;
        this.port = Integer.parseInt(port);
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            //TODO add logging
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
        int dataAndHeadSize = data.length() + Header.HEADER_SIZE;
        // TODO change message length into size - utility verifier
        int frameSize = Integer.parseInt(size);
        if (dataAndHeadSize > frameSize) {
            this.sendFirstPacket(frameSize);
            this.sendData(data, frameSize);
        }
        else {
            this.sendFirstPacket(dataAndHeadSize);
            this.sendOnePacket(dataAndHeadSize, 0, 0, Packet.DATA_LAST, data);
        }
    }

    private void sendData(String data, int size) {
        int packetDataSize = size - Header.HEADER_SIZE;
        int frameSizedPackets = (data.length() / packetDataSize);
        int lastPacketSize = (data.length() % packetDataSize);
        int index = 0;
        List<String> packetsData = new ArrayList<>();

        while (index < data.length()) {
            packetsData.add(data.substring(index, Math.min(index + packetDataSize, data.length())));
            index += packetDataSize;
        }

        for (int i = 1; i < frameSizedPackets; i++) {
            this.sendOnePacket(size, size, i + 1, Packet.DATA_SENT, packetsData.get(i - 1));
        }

        // Send one but last packet
        this.sendOnePacket(size, lastPacketSize + Header.HEADER_SIZE, frameSizedPackets + 1, Packet.DATA_SENT, packetsData.get(frameSizedPackets - 1));
        // Send last packet
        this.sendOnePacket(lastPacketSize + Header.HEADER_SIZE, 0, 0, Packet.DATA_LAST, packetsData.get(frameSizedPackets));
    }

    private void sendOnePacket(int currentPacketSize, int nextPacketSize, int nextPacketSerialNumber, int packetType, String packetData) {
        Header header = new Header(nextPacketSize, nextPacketSerialNumber, packetType);
        Packet packet = new Packet(header, new Checksum(header.toString()), packetData);
        DatagramPacket datagramPacket = new DatagramPacket(packet.getBytes(), currentPacketSize, this.address, this.port);
        this.sendDatagramPacket(datagramPacket);
    }

    private void sendFirstPacket(int packetSize) {
        this.sendOnePacket(Header.HEADER_SIZE, packetSize, 1, Packet.DATA_FIRST, "");
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
}
