package com.baseball.game.util;

import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.dto.GameDto;
import java.util.List;
import java.util.ArrayList;

public class GameLogicUtil {

    /**
     * 투수의 제구력에 따라 실제 결과가 바뀌는 투구 결과 결정 (기존 로직)
     * 이제 이 메서드는 사용되지 않습니다. (determinePitchResultByStats로 대체)
     */
    /*
    public static String determinePitchResult(Pitcher pitcher, String pitchType) {
        int control = pitcher.getControl();
        // 제구력 50: 70%, 100: 100%, 0: 40% (선수의 제구력 100점은 완벽에 가까움을 의미)
        double baseProb = 0.7 + (control - 50) * 0.006;
        baseProb = Math.max(0.4, Math.min(1.0, baseProb)); // 확률 범위 40% ~ 100%

        double rand = Math.random();
        if (rand < baseProb) { // 제구에 성공하여 의도한 결과가 나옴
            return pitchType.equals("strike") ? "스트라이크" : "볼";
        } else { // 제구에 실패하여 반대 결과가 나옴
            return pitchType.equals("strike") ? "볼" : "스트라이크";
        }
    }
    */

    /**
     * 투수와 타자의 실제 스탯을 기반으로 투구 결과 (스트라이크, 볼, 인플레이)를 결정합니다.
     *
     * @param pitcher 현재 투수 객체 (탈삼진, 볼넷, 상대 타자 수 통계 포함)
     * @param batter 현재 타자 객체 (삼진, 볼넷, 타석 수 통계 포함)
     * @return "스트라이크", "볼", "인플레이" 중 하나
     */
    public static String determinePitchResultByStats(Pitcher pitcher, Batter batter) {
        // 투수 삼진율: 탈삼진 / 총 상대 타자 수
        double pitcherStrikeOutRate = (pitcher.getTotalBattersFaced() > 0) ?
                                      (double) pitcher.getStrikeouts() / pitcher.getTotalBattersFaced() : 0.0;
        // 타자 삼진율: 삼진 / 타석 수
        double batterStrikeOutRate = (batter.getPlateAppearances() > 0) ?
                                     (double) batter.getStrikeOuts() / batter.getPlateAppearances() : 0.0;
        // 스트라이크 확률 = (투수 삼진율 + 타자 삼진율) / 2
        double strikeProb = (pitcherStrikeOutRate + batterStrikeOutRate) / 2.0;

        // 투수 볼넷율: 볼넷 / 총 상대 타자 수
        double pitcherWalkRate = (pitcher.getTotalBattersFaced() > 0) ?
                                 (double) pitcher.getWalks() / pitcher.getTotalBattersFaced() : 0.0;
        // 타자 볼넷율: 볼넷 / 타석 수
        double batterWalkRate = (batter.getPlateAppearances() > 0) ?
                                (double) batter.getFourBalls() / batter.getPlateAppearances() : 0.0;
        // 볼 확률 = (투수 볼넷율 + 타자 볼넷율) / 2
        double ballProb = (pitcherWalkRate + batterWalkRate) / 2.0;

        // 인플레이 확률 = 1 - (스트라이크 확률 + 볼 확률)
        // 확률의 합이 1을 넘지 않도록 보정 (극히 드물지만 데이터 이상 시 대비)
        double sumProbs = strikeProb + ballProb;
        if (sumProbs > 1.0) {
            strikeProb /= sumProbs;
            ballProb /= sumProbs;
            sumProbs = 1.0;
        }
        double inPlayProb = 1.0 - sumProbs;

        // 결과 결정
        double rand = Math.random();
        if (rand < strikeProb) {
            return "스트라이크";
        } else if (rand < strikeProb + ballProb) {
            return "볼";
        } else {
            return "인플레이";
        }
    }

    /**
     * 타이밍을 반영한 타격 결과 결정 (스윙 여부, 투수/타자 능력치 포함)
     * 이 메서드는 이제 'determinePitchResultByStats'와 'determineInPlayResult'를 활용하여 통합적으로 동작합니다.
     * 스윙 시, 투수의 제구력과 타자의 능력치가 아닌 실제 스탯 기반의 인플레이 결과로 대체됩니다.
     *
     * @param swing: 스윙 여부 (true = 스윙, false = 노스윙)
     * @param pitcher: 현재 투수 객체
     * @param pitchType: 투수가 던진 공의 실제 유형 ("strike" or "ball") - (현재는 사용되지 않음, 스탯 기반으로 결정)
     * @param timing: 0.0 ~ 1.0 (0.5가 정타) - (현재는 사용되지 않음, 스탯 기반으로 결정)
     * @param batter: 현재 타자 객체
     * @return 타격 결과 문자열 ("스트라이크", "볼", "헛스윙", "안타", "홈런", "삼진 아웃" 등)
     */
    public static String determineHitResultWithTiming(boolean swing, Pitcher pitcher, String pitchType, double timing, Batter batter) {
        // 스윙을 하지 않았을 경우, 투구 결과만 반환 (기존 로직 유지, 스탯 기반 판정X)
        // Note: 실제 게임 흐름에서는 determinePitchResultByStats 호출 후 스윙 여부를 판단하는 것이 더 자연스러움.
        // 이 메서드는 스윙 후의 최종 결과를 결정하는 데 사용된다고 가정.
        if (!swing) {
            // 이 경우 determinePitchResultByStats 호출 결과가 "스트라이크" 또는 "볼"이었을 것임.
            // 여기서는 단순화하여 pitchType을 반환 (만약 pitchType이 인플레이가 아니라면).
            // 더 정확하게는 실제 투구 결과에 따라 스트라이크/볼을 반환해야 함.
            // TODO: 이곳의 로직은 게임 흐름과 determinePitchResultByStats의 호출 시점에 따라 조정 필요.
            // 현재 구조에서는 determinePitchResultByStats가 먼저 호출되어 결과를 "스트라이크", "볼", "인플레이"로 나눈 뒤,
            // "인플레이"일 때만 이 메서드가 스윙과 함께 호출되는 것이 이상적입니다.
            // 따라서 'pitchType' 파라미터는 더 이상 'strike'/'ball' 구분에 사용되지 않을 수 있습니다.
            // 대신, 외부에서 'determinePitchResultByStats'를 먼저 호출하고 그 결과가 '인플레이'일 때만 이 메서드를 호출하는 흐름이 필요합니다.
            // 여기서는 스윙하지 않았으면 볼/스트라이크로 간주하는 기존 로직을 따릅니다.
            // 이 메서드 파라미터의 pitchType은 투수가 던진 공의 존 여부를 나타낸다고 가정합니다.
            return (pitchType.equals("strike") ? "스트라이크" : "볼");
        }

        // 스윙을 한 경우, 스탯 기반의 인플레이 결과 결정 로직으로 변경
        // 1. 투수/타자 스탯 기반의 '인플레이' 확률 결정 (외부에서 이미 결정되었다고 가정하고 이 메서드에 들어옴)
        // 2. 인플레이일 경우 타격 결과 (안타, 홈런, 아웃 등)를 결정
        return determineInPlayResult(batter);
    }

    /**
     * 최종 타격 점수에 따른 실제 타격 결과 결정 (기존 헬퍼)
     * 이제 이 메서드는 사용되지 않습니다. (determineInPlayResult로 대체)
     */
    /*
    private static String getActualHitResultBasedOnFinalScore(double finalScore) {
        if (finalScore > 95) return "홈런";
        if (finalScore > 90) return "3루타";
        if (finalScore > 80) return "2루타";
        if (finalScore > 60) return "안타";
        if (finalScore > 40) return "땅볼 아웃";
        if (finalScore > 20) return "뜬공 아웃";
        return "삼진 아웃"; // 점수가 매우 낮으면 삼진
    }
    */

    /**
     * 인플레이 상황에서 타자의 스탯을 기반으로 실제 타격 결과를 결정합니다.
     * (안타, 2루타, 3루타, 홈런, 땅볼 아웃, 뜬공 아웃)
     *
     * @param batter 현재 타자 객체 (Hit, At_Bat, two_Base, three_Base, Home_Run 통계 포함)
     * @return 타격 결과 문자열
     */
    private static String determineInPlayResult(Batter batter) {
        // 안타 확률 (전체 안타 / 타수)
        double totalHitsProb = (batter.getAtBats() > 0) ?
                               (double) batter.getHits() / batter.getAtBats() : 0.0;

        // 조건부 확률: 안타 발생 시 특정 유형의 안타 확률 (각 안타 / 전체 안타)
        // 주의: totalHits가 0일 경우 0으로 나눔 방지
        double homeRunInHitProb = (batter.getHits() > 0) ?
                                  (double) batter.getHomeRuns() / batter.getHits() : 0.0;
        double tripleInHitProb = (batter.getHits() > 0) ?
                                 (double) batter.getThreeBases() / batter.getHits() : 0.0;
        double doubleInHitProb = (batter.getHits() > 0) ?
                                 (double) batter.getTwoBases() / batter.getHits() : 0.0;
        // 단타는 전체 안타에서 홈런, 3루타, 2루타를 뺀 나머지
        double singleInHitProb = 1.0 - (homeRunInHitProb + tripleInHitProb + doubleInHitProb);
        // 음수 확률 방지 (데이터 이상 시)
        singleInHitProb = Math.max(0.0, singleInHitProb);

        double rand = Math.random();

        if (rand < totalHitsProb) { // 안타 발생
            double hitTypeRand = Math.random();
            if (hitTypeRand < homeRunInHitProb) {
                return "홈런";
            } else if (hitTypeRand < homeRunInHitProb + tripleInHitProb) {
                return "3루타";
            } else if (hitTypeRand < homeRunInHitProb + tripleInHitProb + doubleInHitProb) {
                return "2루타";
            } else {
                return "안타"; // 단타
            }
        } else { // 아웃 발생 (인플레이 아웃)
            // 땅볼 아웃과 뜬공 아웃은 고정 비율 또는 리그 평균 기반 추정
            if (Math.random() < 0.5) { // 50% 확률로 땅볼
                return "땅볼 아웃";
            } else { // 50% 확률로 뜬공
                return "뜬공 아웃";
            }
        }
    }

    /**
     * 땅볼 처리: 병살/진루/아웃
     * GameDto의 out, bases를 직접 변경합니다.
     */
    public static String processGroundBall(GameDto game, Batter batter) {
        Batter[] bases = game.getBases();
        int currentOuts = game.getOut();

        // 2아웃이면 병살 불가, 타자만 아웃
        if (currentOuts == 2) {
            game.setOut(currentOuts + 1);
            return "땅볼 아웃";
        }
        // 1루에 주자 있는 경우 (병살 가능성)
        if (bases[1] != null) {
            if (Math.random() < 0.3) { // 30% 병살 확률 (나중에 수비력 반영 가능)
                game.setOut(currentOuts + 2); // 2아웃 추가
                GameLogicUtil.resetBases(game); // 병살 시 모든 주자 귀루 또는 아웃 처리
                return "병살타";
            } else { // 병살 실패, 타자 아웃, 1루 주자 진루
                game.setOut(currentOuts + 1); // 1아웃 추가
                // GameServiceImpl의 batterSwing에서 advanceRunners(game, 0) 호출로 주자 진루 처리 예정
                return "땅볼 아웃";
            }
        } else { // 1루에 주자 없는 경우 (타자만 아웃)
            game.setOut(currentOuts + 1); // 1아웃 추가
            return "땅볼 아웃";
        }
    }

    /**
     * 모든 베이스 주자를 초기화합니다.
     */
    public static void resetBases(GameDto game) {
        game.setBases(new Batter[4]); // 0번 인덱스는 타자를 의미
        game.setBaseRunners(new ArrayList<>()); // 주자 목록 초기화
    }

    /**
     * 특정 베이스에 주자를 추가합니다. (예: 타자가 안타 치고 1루 진루)
     */
    public static void addRunnerToBase(GameDto game, int base, Batter runner) {
        if (base >= 1 && base <= 3) { // 1루, 2루, 3루만 가능
            Batter[] bases = game.getBases();
            bases[base] = runner;
            // List<Batter> baseRunners 리스트에도 추가 (중복 방지 로직 필요 시 추가)
            if (!game.getBaseRunners().contains(runner)) {
                game.getBaseRunners().add(runner);
            }
        }
    }

    /**
     * 루상 주자를 지정된 베이스 수만큼 진루시킵니다.
     * 홈인 시 점수를 증가시킵니다.
     * @param game 현재 게임 DTO
     * @param basesToAdvance 진루시킬 베이스 수
     */
    public static void advanceRunners(GameDto game, int basesToAdvance) {
        // 기존 베이스 상태를 복사하여 안전하게 처리 (새로운 베이스 상태를 업데이트할 것임)
        Batter[] oldBases = game.getBases();
        Batter[] newBases = new Batter[4]; // 새로운 베이스 배열 초기화
        List<Batter> newBaseRunners = new ArrayList<>(); // 새로운 주자 목록

        // 3루 -> 2루 -> 1루 순서로 주자를 처리하여 겹쳐쓰기 문제 방지
        for (int i = 3; i >= 1; i--) { // 1루부터 3루까지
            if (oldBases[i] != null) {
                Batter runner = oldBases[i];
                int newBasePosition = i + basesToAdvance;

                if (newBasePosition >= 4) { // 홈으로 들어옴 (득점)
                    // 현재 공격 팀에 따라 점수 추가
                    if (game.isTop()) { // 초: 원정팀 공격
                        game.setAwayScore(game.getAwayScore() + 1);
                    } else { // 말: 홈팀 공격
                        game.setHomeScore(game.getHomeScore() + 1);
                    }
                    // TODO: 득점한 주자의 'runsScored' 필드 업데이트 등 기록 추가
                } else { // 다음 베이스로 진루
                    newBases[newBasePosition] = runner;
                    newBaseRunners.add(runner);
                }
            }
        }
        game.setBases(newBases); // 새로운 베이스 상태로 업데이트
        game.setBaseRunners(newBaseRunners); // 새로운 주자 목록으로 업데이트
    }
}