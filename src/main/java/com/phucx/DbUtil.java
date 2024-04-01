package com.phucx;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.keycloak.component.ComponentModel;

import com.phucx.user.CustomUserStorageProviderConstants;

public class DbUtil {
    // private static final Logger log = LoggerFactory.getLogger(DbUtil.class);
    public static Connection getConnection(ComponentModel config) throws SQLException{
        String dbURL = config.get(CustomUserStorageProviderConstants.CONFIG_KEY_JDBC_URL);
        // try (Connection c = DriverManager.getConnection(dbURL)){
            // Class.forName(driver);
        // } catch (Exception e) {
        //     throw new RuntimeException("Invalid JDBC driver "  + ". Please check if your driver if properly installed");
        // }
        
        Connection c = DriverManager.getConnection(dbURL,
            config.get(CustomUserStorageProviderConstants.CONFIG_KEY_DB_USERNAME),
            config.get(CustomUserStorageProviderConstants.CONFIG_KEY_DB_PASSWORD));
        return c;
    }
}
