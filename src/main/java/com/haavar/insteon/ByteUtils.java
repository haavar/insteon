package com.haavar.insteon;

/**
 * @author Haavar Valeur
 */
public class ByteUtils {
    /**
     * Convert a string of hex digits into a byte array. Ignores spaces.
     *
     * @param input the string to parse
     * @return the byte array representing the hex digits
     */
    public static byte[] hexToBytes(String input) {
        input = input.replaceAll(" ", "");
        input = input.replaceAll("\\.", "");

        byte[] result = new byte[input.length() / 2];
        for (int i = 0; i < result.length; i++) { // can be reversable
            String s = input.substring(i * 2, i * 2 + 2);
            result[i] = Integer.decode("0x" +s).byteValue();
        }
        return result;
    }


    public static String bytesToHex(byte[] in) {
        if (in == null) return "";
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static boolean isBitSet(byte b, int position) {
       return  (b & (1 << position)) != 0;
    }

}
