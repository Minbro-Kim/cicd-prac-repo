package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
@Schema(description = "로그인 정보")
public record LoginRequest(
        @NotEmpty
        String username,
        @NotEmpty
        String password
) {
}
