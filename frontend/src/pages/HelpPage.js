import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/HelpPage.css";

const HelpPage = () => {
  const navigate = useNavigate();
  return (
    <div className="help-container">
      <div className="help-header">
        <h1 className="help-title">도움말</h1>
        <p className="help-subtitle">게임 사용법을 확인하세요</p>
      </div>
      
      <div className="help-content">
        <div className="help-section">
          <h2>게임 기본 규칙</h2>
          <ul className="help-list">
            <li>총 이닝 수 선택 후 팀을 선택하고 게임을 시작합니다.</li>
            <li>투구: 투구 게이지를 클릭하여 투구를 시도합니다.</li>
            <li>타격: 타격 게이지를 클릭하여 타격을 시도합니다.</li>
            <li>S/B/O 규칙:
              <ul className="help-sub-list">
                <li>3스트라이크 = 아웃</li>
                <li>4볼 = 볼넷</li>
                <li>3아웃 = 공격/수비 전환</li>
              </ul>
            </li>
            <li>주자 이동: 1/2/3루 클릭하여 주자 상태를 조정할 수 있습니다.</li>
            <li>게임 종료: 9이닝 종료 또는 콜드게임 조건 충족 시 결과 화면으로 이동합니다.</li>
          </ul>
        </div>

        <div className="help-navigation">
          <button className="help-button" onClick={()=>navigate("/")}>메인 화면</button>
        </div>
      </div>
    </div>
  );
};

export default HelpPage;
