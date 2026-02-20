package com.sprint.mission.discodeit.dto.userstatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;
@Schema(description = "변경할 User 온라인 상태 정보")
public record UserStatusUpdateDto(
        @JsonProperty("newLastActiveAt")
        Instant lastActiveAt
) {
}
