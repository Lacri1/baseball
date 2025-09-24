// StrikeZone.js
import React from 'react';

const StrikeZone = ({ shots, CM_IN_PX, STRIKE_ZONE_SIZE, onClick }) => {
  return (
    <div
      onClick={onClick}
      style={{
        width: STRIKE_ZONE_SIZE + CM_IN_PX*2,
        height: STRIKE_ZONE_SIZE + CM_IN_PX*2,
        position:'relative',
        cursor:'crosshair',
        border:'1px solid #ccc',
        marginTop: 10
      }}
    >
      <div style={{
        position:'absolute', top:CM_IN_PX, left:CM_IN_PX,
        width:STRIKE_ZONE_SIZE, height:STRIKE_ZONE_SIZE,
        border:'2px solid black',
        display:'grid', gridTemplateColumns:'repeat(3,1fr)', gridTemplateRows:'repeat(3,1fr)'
      }}>
        {[...Array(9)].map((_, i) => (
          <div key={i} style={{
            borderRight:i%3!==2?'1px dashed gray':'none',
            borderBottom:i<6?'1px dashed gray':'none'
          }}></div>
        ))}
      </div>
      {shots.map((s,i) => (
        <span key={i} style={{
          position:'absolute',
          top: s.relY - 8,   // 이모티콘 중심 맞춤
          left: s.relX - 8,
          fontSize: '16px',
          pointerEvents: 'none', // 클릭 방해 안 함
        }}>
          ⚾
        </span>
      ))}
    </div>
  );
};
export default StrikeZone;
