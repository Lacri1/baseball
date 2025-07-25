package com.baseball.game.controller;

import com.baseball.game.dto.BoardDto;
import com.baseball.game.dto.BoardDetailDto;
import com.baseball.game.dto.BoardPageResponse;
import com.baseball.game.dto.BoardRequestDto;
import com.baseball.game.dto.CommentDto;
import com.baseball.game.service.BoardService;
import com.baseball.game.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/board")
public class BoardController {

	private final BoardService boardService;
	private final CommentService commentService;

	@Autowired // 생성자가 1개면 생략 가능
	public BoardController(BoardService boardService, CommentService commentService) {
		this.boardService = boardService;
		this.commentService = commentService;
	}

	// 게시글 목록 (카테고리별 조회 포함)
	@GetMapping
	public ResponseEntity<BoardPageResponse> getBoards(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String category) {
		BoardPageResponse response = (category == null)
				? boardService.getPagedList(page, size)
				: boardService.getPagedListCategory(page, size, category);
		return ResponseEntity.ok(response);
	}

	// 게시글 상세 조회 (댓글 포함)
	@GetMapping("/{no}")
	public ResponseEntity<BoardDetailDto> getBoard(@PathVariable("no") int no) {
		BoardDto board = boardService.getBoard(no);
		ArrayList<CommentDto> comments = commentService.getComment(no);
		return ResponseEntity.ok(new BoardDetailDto(board, comments));
	}

	// 게시글 작성
	@PostMapping
	public ResponseEntity<Void> createBoard(@RequestBody BoardRequestDto requestDto) {
		boardService.write(requestDto);
		return ResponseEntity.ok().build();
	}

	// 게시글 수정
	@PutMapping("/{no}")
	public ResponseEntity<Void> updateBoard(@PathVariable("no") int no, @RequestBody BoardRequestDto requestDto) {
		boardService.modify(no, requestDto.getText());
		return ResponseEntity.ok().build();
	}

	// 게시글 삭제
	@DeleteMapping("/{no}")
	public ResponseEntity<Void> deleteBoard(@PathVariable("no") int no) {
		boardService.delete(no);
		return ResponseEntity.ok().build();
	}
}
