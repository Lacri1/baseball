import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import HomePage from './pages/HomePage';
import KboPage from './pages/KboPage';
import KboBoard from './pages/KboBoard';
import LoginPage from './pages/loginpage';
import ProfilePage from './pages/ProfilePage';
import PostForm2 from './pages/PostForm2';
import KboBoardDetail from './pages/KboBoardDetail';
// import BaseballGamePage from './pages/BaseballGamePage';
import MainPage from './pages/MainPage';
import TeamSetupPage from './pages/TeamSetupPage';
import GamePage from './pages/GamePage';
import ResultPage from './pages/ResultPage';
import TeamInfoPage from './pages/TeamInfoPage';
import HelpPage from './pages/HelpPage';

import './App.css';

function App() {
  const [userId, setUserId] = useState(localStorage.getItem('userId') || null);

  return (
    <Router>
      <Header userId={userId} setUserId={setUserId} />
      <div className="App">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/kbo" element={<KboPage />} />
          <Route path="/kboBoard" element={<KboBoard />} />
          <Route path="/kboBoard/detail/:id" element={<KboBoardDetail />} />
          <Route path="/PostForm2/new" element={<PostForm2 />} />
          <Route path="/PostForm2/edit/:id" element={<PostForm2 />} />
          <Route path="/login" element={<LoginPage setUserId={setUserId} />} />
          <Route path="/ProfilePage" element={<ProfilePage />} />
          {/* <Route path="/Game" element={<BaseballGamePage />} /> */}
          <Route path="/game" element={<MainPage />} />
        <Route path="/game/setup" element={<TeamSetupPage />} />
        <Route path="/game/:gameId/play" element={<GamePage />} />
        <Route path="/game/result" element={<ResultPage />} />
        <Route path="/team/info" element={<TeamInfoPage />} />
        <Route path="/help" element={<HelpPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
