package com.baseball.game;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan({"com.baseball.game.mapper", "com.baseball.ranking.mapper"}) // com.baseball.ranking.mapper 패키지 추가
@ComponentScan({"com.baseball.game", "com.baseball.ranking"})
public class GameApplication {
    public static void main(String[] args) {
        SpringApplication.run(GameApplication.class, args);
    }
}