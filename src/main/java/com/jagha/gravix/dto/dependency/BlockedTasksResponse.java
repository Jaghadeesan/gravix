package com.jagha.gravix.dto.dependency;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class BlockedTasksResponse {

    private Long taskId;
    private Set<Long> allBlockedTaskIds;
    private List<Long> directBlockerIds;
    private int totalBlockedCount;
}
