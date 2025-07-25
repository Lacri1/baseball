import React, { useState } from 'react';
import '../styles/App.css';
import { CircularProgressbarWithChildren } from 'react-circular-progressbar';
import'../styles/Custom.css';
import { buildStyles } from 'react-circular-progressbar';

const BaseballGamePage = () => {
  const [gameStarted, setGameStarted] = useState(false);
 



  
  return (
    <div>
    <div className="game-container" style={{ position: 'relative' }}>
      {/* 왼쪽 위에 고정된 팀/상대팀 */}
      {/* <div style={{ position: 'absolute', top: 120, left: 20, zIndex: 10, textAlign: 'center' }}>
        <h2 style={{ margin: 0 }}>내 팀</h2>
        <label className='progress-bowler-label'>
        
          <span>점수</span>
          <span>스트라이크</span>
          <span>볼</span>
          <span>아웃</span>
          <span>타석</span>
          <span>타자</span>
          <span>타자</span>
       </label>
        
      </div>
      <div style={{ position: 'absolute', top: 120, left: 1230, zIndex: 10, textAlign: 'center' }}>
        <h2 style={{ margin: 0 }}>상대팀</h2>
        <label className='progress-bowler-label'>
          <span>점수</span>
          <span>스트라이크</span>
          <span>볼</span>
          <span>아웃</span>
          <span>타석</span>
          <span>타자</span>
          <span>타자</span>
       </label> */}
        <div>
      </div>
      <h1 className="game-title">⚾ Baseball Game</h1>
      <div className="game-info">
        
        {!gameStarted && (
          <button className="game-button" onClick={() => setGameStarted(true)}>
            게임 시작
          </button>
        )}
        {gameStarted && (
          <div>
            <div style={{ position: 'absolute', top: 120, left: 20, zIndex: 10, textAlign: 'center' }}>
        <h2 style={{ margin: 0 }}>내 팀</h2>
        <label className='progress-bowler-label'>
        
          <span>점수</span>
          <span>스트라이크</span>
          <span>볼</span>
          <span>아웃</span>
          <span>타석</span>
          <span>타자</span>
          <span>타자</span>
       </label>
       </div>
       <div style={{ position: 'absolute', top: 120, left: 1230, zIndex: 10, textAlign: 'center' }}>
        <h2 style={{ margin: 0 }}>상대팀</h2>
        <label className='progress-bowler-label'>
          <span>점수</span>
          <span>스트라이크</span>
          <span>볼</span>
          <span>아웃</span>
          <span>타석</span>
          <span>타자</span>
          <span>타자</span>
       </label>
        
      </div>
      <CircularProgressbarWithChildren className='progress-bowler' minValue={0} maxValue={100} value={null} styles={buildStyles({
          pathColor: '#00C6AE',           // 진행 색
          trailColor: '#eee',             // 배경 트레일 색
          strokeLinecap: 'round',   
            })}>
  <div className='progress-bowler-button'>
    <button className='progress-bowler-button-text'>던지기</button>
  </div>
</CircularProgressbarWithChildren>
          
            <CircularProgressbarWithChildren className='progress-batter' minValue={0} maxValue={100} value={null} styles={buildStyles({
          pathColor: '#00C6AE',           // 진행 색
          trailColor: '#eee',             // 배경 트레일 색
          strokeLinecap: 'round',   
            })}>
  
    <button  className='progress-batter-button'>치기</button>
</CircularProgressbarWithChildren>
          </div>
        )}
      </div>
    </div>
    </div>
  );
};

export default BaseballGamePage;