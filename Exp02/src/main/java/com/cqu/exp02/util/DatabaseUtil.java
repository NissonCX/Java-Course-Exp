package com.cqu.exp02.util;

import com.cqu.exp02.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static DatabaseConfig config = DatabaseConfig.getInstance();
    
    static {
        try {
            Class.forName(config.getDriver());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            config.getUrl(),
            config.getUsername(),
            config.getPassword()
        );
    }
}