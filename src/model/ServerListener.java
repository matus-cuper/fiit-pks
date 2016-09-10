package model;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Matus Cuper on 10.9.2016.
 *
 * ServerListener open UDP socket for listening on specific address (localhost) and port
 */
public class ServerListener {

    private InetAddress address;
    private int port;

    public ServerListener(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public ServerListener(String address, String port) {
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
