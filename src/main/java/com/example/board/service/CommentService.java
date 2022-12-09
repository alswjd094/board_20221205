package com.example.board.service;

import com.example.board.dto.CommentDTO;
import com.example.board.entity.BoardEntity;
import com.example.board.entity.CommentEntity;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    public Long save(CommentDTO commentDTO) {

     //게시글(부모데이터)를 조회
     BoardEntity entity = boardRepository.findById(commentDTO.getBoardId()).get();
     //comment_table 저장
     //연관관계가 있는 자식데이터를 저장할 때에는 부모데이터를 가져가야 함
     CommentEntity commentEntity = CommentEntity.toCommentEntity(entity,commentDTO);
     Long id = commentRepository.save(commentEntity).getId();
     return id;
    }
@Transactional
    public List<CommentDTO> findAll(Long boardId) {
        //select*from comment_table where board_id=? ,id값만 필요하지만 entity 전체 사용
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        //1. comment_table에서 직접 해당 게시글의 댓글 목록을 가져오기
        List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        //2. BoardEntity를 조회해서 댓글 목록 가져오기(boardEntity에 commentEntityList있음)
//        List<CommentEntity> commentEntities = boardEntity.getCommentEntityList();
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for(CommentEntity commentEntity: commentEntityList){
          commentDTOList.add(CommentDTO.toCommentDTO(commentEntity));
        }
        return commentDTOList;
    }
}
