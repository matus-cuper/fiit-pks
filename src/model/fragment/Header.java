package model.fragment;

import model.Utils;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * Header class represents bytes in fragment header of custom protocol without CRC checksum
 */
public class Header {

    public static final int SIZE = 9;
    public static final int CHECKSUM_SIZE = 4;
    public static final int LENGTH_SIZE = 2;
    public static final int SERIAL_SIZE = 2;
    public static final int TYPE_SIZE = 1;
    public static final int HEADER_SIZE = LENGTH_SIZE + SERIAL_SIZE + TYPE_SIZE;

    private byte[] length;
    private byte[] serialNumber;
    private byte[] type;
    private byte[] header;

    public Header(int length, int serialNumber, int type) {
        this.length = Utils.intTo2ByteArray(length);
        this.serialNumber = Utils.intTo2ByteArray(serialNumber);
        this.type = new byte[] {(byte)type};
        header = createHeader();
    }

    public byte[] getHeader() {
        return header;
    }

    private byte[] createHeader() {
        byte[] header = new byte[HEADER_SIZE];
        System.arraycopy(length, 0, header, 0, LENGTH_SIZE);
        System.arraycopy(serialNumber, 0, header, LENGTH_SIZE, SERIAL_SIZE);
        System.arraycopy(type, 0, header, LENGTH_SIZE + SERIAL_SIZE, TYPE_SIZE);
        return header;
    }

    public byte[] getLength() {
        return length;
    }

    public void setLength(byte[] length) {
        this.length = length;
    }

    public byte[] getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(byte[] serialNumber) {
        this.serialNumber = serialNumber;
    }

    public byte[] getType() {
        return type;
    }

    public void setType(byte[] type) {
        this.type = type;
    }
}
