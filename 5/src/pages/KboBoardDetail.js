import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/NoticeboardPage.css';

const mockPosts = [
  { id: 1, title: 'KBO 첫 번째 게시글', author: 'kbo1', date: '2024-06-01', views: 10 },
  { id: 2, title: 'KBO 경기 분석', author: 'kbo2', date: '2024-06-02', views: 8 },
  { id: 3, title: 'KBO 선수 소식', author: 'kbo3', date: '2024-06-03', views: 5 },
  { id: 4, title: '오늘의 KBO', author: 'kbo4', date: '2024-06-04', views: 20 },
  { id: 5, title: 'KBO 규칙', author: 'admin', date: '2024-06-05', views: 30 },
];

const KboBoardDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const post = mockPosts.find(p => String(p.id) === String(id));

  // 댓글 상태
  const [comments, setComments] = useState([
    { id: 1, author: '댓글러1', content: '좋은 글입니다!', date: '2024-06-07' },
    { id: 2, author: '댓글러2', content: 'KBO 화이팅!', date: '2024-06-07' },
  ]);
  const [commentInput, setCommentInput] = useState('');
  const [commentAuthor, setCommentAuthor] = useState('');

  const handleCommentSubmit = (e) => {
    e.preventDefault();
    if (!commentInput.trim() || !commentAuthor.trim()) return;
    const newComment = {
      id: comments.length + 1,
      author: commentAuthor,
      content: commentInput,
      date: new Date().toISOString().slice(0, 10),
    };
    setComments([...comments, newComment]);
    setCommentInput('');
    setCommentAuthor('');
  };

  if (!post) {
    return <div style={{ padding: 40 }}>게시글을 찾을 수 없습니다.</div>;
  }

  return (
    <div className="noticeboard-detail-container" style={{ maxWidth: 600, margin: '40px auto', background: 'white', borderRadius: 10, boxShadow: '0 4px 24px rgba(0,0,0,0.08)', padding: 32 }}>
      <h2 style={{ marginBottom: 8 }}>{post.title}</h2>
      <div style={{ color: '#666', fontSize: 14, marginBottom: 12 }}>
        <span>작성자: {post.author}</span> | <span>날짜: {post.date}</span> | <span>조회수: {post.views}</span>
      </div>
      <hr style={{ margin: '12px 0' }} />
      <div style={{ minHeight: 80, marginBottom: 16 }}>
        {/* 실제 내용이 없으므로 mock 내용 */}
        <p>이곳에 게시글의 상세 내용이 표시됩니다.</p>
      </div>
      {/* 댓글 영역 시작 */}
      <div className="board-detail-comments">
        <h3>댓글</h3>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {comments.map((c) => (
            <li key={c.id} style={{ borderBottom: '1px solid #eee', marginBottom: 8, paddingBottom: 8 }}>
              <strong>{c.author}</strong> <span style={{ color: '#888', fontSize: 12 }}>{c.date}</span>
              <div>{c.content}</div>
            </li>
          ))}
        </ul>
        <form onSubmit={handleCommentSubmit} style={{ marginTop: 16, display: 'flex', gap: 8 }}>
          <input
            type="text"
            placeholder="작성자"
            value={commentAuthor}
            onChange={e => setCommentAuthor(e.target.value)}
            style={{ flex: '0 0 100px', padding: 4 }}
            required
          />
          <input
            type="text"
            placeholder="댓글을 입력하세요"
            value={commentInput}
            onChange={e => setCommentInput(e.target.value)}
            style={{ flex: 1, padding: 4 }}
            required
          />
          <button type="submit" style={{ padding: '4px 12px' }}>등록</button>
        </form>
      </div>
      {/* 댓글 영역 끝 */}
      <button onClick={() => navigate(-1)} style={{ marginTop: 24, padding: '6px 18px', background: '#3498db', color: 'white', border: 'none', borderRadius: 5, fontWeight: 500, cursor: 'pointer' }}>목록으로</button>
    </div>
  );
};

export default KboBoardDetail; 