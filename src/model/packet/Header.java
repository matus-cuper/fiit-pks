package model.packet;

import model.Utils;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * Header class represents bytes in packet header of custom protocol without CRC checksum
 */
public class Header {

    public static final int HEADER_SIZE = 7;

    private byte[] nextSize;
    private byte[] nextSerialNumber;
    private byte[] type;
    private byte[] header;

    public Header(int size, int count, int type) {
        this.nextSize = Utils.intTo2ByteArray(size);
        this.nextSerialNumber = Utils.intTo2ByteArray(count);
        this.type = new byte[] {(byte)type};
        this.header = null;
    }

    byte[] getHeader() {
        if (this.header == null) {
            this.header = new byte[this.nextSize.length + this.nextSerialNumber.length + this.type.length];
            System.arraycopy(this.nextSize, 0, this.header, 0, this.nextSize.length);
            System.arraycopy(this.nextSerialNumber, 0, this.header, this.nextSize.length, this.nextSerialNumber.length);
            System.arraycopy(this.type, 0, this.header, this.nextSize.length + this.nextSerialNumber.length, this.type.length);
        }
        return header;
    }

    public byte[] getNextSize() {
        return nextSize;
    }

    public void setNextSize(byte[] nextSize) {
        this.nextSize = nextSize;
    }

    public byte[] getNextSerialNumber() {
        return nextSerialNumber;
    }

    public void setNextSerialNumber(byte[] nextSerialNumber) {
        this.nextSerialNumber = nextSerialNumber;
    }

    public byte[] getType() {
        return type;
    }

    public void setType(byte[] type) {
        this.type = type;
    }
}
