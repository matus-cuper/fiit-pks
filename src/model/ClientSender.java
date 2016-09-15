package model;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import model.packet.Checksum;
import model.packet.Header;
import model.packet.Packet;

import javax.xml.crypto.Data;
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
        // TODO change message length into size
        this.sendFirstPacket(data.length(), Integer.parseInt(size));
        this.sendData(data, Integer.parseInt(size));
    }

    public void sendData(String data, int size) {
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
            Header commonHeader = new Header(size, i + 1, Packet.DATA_SENT);
            Packet commonPacket = new Packet(commonHeader, new Checksum(commonHeader.toString() + packetsData.get(i - 1)), packetsData.get(i - 1));
            DatagramPacket commonDatagramPacket = new DatagramPacket(commonPacket.getBytes(), size, this.address, this.port);
            this.sendDatagramPacket(commonDatagramPacket);
        }

        Header lastButOneHeader = new Header(lastPacketSize + Header.HEADER_SIZE, frameSizedPackets + 1, Packet.DATA_SENT);
        Packet lastButOnePacket = new Packet(lastButOneHeader, new Checksum(lastButOneHeader.toString() + packetsData.get(frameSizedPackets - 1)), packetsData.get(frameSizedPackets - 1));
        DatagramPacket lastButOneDatagramPacket = new DatagramPacket(lastButOnePacket.getBytes(), size, this.address, this.port);
        this.sendDatagramPacket(lastButOneDatagramPacket);

        Header lastHeader = new Header(0, 0, Packet.DATA_LAST);
        Packet lastPacket = new Packet(lastHeader, new Checksum(lastHeader.toString() + packetsData.get(frameSizedPackets)), packetsData.get(frameSizedPackets));
        // TODO fix data and size
        DatagramPacket lastDatagramPacket = new DatagramPacket(lastPacket.getBytes(), lastPacketSize + Header.HEADER_SIZE, this.address, this.port);
        this.sendDatagramPacket(lastDatagramPacket);
/*
        Packet packet = new Packet(new Header(message.length(), 2222, 10), new Checksum(""), message);
        DatagramPacket datagramPacket = new DatagramPacket(packet.getBytes(), packet.getBytes().length, this.address, this.port);
        try {
            this.socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void sendFirstPacket(int dataSize, int frameSize) {
        int packetSize = (dataSize + Header.HEADER_SIZE) > frameSize ? frameSize : (dataSize + Header.HEADER_SIZE);
        Header header = new Header(packetSize, 1, Packet.DATA_FIRST);
        Packet packet = new Packet(header, new Checksum(header.toString()), "");
        DatagramPacket datagramPacket = new DatagramPacket(packet.getBytes(), Header.HEADER_SIZE, this.address, this.port);
        this.sendDatagramPacket(datagramPacket);
    }

    private void sendLastPacket() {
        Header header = new Header(0, 0, Packet.DATA_LAST);
        Packet packet = new Packet(header, new Checksum(header.toString()), "");
        DatagramPacket datagramPacket = new DatagramPacket(packet.getBytes(), Header.HEADER_SIZE, this.address, this.port);
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
}
