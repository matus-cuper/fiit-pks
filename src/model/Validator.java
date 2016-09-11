package model;

/**
 * Created by Matus Cuper on 10.9.2016.
 *
 * Validator class provide static methods, which validate InetAddress and port number of host.
 * Parameters are checked, before creation of object.
 */
public class Validator {

    public static boolean isValidHost(String address, String port) {
        return Validator.isValidInetAddress(address) && Validator.isValidPort(port);
    }

    private static boolean isValidInetAddress(String address){
        try {
            if (address == null || address.isEmpty())
                return false;

            int dotCount = address.length() - address.replace(".", "").length();
            if (dotCount != 3)
                return false;

            String[] octets = address.split("\\.");

            if (octets.length != 4)
                return false;

            for (String octet : octets) {
                if (!Validator.isValidOctet(octet))
                    return false;
            }

            return true;
        } catch (Exception e) {
            // TODO add logging
            return false;
        }
    }

    private static boolean isValidPort(String port) {
        try {
            int p = Integer.parseInt(port);
            return (p >= 1024) && (p <= 65535);
        } catch (Exception e) {
            //TODO add logging
            return false;
        }
    }

    private static boolean isValidOctet(String octet) {
        int o = Integer.parseInt(octet);
        return (o >= 0) && (o <= 255);
    }

}
