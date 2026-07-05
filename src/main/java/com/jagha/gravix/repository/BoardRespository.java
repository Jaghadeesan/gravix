package com.jagha.gravix.repository;

import com.jagha.gravix.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRespository extends JpaRepository<Board,Long> {

    // Find all boards belonging to a specific user
    List<Board> findByOwnerId(Long ownerId);

    // Check if a board belongs to a user (for authorization)
    boolean existsByIdAndOwnerId(Long id,Long ownerId);
}
