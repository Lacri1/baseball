import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/PostForm2.css";
const PostForm2 = ({ addPost }) => {
  const navigate = useNavigate();
  const [writer, setWriter] = useState("");
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");
  const [roomId, setRoomId] = useState('general');
  const savePost = async (e) => {
    try {
      const response = await axios.post(
        "http://localhost:8080/board",
        {
          writer: writer,
          title: title,
          body: body,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.status === 200) {
        alert("게시물이 등록되었습니다.");
        navigate("/");
      } else {
        throw new Error("게시물 등록 실패");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("게시물 등록에 실패했습니다.");
    }
  };

  const backToBoard = () => {
    navigate("/noticeboard");
  };

  return (
    <div className="container">
      <div className="input-group">
        <h2 style={{ textAlign: "center" }}>글쓰기</h2>
        <span>제목</span>
        <input
          type="text"
          placeholder="제목"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

      <div className="input-group2">
        <span>작성자</span>
        <input
          type="text"
          placeholder="작성자"
          value={writer}
          onChange={(e) => setWriter(e.target.value)}
        />
      </div>
      <label>
        종류{' '}
        <select
          value={roomId}
          onChange={e => setRoomId(e.target.value)}
          >
          <option value="general">자유게시글</option>
          <option value="travel">KBO게시글</option>
        </select>
      </label>

      <textarea
        placeholder="내용"
        cols="100"
        rows="10"
        value={body}
        onChange={(e) => setBody(e.target.value)}
      />

      <div className="button-group">
        <button onClick={savePost}>저장</button>
        <button onClick={backToBoard}>취소</button>
      </div>
        </div>
    </div>
  );
};

export default PostForm2;