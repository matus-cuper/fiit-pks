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
        return isValidNumberInRange(port, 1024, 65535);
        // TODO add configuration file
    }

    private static boolean isValidOctet(String octet) {
        return isValidNumberInRange(octet, 0, 255);
    }

    public static boolean isValidSize(String size) {
        return isValidNumberInRange(size, 10, 65507);
        // TODO add configuration file
    }

    private static boolean isValidNumberInRange(String s, int lower, int upper) {
        if (isValidNumber(s)) {
            int tmp = Integer.parseInt(s);
            return (tmp >= lower) && (tmp <= upper);
        }
        return false;
    }

    private static boolean isValidNumber(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            // TODO add logging
            return false;
        }
        return true;
    }
}
