package com.example.board;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.CommentDTO;
import com.example.board.entity.BoardEntity;
import com.example.board.entity.CommentEntity;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;
import com.example.board.service.BoardService;
import com.example.board.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class BoardTest {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
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

    @Test
    @Transactional
    @Rollback(value = false)
    @DisplayName("글작성 여러개 샘플 데이터")
    public void saveList() throws IOException {
        for(int i = 1; i<=20; i++){
            boardService.save(newBoardDTO(i));
        }

//        IntStream.rangeClosed(21,40).forEach(i->{
//                //21~40숫자 만들어 줌
//            boardService.save(newBoardDTO(i));
//        });
    }

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
    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("댓글작성 테스트")
    public void commentSaveTest() throws IOException {
        //1. 게시글 작성
        BoardDTO boardDTO = newBoardDTO(100);
        Long savedId = boardService.save(boardDTO);
        //2. 댓글 작성
        CommentDTO commentDTO = newComment(savedId,1);
        Long commentSavedId = commentService.save(commentDTO);
        //3. 저장된 댓글 아이디로 댓글 조회
        CommentEntity commentEntity = commentRepository.findById(commentSavedId).get();
        //4. 댓글 작성자와 db에 저장된 댓글 작성자 일치하는지
        assertThat(commentDTO.getCommentWriter()).isEqualTo(commentEntity.getCommentWriter());

    }
    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("댓글 목록 테스트")
    public void commentListTest() throws IOException {
        //1.게시글 작성
        BoardDTO boardDTO = newBoardDTO(100);
        Long savedId = boardService.save(boardDTO);

        //2. 해당 게시글에 댓글 3개 작성
        IntStream.rangeClosed(1,3).forEach(i->{
            CommentDTO commentDTO = newComment(savedId,i);
            commentService.save(commentDTO);
        });

        //3. 댓글 목록 조회했을 때 목록 개수가 3이면 테스트 통과
       List<CommentDTO> commentDTOList = commentService.findAll(savedId);
        assertThat(commentDTOList.size()).isEqualTo(3);

    }
    private CommentDTO newComment(Long boardId, int i){
                                  //게시글 번호
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setCommentWriter("commentWriter"+i);
        commentDTO.setCommentContents("commentContents"+i);
        commentDTO.setBoardId(boardId);
        return commentDTO;
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("페이징 객체 확인")
    public void pagingParams(){
        int page = 0;
        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id")));
        // Page 객체가 제공해주는 메서드 확인
        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청페이지에 들어있는 데이터
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글개수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // 요청페이지(jpa 기준)
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 개수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한페이지에 보여지는 글개수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫페이지인지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막페이지인지 여부

        //Page<BoardEntity> -> Page<BoardDTO>
        Page<BoardDTO> boardList = boardEntities.map(
                //boardEntities에 담김 boardEntity 객체를 board에 담아서
                //boardDTO 객체로 하나씩 옮겨 담는 과정
                //boardDTO 생성자 순서와 동일하게
                board->new BoardDTO(board.getId(),
                                    board.getBoardWriter(),
                                    board.getBoardTitle(),
                                    board.getBoardHits(),
                                    board.getCreatedTime()));

 }
}
