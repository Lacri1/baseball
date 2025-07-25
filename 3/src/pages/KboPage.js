import React from 'react';
import '../styles/kboPage.css';
import Slider from "react-slick"; 

const KboPage = () => {

  return (
    <div className="about-container">
      <h1>순위</h1>
      <div className="player-record-container">
      <button className='team-record'>팀 순위</button>
      <button className='team-record'>선수 순위</button>
      </div>
      <h1>기록</h1>
      <div className="player-record">
      <button className='team-record'>팀 기록</button>
      <button className='team-record'>선수 기록</button>
     </div>
   </div>
  );
};

export default KboPage; 