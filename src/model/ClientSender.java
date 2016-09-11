package model;

import model.packet.Header;

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

    public ClientSender(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public ClientSender(String address, String port) {
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

    public void start(String text) {
        try {
            DatagramSocket socket = new DatagramSocket();
            //byte[] data = "Hello hovno".getBytes();
            int size = Integer.parseInt(text);
            byte[] data = new Header(size, 2222, 10).getHeader();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
        } catch (SocketException e) {
            //TODO addlogging
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO addlogging
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
