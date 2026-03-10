package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class BasicMessageServiceTest {
  @Mock private MessageRepository messageRepository;
  @Mock private ChannelRepository channelRepository;
  @Mock private UserRepository userRepository;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private BinaryContentMapper binaryContentMapper;
  @Mock private MessageMapper messageMapper;
  @Mock private ReadStatusRepository readStatusRepository;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  void createMessage_Success() {
    // Arrange
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest dto = new MessageCreateRequest("Hello World", channelId, authorId);
    List<BinaryContentCreateDto> attachments = List.of(new BinaryContentCreateDto("fileName", "txt",
        new byte[]{1,2,3},50));

    // Mock 설정
    when(channelRepository.existsById(any())).thenReturn(true);
    when(userRepository.existsById(any())).thenReturn(true);
    when(readStatusRepository.findByUserIdAndChannelId(authorId, channelId)).thenReturn(Optional.of(new ReadStatus(authorId, channelId)));

    // binaryContent 저장 로직 모킹
    BinaryContent content = new BinaryContent("fileName", "txt", new byte[]{1, 2, 3},50);
    when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(content);
    when(binaryContentMapper.toEntity(any())).thenReturn(content);

    // 메시지 저장
    Message message = new Message("Hello World", channelId, authorId,List.of(content.getId()));
    when(messageRepository.save(any(Message.class))).thenReturn(message);
    when(messageMapper.toDto(any())).thenReturn(
        new MessageResponseDto(
        message.getId(), "Hello World",
            channelId, authorId, List.of(content.getId()), Instant.now(),null));

    // Act
    MessageResponseDto result = messageService.create(channelId, authorId, dto, attachments);

    // Assert
    assertNotNull(result);
    verify(binaryContentRepository, times(1)).save(any());
    verify(messageRepository, times(1)).save(any());
  }

  @Test
  void deleteMessage_WithAttachments_Success() {
    // Arrange
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID attachmentId = UUID.randomUUID();
    Message message = new Message("Content", channelId, authorId, List.of(attachmentId));

    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

    // Act
    messageService.delete(messageId);

    // Assert
    verify(binaryContentRepository, times(1)).delete(attachmentId);
    verify(messageRepository, times(1)).deleteById(messageId);
  }

  @Test
  void createMessage_Invalid_ThrowsException() {
    // Arrange
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    // 빈 요청
    MessageCreateRequest dto = new MessageCreateRequest("", channelId, authorId);
    List<BinaryContentCreateDto> attachments = List.of();

    // Act & Assert
    assertThrows(BusinessLogicException.class, () ->
        messageService.create(channelId, authorId, dto, attachments)
    );
  }

  @Test
  void updateMessage_Success() {
    // Arrange
    UUID messageId = UUID.randomUUID();
    Message message = new Message("Original Content", UUID.randomUUID(), UUID.randomUUID(), null);
    MessageUpdateRequest updateDto = new MessageUpdateRequest("Updated Content");

    // Mock 설정
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(messageRepository.save(any(Message.class))).thenReturn(message);
    when(messageMapper.toDto(any(Message.class))).thenReturn(
        new MessageResponseDto(messageId, "Updated Content", message.getChannelId(), message.getAuthorId(), null, Instant.now(), null)
    );

    // Act
    MessageResponseDto result = messageService.update(messageId, updateDto);

    // Assert
    assertNotNull(result);
    assertEquals("Updated Content", result.content());
    verify(messageRepository, times(1)).save(message);
  }

  @Test
  void updateMessage_NotFound_ThrowsException() {
    // Arrange
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest updateDto = new MessageUpdateRequest("New Content");

    // 메시지가 존재하지 않는 상황 설정
    when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(BusinessLogicException.class, () ->
        messageService.update(messageId, updateDto)
    );
  }
}