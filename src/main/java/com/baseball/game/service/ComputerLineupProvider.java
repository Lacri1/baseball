package com.baseball.game.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 간단한 인메모리 컴퓨터 라인업/선발투수 제공자.
 * 운영/DB 미연결 환경에서 기본 라인업을 제공하기 위한 용도.
 */
public final class ComputerLineupProvider {

	private static final Map<String, List<String>> TEAM_TO_DEFAULT_BATTING_ORDER;
	private static final Map<String, String> TEAM_TO_STARTING_PITCHER;

	static {
		Map<String, List<String>> batting = new HashMap<>();
		Map<String, String> pitchers = new HashMap<>();
        // KIA 타이거즈
        batting.put("KIA 타이거즈", Arrays.asList(
			"박찬호", "소크라테스", "김도영", "최형우",
			"나성범", "김선빈", "이우성", "김태군", "최원준"
		));
		pitchers.put("KIA 타이거즈", "네일");
        // 삼성 라이온즈
        batting.put("삼성 라이온즈", Arrays.asList(
			"김지찬", "김헌곤", "디아즈", "강민호",
			"김영웅", "박병호", "류지혁", "이재현", "김현준"
		));
		pitchers.put("삼성 라이온즈", "코너");

        // LG 트윈스
		batting.put("LG 트윈스", Arrays.asList(
			"홍창기", "신민재", "오스틴", "문보경",
			"오지환", "김현수", "박동원", "박해민", "문성주"
		));
		pitchers.put("LG 트윈스", "손주영");
        // 두산 베어스
		batting.put("두산 베어스", Arrays.asList(
			"정수빈", "김재호", "제러드", "김재환",
			"양석환", "양의지", "강승호", "허경민", "조수행"
		));
		pitchers.put("두산 베어스", "곽빈");
        // kt 위즈
        batting.put("kt 위즈", Arrays.asList(
			"김민혁", "로하스", "장성우", "강백호",
			"문상철", "오윤석", "황재균", "배정대", "심우준"
		));
		pitchers.put("kt 위즈", "쿠에바스");

        // SSG 랜더스
		batting.put("SSG 랜더스", Arrays.asList(
			"최지훈", "박성한", "최정", "에레디아",
			"한유섬", "이지영", "고명준", "정준재", "박지환"
		));
		pitchers.put("SSG 랜더스", "앤더슨");

        // 롯데 자이언츠
        batting.put("롯데 자이언츠", Arrays.asList(
			"황성빈", "윤동희", "레이예스", "전준우",
			"나승엽", "손호영", "손성빈", "고승민", "박승욱"
		));
		pitchers.put("롯데 자이언츠", "반즈");

        // 한화 이글스
		batting.put("한화 이글스", Arrays.asList(
			"최인호", "페라자", "문현빈", "노시환",
			"채은성", "안치홍", "장진혁", "이도윤", "최재훈"
		));
		pitchers.put("한화 이글스", "류현진");
		
        // NC 다이노스
		batting.put("NC 다이노스", Arrays.asList(
			"박민우", "서호철", "데이비슨", "권희동",
			"김휘집", "천재환", "김주원", "김형준", "한석현"
		));
		pitchers.put("NC 다이노스", "하트");
        //키움 히어로즈
		batting.put("키움 히어로즈", Arrays.asList(
			"김태진", "이주형", "송성문", "김혜성",
			"최주환", "김건희", "장재영", "김웅빈", "박수종"
		));
		pitchers.put("키움 히어로즈", "후라도");

		TEAM_TO_DEFAULT_BATTING_ORDER = Collections.unmodifiableMap(batting);
		TEAM_TO_STARTING_PITCHER = Collections.unmodifiableMap(pitchers);
	}

	private ComputerLineupProvider() {}

	public static List<String> getDefaultBattingOrder(String teamName) {
		if (teamName == null) return Collections.emptyList();
		List<String> list = TEAM_TO_DEFAULT_BATTING_ORDER.get(teamName);
		return list != null ? list : Collections.emptyList();
	}

	public static String getDefaultStartingPitcher(String teamName) {
		if (teamName == null) return null;
		return TEAM_TO_STARTING_PITCHER.get(teamName);
	}
}


