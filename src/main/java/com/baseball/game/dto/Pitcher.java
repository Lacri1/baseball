package com.baseball.game.dto;

import lombok.Data;

@Data
public class Pitcher extends Player {
    // 순수 실제 성적만 유지
    private int inningsPitched; // 이닝 수
    private int strikeouts; // 삼진
    private int walks; // 볼넷
    private int hits; // 피안타
    private int earnedRuns; // 자책점
    private double era; // 평균자책점
    private double whip; // WHIP (Walks plus Hits per Inning Pitched)
    private int totalBattersFaced; // 상대 타자 수

    public Pitcher() {
        this.totalBattersFaced = 0;
    }
    public Pitcher(String name,String team) {
        // 부모 클래스의 생성자를 통해 이름과 팀을 설정할 수 있다고 가정
        // super(name, team); // Player 클래스에 적절한 생성자 있다면 사용
        this.setName(name);
        this.setTeam(team);
        // 기본값 초기화
        this.totalBattersFaced = 0;
    }
}