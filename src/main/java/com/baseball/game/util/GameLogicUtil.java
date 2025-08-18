package com.baseball.game.util;

import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.dto.GameDto;
import java.util.List;
import java.util.ArrayList;

public class GameLogicUtil {

    /**
     * 게임 진행 중 공통으로 사용되는 순수 로직 모음.
     * - 입력으로 전달된 DTO의 상태를 기반으로 확률/점수 등을 계산하여 결과 문자열 또는 점수를 산출합니다.
     * - 외부 자원 접근(DB/네트워크)이 없어 테스트가 쉽고, 비즈니스 규칙 변경 시 이 클래스만 집중해서 수정할 수 있습니다.
     * 주의: 이 유틸은 상태를 저장하지 않으며, 부수효과가 필요한 경우(GameDto 변경 등)에는 메서드에 명시적으로 나타냅니다.
     */
    /**
     * 투수의 WHIP을 기반으로 control 스탯 (0-100)을 계산합니다.
     * 낮은 WHIP은 높은 control을 의미합니다.
     * 
     * @param whipValue 투수의 실제 WHIP 값
     * @return 계산된 control 스탯 (0-100)
     */
    private static int calculateControlFromWhip(double whipValue) {
        // KBO 투수의 일반적인 WHIP 범위를 0.9 (매우 좋음) ~ 2.0 (매우 나쁨)으로 가정
        double minWHIP = 0.9;
        double maxWHIP = 2.0;

        double calculatedControl;
        if (whipValue <= minWHIP) {
            calculatedControl = 100.0;
        } else if (whipValue >= maxWHIP) {
            calculatedControl = 0.0;
        } else {
            calculatedControl = 100 - ((whipValue - minWHIP) / (maxWHIP - minWHIP)) * 100;
        }
        return (int) Math.max(0, Math.min(100, calculatedControl));
    }
    // GameLogicUtil.java 파일에 아래 메서드를 추가합니다.

    /**
     * 투수와 타자의 실제 기록을 기반으로 스트라이크/볼 확률을 계산하여 투구 결과를 결정합니다.
     * 1) 스트라이크 확률 = {(투수의 삼진 / 총 상대 타자 수) + (타자의 삼진 / 타석 수)} / 2
     * 2) 볼 확률 = {(투수의 볼넷 + 투수의 HBP) / 총 상대 타자 수) + ((타자의 볼넷 + 타자의 HBP) / 타석 수)} / 2
     * 
     * @param pitcher 현재 투수 객체
     * @param batter  현재 타자 객체
     * @return "스트라이크" 또는 "볼"
     */
    public static String determinePitchResultByStats(Pitcher pitcher, Batter batter) {
        // 목적: 투수/타자 양측의 과거 성적을 기반으로 스트라이크/볼의 확률을 가중 평균하여 단일 투구 결과를 도출
        // 특징: 투수의 삼진/볼넷 성향과 타자의 삼진/볼넷 성향을 동시에 반영하여 극단값을 완화합니다.
        // DTO에 아래와 같은 getter가 있다고 가정합니다.
        // pitcher: getStrikeouts(), getTotalBattersFaced(), getWalks(), getHitByPitch()
        // batter: getStrikeouts(), getPlateAppearances(), getWalks(), getHitByPitch()

        // 투수/타자 데이터가 0인 경우를 대비한 안전장치
        double pitcherTotalBatters = pitcher.getPitchersBattersFaced() > 0 ? pitcher.getPitchersBattersFaced() : 1;
        double batterTotalPAs = batter.getPlateAppearances() > 0 ? batter.getPlateAppearances() : 1;

        // 1. 스트라이크 확률 계산
        double pitcherStrikeoutRate = pitcher.getStrikeouts() / pitcherTotalBatters;
        double batterStrikeoutRate = batter.getStrikeOuts() / batterTotalPAs;
        double finalStrikeProb = (pitcherStrikeoutRate + batterStrikeoutRate) / 2.0;

        // 2. 볼 확률 계산
        double pitcherWalkRate = (pitcher.getWalks() + pitcher.getHitByPitch()) / pitcherTotalBatters;
        double batterWalkRate = (batter.getWalks() + batter.getHitByPitch()) / batterTotalPAs;
        double finalBallProb = (pitcherWalkRate + batterWalkRate) / 2.0;

        // 3. 최종 확률에 기반한 결과 결정
        // 두 확률의 합을 1로 정규화하여 스트라이크가 나올 확률을 계산
        double totalProb = finalStrikeProb + finalBallProb;
        if (totalProb == 0) {
            return (Math.random() < 0.6) ? "스트라이크" : "볼"; // 데이터가 전혀 없는 경우 기본 확률 (60% 스트라이크)
        }

        double strikeChance = finalStrikeProb / totalProb;

        if (Math.random() < strikeChance) {
            return "스트라이크";
        } else {
            return "볼";
        }
    }

    /**
     * 타자의 타율을 기반으로 contact 스탯 (0-100)을 계산합니다.
     * 높은 타율은 높은 contact를 의미합니다.
     * 
     * @param battingAverage 타자의 실제 타율 (예: 0.250)
     * @return 계산된 contact 스탯 (0-100)
     */
    public static int calculateContactFromBattingAverage(double battingAverage) {
        // 목적: 직관적인 타율을 0~100 스케일의 컨택 값으로 변환하여 다른 요소(파워, 제구)와 가중 합산하기 위함
        // 타율 범위를 0.150 (낮음) ~ 0.350 (높음)으로 가정
        double minAvg = 0.150;
        double maxAvg = 0.350;

        double calculatedContact;
        if (battingAverage <= minAvg) {
            calculatedContact = 0.0;
        } else if (battingAverage >= maxAvg) {
            calculatedContact = 100.0;
        } else {
            calculatedContact = ((battingAverage - minAvg) / (maxAvg - minAvg)) * 100;
        }
        return (int) Math.max(0, Math.min(100, calculatedContact));
    }

    /**
     * 타자의 홈런 수를 기반으로 power 스탯 (0-100)을 계산합니다.
     * 홈런 수가 많을수록 높은 power를 의미합니다. (단순화된 예시)
     * 
     * @param homeRuns 타자의 실제 홈런 수
     * @return 계산된 power 스탯 (0-100)
     */
    private static int calculatePowerFromHomeRuns(int homeRuns) {
        // 목적: 시즌 누적 홈런 개수를 단순 선형 변환하여 파워 점수로 사용
        // 주의: 파워의 세밀한 보정(경기수, 홈런율 등)은 추후 필요 시 이 메서드를 확장합니다.
        // 홈런 수에 비례하여 파워를 계산. 예를 들어, 50홈런을 치면 파워 100으로 간주
        // 더 정교한 계산을 위해 홈런율 (HR/PA) 등을 사용할 수 있으나, 현재 필드 기준 단순화
        double calculatedPower = homeRuns * 2.0; // 홈런 50개면 파워 100
        return (int) Math.max(0, Math.min(100, calculatedPower));
    }

    /**
     * 투수의 제구력과 프론트엔드에서 선택한 투구 타입에 따라 실제 결과가 바뀌는 투구 결과 결정.
     * 유저가 선택한 공(pitchType)이 투수의 제구력(control)에 따라 성공하거나 낮은 확률로 실패하여 반대 결과가 됩니다.
     * 
     * @param pitcher   현재 투수 객체 (WHIP 스탯 포함)
     * @param pitchType 프론트엔드에서 선택한 투구 타입 ("strike" 또는 "ball")
     * @return "스트라이크" 또는 "볼"
     */
    public static String determinePitchResult(Pitcher pitcher, String pitchType) {
        // 목적: 사용자가 선택한 투구 타입(의도)이 투수의 제구력에 의해 실제 결과로 얼마나 잘 반영되는지 모델링
        // 직관: WHIP이 낮을수록 의도한 결과가 나올 확률이 높음
        // control 스탯을 WHIP으로부터 실시간 계산
        int control = calculateControlFromWhip(pitcher.getWhip());

        // 제구력 50: 70%, 100: 100%, 0: 40% (선수의 제구력 100점은 완벽에 가까움을 의미)
        double baseProb = 0.7 + (control - 50) * 0.006;
        baseProb = Math.max(0.4, Math.min(1.0, baseProb)); // 확률 범위 40% ~ 100%

        double rand = Math.random();
        if (rand < baseProb) { // 제구에 성공하여 의도한 결과가 나옴
            return pitchType.equals("strike") ? "스트라이크" : "볼";
        } else { // 제구에 실패하여 반대 결과가 나옴 (낮은 확률로 바뀜)
            return pitchType.equals("strike") ? "볼" : "스트라이크";
        }
    }

    /**
     * 투수와 타자의 실제 스탯을 기반으로 투구 결과 (스트라이크, 볼, 인플레이)를 결정합니다.
     * 이 메서드는 현재 사용하지 않습니다. (determinePitchResult로 대체)
     */
    /*
     * public static String determinePitchResultByStats(Pitcher pitcher, Batter
     * batter) {
     * // ... (이전과 동일한 주석 처리된 내용) ...
     * }
     */

    /**
     * 타이밍과 투수/타자의 스탯을 반영하여 스윙 시의 타격 결과를 결정합니다.
     * 스윙하지 않으면 투구 타입("스트라이크" 또는 "볼")을 그대로 반환합니다.
     * 스윙 시에는 투수와 타자의 능력치를 기반으로 타격 점수를 계산하고, 이 점수에 따라 안타, 홈런, 아웃 등을 결정합니다.
     *
     * @param swing     스윙 여부 (true = 스윙, false = 노스윙)
     * @param pitcher   현재 투수 객체
     * @param pitchType 투수가 던진 공의 실제 유형 ("strike" 또는 "ball") - 투구의 존 여부
     * @param timing    0.0 ~ 1.0 (0.5가 정타)
     * @param batter    현재 타자 객체
     * @return 타격 결과 문자열 ("스트라이크", "볼", "헛스윙", "안타", "홈런", "삼진 아웃" 등)
     */
    public static String determineHitResultWithTiming(boolean swing, Pitcher pitcher, String pitchType, double timing,
            Batter batter) {
        // 목적: 스윙을 한 경우, 타자/투수 능력과 타이밍을 조합해 최종 타격 결과를 결정합니다.
        // 설계 의도:
        // - 컨택(=타율 기반)과 파워(=홈런 기반)를 가중 합산해 타격 기본 점수를 구성
        // - 투수 제구력은 페널티로 반영(제구력이 좋을수록 타격 점수에서 불리)
        // - 타이밍은 미세 조정(과도한 랜덤성 보다는 플레이어 입력 영향 강조)
        // 스윙을 하지 않았을 경우, 투구 결과만 반환
        if (!swing) {
            return pitchType.equals("strike") ? "스트라이크" : "볼";
        }

        // 스윙을 한 경우, 타격 점수를 계산하여 결과 결정
        // 파워, 컨택, 컨트롤 페널티를 실제 성적에서 실시간 계산
        int contact = calculateContactFromBattingAverage(batter.getBattingAverage());
        int power = calculatePowerFromHomeRuns(batter.getHomeRuns());
        int pitcherControl = calculateControlFromWhip(pitcher.getWhip()); // 투수 컨트롤도 실시간 계산

        // 타격 점수 = 계산된 컨택 * 0.4 + 계산된 파워 * 0.3 + 투수 제구력 페널티(컨트롤 역영향) * 0.2 + 타이밍 * 0.1
        double contactScore = contact * 0.4;
        double powerScore = power * 0.3;
        double controlPenalty = (100 - pitcherControl) * 0.2; // 계산된 투수 컨트롤 사용
        double timingScore = timing * 100 * 0.1;

        double finalScore = contactScore + powerScore - controlPenalty + timingScore;
        finalScore = Math.max(0, Math.min(100, finalScore));

        return getActualHitResultBasedOnFinalScore(finalScore);
    }

    /**
     * 최종 타격 점수에 따른 실제 타격 결과 결정 (헬퍼 메서드)
     * 
     * @param finalScore 계산된 타격 점수
     * @return 타격 결과 문자열
     */
    private static String getActualHitResultBasedOnFinalScore(double finalScore) {
        // 목적: 연속값의 타격 점수를 실제 이벤트로 매핑하는 계단 함수
        // 주의: 임계값은 체감 난이도/리워드 밸런싱 포인트로, 향후 게임 튜닝 시 조정됩니다.
        if (finalScore > 95)
            return "홈런";
        if (finalScore > 90)
            return "3루타";
        if (finalScore > 80)
            return "2루타";
        if (finalScore > 60)
            return "안타";
        if (finalScore > 40)
            return "땅볼 아웃";
        if (finalScore > 20)
            return "뜬공 아웃";
        return "삼진 아웃";
    }

    /**
     * 땅볼 처리: 병살/진루/아웃
     * GameDto의 out, bases를 직접 변경합니다.
     * 
     * @param game   현재 게임 DTO
     * @param batter 타자
     * @return "땅볼 아웃" 또는 "병살타"
     */
    public static String processGroundBall(GameDto game, Batter batter) {
        // 목적: 땅볼 안타/아웃 상황에서 병살 가능성 및 아웃 카운트 증가를 처리
        // 특징: 2아웃 시에는 추가 처리 없이 이닝 종료까지 고려, 1루 주자 존재 시 30% 확률로 병살 처리
        Batter[] bases = game.getBases();
        int currentOuts = game.getOut();

        if (currentOuts == 2) {
            game.setOut(currentOuts + 1);
            return "땅볼 아웃";
        }
        if (bases[1] != null) {
            if (Math.random() < 0.3) {
                game.setOut(currentOuts + 2);
                GameLogicUtil.resetBases(game);
                return "병살타";
            } else {
                game.setOut(currentOuts + 1);
                return "땅볼 아웃";
            }
        } else {
            game.setOut(currentOuts + 1);
            return "땅볼 아웃";
        }
    }

    /**
     * 모든 베이스 주자를 초기화합니다.
     * 
     * @param game 현재 게임 DTO
     */
    public static void resetBases(GameDto game) {
        // 목적: 모든 루 상황을 초기화하여 이닝 시작/홈런/병살 등 특정 이벤트 이후 상태를 정리
        game.setBases(new Batter[4]);
        game.setBaseRunners(new ArrayList<>());
    }

    /**
     * 특정 베이스에 주자를 추가합니다. (예: 타자가 안타 치고 1루 진루)
     * 
     * @param game   현재 게임 DTO
     * @param base   주자가 위치할 베이스 (1: 1루, 2: 2루, 3: 3루)
     * @param runner 해당 주자 Batter 객체
     */
    public static void addRunnerToBase(GameDto game, int base, Batter runner) {
        // 목적: 타격 결과에 따라 특정 루에 주자를 배치
        // 주의: 베이스 범위는 1~3만 허용(0은 홈, 배열 크기는 4)
        if (base >= 1 && base <= 3) {
            Batter[] bases = game.getBases();
            bases[base] = runner;
            if (!game.getBaseRunners().contains(runner)) {
                game.getBaseRunners().add(runner);
            }
        }
    }

    /**
     * 루상 주자를 지정된 베이스 수만큼 진루시킵니다.
     * 홈인 시 점수를 증가시킵니다.
     * 
     * @param game           현재 게임 DTO
     * @param basesToAdvance 진루시킬 베이스 수
     */
    public static void advanceRunners(GameDto game, int basesToAdvance) {
        // 목적: 주자들을 뒤에서 앞으로 이동시키며 홈인한 주자 수만큼 득점을 반영
        // 구현 포인트:
        // - 3루부터 역순으로 이동시켜 중복 덮어쓰기를 방지
        // - 홈을 넘는 경우(team 공격/수비에 따라) 점수 가산
        Batter[] oldBases = game.getBases();
        Batter[] newBases = new Batter[4];
        List<Batter> newBaseRunners = new ArrayList<>();

        for (int i = 3; i >= 1; i--) {
            if (oldBases[i] != null) {
                Batter runner = oldBases[i];
                int newBasePosition = i + basesToAdvance;

                if (newBasePosition >= 4) {
                    if (game.isTop()) {
                        game.setAwayScore(game.getAwayScore() + 1);
                    } else {
                        game.setHomeScore(game.getHomeScore() + 1);
                    }
                } else {
                    newBases[newBasePosition] = runner;
                    newBaseRunners.add(runner);
                }
            }
        }
        game.setBases(newBases);
        game.setBaseRunners(newBaseRunners);
    }
}