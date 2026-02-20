package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {
    /*
     **사용자 관리**
        - [ ]  사용자를 등록할 수 있다.
        - [ ]  사용자 정보를 수정할 수 있다.
        - [ ]  사용자를 삭제할 수 있다.
        - [ ]  모든 사용자를 조회할 수 있다.
        - [ ]  사용자의 온라인 상태를 업데이트할 수 있다.
     */
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final BinaryContentMapper binaryContentMapper;

    @Operation(summary = "User 등록")
    @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserDto> create(@Valid @RequestPart(value = "userCreateRequest") UserCreateRequest dto,
                                                @RequestPart(value = "profile",required = false) MultipartFile multipartFile) {
        BinaryContentCreateDto binaryContentCreateDto = null;
        if(multipartFile != null && !multipartFile.isEmpty()){
            binaryContentCreateDto = binaryContentMapper.toCreateDto(multipartFile);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto, Optional.ofNullable(binaryContentCreateDto)));
    }

    @Operation(summary = "User 정보 수정", operationId = "update",
            parameters = @Parameter(name = "userId",description = "수정할 User ID")
            )
    @ApiResponse(responseCode = "200",description = "User 정보가 성공적으로 수정됨")
    @PatchMapping(path = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID userId, @Valid @RequestPart("userUpdateRequest") UserUpdateRequest dto,
                                              @RequestPart(value = "profile",required = false) MultipartFile multipartFile) {
        BinaryContentCreateDto binaryContentCreateDto = null;
        if(multipartFile != null && !multipartFile.isEmpty()){
            binaryContentCreateDto = binaryContentMapper.toCreateDto(multipartFile);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(userId, dto,Optional.ofNullable(binaryContentCreateDto)));
    }

    @Operation(
            summary = "User 삭제",
            operationId = "delete",
            parameters = @Parameter(name ="userId",description = "삭제할 User ID",required = true))
    @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId",
                parameters = @Parameter(name = "userId", description = "상태를 변경할 User ID",required = true))
    @ApiResponse(responseCode = "200",description = "User 온라인 상태가 성공적으로 업데이트됨")
    @PatchMapping(path="/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatus(@PathVariable UUID userId, @Valid @RequestBody UserStatusUpdateDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(userStatusService.updateByUserId(userId,dto));
    }
/*
    //추가
   //특정 사용자 조회 - 누구나 조회가능!
    @RequestMapping(path ="/{userid}" , method= RequestMethod.GET)
    public ResponseEntity<UserDto> findUser(@PathVariable UUID userid) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.find(userid));
    }

 */
}
