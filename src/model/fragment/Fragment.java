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

    private Header header;
    private MyChecksum myChecksum;
    private byte[] data;
    private byte[] fragment;

    public Fragment(byte[] fragment) {
        myChecksum = new MyChecksum(Arrays.copyOfRange(fragment, Header.CHECKSUM_SIZE, fragment.length));
        header = new Header(Arrays.copyOfRange(fragment, Header.CHECKSUM_SIZE, Header.SIZE));
        if (fragment.length > Header.SIZE)
            data = Arrays.copyOfRange(fragment, Header.SIZE, fragment.length);
        this.fragment = createFragment();
    }

    public Fragment(MyChecksum myChecksum, Header header, byte[] data) {
        this.myChecksum = myChecksum;
        this.header = header;
        this.data = data;
        fragment = createFragment();
    }

    public Fragment(MyChecksum myChecksum, byte[] headerAndData) {
        this.myChecksum = myChecksum;
        header = new Header(Arrays.copyOfRange(headerAndData, 0, Header.HEADER_SIZE));
        data = Arrays.copyOfRange(headerAndData, Header.HEADER_SIZE, headerAndData.length);
        fragment = createFragment();
    }

    public void isValid(byte[] data) throws CorruptedDataException {
        if (!myChecksum.isChecksumCorrect(Arrays.copyOfRange(data, 0, Header.CHECKSUM_SIZE)))
            throw new CorruptedDataException();
    }

    public byte[] getBytes() {
        return fragment;
    }

    private byte[] createFragment() {
        byte[] fragment = new byte[Header.SIZE + getDataLength()];
        System.arraycopy(myChecksum.getChecksum(), 0, fragment, 0, Header.CHECKSUM_SIZE);
        System.arraycopy(header.getBytes(), 0, fragment, Header.CHECKSUM_SIZE, Header.HEADER_SIZE);
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

    public String getDataPrintable() {
        return (data == null) ? "" : new String(data);
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Contract(pure = true)
    private int getDataLength() {
        return (data == null) ? 0 : data.length;
    }
}
