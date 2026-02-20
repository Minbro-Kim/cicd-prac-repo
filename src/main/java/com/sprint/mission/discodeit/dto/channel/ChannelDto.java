package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        @JsonProperty("type")
        ChannelType channelType,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        Instant lastMessageAt,
        @JsonProperty("participantIds")
        List<UUID> memberIds//public이면 모든 사용자의 아이디
) {
}
