package com.jagha.gravix.contoller;

import com.jagha.gravix.dto.board.BoardRequest;
import com.jagha.gravix.dto.board.BoardResponse;
import com.jagha.gravix.service.BoardService;
import com.jagha.gravix.service.interfaces.BoardServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
//@Tags()
@SecurityRequirement(name = "bearerAuth")
public class BoardController {

    private final BoardServiceInterface boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    @Operation(summary = "Create a new board")
    public ResponseEntity<BoardResponse> createNewBoard(
            @Valid @RequestBody BoardRequest board) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.createBoard(board));
    }

    @GetMapping
    @Operation(summary = "Get all boards owned by the user")
    public ResponseEntity<List<BoardResponse>> getMyBoards() {
        return ResponseEntity.ok(boardService.getMyBoards());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get board by Id")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a board")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable Long id, @RequestBody BoardRequest request) {
        return ResponseEntity.ok(boardService.updateBoard(id, request));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
