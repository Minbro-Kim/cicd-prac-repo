package com.sprint.mission.discodeit.repository.file;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.User;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUserRepositoryTest {

  private FileUserRepository userRepository;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    // FileLockProvider는 간단한 구현체나 Mock을 사용
    userRepository = new FileUserRepository(tempDir.toString(), new FileLockProvider());
  }

  @Test
  void saveAndFindById_Success() {
    // Arrange
    User user = new User("testUser", "test@email.com", "password",null);

    // Act
    userRepository.save(user);
    Optional<User> found = userRepository.findById(user.getId());

    // Assert
    assertTrue(found.isPresent());
    assertEquals("testUser", found.get().getUsername());
  }

  @Test
  void deleteById_Success() {
    // Arrange
    User user = new User("toDelete", "del@email.com", "pw",null);
    userRepository.save(user);
    UUID savedId = user.getId();

    // Act
    userRepository.deleteById(savedId);

    // Assert
    assertFalse(userRepository.existsById(savedId));
  }

  @Test
  void findAll_ShouldReturnAllSavedUsers() {
    // Arrange: 2명의 유저를 저장
    User user1 = new User("user1", "test1@test.com", "pw1", null);
    User user2 = new User("user2", "test2@test.com", "pw2", null);

    userRepository.save(user1);
    userRepository.save(user2);

    // Act: 모든 유저 조회
    List<User> result = userRepository.findAll();

    // Assert
    assertEquals(2, result.size(), "저장된 유저의 수가 일치");
    assertTrue(result.stream().anyMatch(u -> u.getUsername().equals("user1")));
    assertTrue(result.stream().anyMatch(u -> u.getUsername().equals("user2")));
  }
}