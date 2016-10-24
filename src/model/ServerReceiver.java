package model;

import model.fragment.Fragment;
import model.fragment.Header;

import java.io.IOException;
import java.net.*;

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
                DatagramPacket packet = new DatagramPacket(new byte[Header.SIZE], Header.SIZE);
                socket.receive(packet);
                System.out.println( packet.getAddress() + " " + packet.getPort() + ": " + new Fragment(packet.getData()).getData().toString() ) ;
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
