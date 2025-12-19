package com.cqu.exp02.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Properties properties;
    
    private DatabaseConfig() {
        loadProperties();
    }
    
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getDriver() {
        return properties.getProperty("db.driver");
    }
    
    public String getUrl() {
        return properties.getProperty("db.url");
    }
    
    public String getUsername() {
        return properties.getProperty("db.username");
    }
    
    public String getPassword() {
        return properties.getProperty("db.password");
    }
}