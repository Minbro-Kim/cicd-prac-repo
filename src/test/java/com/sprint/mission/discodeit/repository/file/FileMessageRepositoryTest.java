package com.sprint.mission.discodeit.repository.file;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.entity.User;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileMessageRepositoryTest {

  private FileMessageRepository messageRepository;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    // FileLockProvider는 간단한 구현체나 Mock을 사용
    messageRepository = new FileMessageRepository(tempDir.toString(), new FileLockProvider());
  }

  @Test
  void saveAndFindById_Success() {
    // Arrange
    Message message = new Message("test", UUID.randomUUID(), UUID.randomUUID(),null);

    // Act
    messageRepository.save(message);
    Optional<Message> found = messageRepository.findById(message.getId());

    // Assert
    assertTrue(found.isPresent());
    assertEquals("test", found.get().getContent());
  }

  @Test
  void deleteById_Success() {
    // Arrange
    Message message = new Message("toDelete", UUID.randomUUID(), UUID.randomUUID(),null);
    messageRepository.save(message);
    UUID savedId = message.getId();

    // Act
    messageRepository.deleteById(savedId);

    // Assert
    assertFalse(messageRepository.existsById(savedId));
  }

  @Test
  void findAll_ShouldReturnAllSavedMessages() {
    // Arrange: 2명의 유저를 저장
    Message message1 = new Message("test1", UUID.randomUUID(), UUID.randomUUID(),null);
    Message message2 = new Message("test2", UUID.randomUUID(), UUID.randomUUID(),null);

    messageRepository.save(message1);
    messageRepository.save(message2);

    // Act
    List<Message> result = messageRepository.findAll();

    // Assert
    assertEquals(2, result.size(), "저장된 메세지의 수가 일치");
    assertTrue(result.stream().anyMatch(u -> u.getContent().equals("test1")));
    assertTrue(result.stream().anyMatch(u -> u.getContent().equals("test2")));
  }
}