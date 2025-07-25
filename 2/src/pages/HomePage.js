import React from 'react';
import '../styles/HomePage.css';
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import '../styles/calendar.css';
    const HomePage = () => {
      var settings = {
        dots: true,
        infinite: true,
        speed: 500,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        fade: true

        
      };
  


  return (
    <div className="home-container">

      <section className="hero-section">
       
        <Slider {...settings}>
      <div>
        <h2>KBO 타자 TOP5 랭킹</h2>
        <div className="image-container">
        <h4>타율
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
        </h4>
        <h4>타점
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
        </h4>
        <h4>홈런
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
        </h4>
        </div>
      </div>
    
        
      <div>
        <h2>KBO 투수 TOP5 랭킹</h2>
        <div className="image-container">
        <h4>ERA
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
        </h4>
        <h4>승리
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
        </h4>
        <h4>탈삼진
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
          <h5>~~~(~~~)</h5>
        </h4>
        </div>
      </div>
    </Slider>  
      </section>
      <section className="team-ranking-row">
        <div className="team-ranking">
          <h2 className="team-ranking-title">KBO팀순위</h2>
          <div className="team-ranking-container">
            <div className="team-ranking-item">
              <h3>1</h3>
              <h3>2</h3>
              <h3>3</h3>
              <h3>4</h3>
              <h3>5</h3>
            </div>
          </div>
        </div>
      
  
      </section>
  
    </div>
  );
};

export default HomePage; 