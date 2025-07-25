import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/LoginPage.css';

const ProfilePage = () => {
  const [formData, setFormData] = useState({ id: '', password: '', 'password-confirm': '' });
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
    if (!formData.id) newErrors.id = '아이디를 입력해주세요';
    else if (formData.id.length < 6) newErrors.id = '아이디는 최소 6자 이상이어야 합니다';
    if (!formData.password) newErrors.password = '비밀번호를 입력해주세요';
    else if (formData.password.length < 6) newErrors.password = '비밀번호는 최소 6자 이상이어야 합니다';
    if (formData['password-confirm'] !== formData.password) newErrors['password-confirm'] = '비밀번호가 일치하지 않습니다';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      navigate('/');
    }, 1000);
  };

    return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>회원가입</h1>
          <p>계정에 회원가입하여 서비스를 이용하세요</p>
        </div>
        <form onSubmit={handleSubmit} className="login-form">
          {errors.general && <div className="error-message general-error">{errors.general}</div>}
          <div className="form-group">
            <label htmlFor="id">아이디</label>
            <input
              type="id"
              id="id"
              name="id"
              value={formData.id}
              onChange={handleChange}
              placeholder="아이디를 입력하세요"
              className={errors.id ? 'error' : ''}
            />
            {errors.id && <span className="error-text">{errors.id}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="비밀번호를 입력하세요"
              className={errors.password ? 'error' : ''}
            />
            {errors.password && <span className="error-text">{errors.password}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="password-confirm">비밀번호 확인</label>
            <input
              type="password"
              id="password-confirm"
              name="password-confirm"
              value={formData['password-confirm']}
              onChange={handleChange}
              placeholder="비밀번호를 다시 입력하세요"
              className={errors['password-confirm'] ? 'error' : ''}
            />
            {errors['password-confirm'] && <span className="error-text">{errors['password-confirm']}</span>}
          </div>
        
          <button type="submit" className="login-button" disabled={isLoading}>
            {isLoading ? '회원가입 중...' : '회원가입'}
          </button>
          
        
        </form>
      </div>
    </div>
  );
};

export default ProfilePage;
