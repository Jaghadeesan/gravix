package com.jagha.gravix.service.interfaces;

import com.jagha.gravix.dto.board.BoardRequest;
import com.jagha.gravix.dto.board.BoardResponse;

import java.util.List;

public interface BoardServiceInterface {

    BoardResponse createBoard(BoardRequest request);

    List<BoardResponse> getMyBoards();

    BoardResponse getBoardById(Long id);

    BoardResponse updateBoard(Long id, BoardRequest request);

    void deleteBoard(Long id);

}
