package com.example.board.service;

import com.example.board.dto.BoardDTO;
import com.example.board.entity.BoardEntity;
import com.example.board.entity.BoardFileEntity;
import com.example.board.repository.BoardFileRepository;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public Long save(BoardDTO boardDTO) throws IOException {
//        if(boardDTO.getBoardFile().isEmpty()){
        if(boardDTO.getBoardFile().size()==0){
            System.out.println("파일 없음");
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            return boardRepository.save(boardEntity).getId();
        } else{
            System.out.println("파일 있음");
            //dto->entity 옮겨담기
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            //게시글(부모 데이터) 저장, id값 추가된 객체 가져옴
            Long savedId = boardRepository.save(boardEntity).getId();
            //부모데이터를 조회해야함
            BoardEntity entity = boardRepository.findById(savedId).get();

            for(MultipartFile boardFile:boardDTO.getBoardFile()){
                //파일 원본 이름 가져오기
                String originalFileName = boardFile.getOriginalFilename();
                //서버 관리용 이름 만듦
                String storedFileName = System.currentTimeMillis()+"_"+originalFileName;
                //파일 실제 저장 위치 지정
                String savePath = "C:\\springboot_img\\"+storedFileName;
                //파일 저장 처리
                boardFile.transferTo(new File(savePath));
                //boardfileentity 변환할 때 부모entity와 파일원본이름,저장이름을 같이 넘겨줌
                BoardFileEntity boardFileEntity = BoardFileEntity.toSaveBoardFileEntity(entity,originalFileName,storedFileName);

                boardFileRepository.save(boardFileEntity);
            }
            return savedId;
        }

    }
@Transactional
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
@Transactional//부모엔티티에서 자식엔티티를 직접 가져올 때 필요
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
    @Transactional
//특별한 쿼리를 호출하는 경우 작성
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public void update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
    }
}
