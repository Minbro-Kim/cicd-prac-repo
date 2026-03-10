package com.sprint.mission.discodeit.repository.file;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUserStatusRepositoryTest {

  private FileUserStatusRepository userStatusRepository;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    // FileLockProvider는 간단한 구현체나 Mock을 사용
    userStatusRepository = new FileUserStatusRepository(tempDir.toString(), new FileLockProvider());
  }

  @Test
  void saveAndFindById_Success() {
    // Arrange
    UserStatus userStatus = new UserStatus(UUID.randomUUID());

    // Act
    userStatusRepository.save(userStatus);
    Optional<UserStatus> found = userStatusRepository.find(userStatus.getId());

    // Assert
    assertTrue(found.isPresent());
    assertEquals(userStatus.getId(), found.get().getId());
  }

  @Test
  void deleteByUserId_Success() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserStatus userStatus = new UserStatus(userId);
    userStatusRepository.save(userStatus);

    // Act
    userStatusRepository.deleteByUserId(userId);

    // Assert
    assertFalse(userStatusRepository.existsByUserId(userId));
  }
}