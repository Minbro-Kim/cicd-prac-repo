package com.sprint.mission.discodeit.repository.file;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileReadStatusRepositoryTest {

  private FileReadStatusRepository readStatusRepository;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    // FileLockProvider는 간단한 구현체나 Mock을 사용
    readStatusRepository = new FileReadStatusRepository(tempDir.toString(), new FileLockProvider());
  }

  @Test
  void saveAndFindById_Success() {
    // Arrange
    ReadStatus readStatus = new ReadStatus(UUID.randomUUID(),UUID.randomUUID());

    // Act
    readStatusRepository.save(readStatus);
    Optional<ReadStatus> found = readStatusRepository.find(readStatus.getId());

    // Assert
    assertTrue(found.isPresent());
    assertEquals(readStatus.getId(), found.get().getId());
  }

  @Test
  void deleteByChannelId_Success() {
    // Arrange
    UUID channelId = UUID.randomUUID();
    ReadStatus readStatus = new ReadStatus(UUID.randomUUID(),channelId);
    readStatusRepository.save(readStatus);

    // Act
    readStatusRepository.deleteByChannelId(channelId);

    // Assert
    assertTrue(readStatusRepository.findAllByChannelId(channelId).isEmpty());
  }
  @Test
  void deleteByUserId_Success() {
    // Arrange
    UUID userId = UUID.randomUUID();
    ReadStatus readStatus = new ReadStatus(userId,UUID.randomUUID());
    readStatusRepository.save(readStatus);

    // Act
    readStatusRepository.deleteByUserId(userId);

    // Assert
    assertTrue(readStatusRepository.findAllByUserId(userId).isEmpty());
  }
}