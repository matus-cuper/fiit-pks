package model.fragment;

import model.Utils;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * Header class represents bytes in fragment header of custom protocol without CRC checksum
 */
public class Header {

    public static final int HEADER_SIZE = 9;

    private byte[] size;
    private byte[] serialNumber;
    private byte[] type;
    private byte[] header;

    public Header(int size, int serialNumber, int type) {
        this.size = Utils.intTo2ByteArray(size);
        this.serialNumber = Utils.intTo2ByteArray(serialNumber);
        this.type = new byte[] {(byte)type};
        this.header = getHeader();
    }

    public byte[] getHeader() {
        // TODO do code refactor
        if (this.header == null) {
            this.header = new byte[this.size.length + this.serialNumber.length + this.type.length];
            System.arraycopy(this.size, 0, this.header, 0, this.size.length);
            System.arraycopy(this.serialNumber, 0, this.header, this.size.length, this.serialNumber.length);
            System.arraycopy(this.type, 0, this.header, this.size.length + this.serialNumber.length, this.type.length);
        }
        return header;
    }

    public int getLength() {
        return this.header.length;
    }

    public byte[] getSize() {
        return size;
    }

    public void setSize(byte[] size) {
        this.size = size;
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
