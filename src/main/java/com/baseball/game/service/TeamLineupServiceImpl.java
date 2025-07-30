package com.baseball.game.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator; // Comparator import 추가

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baseball.game.dto.TeamLineup;
import com.baseball.game.dto.CustomLineupRequest;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.mapper.TeamLineupMapper;
import com.baseball.game.exception.ValidationException;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TeamLineupServiceImpl implements TeamLineupService {

    private static final Logger logger = LoggerFactory.getLogger(TeamLineupServiceImpl.class);

    @Setter(onMethod_ = @Autowired)
    private TeamLineupMapper teamLineupMapper;

    private final Map<String, Batter> allBattersByName = new HashMap<>();
    private final Map<String, Pitcher> allPitchersByName = new HashMap<>();

    private final Map<String, Map<String, List<Batter>>> userCustomBattingOrders = new HashMap<>();
    private final Map<String, Map<String, Pitcher>> userCustomStartingPitchers = new HashMap<>();

    public TeamLineupServiceImpl() {
        // --- 더미 타자 데이터 ---
        allBattersByName.put("이대호", new Batter("이대호", "Giants"));
        allBattersByName.put("손아섭", new Batter("손아섭", "Giants"));
        allBattersByName.put("전준우", new Batter("전준우", "Giants"));
        allBattersByName.put("안치홍", new Batter("안치홍", "Giants"));
        allBattersByName.put("강민호", new Batter("강민호", "Giants"));
        allBattersByName.put("김주찬", new Batter("김주찬", "Giants"));
        allBattersByName.put("민병헌", new Batter("민병헌", "Giants"));
        allBattersByName.put("정훈", new Batter("정훈", "Giants"));
        allBattersByName.put("정보근", new Batter("정보근", "Giants"));

        allBattersByName.put("박민우", new Batter("박민우", "Dinos"));
        allBattersByName.put("나성범(D)", new Batter("나성범(D)", "Dinos"));
        allBattersByName.put("양의지(D)", new Batter("양의지(D)", "Dinos"));
        allBattersByName.put("노진혁", new Batter("노진혁", "Dinos"));
        allBattersByName.put("강진성", new Batter("강진성", "Dinos"));
        allBattersByName.put("김성욱", new Batter("김성욱", "Dinos"));
        allBattersByName.put("권희동", new Batter("권희동", "Dinos"));
        allBattersByName.put("알테어", new Batter("알테어", "Dinos"));
        allBattersByName.put("박석민", new Batter("박석민", "Dinos"));

        // --- 더미 투수 데이터 ---
        allPitchersByName.put("장원준", new Pitcher("장원준", "Giants"));
        allPitchersByName.put("이용찬", new Pitcher("이용찬", "Giants"));

        allPitchersByName.put("구창모", new Pitcher("구창모", "Dinos"));
        allPitchersByName.put("루친스키", new Pitcher("루친스키", "Dinos"));
    }

    @Override
    public List<TeamLineup> getDefaultLineup(String teamName) {
        // 컴퓨터 라인업은 DB에서 조회합니다.
        List<TeamLineup> lineup = teamLineupMapper.findDefaultLineupByTeam(teamName);
        if (lineup == null || lineup.isEmpty()) {
            throw new ValidationException("DB에서 팀 " + teamName + "의 기본 라인업을 찾을 수 없습니다.");
        }
        logger.info("팀 {}의 기본 라인업(DB에서 조회)이 로드되었습니다.", teamName);
        return lineup;
    }

    @Override
    public List<TeamLineup> getCustomLineup(String userId, String teamName) {
        List<TeamLineup> lineup = new ArrayList<>();

        // 사용자 정의 타자 라인업 조회 (메모리)
        List<Batter> customBattingOrder = userCustomBattingOrders
            .getOrDefault(userId, new HashMap<>())
            .get(teamName);

        if (customBattingOrder != null) {
            for (int i = 0; i < customBattingOrder.size(); i++) {
                Batter batter = customBattingOrder.get(i);
                TeamLineup tl = new TeamLineup();
                tl.setTeamName(teamName);
                tl.setUserId(userId);
                // 타순은 1부터 시작
                tl.setPosition((i + 1) + "th_Batter"); // "1st_Batter", "2nd_Batter" 형식으로 저장
                tl.setPlayerName(batter.getName());
                lineup.add(tl);
            }
        }

        // 사용자 정의 선발 투수 조회 (메모리)
        Pitcher customStartingPitcher = userCustomStartingPitchers
            .getOrDefault(userId, new HashMap<>())
            .get(teamName);

        if (customStartingPitcher != null) {
            TeamLineup tlPitcher = new TeamLineup();
            tlPitcher.setTeamName(teamName);
            tlPitcher.setUserId(userId);
            tlPitcher.setPosition("Starting_Pitcher"); // 투수는 "Starting_Pitcher"로 저장
            tlPitcher.setPlayerName(customStartingPitcher.getName());
            lineup.add(tlPitcher);
        }

        if (lineup.isEmpty()) {
            logger.warn("사용자 {}의 팀 {}에 대한 커스텀 라인업을 메모리에서 찾을 수 없습니다.", userId, teamName);
        } else {
             logger.info("사용자 {}의 팀 {}에 대한 커스텀 라인업(메모리에서 조회)이 로드되었습니다.", userId, teamName);
        }
        return lineup;
    }

    @Override
    @Transactional
    public void saveCustomLineup(CustomLineupRequest request) {
        userCustomBattingOrders.computeIfAbsent(request.getUserId(), k -> new HashMap<>()).remove(request.getTeamName());
        userCustomStartingPitchers.computeIfAbsent(request.getUserId(), k -> new HashMap<>()).remove(request.getTeamName());

        // 임시 리스트에 타순과 함께 타자를 저장하여 나중에 정렬
        List<Map.Entry<Integer, Batter>> orderedBattersTemp = new ArrayList<>();
        Pitcher newStartingPitcher = null;

        for (CustomLineupRequest.LineupPosition requestPosition : request.getLineup()) {
            String playerName = requestPosition.getPlayerName();
            Integer lineupOrder = requestPosition.getPosition(); // getPosition()이 Integer를 반환한다고 가정

            Batter batter = allBattersByName.get(playerName);
            Pitcher pitcher = allPitchersByName.get(playerName);

            if (batter != null && batter.getTeam().equals(request.getTeamName())) {
                // 타자인 경우
                if (lineupOrder == null || lineupOrder < 1 || lineupOrder > 9) {
                    throw new ValidationException("타자 '" + playerName + "'의 라인업 순서가 유효하지 않습니다: " + lineupOrder + " (1-9 사이여야 합니다).");
                }
                orderedBattersTemp.add(Map.entry(lineupOrder, batter));
            } else if (pitcher != null && pitcher.getTeam().equals(request.getTeamName())) {
                // 투수인 경우
                if (newStartingPitcher != null) {
                    throw new ValidationException("선발 투수는 한 명만 지정할 수 있습니다.");
                }
                newStartingPitcher = pitcher;
            } else {
                throw new ValidationException("라인업에 포함된 선수 '" + playerName + "'를 팀 '" + request.getTeamName() + "'에서 찾을 수 없거나 소속 팀이 다릅니다.");
            }
        }

        // 타자들을 타순(position)에 따라 정렬
        orderedBattersTemp.sort(Map.Entry.comparingByKey());

        // 정렬된 타자 리스트를 최종 라인업에 추가
        List<Batter> newBattingOrder = new ArrayList<>();
        for (Map.Entry<Integer, Batter> entry : orderedBattersTemp) {
            newBattingOrder.add(entry.getValue());
        }

        // 유효성 검사
        if (newBattingOrder.size() != 9) {
            throw new ValidationException("라인업은 9명의 타자로 정확히 구성되어야 합니다 (현재 " + newBattingOrder.size() + "명).");
        }
        // 타순 중복/누락 검사
        if (orderedBattersTemp.stream().map(Map.Entry::getKey).distinct().count() != 9) {
            throw new ValidationException("타자 라인업 순서가 중복되거나 누락되었습니다.");
        }
        if (newStartingPitcher == null) {
            throw new ValidationException("선발 투수는 필수로 지정되어야 합니다.");
        }

        // 새로운 커스텀 라인업을 메모리에 저장
        userCustomBattingOrders
            .computeIfAbsent(request.getUserId(), k -> new HashMap<>())
            .put(request.getTeamName(), newBattingOrder);

        userCustomStartingPitchers
            .computeIfAbsent(request.getUserId(), k -> new HashMap<>())
            .put(request.getTeamName(), newStartingPitcher);

        logger.info("사용자 {}의 팀 {}에 대한 커스텀 라인업이 메모리에 저장되었습니다.", request.getUserId(), request.getTeamName());
    }

    @Override
    public List<TeamLineup> getAllCustomLineups(String userId) {
        List<TeamLineup> allUserCustomLineups = new ArrayList<>();

        Map<String, List<Batter>> userBattingOrders = userCustomBattingOrders.get(userId);
        if (userBattingOrders != null) {
            userBattingOrders.forEach((teamName, batters) -> {
                for (int i = 0; i < batters.size(); i++) {
                    Batter batter = batters.get(i);
                    TeamLineup tl = new TeamLineup();
                    tl.setTeamName(teamName);
                    tl.setUserId(userId);
                    tl.setPosition((i + 1) + "th_Batter");
                    tl.setPlayerName(batter.getName());
                    allUserCustomLineups.add(tl);
                }
            });
        }

        Map<String, Pitcher> userPitchers = userCustomStartingPitchers.get(userId);
        if (userPitchers != null) {
            userPitchers.forEach((teamName, pitcher) -> {
                TeamLineup tlPitcher = new TeamLineup();
                tlPitcher.setTeamName(teamName);
                tlPitcher.setUserId(userId);
                tlPitcher.setPosition("Starting_Pitcher");
                tlPitcher.setPlayerName(pitcher.getName());
                allUserCustomLineups.add(tlPitcher);
            });
        }
        logger.info("사용자 {}의 모든 커스텀 라인업을 메모리에서 조회했습니다.", userId);
        return allUserCustomLineups;
    }

    @Override
    public List<String> getAvailablePlayers(String teamName) {
        return allBattersByName.values().stream()
                .filter(batter -> batter.getTeam().equals(teamName))
                .map(Batter::getName)
                .collect(Collectors.toList());
    }

    public Batter getBatterByName(String playerName) {
        return allBattersByName.get(playerName);
    }

    public Pitcher getPitcherByName(String pitcherName) {
        return allPitchersByName.get(pitcherName);
    }
}