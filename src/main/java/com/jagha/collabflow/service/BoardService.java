package com.jagha.collabflow.service;

import com.jagha.collabflow.dto.board.BoardRequest;
import com.jagha.collabflow.dto.board.BoardResponse;
import com.jagha.collabflow.entity.Board;
import com.jagha.collabflow.entity.User;
import com.jagha.collabflow.repository.BoardRespository;
import com.jagha.collabflow.service.interfaces.BoardServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService implements BoardServiceInterface {

    private final BoardRespository boardRespository;
    private final AuthHelper authHelper;

    public BoardResponse createBoard(BoardRequest request) {
        User currentUser = authHelper.getCurrentUser();

        Board board = new Board();
        board.setName(request.getName());
        board.setDescription(request.getDescription());
        board.setOwner(currentUser);

        Board saved = boardRespository.save(board);
        return toResponse(saved);
    }

    public List<BoardResponse> getMyBoards() {
        User currentUser = authHelper.getCurrentUser();

        return boardRespository.findByOwnerId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BoardResponse getBoardById(Long id) {
        Board board = boardRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        return toResponse(board);
    }

    public BoardResponse updateBoard(Long id, BoardRequest request) {
        User currentUser = authHelper.getCurrentUser();

        Board board = boardRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        // Authorization check — only owner can update
        if(!currentUser.getId().equals(board.getOwner().getId())) {
            throw new RuntimeException("You don't have permission to update this board");
        }

        board.setName(request.getName());
        board.setDescription(request.getDescription());

        return toResponse(boardRespository.save(board));
    }

    public void deleteBoard(Long id) {
        User currentUser = authHelper.getCurrentUser();

        Board board = boardRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        if(!currentUser.getId().equals(board.getOwner().getId())) {
            throw new RuntimeException("You don't have permission to delete this board");
        }
        boardRespository.delete(board);
    }

    // Convert entity to DTO — keeps controller/service clean
    private BoardResponse toResponse(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getOwner().getId(),
                board.getOwner().getFullName(),
                board.getCreatedAt()
        );
    }
}
