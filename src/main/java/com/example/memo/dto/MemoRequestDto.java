package com.example.memo.dto;

import lombok.Getter;

@Getter
public class MemoRequestDto { // 요청할 dto

    private String title;
    private String contents;
}
