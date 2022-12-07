package com.example.board.controller;

import com.example.board.dto.BoardDTO;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
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
        model.addAttribute("findAll",boardDTOList);
        return "boardPages/boardList";
   }
   @GetMapping("/board/{id}")
    public String findById(@PathVariable Long id, Model model){
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board",boardDTO);
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
