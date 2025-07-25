import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css';

const Header = () => {
  return (
    <header className="header-container">
      <nav className="header-nav">
        <Link to="/" className="header-logo">.</Link>
        <div className="header-nav-links">
          <Link to="/kboboard" className="header-nav-link">게시판</Link>
          <Link to="/kbo" className="header-nav-link">BKO</Link>
          <Link to="/game" className="header-nav-link">게임</Link>
        </div>
          <Link to="/loginpage" className="header-nav-link">로그인</Link>
      </nav>
    </header>
  );
};

export default Header;