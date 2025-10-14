/**
 * 야구 이닝 소수점 변환 함수
 * .333 -> .1, .667 -> .2로 변환
 * @param {string|number} innings - 이닝 값
 * @returns {string} 변환된 이닝 값
 */
export const convertInnings = (innings) => {
    if (!innings && innings !== 0) return '0';

    const inningsStr = String(innings);

    // 소수점이 없는 경우 그대로 반환
    if (!inningsStr.includes('.')) {
        return inningsStr;
    }

    const [integerPart, decimalPart] = inningsStr.split('.');

    // 소수점 부분이 없는 경우
    if (!decimalPart) {
        return integerPart;
    }

    // 소수점 부분을 숫자로 변환하여 확인
    const decimal = parseFloat('0.' + decimalPart);

    // .333 (1/3)에 가까운 경우 .1로 변환
    if (Math.abs(decimal - 0.333) < 0.01) {
        return integerPart + '.1';
    }

    // .667 (2/3)에 가까운 경우 .2로 변환
    if (Math.abs(decimal - 0.667) < 0.01) {
        return integerPart + '.2';
    }

    // 기타 소수점이 있는 경우 원래 값 반환
    return inningsStr;
};
