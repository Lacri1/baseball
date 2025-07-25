import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/NoticeboardPage.css';

const mockPosts = [
  { id: 1, title: '첫 번째 자유게시글', author: 'user1', date: '2024-06-01', views: 12 },
  { id: 2, title: 'React로 게시판 만들기', author: 'user2', date: '2024-06-02', views: 8 },
  { id: 3, title: '1 팁', author: 'user3', date: '2024-06-03', views: 5 },
  { id: 4, title: '오늘의 야구 이야기', author: 'user4', date: '2024-06-04', views: 20 },
  { id: 6, title: '커뮤니티 이용 규칙', author: 'admin', date: '2024-06-06', views: 30 },
];

const POSTS_PER_PAGE = 5;

const NoticeBoard = () => {
  const [page, setPage] = useState(1);
  const totalPages = Math.ceil(mockPosts.length / POSTS_PER_PAGE);
  const pagedPosts = mockPosts.slice((page-1)*POSTS_PER_PAGE, page*POSTS_PER_PAGE);
  const navigate = useNavigate();
 ;

  return (
    <div className="noticeboard-container">
      <div className="board-tabs">
        <Link to="/noticeboard" className="board-tab">자유게시판</Link>
        <Link to="/kboboard" className="board-tab">KBO게시판</Link>
      </div>
      <div className="noticeboard-header">
        <h1 className="noticeboard-title">자유게시판</h1>
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
        <button className="noticeboard-write-btn" onClick={() => navigate('/PostForm2')}>글쓰기</button>
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

export default NoticeBoard;

