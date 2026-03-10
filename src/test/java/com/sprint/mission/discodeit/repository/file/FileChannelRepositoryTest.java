package com.sprint.mission.discodeit.repository.file;

import static org.junit.jupiter.api.Assertions.*;

    import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileChannelRepositoryTest {

  private FileChannelRepository channelRepository;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    // FileLockProvider는 간단한 구현체나 Mock을 사용
    channelRepository = new FileChannelRepository(tempDir.toString(), new FileLockProvider());
  }

  @Test
  void saveAndFindById_Success() {
    // Arrange
    Channel channel = new Channel(ChannelType.PUBLIC, "test", "test");

    // Act
    channelRepository.save(channel);
    Optional<Channel> found = channelRepository.findById(channel.getId());

    // Assert
    assertTrue(found.isPresent());
    assertEquals("test", found.get().getName());
  }

  @Test
  void deleteById_Success() {
    // Arrange
    Channel channel = new Channel(ChannelType.PUBLIC, "test", "test");
    channelRepository.save(channel);
    UUID savedId = channel.getId();

    // Act
    channelRepository.deleteById(savedId);

    // Assert
    assertFalse(channelRepository.existsById(savedId));
  }
}