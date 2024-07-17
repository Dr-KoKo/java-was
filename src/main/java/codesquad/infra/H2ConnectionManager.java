package codesquad.infra;

import org.h2.jdbcx.JdbcDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class H2ConnectionManager {
    private static JdbcDataSource dataSource;

    static {
        try (InputStream input = H2ConnectionManager.class.getResourceAsStream("/h2.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find h2.properties");
            }
            Properties properties = new Properties();
            properties.load(input);
            dataSource = new JdbcDataSource();
            dataSource.setURL(properties.getProperty("jdbc.url"));
            dataSource.setUser(properties.getProperty("jdbc.username"));
            dataSource.setPassword(properties.getProperty("jdbc.password"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
