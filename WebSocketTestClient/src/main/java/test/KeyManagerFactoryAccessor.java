package test;

import com.sun.net.ssl.KeyManagerFactory;

import java.security.NoSuchAlgorithmException;

/**
 * Created by pr on 05.05.17.
 */
public class KeyManagerFactoryAccessor {

    public static Object accessKeyManagerFactory() throws NoSuchAlgorithmException {
        return KeyManagerFactory.getInstance("SunX509");
    }

}
