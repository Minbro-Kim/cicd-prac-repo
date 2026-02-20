package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDto create(UserCreateRequest userCreateDto, Optional<BinaryContentCreateDto> binaryContentCreateDto);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    UserDto update(UUID userId, UserUpdateRequest userUpdateDto, Optional<BinaryContentCreateDto> binaryContentCreateDto);
    void delete(UUID userId);
}
