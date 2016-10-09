package model.fragment;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * Checksum class represents CRC checksum of each sent fragment.
 * Provides data verification of received packets
 */
public class Checksum {

    private byte[] checksum;

    public Checksum(byte[] checksum) {
        this.checksum = checksum;
    }

    public Checksum(String data) {
        this.computeChecksum(data);
    }

        private void computeChecksum(String data) {
        this.checksum = new byte[] {16, 16};
    }

    byte[] getChecksum() {
        return checksum;
    }

    public void setChecksum(byte[] checksum) {
        this.checksum = checksum;
    }
}
