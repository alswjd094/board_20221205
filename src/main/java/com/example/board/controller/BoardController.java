package com.example.board.controller;

import com.example.board.dto.BoardDTO;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String save(@ModelAttribute BoardDTO boardDTO){
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
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board",boardDTO);
        return"boardPages/boardDetail";
   }
   @GetMapping("/board/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/";
   }

}
