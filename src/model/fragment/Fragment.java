package model.fragment;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * Fragment class represents sent and received data stream via UDP
 */
public class Fragment {

    public static final byte DATA_FIRST_FILE = 0;
    public static final byte DATA_FIRST_MESSAGE = 1;
    public static final byte DATA_SENT = 2;
    public static final byte DATA_LAST = 3;
    public static final byte START_CONNECTION = 4;
    public static final byte HOLD_CONNECTION = 5;
    public static final byte STOP_CONNECTION = 6;
    public static final byte DATA_OK = 7;
    public static final byte DATA_RESENT = 8;

    public static final int MAX_SIZE = 65498;

    private Header header;
    private MyChecksum myChecksum;
    private byte[] data;
    private byte[] fragment;

    public Fragment(byte[] fragment) {
        this.data = Arrays.copyOfRange(fragment, Header.SIZE, fragment.length);
    }

    public Fragment(MyChecksum myChecksum, Header header, byte[] data) {
        this.myChecksum = myChecksum;
        this.header = header;
        this.data = data;
        fragment = createFragment();
    }

    public byte[] getFragment() {
        return fragment;
    }

    private byte[] createFragment() {
        byte[] fragment = new byte[Header.SIZE + getDataLength()];
        System.arraycopy(myChecksum.getChecksum(), 0, fragment, 0, Header.CHECKSUM_SIZE);
        System.arraycopy(header.getHeader(), 0, fragment, Header.CHECKSUM_SIZE, Header.HEADER_SIZE);
        if (data != null)
            System.arraycopy(data, 0, fragment, Header.SIZE, getDataLength());

        return fragment;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public MyChecksum getMyChecksum() {
        return myChecksum;
    }

    public void setMyChecksum(MyChecksum myChecksum) {
        this.myChecksum = myChecksum;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Contract(pure = true)
    private int getDataLength() {
        return (data == null) ? 0 : data.length;
    }
}
