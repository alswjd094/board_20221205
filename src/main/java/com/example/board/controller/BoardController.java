package com.example.board.controller;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.CommentDTO;
import com.example.board.service.BoardService;
import com.example.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/board/save")
    public String saveForm(){
        return "boardPages/boardSave";
    }

    @PostMapping("/board/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
         boardService.save(boardDTO);
             return "redirect:/board/";
    }
   @GetMapping("/board/")
    public String findAll(Model model){
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList",boardDTOList);
        return "boardPages/boardList";
   }
   @GetMapping("/board")
   public String paging(@PageableDefault(page = 1)Pageable pageable, Model model){
       System.out.println("page"+pageable.getPageNumber());
       //해당 페이지에서 보여줄 글 목록
       Page<BoardDTO> boardDTOList = boardService.paging(pageable);
       model.addAttribute("boardList",boardDTOList);
       int blockLimit = 3;
       //시작 페이지 값 계산
       int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
      //끝 페이지 값 계산(3, 6, 9, 12---)
       //endPage 값이 totalPage값보다 크다면 endPage값을 totalPage값으로 덮어쓴다.
       int endPage = ((startPage + blockLimit - 1) < boardDTOList.getTotalPages()) ? startPage + blockLimit - 1 : boardDTOList.getTotalPages();
       //삼항연산자
       int test = 10;
       int num = (test>5)?test:100;
       if(test>5){
           num=test;
       }else{
           num=100;
       }

       model.addAttribute("startPage", startPage);
       model.addAttribute("endPage", endPage);

       return "boardPages/paging";
   }

   @GetMapping("/board/{id}")
    public String findById(@PathVariable Long id, Model model){
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board",boardDTO);
        List<CommentDTO> commentDTOList = commentService.findAll(id);
       if (commentDTOList.size() > 0) {
           model.addAttribute("commentList", commentDTOList);
       }else {
           model.addAttribute("commentList", "empty");
       }
        return"boardPages/boardDetail";
   }
   @GetMapping("/board/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/";
   }

   @GetMapping("/board/update/{id}")
    public String updateForm(@PathVariable Long id, Model model){
       BoardDTO updateForm =  boardService.findById(id);
       model.addAttribute("board",updateForm);
       return "boardPages/boardUpdate";
   }

   @PostMapping("/board/update")
    public String update(@ModelAttribute BoardDTO boardDTO,Model model){
        boardService.update(boardDTO);
       BoardDTO dto = boardService.findById(boardDTO.getId());
       model.addAttribute("board",dto);
       return"boardPages/boardDetail";
   }

   @PutMapping("/board/{id}")
    public ResponseEntity update(@RequestBody BoardDTO boardDTO){
        boardService.update(boardDTO);
        return new ResponseEntity<>(HttpStatus.OK);
   }

   @DeleteMapping("/board/{id}")
    public ResponseEntity deleteAxios(@PathVariable Long id){
        boardService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
   }

}
