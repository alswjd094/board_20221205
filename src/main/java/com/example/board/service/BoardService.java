package com.example.board.service;

import com.example.board.dto.BoardDTO;
import com.example.board.entity.BoardEntity;
import com.example.board.entity.BoardFileEntity;
import com.example.board.entity.MemberEntity;
import com.example.board.repository.BoardFileRepository;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    private final MemberRepository memberRepository;

    public Long save(BoardDTO boardDTO) throws IOException {
       MemberEntity memberEntity = memberRepository.findByMemberEmail(boardDTO.getBoardWriter()).get();
//        if(boardDTO.getBoardFile().isEmpty()){
        if(boardDTO.getBoardFile() == null || boardDTO.getBoardFile().size()==0){
            System.out.println("파일 없음");
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO,memberEntity);
            return boardRepository.save(boardEntity).getId();
        } else{
            System.out.println("파일 있음");
            //dto->entity 옮겨담기
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO,memberEntity);
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

        List<BoardEntity> boardEntityList = boardRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        //page: 가장 첫번째 데이터(0)
        final int pageLimit = 3;
        //pageLimit: 개수 , (0,3) 첫번째 자리부터 3개
        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
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
        return boardList;
    }

    public List<BoardDTO> search(String type, String q) {
        List<BoardDTO> boardDTOList = new ArrayList<>();
        List<BoardEntity> boardEntityList = null;
        if(type.equals("boardWriter")){
            boardEntityList = boardRepository.findByBoardWriterContainingOrderByIdDesc(q);
        } else if(type.equals("boardTitle")){
            boardEntityList = boardRepository.findByBoardTitleContainingOrderByIdDesc(q);
        } else{
            boardEntityList = boardRepository.findByBoardTitleContainingOrBoardWriterContainingOrderByIdDesc(q,q);
        }
        for(BoardEntity boardEntity:boardEntityList){
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            boardDTOList.add(boardDTO);
        }
        return boardDTOList;
    }
}
