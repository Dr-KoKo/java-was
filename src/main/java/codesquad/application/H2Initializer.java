package codesquad.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class H2Initializer {
    private static final Logger logger = LoggerFactory.getLogger(H2Initializer.class);

    public static void initialize() {
        logger.info("Initializing H2 database...");
        // 데이터베이스 URL
        String jdbcUrl = "jdbc:h2:file:./xtagram";

        // 초기화 스크립트 파일 경로
        InputStream inputStream = H2Initializer.class.getResourceAsStream("/xtagram.sql");

        if (inputStream == null) {
            logger.error("Resource not found: xtagram.sql");
            return;
        }

        try (Connection connection = DriverManager.getConnection(jdbcUrl, "", "")) {
            // 초기화 스크립트 읽기
            String sql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                logger.error("Error reading the SQL script file.", e);
                return;
            }

            // SQL 문 실행
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
                logger.info("Database initialized successfully.");
            }
        } catch (SQLException e) {
            logger.error("Error connecting to the database or executing SQL.", e);
        }
    }
}
