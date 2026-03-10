package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicAuthService basicAuthService;

  @Test
  void testCreatePublicChannel_Success() {

    LoginRequest request = new LoginRequest("testUser", "password123");
    User mockUser = new User("testUser","test@test.com","password123",null);
    UserStatus mockStatus = new UserStatus(mockUser.getId());

    UserDto mockDto = new UserDto(mockUser.getId(), "testUser", "test@test.com", null,true, Instant.now(), Instant.now());
    when(userMapper.toDto(any(User.class), any(UserStatus.class)))
        .thenReturn(mockDto);
    when(userRepository.findByUsernameAndPassword("testUser", "password123"))
        .thenReturn(Optional.of(mockUser));
    when(userStatusRepository.findByUserId(mockUser.getId()))
        .thenReturn(Optional.of(mockStatus));

    UserDto result = basicAuthService.login(request);

    assertNotNull(result);
    verify(userStatusRepository, times(1)).save(any(UserStatus.class));
  }


  @Test
  void testLogin_Fail_InvalidCredentials() {

    LoginRequest request = new LoginRequest("wrongUser", "wrongPass");

    when(userRepository.findByUsernameAndPassword("wrongUser", "wrongPass"))
        .thenReturn(Optional.empty());

    assertThrows(BusinessLogicException.class, () -> {
      basicAuthService.login(request);
    });
  }

  @Test
  void testLogin_Fail_UserStatusNotFound() {
    LoginRequest request = new LoginRequest("testUser", "password123");
    User mockUser = new User("testUser","test@test.com","password123",null);

    when(userRepository.findByUsernameAndPassword("testUser", "password123"))
        .thenReturn(Optional.of(mockUser));
    when(userStatusRepository.findByUserId(any(UUID.class)))//유저 스테이터스 못찾음
        .thenReturn(Optional.empty());
    assertThrows(BusinessLogicException.class, () -> {
      basicAuthService.login(request);
    });
  }
}