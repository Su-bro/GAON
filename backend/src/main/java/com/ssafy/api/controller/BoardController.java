package com.ssafy.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.api.request.BoardRegisterPostReq;
import com.ssafy.api.response.BoardRes;
import com.ssafy.api.service.BoardService;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.Board;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 게시판 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@Api(value = "게시판 API", tags = {"Board"})
@RestController
@RequestMapping("/api/v1/boards")
public class BoardController {
	@Autowired
	BoardService boardService;

	@PostMapping()
	@ApiOperation(value = "게시판 생성", notes = "<strong>(게시판 이름, 게시판 설명, 속한 게시판 id(pk))로 게시판을 생성한다.</strong>") 
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공"),
        @ApiResponse(code = 404, message = "rid에 해당하는 room이 존재하지 않음"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
	public ResponseEntity<BoardRes> create(
			@RequestBody @ApiParam(value="게시판 생성 정보", required = true) BoardRegisterPostReq boardInfo) {
		try {
			Board board = boardService.createBoard(boardInfo);
//			바로는 적용 안됨
//			System.out.println(board.getRoom().getBoards());
			return ResponseEntity.ok(BoardRes.of(200, "Success", board));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(404).body(BoardRes.of(404, "No matching room for rid", null));
		}
	}
	
	
	@GetMapping("/id/{id}")
	@ApiOperation(value = "게시판 정보 id로 찾기", notes = "<strong>게시판 id로 게시판을 찾는다</strong>") 
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
	public ResponseEntity<BoardRes> findById(
			@PathVariable @ApiParam(value="게시판 id", required = true) Long id) {
		try {
			Board board = boardService.getBoardById(id);
			for(Board b : board.getRoom().getBoards()) {
			System.out.println(b.getName());
			}
			return ResponseEntity.ok(BoardRes.of(200, "Success", board));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(BoardRes.of(404, "Board does not exist using this code", null));
		}
	}
	
	@PutMapping("/{id}")
	@ApiOperation(value = "게시판 수정", notes = "<strong>게시판 id로 게시판을 찾아서 수정한다. </strong> </br> 수정하고 싶은 속성을 입력하면 된다 (입력하지 않은 속성은 유지) ") 
    @ApiResponses({
        @ApiResponse(code = 200, message = "성공"),
        @ApiResponse(code = 404, message = "id에 해당하는 게시판이 존재하지 않음"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
	public ResponseEntity<BoardRes> update(
			@PathVariable @ApiParam(value="수정하고싶은 게시판 id", required = true) Long id,
			@RequestBody @ApiParam(value="수정하고싶은 게시판 정보", required = true, example="{\n \"name\":\"String\", \n \"description\":\"String\"\n}") Map<String, String> boardInfo) {
		try {
			Board board = boardService.modifyBoard(id, boardInfo);
			return ResponseEntity.ok(BoardRes.of(200, "Success", board));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(BoardRes.of(404, "Board does not exist using this id", null));
		}
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation(value = "게시판 삭제", notes = "<strong>게시판 id로 게시판을 삭제한다.</strong>") 
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공"),
		@ApiResponse(code = 404, message = "해당 게시판 없음"),
		@ApiResponse(code = 500, message = "서버 오류")
	})
	public ResponseEntity<BaseResponseBody> delete(
			@PathVariable @ApiParam(value="게시판 id", required = true) Long id) {
		try {
			if(boardService.removeBoard(id)) {
				return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
			}
			else {
				return ResponseEntity.status(404).body(BaseResponseBody.of(404, "Board does not exist"));
			}
		} catch (Exception e) {
			return ResponseEntity.status(500).body(BaseResponseBody.of(404, "server error"));
		}
	}
}
