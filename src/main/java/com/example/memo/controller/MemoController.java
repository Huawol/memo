package com.example.memo.controller;

import com.example.memo.dto.MemoRequestDto;
import com.example.memo.dto.MemoResponseDto;
import com.example.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// 데이터를 항상 JSON 형태로 통신함
@RestController
@RequestMapping("/memos") // 프리픽스 : url을 설정할때 사용한다
public class MemoController {

    // 실제로 데이터베이스에 저장하는게 아니라 자료구조 형식으로 임시로 저장함
    private final Map<Long, Memo> memoList = new HashMap<>(); // HashMap으로 초기화

    // 메모 생성 (C)
    @PostMapping // 생성이기 때문에 어노테인션 포스트메핑을 사용
    public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoRequestDto dto) {

        // 식별자가 1씩 증가하도록  만듬
        Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;
        // 삼항연산자로 memoList에 값이 비어있으면 1로 초기 설정을 하고
        // Collections안에 max라는 함수를 이용
        // Collections.max 매서드는 memoList.keySet() 에서 최대값을 뽑아냄
        // memoList.KeySet() 은 memoList안에 있는 key값을 다 꺼내옴

        // 요청받은 데이터로 Memo 객체 생성
        Memo memo = new Memo(memoId, dto.getTitle(), dto.getContents());

        // Inmemory DB에 Memo 저장
        memoList.put(memoId, memo);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.CREATED);
    }

    @GetMapping
    public List<MemoResponseDto> findAllMemos() { // 새로운 기능! 메모 목록 기능 ( 전체 조회 )
        //init List
        List<MemoResponseDto> responseList = new ArrayList<>();

        // HashMap List<Memo -> List<MemoresponseDto>
        for (Memo memo : memoList.values()) {
            MemoResponseDto responseDto = new MemoResponseDto(memo);
            responseList.add(responseDto);
        }
        // Map To List stream에 익숙해지면 그때 사용해보자(이렇게도 사용할수있다)
        //responseList = memoList.values().stream().map(MemoResponseDto::new).toList();

        return responseList;
    }


    // 메모 조회 (R)
    @GetMapping("/{id}") // 식별자를 담음
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) { // 식별자를 파라미터로 바인딩할때는 PathVariable을 사용

        Memo memo = memoList.get(id);
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new MemoResponseDto(memo),HttpStatus.OK);
    }

    // 메모 수정 (U) 메모 단건 전체 수정
    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateMemoById(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ) {
        Memo memo = memoList.get(id);

        // nullpointExeption 방지
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 필수값 방지
        if (dto.getTitle() == null || dto.getContents() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // memo 수정
        memo.update(dto);
        // 응답
        return new ResponseEntity<>(new MemoResponseDto(memo),HttpStatus.OK);
    }

    @PatchMapping("/{id}") // 제목을 수정하는 기능
    public ResponseEntity<MemoResponseDto> updateTitle(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto) {
        Memo memo = memoList.get(id);
        // npe 방지
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // 필수값 방지
        if (dto.getTitle() == null || dto.getContents() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.updateTitle(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    // 메모 삭제 (D)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {

        // memoList의 key 값에 id를 포함하고 있다면
        if (memoList.containsKey(id)) {
            memoList.remove(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
