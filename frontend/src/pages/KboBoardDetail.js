// src/pages/KboBoardDetail.js
import React, { useState, useEffect, useContext, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';
import '../styles/KboBoardDetail.css';

const KboBoardDetail = () => {
  const { user } = useContext(AuthContext);
  const { id } = useParams(); // 게시글 번호
  const navigate = useNavigate();

  const [board, setBoard] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingCommentText, setEditingCommentText] = useState('');
  const [loading, setLoading] = useState(true);

  const commentsEndRef = useRef(null);

  const scrollToBottom = () => {
    commentsEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  // 게시글 + 댓글 불러오기
  const fetchBoardDetail = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/api/board/${id}`);
      setBoard(res.data.board);
      setComments(res.data.comments || []);
    } catch (err) {
      console.error(err);
      alert('게시글 로딩 실패');
    }
  };

  useEffect(() => {
    setLoading(true);
    fetchBoardDetail().finally(() => setLoading(false));

    const interval = setInterval(fetchBoardDetail, 5000); // 5초마다 댓글 갱신
    return () => clearInterval(interval);
  }, [id]);


  // 댓글 작성
  const handleCommentSubmit = async () => {
    if (!user) { alert('로그인 후 작성 가능합니다.'); return; }
    if (!newComment.trim()) return;

    try {
      await axios.post(`http://localhost:8080/api/comment`, {
        boardNo: id,
        writer: user.Id, // 문자/숫자 모두 가능
        text: newComment.trim(),
      }, { headers: { 'Content-Type': 'application/json' } });

      setNewComment('');
      fetchBoardDetail(); // 댓글 목록 갱신
    } catch (err) {
      console.error(err);
      alert('댓글 작성 실패');
    }
  };

  // 댓글 삭제
  const handleCommentDelete = async (commentId, commentWriter) => {
    if (!user || user.Id !== commentWriter) {
      alert('자신의 댓글만 삭제 가능합니다.');
      return;
    }
    if (!window.confirm('댓글을 삭제하시겠습니까?')) return;

    try {
      await axios.delete(`http://localhost:8080/api/comment/${id}/${commentId}`);
      setComments(prev => prev.filter(c => c.commentId !== commentId));
    } catch (err) {
      console.error(err);
      alert('댓글 삭제 실패');
    }
  };

  const startEditing = (commentId, text) => {
    setEditingCommentId(commentId);
    setEditingCommentText(text);
  };

  const saveEditComment = async () => {
    if (!editingCommentText.trim()) return;
    try {
      await axios.put(`http://localhost:8080/api/comment/${id}/${editingCommentId}`, {
        text: editingCommentText
      });
      setEditingCommentId(null);
      setEditingCommentText('');
      fetchBoardDetail();
    } catch (err) {
      console.error(err);
      alert('댓글 수정 실패');
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleString('ko-KR', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit'
    });
  };

  if (loading) return <div className="loading-message">로딩 중...</div>;
  if (!board) return <div className="error-message">게시글이 존재하지 않습니다.</div>;

  return (
    <div className="board-detail-container">
      <div className="board-detail-header">
      
      <div className="board-detail-content">
          <span>작성자: {board.writer}</span>
          <span>작성일: {formatDate(board.createdAt)}</span>
        <h1 className="board-title">{board.title}</h1>
       

      {/* 댓글 섹션 */}
      <div className="comments-section">
        <div className="comments-header">
          <h3>댓글 {comments.length}개</h3>
        <button onClick={() => navigate(-1)} className="back-button">← 목록으로</button>
        </div>
        
        {/* 댓글 작성 */}
        <div className="comment-write-area">
          <div className="comment-write-form">
            <textarea
              value={newComment}
              onChange={e => setNewComment(e.target.value)}
              placeholder="댓글을 입력하세요"
              rows={3}
              className="comment-input"
              />
            <div className="comment-write-buttons">
              <button 
                onClick={handleCommentSubmit} 
                disabled={!user || !newComment.trim()} 
                className="comment-submit"
                >
                댓글등록
              </button>
            </div>
          </div>
          {!user && <div className="login-notice">로그인 후 댓글 작성이 가능합니다.</div>}
        </div>

        {/* 댓글 목록 */}
        <div className="comments-list">
          {comments.length === 0 ? (
            <div className="empty-comments">
              <p>등록된 댓글이 없습니다.</p>
            </div>
          ) : (
            comments.map(c => (
              <div key={c.commentId} className="comment-row">
                {editingCommentId === c.commentId ? (
                  <div className="comment-edit-mode">
                    <textarea
                      value={editingCommentText}
                      onChange={e => setEditingCommentText(e.target.value)}
                      rows={3}
                      className="comment-edit-input"
                    />
                    <div className="comment-edit-buttons">
                      <button onClick={saveEditComment} className="btn-save">저장</button>
                      <button onClick={() => setEditingCommentId(null)} className="btn-cancel">취소</button>
                    </div>
                  </div>
                ) : (
                  <div className="comment-content">
                    <div className="comment-body">
                      <div className="comment-text">{c.text}</div>
                    </div>
                    <div className="comment-footer">
                      <div className="comment-author">
                        <span className="author-name">{c.writer}</span>
                        <span className="comment-date">{formatDate(c.date)}</span>
                      </div>
                      {user && user.Id === c.writer && (
                        <div className="comment-actions">
                          <button 
                            onClick={() => startEditing(c.commentId, c.text)} 
                            className="action-btn edit-btn"
                          >
                            수정
                          </button>
                          <button 
                            onClick={() => handleCommentDelete(c.commentId, c.writer)} 
                            className="action-btn delete-btn"
                          >
                            삭제
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            ))
          )}
          <div ref={commentsEndRef} />
          </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default KboBoardDetail;
