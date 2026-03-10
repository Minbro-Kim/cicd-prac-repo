package com.sprint.mission.discodeit.controller;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // 이 패키지입니다
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean // @MockBean 대신 사용
  private UserService userService;

  @MockitoBean // @MockBean 대신 사용
  private UserStatusService userStatusService;

  @MockitoBean // @MockBean 대신 사용
  private BinaryContentMapper binaryContentMapper;

  @Test
  void create_User_Success() throws Exception {
    UserCreateRequest request = new UserCreateRequest("testUser", "test@email.com", "password");
    String requestJson = objectMapper.writeValueAsString(request);

    MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "",
        MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes());

    when(userService.create(any(), any())).thenReturn(new UserDto(UUID.randomUUID(), "testUser", "test@email.com", null, true, null, null));

    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  @Test
  void deleteUser_Success() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  void updateUser_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newName", "new@email.com", "newPass");
    String json = objectMapper.writeValueAsString(request);
    MockMultipartFile userPart = new MockMultipartFile("userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, json.getBytes());

    when(userService.update(eq(userId), any(), any())).thenReturn(new UserDto(userId, "newName", "new@email.com", null, true, Instant.now(), Instant.now()));

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userPart)
            .with(request1 -> { request1.setMethod("PATCH"); return request1; }) // PATCH 요청 처리
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
  }

  @Test
  void findAll_ReturnsList() throws Exception {
    when(userService.findAll()).thenReturn(
        List.of(new UserDto(UUID.randomUUID(), "name", "email", null, true, null, null)));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void updateUserStatus_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserStatusUpdateDto dto = new UserStatusUpdateDto(Instant.now());

    when(userStatusService.updateByUserId(eq(userId), any())).thenReturn(new UserStatus(userId));

    mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }
}