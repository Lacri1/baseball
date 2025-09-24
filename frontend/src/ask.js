// 1. 설치한 라이브러리를 불러옵니다.
const { GoogleGenerativeAI } = require("@google/generative-ai");

// 2. 환경 변수에서 API 키를 가져옵니다. (보안상 더 안전합니다)
const genAI = new GoogleGenerativeAI(process.env.GOOGLE_API_KEY);

// 3. 터미널에서 입력한 질문을 가져옵니다.
//    (예: node ask.js "너는 누구니?" 에서 "너는 누구니?")
const userPrompt = process.argv[2];

if (!userPrompt) {
    console.error("질문을 입력해주세요! 예: node ask.js '오늘 날씨 어때?'");
    process.exit(1);
}

async function run() {
    // 4. 모델을 선택하고 실행합니다.
    const model = genAI.getGenerativeModel({ model: "gemini-pro" });
    const result = await model.generateContent(userPrompt);
    const response = await result.response;
    const text = response.text();
    console.log(text); // 5. 결과를 터미널에 출력합니다.
}

run();