package feeker.net.tools.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


/**
 * 配置文件获取
 * Created by LGZ on 2016/8/12.
 */
public class Config {
    private static Configuration config = null;
    private static final String CONFIG_FILE = "tools.properties";

    static {
        try {
            config = new PropertiesConfiguration(CONFIG_FILE);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getStringProperty(String name) {
        return config.getString(name);
    }

//    public static int getIntProperty(String name) {
//        return config.getInt(name);
//    }


}
