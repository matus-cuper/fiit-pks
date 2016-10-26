package model.fragment;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * MyChecksum class represents CRC checksum of each sent fragment.
 * Provides data verification of received packets
 */
public class MyChecksum {

    private byte[] checksum;

    public MyChecksum(byte[] data) {
        checksum = computeChecksum(data);
    }

    private byte[] computeChecksum(byte[] data) {
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        return longTo2Bytes(checksum.getValue());
    }

    private byte[] longTo2Bytes(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt((int) l);
        return buffer.array();
    }

    byte[] getChecksum() {
        return checksum;
    }

    boolean isChecksumCorrect(byte[] checksum) {
        if (this.checksum == null || checksum == null)
            return false;

        for (int i = 0; i < checksum.length; ++i)
            if (this.checksum[i] != checksum[i])
                return false;

        return true;
    }
}
