import React, { useState } from 'react';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';

const WriteContainer = styled.div`
  max-width: 600px;
  margin: 3rem auto 0 auto;
  padding: 2.5rem 2rem 2rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(52,152,219,0.08);
`;

const Title = styled.h1`
  font-size: 1.7rem;
  color: #2c3e50;
  margin-bottom: 2rem;
  text-align: center;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

const Label = styled.label`
  font-weight: 500;
  margin-bottom: 0.5rem;
`;

const Input = styled.input`
  padding: 0.7rem 1rem;
  border: 1.5px solid #dbeafe;
  border-radius: 5px;
  font-size: 1rem;
  &:focus {
    outline: none;
    border-color: #3498db;
  }
`;

const TextArea = styled.textarea`
  padding: 0.7rem 1rem;
  border: 1.5px solid #dbeafe;
  border-radius: 5px;
  font-size: 1rem;
  min-height: 120px;
  resize: vertical;
  &:focus {
    outline: none;
    border-color: #3498db;
  }
`;

const ButtonRow = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
`;

const Button = styled.button`
  background: #3498db;
  color: white;
  border: none;
  padding: 0.7rem 2rem;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
  &:hover {
    background: #217dbb;
  }
  &.cancel {
    background: #95a5a6;
    &:hover {
      background: #7f8c8d;
    }
  }
`;

const Writing = () => {
  const [form, setForm] = useState({
    title: '',
    author: '',
    content: ''
  });
  const navigate = useNavigate();

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = e => {
    e.preventDefault();
    // 실제 저장 로직은 추후 구현
    alert('글이 등록되었습니다! (실제 저장 기능은 추후 구현)');
    navigate('/noticeboard');
  };

  const handleCancel = () => {
    navigate('/noticeboard');
  };

  return (
    <WriteContainer>
      <Title>글쓰기</Title>
      <Form onSubmit={handleSubmit}>
        <div>
          <Label htmlFor="title">제목</Label>
          <Input
            id="title"
            name="title"
            value={form.title}
            onChange={handleChange}
            required
            placeholder="제목을 입력하세요"
          />
        </div>
        <div>
          <Label htmlFor="author">작성자</Label>
          <Input
            id="author"
            name="author"
            value={form.author}
            onChange={handleChange}
            required
            placeholder="작성자 이름"
          />
        </div>
        <div>
          <Label htmlFor="content">내용</Label>
          <TextArea
            id="content"
            name="content"
            value={form.content}
            onChange={handleChange}
            required
            placeholder="내용을 입력하세요"
          />
        </div>
        <ButtonRow>
          <Button type="submit">등록</Button>
          <Button type="button" className="cancel" onClick={handleCancel}>취소</Button>
        </ButtonRow>
      </Form>
    </WriteContainer>
  );
};

export default Writing;

