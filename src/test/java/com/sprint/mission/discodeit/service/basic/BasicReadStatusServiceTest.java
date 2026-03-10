package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicReadStatusServiceTest {

  @Mock private ReadStatusRepository readStatusRepository;
  @Mock private UserRepository userRepository;
  @Mock private ChannelRepository channelRepository;

  @InjectMocks
  private BasicReadStatusService readStatusService;

  @Test
  void create_Success() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatusCreateRequest dto = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    when(userRepository.findById(userId)).thenReturn(Optional.of(new User("name", "email", "pw", null)));
    when(channelRepository.findById(channelId)).thenReturn(Optional.of(new Channel(ChannelType.PUBLIC,"test","test")));
    when(readStatusRepository.findByUserIdAndChannelId(userId, channelId)).thenReturn(Optional.empty());
    when(readStatusRepository.save(any(ReadStatus.class))).thenReturn(new ReadStatus(userId, channelId, Instant.now()));

    ReadStatus result = readStatusService.create(dto);

    assertNotNull(result);
    verify(readStatusRepository).save(any(ReadStatus.class));
  }

  @Test
  void create_AlreadyExists_ThrowsException() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatusCreateRequest dto = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    when(userRepository.findById(userId)).thenReturn(Optional.of(new User("name", "email", "pw", null)));
    when(channelRepository.findById(channelId)).thenReturn(Optional.of(new Channel(ChannelType.PUBLIC,"test","test")));
    when(readStatusRepository.findByUserIdAndChannelId(userId, channelId)).thenReturn(Optional.of(new ReadStatus(userId, channelId, Instant.now())));

    assertThrows(BusinessLogicException.class, () -> readStatusService.create(dto));
  }

  @Test
  void update_Success() {
    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatus status = new ReadStatus(userId, channelId, Instant.now());
    ReadStatusUpdateRequest dto = new ReadStatusUpdateRequest(Instant.now());

    when(readStatusRepository.find(id)).thenReturn(Optional.of(status));
    when(readStatusRepository.save(any(ReadStatus.class))).thenReturn(status);

    ReadStatus result = readStatusService.update(id, dto);

    assertNotNull(result);
    verify(readStatusRepository).save(status);
  }

  @Test
  void find_NotFound_ThrowsException() {
    UUID id = UUID.randomUUID();
    when(readStatusRepository.find(id)).thenReturn(Optional.empty());

    assertThrows(BusinessLogicException.class, () -> readStatusService.find(id));
  }

  @Test
  void delete_Success() {
    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    when(readStatusRepository.find(id)).thenReturn(Optional.of(new ReadStatus(userId, channelId, Instant.now())));

    readStatusService.delete(id);

    verify(readStatusRepository).delete(id);
  }
}