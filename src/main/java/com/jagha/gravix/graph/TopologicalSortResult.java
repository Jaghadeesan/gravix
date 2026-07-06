package com.jagha.gravix.graph;

import lombok.Getter;

import java.util.List;

@Getter
public class TopologicalSortResult {

    private final boolean hasCycle;
    private final List<Long> sortedOrder;

    public TopologicalSortResult(boolean hasCycle, List<Long> sortedOrder) {
        this.hasCycle = hasCycle;
        this.sortedOrder = sortedOrder;
    }

    public static TopologicalSortResult withCycle() {
        return new TopologicalSortResult(true, List.of());
    }

    public static TopologicalSortResult success(List<Long> order) {
        return new TopologicalSortResult(false, order);
    }
}
