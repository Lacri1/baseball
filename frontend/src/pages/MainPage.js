import { useNavigate } from 'react-router-dom';
import '../styles/MainPage.css';

const MainPage = () => {
  const navigate = useNavigate();

  return (
    <div className="main-container">
      <div className="main-header">
        <h1 className="main-title">⚾ 야구 게임</h1>
        <p className="main-subtitle">실시간 야구 게임을 즐겨보세요!</p>
        <div className="main-buttons">
          <button className="main-button" onClick={() => navigate('/game/setup')}>게임 시작</button>
          <button className="main-button" onClick={() => navigate('/help')}>도움말</button>
        </div>
      </div>
      
      <div className="main-content">
        <p className="main-description">
          한국 프로야구의 모든 것을 경험해보세요. 팀을 선택하고, 선수를 배치하고, 
          실시간으로 경기를 진행하며 승부를 겨뤄보세요!
        </p>
        
        <div className="main-features">
          <div className="main-feature">
            <span className="main-feature-icon">⚾</span>
            <h3>실시간 게임</h3>
            <p>실제 야구 경기처럼 실시간으로 게임을 진행하고 결과를 확인하세요.</p>
          </div>
          
          <div className="main-feature">
            <span className="main-feature-icon">👥</span>
            <h3>팀 관리</h3>
            <p>KBO 10개 팀 중에서 선택하고 선수들을 자유롭게 배치하세요.</p>
          </div>
          
          <div className="main-feature">
            <span className="main-feature-icon">📊</span>
            <h3>통계 확인</h3>
            <p>경기 결과와 선수들의 성과를 자세히 확인할 수 있습니다.</p>
          </div>
        </div>
        
        <div className="main-cta">
          <h3>지금 시작하세요!</h3>
          <p>팀을 선택하고 첫 경기를 시작해보세요.</p>
          <button className="main-cta-button" onClick={() => navigate('/game/setup')}>
            게임 시작하기
          </button>
        </div>
        
        <div className="main-footer">
          <p>© 2024 야구 게임. 모든 권리 보유.</p>
        </div>
      </div>
    </div>
  );
};

export default MainPage;
