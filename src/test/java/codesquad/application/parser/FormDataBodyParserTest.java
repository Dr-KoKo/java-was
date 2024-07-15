package codesquad.application.parser;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.charset.StandardCharsets;
import java.util.Map;

class FormDataBodyParserTest {
    private FormDataBodyParser parser;

    @BeforeEach
    void setUp() {
        parser = new FormDataBodyParser();
    }

    @DisplayName("FormDataBodyParser는")
    @Nested
    class FormDataBodyParserIs {
        @DisplayName("'='를 기준으로 name/value를 구분한다.")
        @ParameterizedTest
        @CsvSource(value = {"userId=javajigi,userId,javajigi", "password=password,password,password", "name=박재성,name,박재성", "email=javajigi@slipp.net,email,javajigi@slipp.net"})
        void parse(String body, String expectedName, String expectedValue) {
            Map<String, String> result = parser.parse(body.getBytes(StandardCharsets.UTF_8));
            Assertions.assertEquals(result.get(expectedName), expectedValue);
        }

        @DisplayName("'&'를 기준으로 data를 구분한다.")
        @Test
        void parse() {
            String subject = "userId=javajigi&password=password&name=박재성&email=javajigi@slipp.net";
            Map<String, String> result = parser.parse(subject.getBytes(StandardCharsets.UTF_8));
            Assertions.assertEquals(result.get("userId"), "javajigi");
            Assertions.assertEquals(result.get("password"), "password");
            Assertions.assertEquals(result.get("name"), "박재성");
            Assertions.assertEquals(result.get("email"), "javajigi@slipp.net");
        }
    }
}
