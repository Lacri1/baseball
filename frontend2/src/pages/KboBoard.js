import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api";
import "../styles/NoticeboardPage.css";

const KboBoardList = () => {
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]);
  const [category, setCategory] = useState("all");
  const [keyword, setKeyword] = useState("");
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);

  const size = 10; // 한 페이지에 보여줄 게시글 수

  const fetchPosts = async () => {
    setLoading(true);
    try {
      const res = await api.get("/kbo/board", {
        params: { category, keyword, page, size },
      });
      setPosts(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error(err);
      alert("게시글 로딩 실패");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, [category, keyword, page]);

  const handleSearch = () => {
    setPage(1);
    fetchPosts();
  };

  return (
    <div className="noticeboard-container">
      <h2>KBO 게시판</h2>

      <div style={{ marginBottom: "20px" }}>
        <select value={category} onChange={(e) => setCategory(e.target.value)}>
          <option value="all">전체</option>
          <option value="general">자유</option>
          <option value="kbo">KBO</option>
          <option value="NPB">NPB</option>
          <option value="mlb">MLB</option>
          <option value="amateur">사회인야구</option>
        </select>

        <input
          type="text"
          placeholder="검색어"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          style={{ marginLeft: "10px" }}
        />
        <button onClick={handleSearch} style={{ marginLeft: "5px" }}>
          검색
        </button>

        <button
          onClick={() => navigate("/PostForm2/new")}
          style={{ float: "right" }}
        >
          글쓰기
        </button>
      </div>

      {loading ? (
        <div style={{ textAlign: "center" }}>로딩 중...</div>
      ) : posts.length === 0 ? (
        <div>게시글이 없습니다.</div>
      ) : (
        <table className="noticeboard-table">
          <thead>
            <tr>
              <th>번호</th>
              <th>제목</th>
              <th>작성자</th>
              <th>카테고리</th>
              <th>작성일</th>
            </tr>
          </thead>
          <tbody>
            {posts.map((post) => (
              <tr
                key={post.postId}
                onClick={() => navigate(`/kboBoard/${post.postId}`)}
                style={{ cursor: "pointer" }}
              >
                <td>{post.postId}</td>
                <td>{post.title}</td>
                <td>{post.writer}</td>
                <td>{post.category}</td>
                <td>{post.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* 페이징 */}
      <div style={{ marginTop: "20px", textAlign: "center" }}>
        {Array.from({ length: totalPages }, (_, i) => i + 1).map((p) => (
          <button
            key={p}
            onClick={() => setPage(p)}
            style={{
              margin: "0 3px",
              backgroundColor: p === page ? "#666" : "#eee",
              color: p === page ? "#fff" : "#000",
            }}
          >
            {p}
          </button>
        ))}
      </div>
    </div>
  );
};

export default KboBoardList;
