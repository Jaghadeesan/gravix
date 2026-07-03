package com.jagha.collabflow.service;

import com.jagha.collabflow.dto.board.BoardRequest;
import com.jagha.collabflow.dto.board.BoardResponse;
import com.jagha.collabflow.entity.Board;
import com.jagha.collabflow.entity.User;
import com.jagha.collabflow.repository.BoardRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private BoardRespository boardRespository;

    @Mock
    private AuthHelper authHelper;

    @InjectMocks
    private BoardService boardService;

    private User mockUser;
    private Board mockBoard;
    private BoardRequest boardRequest;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setFullName("Test User");

        mockBoard = new Board();
        mockBoard.setId(1L);
        mockBoard.setName("Test Board");
        mockBoard.setDescription("Test Board Description");
        mockBoard.setOwner(mockUser);
        mockBoard.setCreatedAt(Instant.now());

        boardRequest = new BoardRequest();
        boardRequest.setName("Test Board");
        boardRequest.setDescription("Test Board Description");
    }

    @Test
    void createBoard_Success() {
        when(authHelper.getCurrentUser()).thenReturn(mockUser);
        when(boardRespository.save(any())).thenReturn(mockBoard);

        BoardResponse response = boardService.createBoard(boardRequest);

        assertNotNull(response);
        assertEquals("Test Board", response.getName());
        assertEquals("Test Board Description", response.getDescription());
        assertEquals(1L, response.getId());
        verify(boardRespository, times(1)).save(any(Board.class));
    }

    @Test
    void getMyBoards_ReturnsOnlyUserBoards() {
        when(authHelper.getCurrentUser()).thenReturn(mockUser);
        when(boardRespository.findByOwnerId(1L)).thenReturn(List.of(mockBoard));

        List<BoardResponse> responses = boardService.getMyBoards();

        assertEquals(1, responses.size());
        assertEquals("Test Board", responses.get(0).getName());
    }

    @Test
    void getBoardById_NotFound_ThrowsNotFoundException() {
        when(boardRespository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> boardService.getBoardById(99L));

        assertEquals("Board not found", exception.getMessage());
    }

    @Test
    void updateBoard_NotOwner_ThrowsException() {
        User otheruser = new User();
        otheruser.setId(2L);

        when(authHelper.getCurrentUser()).thenReturn(otheruser);
        when(boardRespository.findById(1L)).thenReturn(Optional.of(mockBoard));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> boardService.updateBoard(1L, boardRequest));

        assertTrue(exception.getMessage().contains("permission"));
        verify(boardRespository, never()).save(any());
    }

    @Test
    void deleteBoard_Owner_Success() {
        when(authHelper.getCurrentUser()).thenReturn(mockUser);
        when(boardRespository.findById(1L)).thenReturn(Optional.of(mockBoard));

        boardService.deleteBoard(1L);

        verify(boardRespository, times(1)).delete(mockBoard);
    }
}
