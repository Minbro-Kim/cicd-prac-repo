package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;
@Schema(description = "Private Channel 생성 정보")
public record PrivateChannelCreateRequest(
        @NotEmpty
        @JsonProperty("participantIds")
        List<UUID> memberIds
) {
}
