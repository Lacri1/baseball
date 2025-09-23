import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { authAPI } from '../api/api'; // Import authAPI
import '../styles/LoginPage.css';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  // const [loginMessage, setLoginMessage] = useState('');
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const validateForm = () => {
    const newErrors = {};

    if (!username.trim()) {
      newErrors.username = '아이디를 입력해주세요';
    }
    if (!password.trim()) {
      newErrors.password = '비밀번호를 입력해주세요';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (field, value) => {
    if (field === 'username') {
      setUsername(value);
    } else if (field === 'password') {
      setPassword(value);
    }

    // 에러가 있던 필드의 에러 제거
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }

    // 로그인 메시지 초기화
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    try {
      const res = await authAPI.login({ id: username, pw: password }); // Actual API call

      if (res.data.success) {
        login(res.data.userInfo); // Assuming res.data.userInfo contains user info
        alert('로그인 성공!');
        setTimeout(() => {
          navigate('/');
        }, 1500);
      } else {
        // Handle login failure based on backend response
        alert(res.data.message || '로그인 실패: 알 수 없는 오류');
      }

    } catch (error) {
      console.error('Login error:', error);
      if (error.response && error.response.data && error.response.data.message) {
        alert(error.response.data.message);
      } else {
        alert('로그인 중 오류가 발생했습니다.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegisterNavigation = () => {
    navigate('/ProfilePage'); // 회원가입 페이지로 이동
  };

  return (
      <div className="login-container">
        <div className="login-card">
          <h1>로그인</h1>
          <form onSubmit={handleLogin} className="login-form">
            <input
                type="text"
                placeholder="아이디"
                value={username}
                onChange={(e) => handleChange('username', e.target.value)}
                disabled={isLoading}
                className={errors.username ? 'error' : ''}
                autoComplete="username"
            />
            {errors.username && <div className="error-text">{errors.username}</div>}

            <input
                type="password"
                placeholder="비밀번호"
                value={password}
                onChange={(e) => handleChange('password', e.target.value)}
                disabled={isLoading}
                className={errors.password ? 'error' : ''}
                autoComplete="current-password"
            />
            {errors.password && <div className="error-text">{errors.password}</div>}



            <button type="submit" disabled={isLoading}>
              {isLoading ? '로그인 중...' : '로그인'}
            </button>
          </form>

          <button onClick={handleRegisterNavigation} disabled={isLoading}>
            회원가입하러 가기
          </button>
        </div>
      </div>
  );
};

export default LoginPage;