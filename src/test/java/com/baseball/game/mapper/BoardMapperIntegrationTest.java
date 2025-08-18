package com.baseball.game.mapper;

import com.baseball.game.dto.BoardDto;
import com.baseball.game.dto.BoardRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BoardMapperIntegrationTest {

    @Autowired
    private BoardMapper boardMapper;

    private void insertBoard(String category, String title, String writer) {
        BoardRequestDto req = new BoardRequestDto();
        req.setCategory(category);
        req.setTitle(title);
        req.setText("content");
        req.setWriter(writer);
        boardMapper.write(req);
    }

    @Test
    @DisplayName("getPagedList - no DESC 정렬 확인")
    void getPagedList_orderByNoDesc() {
        insertBoard("notice", "t1", "u1");
        insertBoard("notice", "t2", "u1");
        insertBoard("notice", "t3", "u1");

        ArrayList<BoardDto> list = boardMapper.getPagedList(0, 10);
        assertThat(list).hasSizeGreaterThanOrEqualTo(3);
        int n0 = list.get(0).getNo();
        int n1 = list.get(1).getNo();
        int n2 = list.get(2).getNo();
        assertThat(n0).isGreaterThan(n1);
        assertThat(n1).isGreaterThan(n2);
    }

    @Test
    @DisplayName("getPagedListCate - 카테고리 필터 및 no DESC 정렬")
    void getPagedListCate_orderAndFilter() {
        insertBoard("free", "a1", "u2");
        insertBoard("free", "a2", "u2");
        insertBoard("notice", "b1", "u2");

        ArrayList<BoardDto> list = boardMapper.getPagedListCate(0, 10, "free");
        assertThat(list).isNotEmpty();
        assertThat(list.stream().allMatch(b -> "free".equals(b.getCategory()))).isTrue();
        if (list.size() >= 2) {
            assertThat(list.get(0).getNo()).isGreaterThan(list.get(1).getNo());
        }
    }
}
