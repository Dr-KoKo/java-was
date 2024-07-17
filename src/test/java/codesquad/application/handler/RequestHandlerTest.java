package codesquad.application.handler;

import codesquad.application.adapter.RequestHandlerAdapter;
import codesquad.application.argumentresolver.FormUrlEncodedResolver;
import codesquad.application.argumentresolver.NoOpArgumentResolver;
import codesquad.application.fixture.HttpRequestFixture;
import codesquad.application.model.User;
import codesquad.application.returnvaluehandler.ModelViewHandler;
import codesquad.infra.MemorySessionStorage;
import org.junit.jupiter.api.*;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.startline.Method;

import java.util.List;
import java.util.Map;

class RequestHandlerTest implements HttpRequestFixture {
    private MockUserDao mockUserDao;
    private MockArticleDao mockArticleDao;
    private MemorySessionStorage memorySessionStorage;
    private RequestHandler requestHandler;
    private RequestHandlerAdapter requestHandlerAdapter;

    @BeforeEach
    void setUp() {
        mockUserDao = new MockUserDao();
        memorySessionStorage = new MemorySessionStorage();
        requestHandler = new RequestHandler(mockUserDao, mockArticleDao, memorySessionStorage);
        requestHandlerAdapter = new RequestHandlerAdapter(List.of(new NoOpArgumentResolver(), new FormUrlEncodedResolver()), List.of(new ModelViewHandler()));
    }

    @DisplayName("html을 요청하면")
    @Nested
    class WhenRequestDynamicResource {
        @DisplayName("/ 요청은")
        @Nested
        class index {
            @DisplayName("세션이 있으면 login_header를 반환한다.")
            @Test
            void login_header() throws NoSuchMethodException {
                // when
                HttpRequest httpRequest = simpleRequest(Method.GET, "/");
                HttpResponse httpResponse = requestHandlerAdapter.handle(requestHandler, requestHandler.getClass().getMethod("index", HttpRequest.class), httpRequest);

                // then
                Assertions.assertEquals(httpResponse.getStatusLine().toString(), "HTTP/1.1 200 OK\n");
            }

            @DisplayName("세션이 없으면 not_login_header를 반환한다.")
            @Test
            void not_login_header() throws NoSuchMethodException {
                // when
                HttpRequest httpRequest = simpleRequest(Method.GET, "/");
                HttpResponse httpResponse = requestHandlerAdapter.handle(requestHandler, requestHandler.getClass().getMethod("index", HttpRequest.class), httpRequest);

                // then
                Assertions.assertEquals(httpResponse.getStatusLine().toString(), "HTTP/1.1 200 OK\n");
            }
        }

        @DisplayName("/user/registration 요청은")
        @Nested
        class createUser {
        }
    }

    @DisplayName("회원가입이 성공하면")
    @Nested
    class WhenCreateSuccess {
        @DisplayName("회원정보가 저장된다.")
        @Test
        void save() throws NoSuchMethodException {
            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/create", Map.of("userId", "donghar", "password", "password", "nickname", "dr-koko"));
            HttpResponse httpResponse = requestHandlerAdapter.handle(requestHandler, requestHandler.getClass().getMethod("create", Map.class), httpRequest);

            // then
            Assertions.assertEquals(mockUserDao.getCountSave(), 1);
        }

        @DisplayName("/로 redirect한다.")
        @Test
        void redirect() throws NoSuchMethodException {
            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/create", Map.of("userId", "donghar", "password", "password", "nickname", "dr-koko"));
            HttpResponse httpResponse = requestHandlerAdapter.handle(requestHandler, requestHandler.getClass().getMethod("create", Map.class), httpRequest);

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
        void setCookie() throws NoSuchMethodException {
            // given
            mockUserDao.stub(new User("donghar", "password", "dr-koko"));

            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/login", Map.of("userId", "donghar", "password", "password"));
            HttpResponse httpResponse = requestHandlerAdapter.handle(requestHandler, requestHandler.getClass().getMethod("login", Map.class), httpRequest);

            // then
            Assertions.assertNotNull(httpResponse.getHeader().get("Set-Cookie"));
        }

        @DisplayName("/l로 redirect한다.")
        @Test
        void redirect() throws NoSuchMethodException {
            // given
            mockUserDao.stub(new User("donghar", "password", "dr-koko"));

            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/login", Map.of("userId", "donghar", "password", "password"));
            HttpResponse httpResponse = requestHandlerAdapter.handle(requestHandler, requestHandler.getClass().getMethod("login", Map.class), httpRequest);

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
        void redirect() throws NoSuchMethodException {
            // given
            mockUserDao.stub(new User("donghar", "xx", "dr-koko"));

            // when
            HttpRequest httpRequest = formDataRequest(Method.POST, "/login", Map.of("userId", "donghar", "password", "password"));
            HttpResponse httpResponse = requestHandlerAdapter.handle(requestHandler, requestHandler.getClass().getMethod("login", Map.class), httpRequest);

            // then
            Assertions.assertEquals(httpResponse.getStatusLine().toString(), "HTTP/1.1 302 Found\n");
            Assertions.assertEquals(httpResponse.getHeader().get("Location"), "/user/login?status=fail");
        }
    }
}
