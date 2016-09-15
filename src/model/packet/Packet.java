package model.packet;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * Packet class represents sent and received data stream via UDP
 */
public class Packet {

    public static final byte DATA_FIRST = 0;
    public static final byte DATA_SENT = 1;
    public static final byte DATA_LAST = 2;
    public static final byte START_CONNECTION = 3;
    public static final byte HOLD_CONNECTION = 4;
    public static final byte STOP_CONNECTION = 5;
    public static final byte DATA_OK = 6;
    public static final byte DATA_RESENT = 7;

    private Header header;
    private Checksum checksum;
    private byte[] data;
    private byte[] packet;

    public Packet(Header header, Checksum checksum, String data) {
        this.header = header;
        this.checksum = checksum;
        this.data = data.getBytes();
        this.packet = null;
    }

    public byte[] getBytes() {
        if (packet == null) {
            packet = new byte[this.header.getHeader().length + this.checksum.getChecksum().length + this.data.length];
            System.arraycopy(this.header.getHeader(), 0, this.packet, 0, this.header.getHeader().length);
            System.arraycopy(this.checksum.getChecksum(), 0, this.packet, this.header.getHeader().length, this.checksum.getChecksum().length);
            System.arraycopy(this.data, 0, this.packet, this.header.getHeader().length + this.checksum.getChecksum().length, this.data.length);
        }
        return packet;

    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
