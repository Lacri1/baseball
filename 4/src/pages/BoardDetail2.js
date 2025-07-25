import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/Board2Page.css";

const BoardDetail2 = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [board, setBoard] = useState({});

  const getBoard = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/board?writingId=${id}`
      );

      setBoard(response.data);
    } catch (error) {
      console.error("불러오지 못함", error);
    }
  };

  useEffect(() => {
    getBoard();
  }, [id]);

  const moveToEdit = () => {
    navigate("/edit/" + id);
  };

  const deletePost = async () => {
    if (window.confirm("게시글을 삭제하시겠습니까?")) {
      await axios.delete(`http://localhost:8080/board/${id}`);
      alert("삭제되었습니다.");
      navigate("/");
    }
  };

  const moveToBoard = () => {
    navigate("/");
  };

  return (
    <div className="board-detail-container">
      <div className="board-detail-content">
        <h2 className="board-detail-title">{board.title}</h2>
        <div className="board-detail-info">
          <h5>작성자 : {board.writer}</h5>
          <p style={{ fontSize: "12px", color: "gray" }}>{board.writingTime}</p>
        </div>
        <hr />
        <p className="board-detail-body">{board.body}</p>
        <hr />
        <div className="board-detail-button-group">
          <button onClick={moveToEdit}>수정</button>
          <button onClick={deletePost}>삭제</button>
          <button onClick={moveToBoard}>목록</button>
        </div>
      </div>
    </div>
  );
};

export default BoardDetail2;