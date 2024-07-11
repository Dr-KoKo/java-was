package codesquad.infra;

import codesquad.application.model.User;
import org.junit.jupiter.api.*;

import java.util.Optional;

class MemoryStorageTest {
    private MemoryStorage memoryStorage;

    @BeforeEach
    void setUp() {
        memoryStorage = new MemoryStorage();
    }

    @DisplayName("MemoryStorage는")
    @Nested
    class MemoryStorageIs {
        @DisplayName("User를 저장할 수 있다.")
        @Test
        void save() {
            // given
            User user = new User("donghar", "password", "dr-koko");

            // when
            memoryStorage.save(user);

            // then
            Optional<User> findUser = memoryStorage.findByUserId(user.getUserId());
            Assertions.assertTrue(findUser.isPresent());
            Assertions.assertEquals(findUser.get(), user);
        }

        @DisplayName("저장되지 않은 User를 조회하면 Optional<null>을 반환한다.")
        @Test
        void getUnregistered() {
            // given

            // when
            Optional<User> findUser = memoryStorage.findByUserId("not-registered-id");

            // then
            Assertions.assertTrue(findUser.isEmpty());
        }
    }
}