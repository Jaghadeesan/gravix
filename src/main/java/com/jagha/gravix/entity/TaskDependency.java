package com.jagha.gravix.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "task_dependencies",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"dependent_task_id", "dependency_task_id"},
                        name = "uk_task_dependency"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The task that depends on another Task
    // e.g. "Deploy API" depends on "Write API"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependent_task_id", nullable = false)
    private Task dependentTask;

    // The task that MUST BE DONE FIRST
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependency_task_id", nullable = false)
    private Task dependencyTask;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}
