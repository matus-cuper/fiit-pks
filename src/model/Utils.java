package model;

/**
 * Created by Matus Cuper on 11.9.2016.
 *
 * Utils class provides static methods, which are needed in many classes
 */
public class Utils {

    public static byte[] intTo2ByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 8),
                (byte)value};
    }

    public static int byte2ArrayToInt(byte[] value) {
        return (value[0] & 0xFF) << 8 | (value[1] & 0xFF);
    }

    public static int byteArrayToInt(byte[] value) {
        return (value[0] & 0xFF);
    }
}
