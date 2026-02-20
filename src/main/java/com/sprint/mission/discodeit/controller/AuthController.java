package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인", operationId = "login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "fieldErrors": null,
                                      "violationErrors": null,
                                      "code": 404,
                                      "message": "존재하지 않는 사용자"
                                    }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "일치하지 않는 로그인 정보",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "fieldErrors": null,
                                      "violationErrors": null,
                                      "code": 400,
                                      "message": "잘못된 아이디 또는 비밀번호"
                                    }
                                """)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<UserDto> login (@RequestBody @Valid LoginRequest dto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(dto));
    }
}
