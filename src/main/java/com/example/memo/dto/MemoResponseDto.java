package com.example.memo.dto;

import com.example.memo.entity.Memo;
import lombok.Getter;

@Getter
public class MemoResponseDto { // 응답할 dto

    private Long id; // 식별자
    private String title;
    private String contents;

    // 생성자
    public MemoResponseDto(Memo memo) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.contents = memo.getContents();
    }
    // memo 객체가 그대로 반환되는게 아니라 ResponseDto 형태로 바껴서 응답

}
