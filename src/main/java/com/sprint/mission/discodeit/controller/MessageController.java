package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
public class MessageController {
    /*
     **메시지 관리**
    - [ ]  메시지를 보낼 수 있다.
    - [ ]  메시지를 수정할 수 있다.
    - [ ]  메시지를 삭제할 수 있다.
    - [ ]  특정 채널의 메시지 목록을 조회할 수 있다.
     */
    private final MessageService messageService;
    private final BinaryContentMapper binaryContentMapper;

    @Operation(summary = "Message 생성", operationId = "create_2")
    @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MessageResponseDto> sendMessage(
            //@RequestHeader UUID userId,
            //@PathVariable UUID channelId,
            @RequestPart(value = "messageCreateRequest",required = false) MessageCreateRequest dto,
            @RequestPart(value = "attachments",required = false) List<MultipartFile> multipartFiles)
    {
        List<BinaryContentCreateDto> binaryContentCreateDtos = new ArrayList<>();
        if(multipartFiles != null){
            for (MultipartFile multipartFile : multipartFiles) {
                binaryContentCreateDtos.add(binaryContentMapper.toCreateDto(multipartFile));
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(dto.channelId(),dto.authorId(),dto,binaryContentCreateDtos));
    }

    @Operation(summary = "Message 내용 수정", operationId = "update_2",
                parameters = @Parameter(name = "messageId",description = "수정할 Message ID"))
    @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨")
    @PatchMapping(path = "/{messageId}")
    public ResponseEntity<MessageResponseDto> updateMessage(
            //@RequestHeader UUID userId,//인증/인가
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest dto)
    {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.update(messageId,dto));
    }

    @Operation(summary = "Message 삭제", operationId = "delete_1",
                parameters = @Parameter(name = "messageId",description = "삭제할 Message ID"))
    @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨")
    @DeleteMapping(path = "/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            //@RequestHeader UUID userId,//인증/인가 추가시
            @PathVariable UUID messageId
            )
    {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Channel의 Message 목록 조회",
                parameters = @Parameter(
                        name = "channelId",
                        description = "조회할 Channel ID",
                        required = true))
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> findMessagesByChannelId(
            //@RequestHeader UUID userId,//나중에 인증/인가로
            @RequestParam("channelId") UUID channelId)
    {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.findAllByChannelId(channelId));
    }

}
