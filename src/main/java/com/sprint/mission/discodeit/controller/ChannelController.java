package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {
    /*
    **채널 관리**
    - [ ]  공개 채널을 생성할 수 있다.
    - [ ]  비공개 채널을 생성할 수 있다.
    - [ ]  공개 채널의 정보를 수정할 수 있다.
    - [ ]  채널을 삭제할 수 있다.
    - [ ]  특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
     */
    private final ChannelService channelService;

    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨")
    @PostMapping("/public")//나중에 소유자 지정
    public ResponseEntity<ChannelDto> createPublicChannel(@Valid @RequestBody PublicChannelCreateRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(dto));
    }

    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨")
    @PostMapping("/private")//나중에 소유자 지정
    public ResponseEntity<ChannelDto> createPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(dto));
    }

    @Operation(summary = "Channel 정보 수정", operationId = "update_3",
            parameters = @Parameter(name = "channelId", description = "수정할 Channel ID"))
    @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨")

    @PatchMapping("/{channelId}")//나중에 소유자가 정해진다면, 권한확인필요
    public ResponseEntity<ChannelDto> updatePublicChannel(@PathVariable UUID channelId, @RequestBody PublicChannelUpdateRequest dto) {
        return ResponseEntity.status(HttpStatus.OK).body(channelService.update(channelId,dto));
    }

    @Operation(summary = "Channel 삭제", operationId = "delete_2",
                parameters = @Parameter(name = "channelId", description = "삭제할 Channel ID"))
    @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨")
    @DeleteMapping("/{channelId}") //나중에 소유자가 정해진다면, 권한확인필요
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회",operationId = "findAll_1",
                parameters = @Parameter(name = "userId",description = "조회할 User ID"))
    @ApiResponse(responseCode = "200",description = "Channel 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findChannelsByUser(@RequestParam UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(channelService.findAllByUserId(userId));
    }

}
