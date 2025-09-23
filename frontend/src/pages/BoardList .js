import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const BoardList = () => {
  const [boards, setBoards] = useState([]);
  const navigate = useNavigate();

  return (
    <div>
      <h2>게시판 목록</h2>
      <button onClick={() => navigate("/write")}>글쓰기</button>
      {boards.length === 0 ? (
        <p>게시글이 없습니다.</p>
      ) : (
        <ul>
          {boards.map((b) => (
            <li key={b.id}>
              <Link to={`/detail/${b.id}`}>{b.title}</Link> - {b.writer}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default BoardList;
