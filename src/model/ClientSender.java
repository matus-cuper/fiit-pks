package model;

import model.packet.Checksum;
import model.packet.Header;
import model.packet.Packet;

import java.io.IOException;
import java.net.*;

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
            //TODO addlogging
            e.printStackTrace();
        }
    }

    public void send(String message, String size) {
        // TODO change message length into size
        Packet packet = new Packet(new Header(message.length(), 2222, 10), new Checksum(""), message);
        DatagramPacket datagramPacket = new DatagramPacket(packet.getBytes(), packet.getBytes().length, this.address, this.port);
        try {
            this.socket.send(datagramPacket);
        } catch (IOException e) {
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
