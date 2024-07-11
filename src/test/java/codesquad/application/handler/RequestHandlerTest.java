package codesquad.application.handler;

import codesquad.application.fixture.HttpRequestFixture;
import codesquad.application.model.User;
import codesquad.infra.MemorySessionStorage;
import org.junit.jupiter.api.*;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.startline.Method;

import java.util.Map;

class RequestHandlerTest implements HttpRequestFixture {
    private StubBodyParser stubBodyParser;
    private MockUserDao mockUserDao;
    private MemorySessionStorage memorySessionStorage;
    private RequestHandler requestHandler;

    @BeforeEach
    void setUp() {
        stubBodyParser = new StubBodyParser();
        mockUserDao = new MockUserDao();
        memorySessionStorage = new MemorySessionStorage();
        requestHandler = new RequestHandler(stubBodyParser, mockUserDao, memorySessionStorage);
    }

    @DisplayName("회원가입이 성공하면")
    @Nested
    class WhenCreateSuccess {
        @DisplayName("회원정보가 저장된다.")
        @Test
        void save() {
            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/create", Map.of("userId", "donghar", "password", "password", "nickname", "dr-koko"));
            HttpResponse httpResponse = requestHandler.create(httpRequest);

            // then
            Assertions.assertEquals(mockUserDao.getCountSave(), 1);
        }

        @DisplayName("/로 redirect한다.")
        @Test
        void redirect() {
            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/create", Map.of("userId", "donghar", "password", "password", "nickname", "dr-koko"));
            HttpResponse httpResponse = requestHandler.create(httpRequest);

            // then
            Assertions.assertEquals(httpResponse.getStatusLine().toString(), "HTTP/1.1 302 Found\n");
            Assertions.assertEquals(httpResponse.getHeader().get("Location"), "/");
        }
    }

    @DisplayName("로그인이 성공하면")
    @Nested
    class WhenLoginSuccess {
        @DisplayName("Cookie를 설정한다.")
        @Test
        void setCookie() {
            // given
            mockUserDao.stub(new User("donghar", "password", "dr-koko"));

            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/login", Map.of("userId", "donghar", "password", "password"));
            HttpResponse httpResponse = requestHandler.login(httpRequest);

            // then
            Assertions.assertNotNull(httpResponse.getHeader().get("Set-Cookie"));
        }

        @DisplayName("/l로 redirect한다.")
        @Test
        void redirect() {
            // given
            mockUserDao.stub(new User("donghar", "password", "dr-koko"));

            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/login", Map.of("userId", "donghar", "password", "password"));
            HttpResponse httpResponse = requestHandler.login(httpRequest);

            // then
            Assertions.assertEquals(httpResponse.getStatusLine().toString(), "HTTP/1.1 302 Found\n");
            Assertions.assertEquals(httpResponse.getHeader().get("Location"), "/");
        }
    }

    @DisplayName("로그인이 실패하면")
    @Nested
    class WhenLoginFail {
        @DisplayName("/user/login?status=fail로 redirect한다.")
        @Test
        void redirect() {
            // given
            mockUserDao.stub(new User("donghar", "xx", "dr-koko"));

            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/login", Map.of("userId", "donghar", "password", "password"));
            HttpResponse httpResponse = requestHandler.login(httpRequest);

            // then
            Assertions.assertEquals(httpResponse.getStatusLine().toString(), "HTTP/1.1 302 Found\n");
            Assertions.assertEquals(httpResponse.getHeader().get("Location"), "/user/login?status=fail");
        }
    }
}
