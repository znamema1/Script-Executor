package znamema1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Martin
 */
public class ConfigLoader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream("/Script-Executor-Service/config.properties"));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load properties file: " + ex.getMessage());
        }
    }
    private static final boolean DEBUG = getString("DEBUG").toLowerCase().equals("true");

    public static boolean debug() {
        return DEBUG;
    }

    public static String getString(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static long getLong(String key) throws NumberFormatException {
        return Long.valueOf(PROPERTIES.getProperty(key));
    }
}
