import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import HomePage from './pages/HomePage';
import KboPage from './pages/KboPage';
import NoticeboardPage from './pages/noticeboard';
import BaseballGamePage from './pages/BaseballGamePage';
import LoginPage from './pages/loginpage';
import KboBoard from './pages/KboBoard';
import ProfilePage from './pages/ProfilePage';
import './styles/common.css';
import './styles/App.css';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

function App() {
  
  return (
    <Router>
     <Header />
     <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/kbo" element={<KboPage />} />
      <Route path="/game" element={<BaseballGamePage />} />
      <Route path="/noticeboard" element={<NoticeboardPage />} />
      <Route path="/kboboard" element={<KboBoard />} />
      <Route path="/loginpage" element={<LoginPage />} />
      <Route path="/profilepage" element={<ProfilePage />} />
     </Routes>
    </Router>
  );
}

export default App;
