package codesquad.infra;

import codesquad.application.model.User;
import org.junit.jupiter.api.*;

class MemorySessionStorageTest {
    private MemorySessionStorage memorySessionStorage;

    @BeforeEach
    void setUp() {
        memorySessionStorage = new MemorySessionStorage();
    }

    @DisplayName("MemorySessionStorage는")
    @Nested
    class MemorySessionStorageIs {
        @DisplayName("유저를 저장할 수 있다.")
        @Test
        void storeUser() {
            // given
            User user = new User("donghar", "password", "dr-koko");

            // when
            String sid = memorySessionStorage.store(user);

            // then
            User findUser = (User) memorySessionStorage.get(sid);
            Assertions.assertEquals(user, findUser);
        }

        @DisplayName("만료시킬 수 있다.")
        @Test
        void invalidate() {
            // given
            User user = new User("donghar", "password", "dr-koko");
            String sid = memorySessionStorage.store(user);

            // when
            memorySessionStorage.invalidate(sid);

            // then
            User findUser = (User) memorySessionStorage.get(sid);
            Assertions.assertNull(findUser);
        }
    }
}