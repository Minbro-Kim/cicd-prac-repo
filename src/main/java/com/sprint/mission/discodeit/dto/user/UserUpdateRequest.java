package com.sprint.mission.discodeit.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
        @JsonProperty("newUsername")
        String username,
        //@Email(message = "이메일 형식이 올바르지 않습니다.")
        @JsonProperty("newEmail")
        String email,
        @JsonProperty("newPassword")
        String password
) {
}
