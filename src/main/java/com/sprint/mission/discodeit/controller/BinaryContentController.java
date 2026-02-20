package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {
    /*
    ### **바이너리 파일 다운로드**
    - [ ]  바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
     */

    private final BinaryContentService binaryContentService;

    @Operation(summary = "첨부 파일 조회", operationId = "find")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "fieldErrors": null,
                                      "violationErrors": null,
                                      "code": 404,
                                      "message": "존재하지 않는 채널"
                                    }
                                """)
                    )
            )
    })
    @GetMapping(path = "/{binaryContentId}")
    public ResponseEntity<BinaryContent> findProfileImage(@PathVariable UUID binaryContentId) {
        return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.find(binaryContentId));
    }

    @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn",
                parameters = @Parameter(name = "binaryContentIds",description = "조회할 첨부 파일 ID 목록"))
    @ApiResponse(responseCode = "200", description = "첨부파일 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<BinaryContent>> findImages(@RequestParam List<UUID> binaryContentIds){
        return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.findAllByIdIn(binaryContentIds));
    }
}
