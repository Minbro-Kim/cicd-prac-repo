package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {
    private final ReadStatusService readStatusService;
    /*
    ### **메시지 수신 정보 관리**
    - [ ]  특정 채널의 메시지 수신 정보를 생성할 수 있다.
    - [ ]  특정 채널의 메시지 수신 정보를 수정할 수 있다.
    - [ ]  특정 사용자의 메시지 수신 정보를 조회할 수 있다.
     */

    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨")
    @PostMapping
    public ResponseEntity<ReadStatus> createReadStatus(@Valid @RequestBody ReadStatusCreateRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(dto));
    }

    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1",
                parameters = @Parameter(name = "readStatusId",description = "수정할 읽음 상태 ID"))
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨")
    @PatchMapping(path="/{readStatusId}")
    public ResponseEntity<ReadStatus> updateReadStatus(@PathVariable UUID readStatusId,
                                                           @RequestBody ReadStatusUpdateRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.update(readStatusId,dto));
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회",
            parameters = @Parameter(
                    name = "userId",
                    description = "조회할 User ID",
                    required = true
            )
    )
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId){
        return ResponseEntity.status(HttpStatus.OK).body(readStatusService.findAllByUserId(userId));
    }
}
