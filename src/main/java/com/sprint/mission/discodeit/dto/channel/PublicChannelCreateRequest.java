package com.sprint.mission.discodeit.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
@Schema(description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(
        @NotEmpty
        String name,
        String description
) {
}
