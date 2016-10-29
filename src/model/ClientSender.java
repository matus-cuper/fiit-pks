package model;

import model.fragment.Data;
import model.fragment.Fragment;
import model.fragment.Header;
import model.fragment.MyChecksum;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Semaphore;

/**
 * Created by Matus Cuper on 10.9.2016.
 *
 * ServerReceiver open UDP socket for sending packets to specific address and port
 */
public class ClientSender extends Thread {

    public static final int MESSAGE = Fragment.DATA_FIRST_MESSAGE;
    public static final int FILE = Fragment.DATA_FIRST_FILE;

    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private Data data;
    private static Semaphore semaphore = new Semaphore(1);

    public ClientSender(String address, String port) {
        data = null;
        socket = null;
        if (Validator.isValidHost(address, port))
        {
            this.port = Integer.parseInt(port);
            try {
                this.address = InetAddress.getByName(address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                // TODO add logging
            }
        }
    }

    public void run() {
        // TODO unify
        startConnection();

        try {
            while (true) {
                while (data == null && socket != null) {
                    semaphore.acquire();
                    do {
                        sendEmptyFragment(Fragment.HOLD_CONNECTION);
                    } while (!receivedDataOKFragment());
                    semaphore.release();
                    sleep(1000);
                }

                if (socket == null)
                    break;
                else {
                    semaphore.acquire();
                    send();
                    data = null;
                    semaphore.release();
                }
            }
        } catch (InterruptedException e) {
            // TODO add logging
            e.printStackTrace();
        } catch (IOException e) {
            // TODO add logging
            e.printStackTrace();
        }
        stopConnection();
    }

    private void startConnection() {
        try {
            this.socket = new DatagramSocket();

            do {
                sendEmptyFragment(Fragment.START_CONNECTION);
            } while (!receivedDataOKFragment());
        } catch (SocketException e) {
            // TODO add logging
            e.printStackTrace();
        } catch (IOException e) {
            // TODO add logging
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        if (socket != null) {
            try {
                semaphore.acquire();
                sendEmptyFragment(Fragment.STOP_CONNECTION);
                semaphore.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        data = null;
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public void send(byte[] data, String fragmentSize, int dataType, boolean isDataCorrupted) {
        if (Validator.isValidSize(fragmentSize)) {
            this.data = new Data(data, Integer.parseInt(fragmentSize) - Header.SIZE, dataType, isDataCorrupted);
            if (!this.data.isValid())
                this.data = null;
        }
        if (data == null) {
            System.out.println("Error occurred invalid parameters");
            // TODO add logging
        }
    }

    private synchronized void send() {
        int dataAndHeadSize = data.getDataLength() + Header.SIZE;

        // Send first fragment corrupted if it is needed by user
        if (data.isDataCorrupted())
            sendCorruptedFragment(data.getDataChunkSize() + Header.HEADER_SIZE - 1, data.getDataChunksCount(), Fragment.DATA_FIRST_FILE);
        data.setCorruptedData(false);

        // Send first fragment with metadata about incoming data stream
        if (data.getDataType() == MESSAGE)
            sendMetadataFragment(data.getDataChunkSize() + Header.HEADER_SIZE - 1, data.getDataChunksCount(), Fragment.DATA_FIRST_MESSAGE);
        else
            sendMetadataFragment(data.getDataChunkSize() + Header.SIZE - 1, data.getDataChunksCount(), Fragment.DATA_FIRST_FILE);

        // Send all data in one fragment or several in loop
        if (dataAndHeadSize > data.getDataChunkSize())
            sendData();
        else
            sendOneFragment(1, Fragment.DATA_SENT, data.getBytes());

        // Send last fragment after all data was sent
        sendEmptyFragment(Fragment.DATA_LAST);
    }

    private synchronized void sendData() {
        // Send all data with same fragment size
        for (int i = 0; i < data.getDataChunksCount() - 1; ++i) {
            sendOneFragment(i + 1, Fragment.DATA_SENT, data.getChunk(i));
        }

        // Send last data fragment only if last fragment contains data
        if (data.getDataLastChunkSize() > 0)
            sendOneFragment(data.getDataChunksCount(), Fragment.DATA_SENT, data.getChunk(data.getDataChunksCount() - 1));
    }

    /**
     * Computes checksum, then creates fragment ready to send and sends fragment
     *
     * @param fragmentWithoutChecksum    byte array of data ready to checksum
     */
    private synchronized void sendFragment(byte[] fragmentWithoutChecksum) {
        Fragment fragment = new Fragment(new MyChecksum(fragmentWithoutChecksum), fragmentWithoutChecksum);
        DatagramPacket datagramPacket = new DatagramPacket(fragment.getBytes(), Header.CHECKSUM_SIZE + fragmentWithoutChecksum.length, address, port);
        sendDatagramPacket(datagramPacket);
    }

    /**
     * Creates header from parameters and creates fragment from header metadata and data,
     * fragment size is computed from data
     *
     * @param fragmentSerialNumber    serial number of fragment
     * @param fragmentType            fragment type required for determination of reaction on server side
     * @param fragmentData            fragment data to send
     */
    private synchronized void sendOneFragment(int fragmentSerialNumber, int fragmentType, byte[] fragmentData) {
        Header header = new Header(Header.SIZE + fragmentData.length, fragmentSerialNumber, fragmentType);

        byte[] fragment = new byte[Header.HEADER_SIZE + fragmentData.length];
        System.arraycopy(header.getBytes(), 0, fragment, 0, Header.HEADER_SIZE);
        System.arraycopy(fragmentData, 0, fragment, Header.HEADER_SIZE, fragmentData.length);

        sendFragment(fragment);
    }

    /**
     * Creates header from parameters and creates empty fragment without data, it is required for start sending
     *
     * @param fragmentSize            size of whole fragment, it tells to server size of next fragments
     * @param fragmentSerialNumber    serial number of fragment
     * @param fragmentType            fragment type required for determination of reaction on server side
     */
    private synchronized void sendMetadataFragment(int fragmentSize, int fragmentSerialNumber, int fragmentType) {
        Header header = new Header(Header.HEADER_SIZE + fragmentSize, fragmentSerialNumber, fragmentType);
        sendFragment(header.getBytes());
    }

    /**
     * Creates header from parameters and creates empty fragment without data, only with custom fragment type,
     * only for signalization usage
     *
     * @param fragmentType    fragment type required for determination of reaction on server side
     */
    private synchronized void sendEmptyFragment(int fragmentType) {
        Header header = new Header(Header.SIZE, 0, fragmentType);
        sendFragment(header.getBytes());
    }

    /**
     * Creates header from parameters and creates empty fragment without data, update checksum with wrong byte sequence,
     * it causes CorruptedDataException on server site, which must be handled
     *
     * @param fragmentSize            size of whole fragment, it tells to server size of next fragments
     * @param fragmentSerialNumber    serial number of fragment
     * @param fragmentType            fragment type required for determination of reaction on server side
     */
    private synchronized void sendCorruptedFragment(int fragmentSize, int fragmentSerialNumber, int fragmentType) {
        Header header = new Header(Header.HEADER_SIZE + fragmentSize, fragmentSerialNumber, fragmentType);
        Fragment fragment = new Fragment(new MyChecksum(new byte[] {0}), header.getBytes());
        DatagramPacket datagramPacket = new DatagramPacket(fragment.getBytes(), Header.CHECKSUM_SIZE + header.getBytes().length, address, port);
        sendDatagramPacket(datagramPacket);
    }

    private synchronized boolean receivedDataOKFragment() throws IOException {
        if (socket != null) {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[Header.SIZE], Header.SIZE);
            socket.receive(datagramPacket);
            Fragment fragment = new Fragment(datagramPacket.getData());

            return fragment.getHeader().getType() == Fragment.DATA_OK;
        }
        // TODO test if if si needed
        // TODO add some kind of wait millis
        return false;
    }

    private synchronized void receiveDataResentFragment() {

    }

    private synchronized void sendDatagramPacket(DatagramPacket datagramPacket) {
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            // TODO add logging
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
