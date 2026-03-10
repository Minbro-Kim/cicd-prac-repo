package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;//공개채널에 멤버추가를 위한 의존성
  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  @Test
  void createPublicChannel_Success() {
    // Arrange
    PublicChannelCreateRequest dto = new PublicChannelCreateRequest("public","test");
    Channel channel = new Channel(ChannelType.PUBLIC,dto.name(),dto.description());

    when(channelMapper.toEntity(dto)).thenReturn(channel);
    UUID memberId1 = UUID.randomUUID();
    UUID memberId2 = UUID.randomUUID();
    when(userRepository.findAll()).thenReturn(List.of(
        new User("test1","test1@test.com","123",null),
        new User("test2","test2@test.com","123",null)));

    when(channelMapper.toDto(any(), any(),any())).thenReturn(
        new ChannelDto(channel.getId(), ChannelType.PUBLIC, "public"
        , "test",
        Instant.now(), Instant.now(),Instant.now(), List.of(memberId1, memberId2)));

    // Act
    ChannelDto result = channelService.create(dto);

    // Assert
    assertNotNull(result);
    assertEquals("public", result.name());
    verify(channelRepository, times(1)).save(channel);
    verify(readStatusRepository, times(2)).save(any(ReadStatus.class));
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void createPrivateChannel_Success() {
    // Arrange
    UUID memberId1 = UUID.randomUUID();
    UUID memberId2 = UUID.randomUUID();
    PrivateChannelCreateRequest dto = new PrivateChannelCreateRequest(List.of(memberId1,memberId2));
    Channel channel = new Channel(ChannelType.PRIVATE,null,null);

    when(channelMapper.toEntity(dto)).thenReturn(channel);

    when(channelMapper.toDto(any(), any(),any())).thenReturn(
        new ChannelDto(channel.getId(), ChannelType.PRIVATE, null,null,
            Instant.now(), Instant.now(),Instant.now(), List.of(memberId1, memberId2)));

    // Act
    ChannelDto result = channelService.create(dto);

    // Assert
    assertNotNull(result);
    assertEquals(dto.memberIds().size(), result.memberIds().size());
    verify(channelRepository, times(1)).save(channel);
    verify(readStatusRepository, times(dto.memberIds().size())).save(any(ReadStatus.class));
  }

  @Test
  void updatePublicChannel_Success() {
    // Arrange
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest dto = new PublicChannelUpdateRequest("newName", "newDesc");
    Channel channel = new Channel(ChannelType.PUBLIC, "oldName", "oldDesc");

    when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
    when(channelRepository.save(any())).thenReturn(channel);
    when(channelMapper.toDto(any(), any(), any())).thenReturn(
        new ChannelDto(channelId, ChannelType.PUBLIC, "newName", "newDesc", null, null, null, List.of())
    );

    // Act
    ChannelDto result = channelService.update(channelId, dto);

    // Assert
    assertEquals("newName", result.name());
    verify(channelRepository).save(any());
  }

  @Test
  void updatePrivateChannel_ThrowsException() {
    // Arrange
    UUID channelId = UUID.randomUUID();
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null ,null);
    when(channelRepository.findById(channelId)).thenReturn(Optional.of(privateChannel));

    // Act & Assert
    assertThrows(BusinessLogicException.class, () ->
        channelService.update(channelId, new PublicChannelUpdateRequest("n", "d"))
    );
  }

  @Test
  void findAllByUserId_Success() {
    // Arrange
    UUID userId = UUID.randomUUID();
    Channel c1 = new Channel(ChannelType.PUBLIC, "name1", "desc1");
    Channel c2 = new Channel(ChannelType.PUBLIC, "name2", "desc2");

    // c1이 c2보다 과거
    when(readStatusRepository.findAllByUserId(userId)).thenReturn(List.of(
        new ReadStatus(userId, c1.getId()),
        new ReadStatus(userId, c2.getId())
    ));
    when(channelRepository.findById(any())).thenReturn(Optional.of(c1), Optional.of(c2));

    when(channelMapper.toDto(any(), any(), any())).thenReturn(
        new ChannelDto(c1.getId(), ChannelType.PUBLIC, "n1", "d1", Instant.now().minusSeconds(10), null, null, List.of()),
        new ChannelDto(c2.getId(), ChannelType.PUBLIC, "n2", "d2", Instant.now(), null, null, List.of())
    );

    // Act
    List<ChannelDto> result = channelService.findAllByUserId(userId);

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.get(0).createdAt().isBefore(result.get(1).createdAt())); // 정렬 확인
  }
}