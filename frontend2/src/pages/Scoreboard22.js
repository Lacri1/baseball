import React from 'react';
import '../styles/Scoreboard22.css';

const Scoreboard = ({ strike, ball, out, innings, bases }) => {

  const renderDots = (count, maxCount, type) => {
    return Array.from({ length: maxCount }).map((_, i) => (
      <div key={i} className={`count-dot ${type} ${i < count ? 'on' : ''}`}></div>
    ));
  };

  

  return (
    <div className="scoreboard">
      {/* S/B/O 전광판 */}
      <div className="count-line">
        <span className="count-label">S</span>
        <div className="count-dots">{renderDots(strike, 2, 'strike')}</div>
      </div>
      <div className="count-line">
        <span className="count-label">B</span>
        <div className="count-dots">{renderDots(ball, 3, 'ball')}</div>
      </div>
      <div className="count-line">
        <span className="count-label">O</span>
        <div className="count-dots">{renderDots(out, 2, 'out')}</div>
      </div>
      </div>

  
  );
};

export default Scoreboard;
