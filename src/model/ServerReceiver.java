package model;

import java.io.IOException;
import java.net.*;

/**
 * Created by Matus Cuper on 10.9.2016.
 *
 * ServerReceiver open UDP socket for listening on specific address (localhost) and port
 */
public class ServerReceiver {

    private InetAddress address;
    private int port;

    public ServerReceiver(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public ServerReceiver(String address, String port) {
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

    public void start() {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            for ( ;; ) {
                DatagramPacket packet = new DatagramPacket(new byte[10], 10);
                socket.receive(packet);
                System.out.println( packet.getAddress() + " " + packet.getPort() + ": " + new String(packet.getData()) ) ;
            }
        } catch (SocketException e) {
            //TODO add logging
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO add logging
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
