import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/NoticeboardPage.css';

const mockPosts = [
  { id: 1, title: 'KBO 첫 번째 게시글', author: 'kbo1', date: '2024-06-01', views: 10 },
  { id: 2, title: 'KBO 경기 분석', author: 'kbo2', date: '2024-06-02', views: 8 },
  { id: 3, title: 'KBO 선수 소식', author: 'kbo3', date: '2024-06-03', views: 5 },
  { id: 4, title: '오늘의 KBO', author: 'kbo4', date: '2024-06-04', views: 20 },
  { id: 5, title: 'KBO 규칙', author: 'admin', date: '2024-06-05', views: 30 },
];

const POSTS_PER_PAGE = 5;

const KboBoard = () => {
  const [page, setPage] = useState(1);
  const totalPages = Math.ceil(mockPosts.length / POSTS_PER_PAGE);
  const pagedPosts = mockPosts.slice((page-1)*POSTS_PER_PAGE, page*POSTS_PER_PAGE);
  const navigate = useNavigate();

  return (
    <div className="noticeboard-container">
      <div className="board-tabs">
        <Link to="/noticeboard" className="board-tab">자유게시판</Link>
        <Link to="/kboboard" className="board-tab">KBO게시판</Link>
      </div>
      <div className="noticeboard-header">
        <h1 className="noticeboard-title">KBO게시판</h1>
      </div>
      <div className="noticeboard-list">
        {pagedPosts.map(post => (
          <div className="noticeboard-card" key={post.id}>
            <div className="noticeboard-post-title">{post.title}</div>
            <div className="noticeboard-meta">
              <span>작성자: {post.author}</span>
              <span>날짜: {post.date}</span>
              <span>조회수: {post.views}</span>
            </div>
          </div>
        ))}
        <button className="noticeboard-write-btn" onClick={() => navigate('/writing')}>글쓰기</button>
      </div>
      <div className="noticeboard-pagination">
        {Array.from({ length: totalPages }, (_, idx) => (
          <button
            key={idx+1}
            className={`noticeboard-page-btn${page === idx+1 ? ' active' : ''}`}
            onClick={() => setPage(idx+1)}
          >
            {idx+1}
          </button>
        ))}
      </div>
    </div>
  );
};

export default KboBoard; 