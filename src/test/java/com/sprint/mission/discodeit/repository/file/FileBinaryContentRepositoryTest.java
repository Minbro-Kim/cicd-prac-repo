package com.sprint.mission.discodeit.repository.file;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileBinaryContentRepositoryTest {

  private FileBinaryContentRepository binaryContentRepository;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    // FileLockProvider는 간단한 구현체나 Mock을 사용
    binaryContentRepository = new FileBinaryContentRepository(tempDir.toString(), new FileLockProvider());
  }

  @Test
  void saveAndFindById_Success() {
    // Arrange
    BinaryContent content = new BinaryContent("testName", "txt", new byte[]{1,2,3},50);

    // Act
    binaryContentRepository.save(content);
    Optional<BinaryContent> found = binaryContentRepository.find(content.getId());

    // Assert
    assertTrue(found.isPresent());
    assertEquals("testName", found.get().getFileName());
  }

  @Test
  void deleteById_Success() {
    // Arrange
    BinaryContent content = new BinaryContent("toDelete", "txt", new byte[]{1,2,4},30);
    binaryContentRepository.save(content);
    UUID savedId = content.getId();

    // Act
    binaryContentRepository.delete(savedId);

    // Assert
    assertFalse(binaryContentRepository.existsById(savedId));
  }
}