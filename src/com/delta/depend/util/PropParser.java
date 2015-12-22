package com.delta.depend.util;

import java.io.*;
import java.util.Properties;

/**
 * The {@code PropParser} class gives methods to read values
 * from properties file.<br/>
 *
 * @author Jim Zhang
 * @since Delta1.0
 */
@SuppressWarnings("ALL")
public final class PropParser {
    private Properties properties;

    public PropParser(String filePath) {
        properties = new Properties();
        try {
            InputStream is = new FileInputStream(filePath);
            properties.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * call this method to create a new properties file.<br/>
     * (it will be replaced if this properties file exists)
     *
     * @param filePath it will be end with suffix ".properties"
     */
    public static void createPropertiesFile(String filePath) {
        if (!filePath.endsWith(".properties"))
            filePath += ".properties";
        try {
            OutputStream ops = new FileOutputStream(filePath);
            Properties newProp = new Properties();
            newProp.put("key", "value");
            newProp.store(ops, "comments");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * scanning properties and returns a value.<br/>
     * (if key not exists, {@code null} will be returned)
     */
    public String getValue(String key) {
        return (String) properties.get(key);
    }

    /**
     * return reference of properties
     *
     * @return
     */
    public Properties getProperties() {
        return properties;
    }
}
