package model.packet;

import model.Utils;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * Header class represents bytes in packet header of custom protocol without CRC checksum
 */
public class Header {

    private byte[] nextSize;
    private byte[] nextCount;
    private byte[] type;
    private byte[] header;

    public Header(byte[] nextSize, byte[] nextCount, byte[] type) {
        this.nextSize = nextSize;
        this.nextCount = nextCount;
        this.type = type;
    }

    public Header(int size, int count, int type) {
        this.nextSize = Utils.intTo2ByteArray(size);
        this.nextCount = Utils.intTo2ByteArray(count);
        this.type = new byte[] {(byte)type};
    }

    public byte[] getHeader() {
        header = new byte[this.nextSize.length + this.nextCount.length + this.type.length];
        System.arraycopy(this.nextSize, 0, this.header, 0, this.nextSize.length);
        System.arraycopy(this.nextCount, 0, this.header, this.nextSize.length, this.nextCount.length);
        System.arraycopy(this.type, 0, this.header, this.nextSize.length + this.nextCount.length, this.type.length);
        return header;
    }

    public byte[] getNextSize() {
        return nextSize;
    }

    public void setNextSize(byte[] nextSize) {
        this.nextSize = nextSize;
    }

    public byte[] getNextCount() {
        return nextCount;
    }

    public void setNextCount(byte[] nextCount) {
        this.nextCount = nextCount;
    }

    public byte[] getType() {
        return type;
    }

    public void setType(byte[] type) {
        this.type = type;
    }
}
