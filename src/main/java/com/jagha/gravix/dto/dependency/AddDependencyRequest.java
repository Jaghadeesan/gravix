package com.jagha.gravix.dto.dependency;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddDependencyRequest {

    @NotNull(message = "Dependency task id is required")
    private Long dependencyTaskId;

    @NotNull(message = "Dependent task id is required")
    private Long dependentTaskId;
}
