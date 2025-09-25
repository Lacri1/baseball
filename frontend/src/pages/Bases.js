// Bases.js
import React from 'react';
import '../styles/Bases.css';

const Bases = ({ bases, basePlayers }) => {
  const positions = [
    { left: '320px', top: '250px' }, // 1루
    { left: '200px', top: '150px' }, // 2루
    { left: '80px', top: '250px' }   // 3루
  ];

  if (!bases) return null;

  return (
    <div className="bases-field">
      {bases.map((b, i) => positions[i] && (
        <div key={i} className={`base base-${i + 1} ${b ? 'occupied' : 'empty'}`}>
          <span className="base-number"></span>
          {b && basePlayers && basePlayers[i] && (
            <span className="base-player">
              {basePlayers[i].name || basePlayers[i]}
            </span>
          )}
        </div>
      ))}
    </div>
  );
};

export default Bases;
