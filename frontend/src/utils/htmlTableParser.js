// Utility to parse HTML table strings into array of objects by mapping header labels to keys

const normalizeLabel = (label) => label.replace(/\s+/g, '').toLowerCase();

const selectHeaderCells = (doc) => {
	let headerCells = Array.from(doc.querySelectorAll('thead th'));
	if (headerCells.length === 0) {
		const firstRow = doc.querySelector('tr');
		if (firstRow) {
			headerCells = Array.from(firstRow.querySelectorAll('th, td'));
		}
	}
	return headerCells.map((el) => (el.textContent || '').trim());
};

const selectDataRows = (doc) => {
	let rows = Array.from(doc.querySelectorAll('tbody tr'));
	if (rows.length === 0) {
		const allRows = Array.from(doc.querySelectorAll('tr'));
		rows = allRows.slice(1); // skip header row
	}
	return rows;
};

const parseTable = (htmlString, headerToKeyMap, numericKeys = new Set(), keepAsStringKeys = new Set()) => {
	try {
		const parser = new DOMParser();
		const doc = parser.parseFromString(htmlString, 'text/html');
		const headerLabels = selectHeaderCells(doc).map(normalizeLabel);
		const headerIndexToKey = {};
		headerLabels.forEach((label, idx) => {
			const key = headerToKeyMap[label];
			if (key) headerIndexToKey[idx] = key;
		});

		const rows = selectDataRows(doc);
		const results = [];
		rows.forEach((tr) => {
			const cells = Array.from(tr.querySelectorAll('td, th'));
			if (cells.length === 0) return;
			const obj = {};
			cells.forEach((td, idx) => {
				const key = headerIndexToKey[idx];
				if (!key) return;
				let value = (td.textContent || '').trim();
				if (numericKeys.has(key) && value !== '') {
					const num = Number(value.replace(/,/g, ''));
					value = Number.isNaN(num) ? value : num;
				}
				obj[key] = value;
			});
			results.push(obj);
		});
		return results;
	} catch (e) {
		return [];
	}
};

export const parseTeamHtmlToArray = (htmlString) => {
	const headerToKeyMap = {
		[normalizeLabel('순위')]: 'rank',
		[normalizeLabel('팀명')]: 'teamName',
		[normalizeLabel('경기')]: 'gameNum',
		[normalizeLabel('경기 수')]: 'gameNum',
		[normalizeLabel('승')]: 'win',
		[normalizeLabel('패')]: 'lose',
		[normalizeLabel('무')]: 'draw',
		[normalizeLabel('승률')]: 'winPercentage',
		[normalizeLabel('게임차')]: 'gamesBehind',
	};
	const numericKeys = new Set(['gameNum', 'win', 'lose', 'draw']);
	const keepAsStringKeys = new Set(['winPercentage', 'gamesBehind']);
	return parseTable(htmlString, headerToKeyMap, numericKeys, keepAsStringKeys);
};

export const parseHitterHtmlToArray = (htmlString) => {
	const headerToKeyMap = {
		[normalizeLabel('순위')]: 'rank',
		[normalizeLabel('선수명')]: 'playerName',
		[normalizeLabel('팀명')]: 'playerTeam',
		[normalizeLabel('팀')]: 'playerTeam',
		[normalizeLabel('타율')]: 'battingAverage',
		[normalizeLabel('경기')]: 'gameNum',
		[normalizeLabel('경기 수')]: 'gameNum',
		[normalizeLabel('타석')]: 'plateAppearance',
		[normalizeLabel('득점')]: 'run',
		[normalizeLabel('안타')]: 'hit',
		[normalizeLabel('2루타')]: 'twoBase',
		[normalizeLabel('3루타')]: 'threeBase',
		[normalizeLabel('홈런')]: 'homeRun',
		[normalizeLabel('타점')]: 'runsBattedIn',
		[normalizeLabel('볼넷')]: 'fourBall',
		[normalizeLabel('삼진')]: 'strikeOut',
		[normalizeLabel('출루율')]: 'onBasePercentage',
		[normalizeLabel('OPS')]: 'onbasePlusSlug',
	};
	const numericKeys = new Set([
		'gameNum', 'plateAppearance', 'run', 'hit', 'twoBase', 'threeBase', 'homeRun', 'runsBattedIn', 'fourBall', 'strikeOut'
	]);
	return parseTable(htmlString, headerToKeyMap, numericKeys);
};

export const parsePitcherHtmlToArray = (htmlString) => {
	const headerToKeyMap = {
		[normalizeLabel('순위')]: 'rank',
		[normalizeLabel('선수명')]: 'playerName',
		[normalizeLabel('팀명')]: 'playerTeam',
		[normalizeLabel('팀')]: 'playerTeam',
		[normalizeLabel('평균 자책점')]: 'earnedRunAverage',
		[normalizeLabel('ERA')]: 'earnedRunAverage',
		[normalizeLabel('경기')]: 'gameNum',
		[normalizeLabel('경기 수')]: 'gameNum',
		[normalizeLabel('승')]: 'win',
		[normalizeLabel('패')]: 'lose',
		[normalizeLabel('세이브')]: 'save',
		[normalizeLabel('홀드')]: 'hold',
		[normalizeLabel('이닝')]: 'inningsPitched',
		[normalizeLabel('피안타')]: 'hits',
		[normalizeLabel('피홈런')]: 'homeRun',
		[normalizeLabel('볼넷')]: 'baseOnBalls',
		[normalizeLabel('탈삼진')]: 'strikeOut',
		[normalizeLabel('실점')]: 'runs',
		[normalizeLabel('자책')]: 'earnedRun',
		[normalizeLabel('WHIP')]: 'whip',
	};
	const numericKeys = new Set([
		'gameNum', 'win', 'lose', 'save', 'hold', 'hits', 'homeRun', 'baseOnBalls', 'strikeOut', 'runs', 'earnedRun'
	]);
	return parseTable(htmlString, headerToKeyMap, numericKeys);
};

export const normalizeList = (data) => {
	if (Array.isArray(data)) return data;
	if (Array.isArray(data?.data)) return data.data;
	if (Array.isArray(data?.list)) return data.list;
	if (Array.isArray(data?.items)) return data.items;
	return [];
};


