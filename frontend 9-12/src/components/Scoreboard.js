import React from 'react';

const Scoreboard = ({ gameState }) => {
  if (!gameState) {
    return <div>Loading Scoreboard...</div>;
  }

  const {
    homeTeam,
    awayTeam,
    homeScore,
    awayScore,
    inning,
    isTop,
    strike,
    ball,
    out,
    homeHit,
    awayHit
  } = gameState;

  const bsoStyle = {
    display: 'flex',
    gap: '10px',
    marginTop: '10px',
    justifyContent: 'center'
  };

  const lightStyle = (on) => ({
    width: '20px',
    height: '20px',
    borderRadius: '50%',
    backgroundColor: on ? 'yellow' : 'grey',
    display: 'inline-block',
    margin: '2px'
  });

  return (
    <div style={{ border: '2px solid black', padding: '10px', width: '300px' }}>
      <h2 style={{ textAlign: 'center' }}>{inning}회 {isTop ? '초' : '말'}</h2>
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th style={{ border: '1px solid black', padding: '5px' }}>팀</th>
            <th style={{ border: '1px solid black', padding: '5px' }}>점수</th>
            <th style={{ border: '1px solid black', padding: '5px' }}>안타</th>
          </tr>
        </thead>
        <tbody>
          <tr style={{ backgroundColor: !isTop ? '#e3f2fd' : 'transparent' }}>
            <td style={{ border: '1px solid black', padding: '5px' }}>{awayTeam}</td>
            <td style={{ border: '1px solid black', padding: '5px' }}>{awayScore}</td>
            <td style={{ border: '1px solid black', padding: '5px' }}>{awayHit}</td>
          </tr>
          <tr style={{ backgroundColor: isTop ? '#e3f2fd' : 'transparent' }}>
            <td style={{ border: '1px solid black', padding: '5px' }}>{homeTeam}</td>
            <td style={{ border: '1px solid black', padding: '5px' }}>{homeScore}</td>
            <td style={{ border: '1px solid black', padding: '5px' }}>{homeHit}</td>
          </tr>
        </tbody>
      </table>
      <div style={bsoStyle}>
        <div>
          <strong>B:</strong>
          <span style={lightStyle(ball > 0)}></span>
          <span style={lightStyle(ball > 1)}></span>
          <span style={lightStyle(ball > 2)}></span>
        </div>
        <div>
          <strong>S:</strong>
          <span style={lightStyle(strike > 0)}></span>
          <span style={lightStyle(strike > 1)}></span>
        </div>
        <div>
          <strong>O:</strong>
          <span style={lightStyle(out > 0)}></span>
          <span style={lightStyle(out > 1)}></span>
        </div>
      </div>
    </div>
  );
};

export default Scoreboard;
