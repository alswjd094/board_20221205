package com.example.board.entity;

import com.example.board.dto.CommentDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.xml.stream.events.Comment;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20,nullable = false)
    private String commentWriter;

    @Column(length = 200,nullable = false)
    private String commentContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id") //테이블에 생성될 컬럼 이름
    private BoardEntity boardEntity;

public static CommentEntity toCommentEntity(BoardEntity entity,CommentDTO commentDTO){
    CommentEntity commentEntity = new CommentEntity();
    commentEntity.setBoardEntity(entity);
    commentEntity.setCommentWriter(commentDTO.getCommentWriter());
    commentEntity.setCommentContents(commentDTO.getCommentContents());
    return commentEntity;
}

}
