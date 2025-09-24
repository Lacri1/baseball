import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const navigate = useNavigate();

  return (
    <div style={{ padding: 20 }}>
      <h1>⚾ 야구 게임</h1>
      <button onClick={() => navigate('/game/setup')}>게임 시작</button>
      <button onClick={() => navigate('/help')} style={{ marginLeft: 10 }}>도움말</button>
    </div>
  );
};

export default MainPage;
