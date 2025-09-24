import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/ProfilePage.css';

const RegisterPage = ({ setUserId }) => {
  const [formData, setFormData] = useState({ id: '', username: '', pw: '', pwConfirm: '', email: '' });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: '' }));
  };

  const validateForm = () => {
    const newErrors = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!formData.id) newErrors.id = '아이디를 입력해주세요';
    if (!formData.username) newErrors.username = '닉네임을 입력해주세요';
    if (!formData.pw) newErrors.pw = '비밀번호를 입력해주세요';
    else if (formData.pw.length < 6) newErrors.pw = '비밀번호는 6자리 이상이어야 합니다';
    if (formData.pw !== formData.pwConfirm) newErrors.pwConfirm = '비밀번호가 일치하지 않습니다';
    if (!formData.email) newErrors.email = '이메일을 입력해주세요';
    else if (!emailRegex.test(formData.email)) newErrors.email = '올바른 이메일 형식이 아닙니다';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    setIsLoading(true);

    try {
      // 회원가입
      const registerRes = await axios.post('http://localhost:8080/api/login/register', {
        id: formData.id,
        pw: formData.pw,
        username: formData.username,
        email: formData.email
      });

      if (!registerRes.data.success) {
        setErrors(prev => ({ ...prev, id: registerRes.data.message }));
        setIsLoading(false);
        return;
      }

      alert('회원가입 완료.');

      // 폼 초기화
      setFormData({ id: '', username: '', pw: '', pwConfirm: '', email: '' });
      setErrors({});

      // 자동 로그인
      const loginRes = await axios.post('http://localhost:8080/api/login/login', {
        id: formData.id,
        pw: formData.pw
      });

      if (loginRes.data.success) {
        localStorage.setItem('userId', formData.id);
        setUserId(formData.id);
        alert('자동 로그인 성공!');
        navigate('/');
      } else {
        alert('자동 로그인 실패: ' + loginRes.data.message);
        navigate('/login');
      }

    } catch (error) {
      console.error(error);
      alert('회원가입 완료.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="profile-container">
      <div className="profile-card">
        <div className="profile-header">
          <h1 className="profile-title">회원가입</h1>
          <p className="profile-subtitle">새로운 계정을 만들어보세요</p>
        </div>
        
        <form onSubmit={handleRegister} className="profile-form">
          <div className="form-group">
            <input
              name="id"
              placeholder="아이디"
              value={formData.id}
              onChange={handleChange}
              disabled={isLoading}
              className={`form-input ${errors.id ? 'error' : ''}`}
            />
            {errors.id && <div className="error-text">{errors.id}</div>}
          </div>

          <div className="form-group">
            <input
              name="username"
              placeholder="닉네임"
              value={formData.username}
              onChange={handleChange}
              disabled={isLoading}
              className={`form-input ${errors.username ? 'error' : ''}`}
            />
            {errors.username && <div className="error-text">{errors.username}</div>}
          </div>

          <div className="form-group">
            <input
              name="pw"
              type="password"
              placeholder="비밀번호"
              value={formData.pw}
              onChange={handleChange}
              disabled={isLoading}
              className={`form-input ${errors.pw ? 'error' : ''}`}
            />
            {errors.pw && <div className="error-text">{errors.pw}</div>}
          </div>

          <div className="form-group">
            <input
              name="pwConfirm"
              type="password"
              placeholder="비밀번호 확인"
              value={formData.pwConfirm}
              onChange={handleChange}
              disabled={isLoading}
              className={`form-input ${errors.pwConfirm ? 'error' : ''}`}
            />
            {errors.pwConfirm && <div className="error-text">{errors.pwConfirm}</div>}
          </div>

          <div className="form-group">
            <input
              name="email"
              placeholder="이메일"
              value={formData.email}
              onChange={handleChange}
              disabled={isLoading}
              className={`form-input ${errors.email ? 'error' : ''}`}
            />
            {errors.email && <div className="error-text">{errors.email}</div>}
          </div>

          <button type="submit" disabled={isLoading} className="form-button form-button-primary">
            {isLoading ? (
              <>
                <span className="loading-spinner"></span>
                회원가입 중...
              </>
            ) : (
              '회원가입'
            )}
          </button>
        </form>

        <div className="form-footer">
          <p className="form-footer-text">이미 계정이 있으신가요?</p>
          <button 
            onClick={() => navigate('/login')} 
            disabled={isLoading}
            className="form-button form-button-secondary"
          >
            로그인으로 돌아가기
          </button>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
