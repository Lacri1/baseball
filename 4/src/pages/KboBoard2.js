import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/KboBoard2.css";

const   KboBoard2 = () => {
  const navigate = useNavigate();
  const [boardList, setBoardList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [postsPerPage, setPostsPerPage] = useState(10); // Number of posts per page, default: 5
  const [totalPages, setTotalPages] = useState(1); // Total number of pages

  useEffect(() => {
    getBoardList();
  }, [currentPage, postsPerPage]);

  const getBoardList = async () => {
    try {
      const response = await axios.get("http://localhost:8080/board/me");
      setBoardList(response.data);
      setTotalPages(Math.ceil(response.data.length / postsPerPage));
    } catch (error) {
      console.error("불러오지 못함", error);
    }
  };

  const indexOfLastPost = currentPage * postsPerPage;
  const indexOfFirstPost = indexOfLastPost - postsPerPage;
  const currentPosts = boardList.slice(indexOfFirstPost, indexOfLastPost);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  const handlePostsPerPage = (e) => {
    setPostsPerPage(parseInt(e.target.value));
    setCurrentPage(1);
  };

  const Post = () => {
    navigate("/postform2");
  };
  return (
    <div className="board-container">
      <h1 className="board-title">게시판</h1>
    <div className="board-posts-per-page">
    <label>
      게시물 수:{" "}
      <select value={postsPerPage} onChange={handlePostsPerPage}>
        <option value={10}>10개</option>
        <option value={20}>20개</option>
        <option value={30}>30개</option>
      </select>
    </label>
  </div>

      <div className="board-button">
        <button onClick={Post}>글쓰기</button>
      </div>
      <br />

      <ul className="board-posts">
        {currentPosts.map((board) => (
          <li key={board.id} className="board-post-item">
            <Link to={`/board/${board.id}`}>{board.title}</Link>
            <span>작성자: {board.writer}</span>
            <span> | 작성 시간: {board.writingTime}</span>
          </li>
        ))}
      </ul>
 

      <div className="board-pagination">
        {[...Array(totalPages).keys()].map((number) => (
          <button
            key={number + 1}
            className={currentPage === number + 1 ? "selected" : ""}
            onClick={() => paginate(number + 1)}
          >
            {number + 1}
          </button>
        ))}
      </div>
    </div>
  );
};

export default  KboBoard2;