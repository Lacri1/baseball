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
        <div key={i} style={{
          position:'absolute',
          width:6, height:6, borderRadius:'50%',
          backgroundColor: s.color,
          top: s.relY-3,
          left: s.relX-3
        }}></div>
      ))}
    </div>
  );
};

export default StrikeZone;
