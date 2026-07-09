package com.jagha.gravix.dto.dependency;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DependencyGraphResponse {

    private Long boardId;
    private List<Long> topologicalOrder;
    private List<Long> criticalPath;
    private int criticalPathLength;
    private boolean hasCycle;
    private List<Long> cyclePath;

}
