package znamema1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class for loading configuration properties. The config file is to be
 * placed into <em>/Script-Executor-Service/config.properties</em>.
 *
 * @author Martin Znamenacek
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

    /**
     * Hints whether the application runs in DEBUG mode.
     *
     * @return
     */
    public static boolean debug() {
        return DEBUG;
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found, the method returns null.
     *
     * @param key the property key
     * @return the property value in String
     */
    public static String getString(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found, a NumberFormatException is thrown.
     *
     * @param key the property key
     * @return the property value in String
     * @throws NumberFormatException when the key was not found or the property
     * value is not a number
     */
    public static long getLong(String key) throws NumberFormatException {
        return Long.valueOf(PROPERTIES.getProperty(key));
    }
}
