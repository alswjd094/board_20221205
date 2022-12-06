package com.example.board.repository;

import com.example.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity,Long> {
@Modifying
    @Query(value = "update BoardEntity b set b.boardHits = b.boardHits+1 where b.id = :id ")
                                                                                    //param값과 동일
//    @Query(value = "update board_table set board_hits=board_hits+1 where id = :id",nativeQuery = true)
    void updateHits(@Param("id") Long id);
}
