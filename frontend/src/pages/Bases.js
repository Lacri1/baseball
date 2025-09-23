
// Bases.js
import React from 'react';
import img111 from '../styles/111.png';

const Bases = ({ bases }) => {
  const positions = [
    { left: '220px', top: '250px' }, // 1루
    { left: '130px', top: '150px' }, // 2루
    { left: '40px', top: '250px' }   // 3루
  ];

  if (!bases) return null;

  return (
    <div style={{
      position: 'relative',
      width: 300,
      height: 500,
      border: '1px solid #ccc',
      marginTop: 10,
      backgroundImage: `url(${img111})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center'
    }}>
      {bases.map((b, i) => positions[i] && (
        <div key={i} style={{
          position: 'absolute',
          width: 40,
          height: 40,
          background: b ? 'yellow' : 'white',
          border: '2px solid #333',
          left: positions[i].left,
          top: positions[i].top,
          transform: 'rotate(45deg)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          cursor: 'default'
        }}>
          <span style={{ transform: 'rotate(-45deg)', color: b ? '#000' : '#666' }}>{i+1}루</span>
        </div>
      ))}
    </div>
  );
};

export default Bases;
