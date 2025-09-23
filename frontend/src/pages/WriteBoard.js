import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";

const WriteBoard = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!user) return alert("로그인 필요");

    const newPost = {
      id: Date.now(),
      title,
      writer: user.nickname || user.Id,
      content,
      comments: [],
    };

    // 임시 저장: localStorage나 상위 state로 전달 가능
    console.log("저장:", newPost);

    navigate("/");
  };

  return (
    <div>
      <h2>글쓰기</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>제목</label>
          <input value={title} onChange={(e) => setTitle(e.target.value)} required />
        </div>
        <div>
          <label>작성자</label>
          <input value={user ? (user.nickname || user.Id) : ""} readOnly />
        </div>
        <div>
          <label>내용</label>
          <textarea value={content} onChange={(e) => setContent(e.target.value)} required />
        </div>
        <button type="submit">저장</button>
        <button type="button" onClick={() => navigate(-1)}>취소</button>
      </form>
    </div>
  );
};

export default WriteBoard;
