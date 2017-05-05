package test;

import sun.misc.BASE64Encoder;

/**
 * Created by pr on 05.05.17.
 */
public class Base64EncoderAccessor {

    public static String encodeBase64(byte[] source) {
        return new BASE64Encoder().encode(source);
    }
}
