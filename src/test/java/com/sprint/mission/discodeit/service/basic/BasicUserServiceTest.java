package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserStatusRepository userStatusRepository;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private ReadStatusRepository readStatusRepository;
  @Mock private ChannelRepository channelRepository;
  @Mock private UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService;

  @Test
  void createUser_Success() {
    // Arrange
    UserCreateRequest dto = new UserCreateRequest("user", "email@test.com", "password");
    User user = new User("user",  "email@test.com", "password",null);
    UserStatus status = new UserStatus(user.getId());

    when(userRepository.existsByEmail(any())).thenReturn(false);
    when(userRepository.existsByUsername(any())).thenReturn(false);
    when(userMapper.toEntity(any(), any())).thenReturn(user);
    when(userMapper.toDto(any(), any())).thenReturn(new UserDto(user.getId(), "user", "email@test.com", null, true,
        Instant.now(), Instant.now()));

    // Act
    UserDto result = userService.create(dto, Optional.empty());

    // Assert
    assertNotNull(result);
    assertEquals("user", result.username());
    verify(userRepository, times(1)).save(any());
  }

  @Test
  void updateUser_Success() {
    // Arrange
    UUID userId = UUID.randomUUID();
    User existingUser = new User( "oldName", "old@test.com", "oldPass",null);
    UserUpdateRequest updateDto = new UserUpdateRequest("newName", "new@test.com", "newPass");

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    when(userMapper.toDto(any(), any())).thenReturn(new UserDto(userId, "newName", "new@test.com", null, true, Instant.now(), Instant.now()));
    when(userStatusRepository.findByUserId(userId)).thenReturn(Optional.of(new UserStatus(userId)));

    // Act
    UserDto result = userService.update(userId, updateDto, Optional.empty());

    // Assert
    assertEquals("newName", result.username());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void updateUser_ThrowsException_WhenUserNotFound() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(BusinessLogicException.class, () ->
        userService.update(userId, new UserUpdateRequest("n", "e", "p"), Optional.empty())
    );
  }
  @Test
  void delete_User_Success_WithoutProfile() {
    UUID userId = UUID.randomUUID();
    User mockUser = new User("name", "email", "pw", null); // profileId가 null인 경우

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    userService.delete(userId);

    // 검증: 프로필은 삭제하지 않고 사용자 관련 데이터만 삭제하는지 확인
    verify(binaryContentRepository, never()).delete(any());
    verify(userRepository).deleteById(userId);
    verify(userStatusRepository).deleteByUserId(userId);
    verify(readStatusRepository).deleteByUserId(userId);
  }

  @Test
  void delete_User_Success_WithProfile() {
    UUID userId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    User mockUser = new User("name", "email", "pw", profileId); // profileId가 존재하는 경우

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    userService.delete(userId);

    // 검증: 프로필 삭제가 호출되었는지 확인
    verify(binaryContentRepository).delete(profileId);
    verify(userRepository).deleteById(userId);
  }

  @Test
  void validateEmail_ThrowsException_IfAlreadyExists() {
    String email = "duplicate@test.com";
    when(userRepository.existsByEmail(email)).thenReturn(true);

    // create 메서드 실행 시 예외 발생 확인
    assertThrows(BusinessLogicException.class, () -> userService.create(new UserCreateRequest("name", email, "pw"), Optional.empty()));
  }

  @Test
  void validateUsername_ThrowsException_IfAlreadyExists() {
    String username = "duplicateUser";
    when(userRepository.existsByUsername(username)).thenReturn(true);

    assertThrows(BusinessLogicException.class, () -> userService.create(new UserCreateRequest(username, "email", "pw"), Optional.empty()));
  }

  @Test
  void get_UserNotFound_ThrowsException() {
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(BusinessLogicException.class, () -> userService.find(userId));
  }
}