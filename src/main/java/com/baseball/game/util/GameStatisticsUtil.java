package com.baseball.game.util;

import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;

/**
 * 야구 통계 계산을 위한 유틸리티 클래스
 */
public class GameStatisticsUtil {

    public static double calculateWHIP(Pitcher p) {
        if (p.getInningsPitched() == 0) {
            return 0.0;
        }
        return (double) (p.getBaseOnBalls() + p.getHits()) / p.getInningsPitched();
    }

    public static double calculateERA(Pitcher p) {
        if (p.getInningsPitched() == 0) {
            return 0.0;
        }
        return (double) (p.getEarnedRun() * 9) / p.getInningsPitched();
    }

    public static double calculateBattingAverage(Batter b) {
        if (b.getAtBats() == 0) {
            return 0.0;
        }
        return (double) b.getHits() / b.getAtBats();
    }
}
