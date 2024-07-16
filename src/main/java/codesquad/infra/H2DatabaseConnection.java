package codesquad.infra;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class H2DatabaseConnection {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream input = H2DatabaseConnection.class.getResourceAsStream("/h2.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find h2.properties");
                return;
            }

            // Load the properties file
            properties.load(input);

            String url = properties.getProperty("jdbc.url");
            String driverClassName = properties.getProperty("jdbc.driverClassName");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");

            // Load the JDBC driver
            Class.forName(driverClassName);

            // Establish the connection
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Connected to the H2 database successfully!");
                // Perform database operations here...
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
