package com.example.board;
import com.example.board.dto.BoardDTO;
import com.example.board.entity.BoardEntity;
import com.example.board.repository.BoardRepository;
import com.example.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.io.IOException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class BoardTest {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;
    private BoardDTO newBoardDTO(int i){
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoardWriter("writer"+i);
        boardDTO.setBoardTitle("title"+i);
        boardDTO.setBoardPass("pass"+i);
        boardDTO.setBoardContents("contents"+i);
        return boardDTO;
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("글작성 테스트")
    public void BoardSaveTest() throws IOException {
        //글작성 기능 수행
        BoardDTO boardDTO = newBoardDTO(1);
        //service save메서드 호출, 리턴 받기
        Long savedId = boardService.save(boardDTO);
        //DB에서 조회한 writer
        BoardDTO savedBoard = boardService.findById(savedId);
        //글 작성시 사용한 writer와 DB에서 조회한 writer 일치하는지 판단
        assertThat(boardDTO.getBoardWriter()).isEqualTo(savedBoard.getBoardWriter());
    }

//    @Test
//    @Transactional
//    @Rollback(value = false)
//    @DisplayName("글작성 여러개 샘플 데이터")
//    public void saveList(){
//        for(int i = 1; i<=20; i++){
//            boardService.save(newBoardDTO(i));
//        }
//
//        IntStream.rangeClosed(21,40).forEach(i->{
//                //21~40숫자 만들어 줌
//            boardService.save(newBoardDTO(i));
//        });
//    }

//    @Test
//    @Transactional
//    @Rollback(value = true)
//    @DisplayName("삭제 테스트")
//    public void deleteTest(){
//        BoardDTO boardDTO = newBoardDTO(1);
//        Long savedId = boardService.save(boardDTO);
//
//        boardService.delete(savedId);
//
//        assertThat(boardService.findById(savedId)).isNull();
//    }
//
//    @Test
//    @Transactional
//    @Rollback(value = true)
//    @DisplayName("수정 테스트")
//    public void updateTest(){
//        BoardDTO boardDTO = newBoardDTO(1);
//        Long savedId = boardService.save(boardDTO);
//
//        boardDTO.setId(savedId);
//        boardDTO.setBoardTitle("수정제목");
//        boardDTO.setBoardContents("수정내용");
//
//        boardService.update(boardDTO);
//
//        BoardDTO boardDB = boardService.findById(savedId);
//
//        assertThat(boardDB.getBoardTitle()).isEqualTo(boardDTO.getBoardTitle());
//        assertThat(boardDB.getBoardContents()).isEqualTo(boardDTO.getBoardContents());
//    }

    @Test
    @Transactional
    @DisplayName("연관관계 조회 테스트")
    public void findTest(){
        //파일이 첨부된 게시글(부모테이블) 조회
       BoardEntity boardEntity = boardRepository.findById(1L).get();
       //첨부파일(자식테이블)의 originalFileName 조회
        System.out.println("boardEntity.getBoardFileEntityList() = " + boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());

    }
}
