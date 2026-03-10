package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
class BasicUserStatusServiceTest {
  @Mock private UserStatusRepository userStatusRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks
  private BasicUserStatusService userStatusService;

  @Test
  void create_UserStatus_Success() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserStatusCreateDto dto = new UserStatusCreateDto(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(new User("name", "email", "pw", null)));
    when(userStatusRepository.existsByUserId(userId)).thenReturn(false);
    when(userStatusRepository.save(any(UserStatus.class))).thenReturn(new UserStatus(userId));

    // Act
    UserStatus result = userStatusService.create(dto);

    // Assert
    assertNotNull(result);
    verify(userStatusRepository).save(any(UserStatus.class));
  }

  @Test
  void create_UserStatus_AlreadyExist_ThrowsException() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserStatusCreateDto dto = new UserStatusCreateDto(userId);
    when(userRepository.findById(userId)).thenReturn(
        Optional.of(new User("name", "email", "pw", null)));
    when(userStatusRepository.existsByUserId(userId)).thenReturn(true);

    // Act & Assert
    assertThrows(BusinessLogicException.class, () -> userStatusService.create(dto));
  }

  @Test
  void updateByUserId_Success() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserStatusUpdateDto dto = new UserStatusUpdateDto(Instant.now());
    UserStatus status = new UserStatus(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(new User("n", "e", "p", null)));
    when(userStatusRepository.findByUserId(userId)).thenReturn(Optional.of(status));
    when(userStatusRepository.save(any(UserStatus.class))).thenReturn(status);

    // Act
    UserStatus result = userStatusService.updateByUserId(userId, dto);

    // Assert
    assertNotNull(result);
    verify(userStatusRepository).save(status);
  }

  @Test
  void delete_UserStatus_Success() {
    // Arrange
    UUID statusId = UUID.randomUUID();
    when(userStatusRepository.existsById(statusId)).thenReturn(true);

    // Act
    userStatusService.delete(statusId);

    // Assert
    verify(userStatusRepository).delete(statusId);
  }

  @Test
  void find_Success() {
    // Arrange
    UUID statusId = UUID.randomUUID();
    UserStatus status = new UserStatus(UUID.randomUUID());
    when(userStatusRepository.find(statusId)).thenReturn(Optional.of(status));

    // Act
    UserStatus result = userStatusService.find(statusId);

    // Assert
    assertNotNull(result);
    assertEquals(status, result);
  }

  @Test
  void findAll_ReturnsList() {
    // Arrange
    when(userStatusRepository.findAll()).thenReturn(List.of(new UserStatus(UUID.randomUUID())));

    // Act
    List<UserStatus> result = userStatusService.findAll();

    // Assert
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  void update_UserStatusNotFound_ThrowsException() {
    // Arrange
    UUID statusId = UUID.randomUUID();
    UserStatusUpdateDto dto = new UserStatusUpdateDto(Instant.now());
    when(userStatusRepository.find(statusId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(BusinessLogicException.class, () -> userStatusService.update(statusId, dto));
  }

  @Test
  void delete_UserStatusNotFound_ThrowsException() {
    // Arrange
    UUID statusId = UUID.randomUUID();
    when(userStatusRepository.existsById(statusId)).thenReturn(false);

    // Act & Assert
    assertThrows(BusinessLogicException.class, () -> userStatusService.delete(statusId));
  }
}