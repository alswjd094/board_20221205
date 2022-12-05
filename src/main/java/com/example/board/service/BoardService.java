package com.example.board.service;

import com.example.board.dto.BoardDTO;
import com.example.board.entity.BoardEntity;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    public Long save(BoardDTO boardDTO) {
     BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
     return boardRepository.save(boardEntity).getId();
    }

    public List<BoardDTO> findAll() {

        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for(BoardEntity boardEntity:boardEntityList){
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
//            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
//            boardDTOList.add(boardDTO);
        }
        return boardDTOList;
    }

    public BoardDTO findById(Long id) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(id);
       if(boardEntityOptional.isPresent()){
//           BoardEntity boardEntity = boardEntityOptional.get();
//           BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
//           return boardDTO;
           return BoardDTO.toBoardDTO(boardEntityOptional.get());
       }else{
           return null;
       }
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }
}
